package net.mancke.microcart.paypal;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import net.mancke.microcart.CartService;
import net.mancke.microcart.model.Cart;

public class PayPalClient {
	
	private static final Logger logger = LoggerFactory.getLogger(PayPalClient.class);

	private PayPalConfiguration configuration;
	private Cart cart;
	private Map<String, String> transactionResult; 

	public PayPalClient(PayPalConfiguration configuration, Cart cart) {
		this.configuration = configuration;
		this.cart = cart;
	}

	public void startTransaction() {
		Map<String, String> params = new HashMap<String, String>(); 
		params.put("method", "SetExpressCheckout");
		params.put("cancelUrl", configuration.getCancelBaseUrl());
		params.put("returnUrl", configuration.getReturnBaseUrl());
		params.put("localecode", "DE");
		
		this.transactionResult = callApi(params);
	}

	public String endTransaction(String token, String payerId) {
		Map<String, String> params = new HashMap<String, String>(); 
		params.put("method", "DoExpressCheckoutPayment");
		params.put("token", token);
		params.put("payerId", payerId);
		
		this.transactionResult = callApi(params);
		
		String amount = this.transactionResult.get("PAYMENTINFO_0_AMT");

		if (! priceToString(cart.getTotalPrice()).equals(amount)) {
			throw new PaypalException("cart amount does not match paypal amount", cart.getId(), params, this.transactionResult);
		}
		return ""+this.transactionResult.get("CORRELATIONID");
	}

	private String priceToString(float totalPrice) {
		return String.format(Locale.US, "%.2f", cart.getTotalPrice());
	}

	public URI getRedirectUri() {
		try {
			return new URI(configuration.getRedirectUrl() +"?cmd=_express-checkout&token="+ transactionResult.get("TOKEN"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, String> callApi(Map<String, String> params) {
		params.put("user", configuration.getUser());
		params.put("pwd", configuration.getPwd());
		params.put("signature", configuration.getSignature());
		params.put("version", "95");
		params.put("paymentrequest_0_paymentaction", "SALE");
		params.put("paymentrequest_0_amt", priceToString(cart.getTotalPrice()));
		params.put("paymentrequest_0_currencycode", "EUR");
		
		RestTemplate restTemplate = new RestTemplate();
		Map<String, String> result = toMap(restTemplate.getForObject(uri(configuration.getApiUrl(), params), String.class));
		
		if (! ("success".equalsIgnoreCase(""+result.get("ACK"))
				|| "SuccessWithWarning".equalsIgnoreCase(""+result.get("ACK")))
			) {
			throw new PaypalException("paypal error (ack=success expected)", cart.getId(), params, result);
		}
		if ("SuccessWithWarning".equalsIgnoreCase(result.get("ACK"))) {
			logger.warn("Paypal SuccessWithWarning (cart="+cart.getId()+")+ result");
		}
		return result;
	}	

	private Map<String, String> toMap( String resultBody ) {
		HashMap<String, String> result = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(resultBody, "&");
		while (tokenizer.hasMoreTokens()) {
			StringTokenizer stInternalTokenizer = new StringTokenizer(tokenizer.nextToken(), "=");
			if (stInternalTokenizer.countTokens() == 2) {
				String key;
				try {
					key = URLDecoder.decode(stInternalTokenizer.nextToken(), "UTF-8");
					String value = URLDecoder.decode(stInternalTokenizer.nextToken(), "UTF-8");
					result.put(key.toUpperCase(), value);
				} catch (UnsupportedEncodingException e) {
					// utf-8 is always there
				}
			}
		}
		return result;
	}
	
    private URI uri(String uri, Map<String, String> queryParams) {
    	UriBuilder uriBuilder = UriBuilder.fromUri(uri);
        
        for (String parameterName : queryParams.keySet()) {
            uriBuilder.queryParam(parameterName, queryParams.get(parameterName));            
        }
        return uriBuilder.build();
	}
}
