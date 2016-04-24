package net.mancke.microcart;

import io.dropwizard.views.View;

import java.nio.charset.StandardCharsets;
import java.util.List;

import net.mancke.microcart.model.Cart;

public class OrderView extends View {

	private FrontConfiguration cfg;
	private Cart cart;
	private List<ValidationError> validationErrors;
	private String paymentInfo;

	protected OrderView(String template, Cart cart, FrontConfiguration cfg) {
		super(template, StandardCharsets.UTF_8);
		this.cart = cart;
		this.cfg = cfg;
	}

	public OrderView(String template, Cart cart,
			List<ValidationError> validationErrors) {
		super(template, StandardCharsets.UTF_8);
		this.cart = cart;
		this.validationErrors = validationErrors;
	}

	public String getPaymentInfo() {
		return paymentInfo;
	}
	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}    

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public FrontConfiguration getCfg() {
		return cfg;
	}

	public void setCfg(FrontConfiguration cfg) {
		this.cfg = cfg;
	}

	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	public void setValidationErrors(List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}
}