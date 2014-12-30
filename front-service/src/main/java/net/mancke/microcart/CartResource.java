package net.mancke.microcart;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.Position;

import com.codahale.metrics.annotation.Timed;

/**
 * 
 */
@Path("/shop")
public class CartResource {

    private static final String CART_RESOURCE = "/shop/cart";
    
	/**
     * The global configuration.
     */
    private FrontConfiguration configuration;

    /**
     */
    @Inject
    public CartResource(final FrontConfiguration cfg) {
        this.configuration = cfg;
    }

//    /**
//     * returns the cart of the current user or session.
//     * @return
//     * @throws URISyntaxException
//     */
//    @Timed
//    @GET
//    @Path("/my-cart")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Cart getMyCart(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId) 
//    		throws URISyntaxException {
//    	
//    	return getOrCreateCartByTrackingId(trackingId);
//    }


    /**
     * returns the cart of the current user or session.
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/my-cart")
    @Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
    public CartView getMyCartHtml(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId) 
    		throws URISyntaxException {
    	
    	return new CartView(getOrCreateCartByTrackingId(trackingId));
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
    	
    	return ""+ getOrCreateCartByTrackingId(trackingId).getPositions().size();
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
    		@FormParam("quantity") float quantity,
    		@Context HttpServletRequest req) 
    		throws URISyntaxException {
    	
    	Cart cart = getOrCreateCartByTrackingId(trackingId);
    	Position position = new Position();
    	position.setArticleId(articleId);
    	position.setQuantity(quantity);
    	
    	cart.addDeleteOrUpdatePosition(position);
    	saveCartToBackend(cart);
    	if (req.getHeader("Referer") != null) {
    		return Response.seeOther(new URI(req.getHeader("Referer"))).build();
    	}
    	return Response.seeOther(new URI("/shop/my-cart")).build();
    }
    
    private Cart getOrCreateCartByTrackingId(String trackingId) {
		if (trackingId != null && ! trackingId.isEmpty()) {
    		Cart cart = loadCartFromBackend(trackingId);
    		if (cart != null) {
    			return cart;
    		}
    	}	
    	return newEmptyCart(trackingId);
	}

	private Cart loadCartFromBackend(String trackingId) {
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.getForObject(configuration.getBackendURL() + CART_RESOURCE + "/"+ trackingId, Cart.class);
		} catch (HttpClientErrorException ex)   {
		    if (ex.getStatusCode().value() == 404) {
		        return null;
		    }
		    throw ex;
		}
	}

	private void saveCartToBackend(Cart cart) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForLocation(configuration.getBackendURL() + CART_RESOURCE, cart);
	}

	private Cart newEmptyCart(String trackingId) {
		Cart cart = new Cart();
		cart.setId(trackingId);
		return cart;
	}
}
