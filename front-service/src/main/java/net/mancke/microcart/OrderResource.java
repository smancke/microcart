package net.mancke.microcart;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.OrderData;
import net.mancke.microcart.osiam.LoginHandler;
import net.mancke.microcart.paypal.PayPalClient;

import com.codahale.metrics.annotation.Timed;

/**
 * 
 */
@Path("/shop")
public class OrderResource {

    private static final String ORDER_RESOURCE = "/shop/order";
    
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
    		@FormParam("forceEmail") boolean forceEmail,
    		@BeanParam LoginHandler loginHandler) 
    		throws URISyntaxException {
    	System.out.println("forceEmail: "+ forceEmail);
    	
    	Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);
    	cart.setOrderData(orderData);
    	cartService.saveCartToBackend(cart);
    	
    	List<ValidationError> validationErrors = validate(orderData, forceEmail);

    	if (goBackAction != null) { //back button
    		return Response.seeOther(new URI("/shop/my-cart")).build();
    	}
    	
    	if (validationErrors.size() > 0) { // show errors
    		return Response.ok().entity(new OrderView("orderData.ftl", cart, validationErrors)).build();
    	}

    	// on precash go to paypal and place the order later
    	if (Cart.PAYPAL.equals(orderData.getPaymentType())) {
    		PayPalClient payPal = new PayPalClient(configuration.getPayPal(), cart);
    		payPal.startTransaction();
    		  
	    	return Response.seeOther(payPal.getRedirectUri()).build();
    	}

    	// otherwise (on precash) place the order and go to order confirmation page
    	loginHandler.setConfig(configuration.getOsiamLogin());
    	String orderId = cartService.placeOrder(cart, loginHandler);
    	return Response.seeOther(new URI("/shop/orderConfirmation/"+ orderId)).build();
    }

	/**
     * returns the order success view
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET //but not save as it should be for ReST
    @Path("/paypalSuccess")
    @Produces({"text/html; charset=utf-8"})
    public Response getOrderConfirmationPaypal (
    		@CookieParam(TrackingIdFilter.TRACKING_COOKIE_KEY) String trackingId,
    		@QueryParam("token") String token,
    		@QueryParam("PayerID") String payerid,
    		@BeanParam LoginHandler loginHandler) 
    		throws URISyntaxException {

    	loginHandler.setConfig(configuration.getOsiamLogin());

    	Cart cart = cartService.getOrCreateCartByTrackingId(trackingId);
    	
    	PayPalClient payPal = new PayPalClient(configuration.getPayPal(), cart);
		String payPalCorrelationId = payPal.endTransaction(token, payerid);
		
		cart.getOrderData().setPayPalCorrelationId(payPalCorrelationId);
    	String orderId = cartService.placeOrder(cart, loginHandler);
    	return Response.seeOther(new URI("/shop/orderConfirmation/"+ orderId)).build();
    }
    
    /**
     * returns the order success view
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/orderConfirmation/{orderId}")
    @Produces({"text/html; charset=utf-8"})
    public OrderView getOrderConfirmation(
    		@PathParam("orderId") String orderId) 
    		throws URISyntaxException {


    	Cart cart = cartService.getOrder(orderId);
    	if (cart == null || cart.getId() == null) {
    		throw new RuntimeException("no such order "+ orderId);
    	}
    	
		OrderView orderView = new OrderView("orderConfirmation.ftl", cart);
		
    	orderView.setPaymentInfo(new TemplateEngine(configuration).renderPrecashPaymentInfo(cart, orderId));
        	
    	return orderView;
    }
    
	private List<ValidationError> validate(OrderData orderData, boolean forceEmail) {
		List<ValidationError> validationErrors = new ArrayList<ValidationError>();
    	if (!orderData.isAgb()) {
    		validationErrors.add(new ValidationError("agb", "Die <b>AGB</b> müssen akzeptiert werden."));
    	}
    	if (! Cart.PAYPAL.equals(orderData.getPaymentType())
    			&& ! Cart.PRECASH.equals(orderData.getPaymentType())) {
    		validationErrors.add(new ValidationError("paymentType", "Es muss ein <b>Bezahlweg</b> ausgewählt werden"));
    	}

    	addErrorIfEmpty(validationErrors, orderData.getGivenName(), "givenName", "Vorname");
    	addErrorIfEmpty(validationErrors, orderData.getFamilyName(), "familyName", "Nachname");
    	addErrorIfEmpty(validationErrors, orderData.getStreetAddress(), "streetAddress", "Straße");
    	addErrorIfEmpty(validationErrors, orderData.getLocality(), "locality", "Ort");
    	addErrorIfEmpty(validationErrors, orderData.getPostalCode(), "postalCode", "Postleitzahl");

    	MailBoxValidator mailBoxValidator = new MailBoxValidator(configuration.getMailCheckFrom());
    	if (! (mailBoxValidator.isEmailSyntaxValid(orderData.getEmail())
    			&& mailBoxValidator.doesHostExist(orderData.getEmail()))) {
    		validationErrors.add(new ValidationError("email", "Die <b>E-Mail Adresse</b> ist nicht gültig."));
    	} else {	    	
	    	if (! forceEmail && ! mailBoxValidator.mayMailboxExist(orderData.getEmail())) {
	    		validationErrors.add(new ValidationError("emailWarning", "Die <b>E-Mail Adresse</b> konnte nicht geprüft werden."));
	    	}
    	}
		return validationErrors;
	}

	private void addErrorIfEmpty(List<ValidationError> validationErrors,
			String value, String fieldId, String fieldLabel) {
		if (value == null
				|| value.trim().isEmpty()) {
	    	validationErrors.add(new ValidationError(fieldId, "Das Feld <b>"+ fieldLabel +"</b> darf nicht leer sein"));
		}		
	}

}
