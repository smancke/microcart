package net.mancke.microcart;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.osiam.resources.scim.User;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.Position;
import net.mancke.microcart.osiam.LoginHandler;

import com.codahale.metrics.annotation.Timed;

/**
 * 
 */
@Path("/shop")
public class CartResource {
    
	/**
     * The global configuration.
     */
    private FrontConfiguration configuration;

	private CartService cartService;

    /**
     */
    @Inject
    public CartResource(final FrontConfiguration cfg, CartService cartService) {
        this.configuration = cfg;
        this.cartService = cartService;
    }

    /**
     * returns the cart of the current user or session.
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/my-cart")
    @Produces({"text/html; charset=utf-8", MediaType.APPLICATION_JSON})
    public CartView getMyCartHtml(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId) 
    		throws URISyntaxException {
    	
    	return new CartView(cartService.getOrCreateCartByTrackingId(trackingId));
    }

    /**
     * returns number if cart items as simple text.
     */
    @Timed
    @GET
    @Path("/my-cart/itemCount")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMyCartItemCount(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId) 
    		throws URISyntaxException {
    	
    	return ""+ cartService.getOrCreateCartByTrackingId(trackingId).getPositions().size();
    }

    /**
     * added an article to the cart or changes the quantity of the article
     * @param articleId 
     * @param quantity 
     */
    @Timed
    @POST
    @Path("/my-cart/article")
    public Response updateCartItem(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId, 
    		@FormParam("articleId") String articleId, 
    		@FormParam("quantity") String quantityInput,
    		@FormParam("action") String action,
    		@Context HttpServletRequest req,
    		@BeanParam LoginHandler loginHandler) 
    		throws URISyntaxException {
    	
    	Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);
    	
    	Float quantity = verifyQuantity(action, quantityInput);
    	Position position = getNewPosition(articleId, quantity);
    	
    	cart.addDeleteOrUpdatePosition(position);
    	
    	setUserIdToCart(loginHandler, cart);
    	
    	cartService.saveCartToBackend(cart);
    	
    	if (req.getHeader("Referer") != null) {
    		return Response.seeOther(new URI(req.getHeader("Referer"))).build();
    	}
    	return Response.seeOther(new URI("/shop/my-cart")).build();
    }

	private float verifyQuantity(String action, String quantityInput) {
		if ("removeArticle".equals(action)) {
    		quantityInput = "0";
    	}
    	quantityInput = quantityInput.replace(",", ".");
    	quantityInput = quantityInput.replaceAll("[^0-9\\.]", "");
    	try {
    		return Float.parseFloat(quantityInput);
    	} catch (NumberFormatException nfe) {
    		return 1f;
    	}
	}

	private void setUserIdToCart(LoginHandler loginHandler, Cart cart) {
		if (loginHandler.verifyLogin()) {
			cart.setUserId( loginHandler.getAuthCookie().getUserId() );
    	}
	}

	private Position getNewPosition(String articleId, float quantity) {
		Position position = new Position();
    	position.setArticleId(articleId);
    	position.setQuantity(quantity);
    	
    	ArticleService articleService = new ArticleService(configuration);
    	ArticleService.Article article = articleService.getArticle(articleId);
    	position.setPricePerUnit(article.getPrice());
    	position.setDiscountPercent(article.getDiscount());
    	position.setImageUrl(article.getImg_thumb());
    	position.setTitle(article.getTitle());
    	
		return position;
	}
}
