package net.mancke.microcart;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import net.mancke.microcart.voucher.VoucherService;

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

	private VoucherService voucherService;

    /**
     * @param voucherService 
     */
    @Inject
    public CartResource(final FrontConfiguration cfg, CartService cartService, VoucherService voucherService) {
        this.configuration = cfg;
        this.cartService = cartService;
        this.voucherService = voucherService;
    }

    /**
     * returns the cart of the current user or session.
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/my-cart/link")
    @Produces({"text/html; charset=utf-8"})
    public CartLinkView getMyCartLink(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId)
    		throws URISyntaxException {

		String cartLabel = getMyCartArticleTotalQuantity(trackingId);
    	return new CartLinkView(cartLabel);
    }

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
     * returns number product meters
     */
    @Timed
    @GET
    @Path("/my-cart/articleTotalQuantity")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMyCartArticleTotalQuantity(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId) 
    		throws URISyntaxException {

		List<Position> positions = cartService.getOrCreateCartByTrackingId(trackingId).getPositions();
		double sumPieces = positions.stream()
				.filter(position -> position.getQuantityUnits() == 1)
				.mapToDouble(Position::getQuantity)
				.sum();
		
		double sumNonPieces = positions.stream()
				.filter(position -> position.getQuantityUnits() != 1)
				.mapToDouble(Position::getQuantity)
				.sum();

		String qty = "";

		if (sumPieces > 0) {
			qty += sumPieces + "x";
		}
		if (sumNonPieces > 0) {
			if (qty.length() > 0) {
				qty += " + ";
			}
			qty += String.format("%1$,.1fm", sumNonPieces);
		}

		return qty;
    }

    /**
     * add an article to the cart or changes the quantity of the article
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
    	
    	loginHandler.setConfig(configuration.getOsiamLogin());
    	setUserIdToCart(loginHandler, cart);
    	
    	cartService.saveCartToBackend(cart);
    	
    	if (req.getHeader("Referer") != null) {
    		return Response.seeOther(new URI(req.getHeader("Referer"))).build();
    	}
    	return Response.seeOther(new URI("/shop/my-cart")).build();
    }
    
    /**
     * add a voucher item to the cart
     */
    @Timed
    @POST
    @Path("/my-cart/voucher")
    public Response addVoucherToCart(
    		@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId, 
    		@FormParam("voucherId") String voucherId, 
    		@Context HttpServletRequest req,
    		@BeanParam LoginHandler loginHandler) 
    		throws URISyntaxException {
    	
    	Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);
    	
    	if (! cart.containsVoucher()) {
	    	Position position = voucherService.positionFromVoucher(voucherId);
	    	if (position == null) {
	        	return Response.seeOther(new URI("/shop/my-cart?invalidVoucher")).build();	    		
	    	}
	    	cart.addDeleteOrUpdatePosition(position);
	    	cartService.saveCartToBackend(cart);
	    	voucherService.use(voucherId, "");
    	}
    	
    	return Response.seeOther(new URI("/shop/my-cart")).build();
    }

	/**
	 * add the order id of an order to bundle for shipping
	 */
	@Timed
	@POST
	@Path("/my-cart/shippingBundle")
	public Response shippingBundle(
			@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId,
			@FormParam("shippingBundleOrderId") String shippingBundleOrderId,
			@Context HttpServletRequest req,
			@BeanParam LoginHandler loginHandler)
			throws URISyntaxException {

		Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);

		shippingBundleOrderId = shippingBundleOrderId.toLowerCase();
		shippingBundleOrderId = shippingBundleOrderId.replaceAll("bs-", "");
		shippingBundleOrderId = shippingBundleOrderId.replaceAll("[^a-zA-Z0-9]", "");

		cart.setShippingBundleOrderId(shippingBundleOrderId);
		cartService.saveCartToBackend(cart);

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
		position.setQuantityFixed(article.isQuantityFixed());
		position.setQuantityMin(article.getQuantityMin());
		position.setQuantityUnits(article.getQuantityUnits());
		position.setDownloadLink(article.getDownloadLink());
		position.setFreeShipping(article.isFreeShipping());
		if (article.getDownloadLink() != null) {
			position.setType(Position.TYPE_DOWNLOAD);
		}

		return position;
	}
}
