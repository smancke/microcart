package net.mancke.microcart;

import java.nio.charset.StandardCharsets;

import net.mancke.microcart.model.Cart;
import io.dropwizard.views.View;

public class CartView extends View {

	private Cart cart;

	protected CartView(Cart cart) {
		super("cart.ftl", StandardCharsets.UTF_8);
		this.cart = cart;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}
}
