package net.mancke.microcart;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.OrderData;
import net.mancke.microcart.osiam.LoginHandler;
import net.mancke.microcart.voucher.VoucherService;

import org.joda.time.DateTime;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class CartService {

	private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private static final String CART_RESOURCE = "/shop/cart";
    private static final String ORDER_RESOURCE = "/shop/order";
    
    private FrontConfiguration configuration;

	private VoucherService voucherService;

    public CartService(final FrontConfiguration cfg, VoucherService voucherService) {
        this.configuration = cfg;
        this.voucherService = voucherService;
    }

	public Cart getOrCreateCartByTrackingId(String trackingId) {
		if (trackingId != null && ! trackingId.isEmpty()) {
    		Cart cart = loadCartFromBackend(trackingId);
    		if (cart != null) {
        		// set the configuration values here, if we changed this
        		cart.setShippingCosts(configuration.getShippingCosts());
        		cart.setShippingCostLimit(configuration.getShippingCostLimit());
    			return cart;
    		}
    	}	
    	return newEmptyCart(trackingId);
	}
	
	public Cart getOrder(String orderId) {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(configuration.getBackendURL() + ORDER_RESOURCE + "/"+ orderId, Cart.class);
	}

	public void saveCartToBackend(Cart cart) {
		cart.setTimestampLastUpdated(new DateTime());
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForLocation(configuration.getBackendURL() + CART_RESOURCE, cart);
	}

	/**
	 * Saves the shoping cart as an order.
	 * This includes 
	 * <ul>
	 *   <li>deletion of the old cart</li>
	 *   <li>Confirmation Mail to the User</li>
	 *   <li>Notification Mail to the shop owner</li>
	 *   <li>Saving of the order date into OSIAM</li> 
	 * </ul>
	 * 
	 * @param cart
	 * @param loginHandler
	 * @return the id of the placed order
	 */
	public String placeOrder(Cart cart, LoginHandler loginHandler) {
		RestTemplate restTemplate = new RestTemplate();

		cart.setTimestampLastUpdated(new DateTime());
		cart.setTimestamp(new DateTime());
		String cartId = cart.getId();
		cart.setId(UUID.randomUUID().toString());
		
		String orderId = null;
		URI orderLocation = null;
		try {
			// save order
			orderLocation = restTemplate.postForLocation(configuration.getBackendURL() + ORDER_RESOURCE, cart);
						
			logger.info("placed order "+orderLocation );
			String[] urlParts = orderLocation.toString().split("/");
			orderId = urlParts[urlParts.length-1];			
		} catch (RuntimeException e) {
			logger.error("Error while placing order. Order Content: "+ saveToString(cart), e);
			throw e;
		}
		
		try {
			// delete cart
			restTemplate.delete(configuration.getBackendURL() + CART_RESOURCE + "/" + cartId);
		} catch (RuntimeException e) {
			logger.error("Error while deleting the shopping cart after order."+ saveToString(cart), e);
			// do not escalate here
		}

		// TODO:
		//saveOrderDataInAccount(cart)

		try {
			userMailNotify(cart, orderId);
			// update the mail sent flag in the order
			Cart order = restTemplate.getForObject(configuration.getBackendURL() + orderLocation, Cart.class);
			order.getPostProcessing().setUserConfirmationMailSent(true);
			restTemplate.put(configuration.getBackendURL() + orderLocation, cart);
		} catch (RuntimeException e) {
			logger.error("User mail notification lead to an error."+ saveToString(cart), e);
			// do not escalate here
		}
		
		try {
			shopOwnerNotify(cart, orderId);
		} catch (RuntimeException e) {
			logger.error("Shop owner notify atter order lead to an error."+ saveToString(cart), e);
			// do not escalate here
		}
		
		return orderId;
	}

	private String saveToString(Cart cart) {
		try {
			return new ObjectMapper().writeValueAsString(cart);
		} catch (JsonProcessingException jsonExecption) {
			logger.error("Error while serializing order for user: "+ cart.getUserId(), jsonExecption);
			return "";
		}
	}

	private void userMailNotify(Cart cart, String orderId) {
		TemplateEngine te = new TemplateEngine(configuration);
		String body = te.renderTemplate(
				"orderConfirmationMail.ftl", cart,
				te.renderPrecashPaymentInfo(cart, orderId).replaceAll("\\<[^>]*>","") 
				);
		String uri = configuration.getBackendURL() + "/mail/self/{to}/{subject}";
		MailHelper.postPlainText(uri, body, cart.getOrderData().getEmail(), configuration.getOrderSuccessMailSubject());
	}

	private void shopOwnerNotify(Cart cart, String orderId) {
		String name = 
				cart.getOrderData().getGivenName()
				+ " "
				+ cart.getOrderData().getFamilyName();
		
		String subject = "Order: "+ cart.getTotalPrice() +", " + cart.getOrderData().getPaymentType() +", " + name + ": "+ orderId.substring(0, 5);

		ObjectWriter mapper = new ObjectMapper().writerWithDefaultPrettyPrinter();
		StringBuilder body = new StringBuilder();
		try {
		    body
			.append(name).append("\n")
			.append("Preis: ").append(cart.getTotalPrice())
			.append("\n\n")
			.append(mapper.writeValueAsString(cart.getPositions()))
			.append("\n\n")
			.append(mapper.writeValueAsString(cart.getOrderData()));
		} catch (Exception e) {
		    logger.error("error constructing shopOwnerNotify mail body", e);
		}

		String uri = configuration.getBackendURL() + "/mail/{from}/self/{subject}";
		MailHelper.postPlainText(uri, body.toString(), cart.getOrderData().getEmail(), subject);
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
    
	private Cart newEmptyCart(String trackingId) {
		Cart cart = new Cart(configuration.getShippingCosts(), configuration.getShippingCostLimit());
		cart.setId(trackingId);
		return cart;
	}
	
    /**
     * Prefill the Order Data with the saved user data,
     * if the user is logged in and the fields areblank.
     * 
     * @param cart
     * @param loginHandler
     */
	public void prefillOrderData(Cart cart, LoginHandler loginHandler) {
		OrderData orderData = cart.getOrderData();
		if (orderData == null) {
			orderData = new OrderData();
			cart.setOrderData(orderData);			
		}
		
		if (loginHandler.verifyLogin()) {
						
			User user = loginHandler.getUser();
			
			cart.setUserId( user.getId() );
			
			if (empty(orderData.getGivenName())) {
				orderData.setGivenName(user.getName().getGivenName());
			}
			
			if (empty(orderData.getFamilyName())) {
				orderData.setFamilyName(user.getName().getFamilyName());			
			}

			if (empty(orderData.getHonorificPrefix())) {
				orderData.setHonorificPrefix(user.getName().getHonorificPrefix());			
			}

			PhoneNumber phoneNumber = filterOrAny(user.getPhoneNumbers(), (number) -> number.isPrimary());
			if (empty(orderData.getPhoneNumber())
					&& phoneNumber != null) {
				orderData.setPhoneNumber(phoneNumber.getValue());
			}
			
			Email email = filterOrAny(user.getEmails(), (mail) -> mail.isPrimary());
			if (empty(orderData.getEmail())
					&& email != null) {
				orderData.setEmail(email.getValue());
			}

			Address osiamAddress = filterOrAny(user.getAddresses(), (address) -> address.isPrimary());
			if (empty(orderData.getStreetAddress())
					&& osiamAddress != null) {				
				
				orderData.setLocality(osiamAddress.getLocality());
				orderData.setStreetAddress(osiamAddress.getStreetAddress());
				orderData.setPostalCode(osiamAddress.getPostalCode());
			}
		}		
	}
    
    /**
     * Returns the first Element matching the given predicate, 
     * or the first element in the list, or null if no such is given.
     * @param list
     * @param filter
     * @return
     */
    private <E> E filterOrAny(List<E> list, Predicate<E> filter) {
        Optional<E> opt = list.stream().filter(filter).findFirst();
        if (opt.isPresent())
        	return opt.get();
        if (!list.isEmpty())
        	return list.get(0);
        return null;
    }

	private boolean empty(String string) {
		return string == null || string.isEmpty();
	}
}
