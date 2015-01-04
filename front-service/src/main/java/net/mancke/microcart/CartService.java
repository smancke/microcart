package net.mancke.microcart;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.inject.Inject;

import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.OrderData;
import net.mancke.microcart.osiam.LoginHandler;

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

public class CartService {

	private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private static final String CART_RESOURCE = "/shop/cart";
    private static final String ORDER_RESOURCE = "/shop/order";
    
    private FrontConfiguration configuration;

    public CartService(final FrontConfiguration cfg) {
        this.configuration = cfg;
    }

	public Cart getOrCreateCartByTrackingId(String trackingId) {
		if (trackingId != null && ! trackingId.isEmpty()) {
    		Cart cart = loadCartFromBackend(trackingId);
    		if (cart != null) {
    			return cart;
    		}
    	}	
    	return newEmptyCart(trackingId);
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
		try {
			RestTemplate restTemplate = new RestTemplate();
	
			cart.setTimestampLastUpdated(new DateTime());
			cart.setTimestamp(new DateTime());
			cart.setId(null);			
			// save order
			URI orderLocation = restTemplate.postForLocation(configuration.getBackendURL() + ORDER_RESOURCE, cart);
			// delete cart
			restTemplate.delete(configuration.getBackendURL() + CART_RESOURCE + "/" + cart.getId(), cart);
			
			//saveOrderDataInAccount(cart)
			//shopOwnerMailNotify(cart, orderLocation);
			//userMailNotify(cart, orderLocation);
			
			logger.info("placed order "+orderLocation );
			String[] urlParts = orderLocation.toString().split("/");
			return urlParts[urlParts.length-1];
		} catch (RuntimeException e) {
			try {
				logger.error("Error while placing order. Order Content: "+ new ObjectMapper().writeValueAsString(cart), e);
			} catch (JsonProcessingException jsonExecption) {
				logger.error("Error while serializing order for user: "+ cart.getUserId(), e);
			}
			throw e;
		}
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
		Cart cart = new Cart();
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
