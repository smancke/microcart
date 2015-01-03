package net.mancke.microcart;

import javax.inject.Inject;

import net.mancke.microcart.model.Cart;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class CartService {

    private static final String CART_RESOURCE = "/shop/cart";
    
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
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForLocation(configuration.getBackendURL() + CART_RESOURCE, cart);
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
}
