package net.mancke.microcart;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.User;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.OrderData;
import net.mancke.microcart.model.Position;
import net.mancke.microcart.osiam.LoginHandler;

import com.codahale.metrics.annotation.Timed;

/**
 * 
 */
@Path("/shop")
public class OrderResource {

    private static final String ORDER_RESOURCE = "/shop/order";

	private static final Object PAYPAL = "paypal";

	private static final Object PRECASH = "preCash";
    
	/**
     * The global configuration.
     */
    private FrontConfiguration configuration;

	private CartService cartService;

    /**
     */
    @Inject
    public OrderResource(final FrontConfiguration cfg, CartService cartService) {
        this.configuration = cfg;
        this.cartService = cartService;
    }

    /**
     * returns the order form
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/orderRegister")
    @Produces({"text/html; charset=utf-8"})
    public Response getOrderRegisterForm(
    		@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId,
    		@BeanParam LoginHandler loginHandler) 
    		throws URISyntaxException {
    	
    	loginHandler.setConfig(configuration.getOsiamLogin());
    	if (loginHandler.verifyLogin()) {
    		return Response.seeOther(new URI("/shop/orderData")).build();
    	}
    	return Response.ok().entity(
    			new OrderView("orderRegister.ftl", cartService.getOrCreateCartByTrackingId(trackingId))
    			).build();
    }

    /**
     * returns the order form
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/orderData")
    @Produces({"text/html; charset=utf-8"})
    public OrderView getOrderDataForm(@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId,
    		@BeanParam LoginHandler loginHandler) 
    		throws URISyntaxException {
    	    	
    	loginHandler.setConfig(configuration.getOsiamLogin());
    	Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);
    	cartService.prefillOrderData(cart, loginHandler);
    	return new OrderView("orderData.ftl", cart);
    }
    
	/**
     * returns the order form
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @POST
    @Path("/orderData")
    @Produces({"text/html; charset=utf-8"})
    public Response saveOrderDataForm(
    		@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId,
    		@BeanParam OrderData orderData,
    		@FormParam("goBack") String goBackAction,
    		@BeanParam LoginHandler loginHandler) 
    		throws URISyntaxException {
    	
    	Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);
    	cart.setOrderData(orderData);
    	cartService.saveCartToBackend(cart);
    	
    	List<ValidationError> validationErrors = validate(orderData);

    	if (goBackAction != null) { //back button
    		return Response.seeOther(new URI("/shop/my-cart")).build();
    	}
    	
    	if (validationErrors.size() > 0) { // show errors
    		return Response.ok().entity(new OrderView("orderData.ftl", cart, validationErrors)).build();
    	}

    	// on precash go to paypal and place the order later
    	if (PAYPAL.equals(orderData.getPaymentType())) {
	    	return Response.seeOther(new URI("http://www.paypal.de")).build();
    	}

    	// otherwise (on precash) place the order and go to order confirmation page
    	loginHandler.setConfig(configuration.getOsiamLogin());
    	String orderId = cartService.placeOrder(cart, loginHandler);
    	return Response.seeOther(new URI("/shop/orderConfirmation/"+ orderId)).build();
    }

	private List<ValidationError> validate(OrderData orderData) {
		List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    	if (!orderData.isAgb()) {
    		validationErrors.add(new ValidationError("agb", "Die AGB müssen akzeptiert werden."));
    	}
    	if (! PAYPAL.equals(orderData.getPaymentType())
    			&& ! PRECASH.equals(orderData.getPaymentType())) {
    		validationErrors.add(new ValidationError("paymentType", "Es muss ein Bezahlweg ausgewählt werden"));
    	}
		return validationErrors;
	}
	
    /**
     * returns the order form
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/orderConfirmation/:orderId")
    @Produces({"text/html; charset=utf-8"})
    public OrderView getOrderConfirmation(
    		@QueryParam("orderId") String orderId) 
    		throws URISyntaxException {
    	    	
//    	Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);
//    	cartService.prefillOrderData(cart, loginHandler);
    	return new OrderView("orderConfirmation.ftl", null);
    }
}
