package net.mancke.microcart.paypal;

import java.util.Map;

public class PaypalException extends RuntimeException {

	public PaypalException(String message, String cartId, Map params, Map result) {
		super(message +" cart="+cartId+". params="+ params +" result="+result);
	}
}
