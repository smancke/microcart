package net.mancke.microcart;

import net.mancke.microcart.model.Cart;
import io.dropwizard.views.View;

public class CartView extends View {

	private Cart cart;

	protected CartView(Cart cart) {
		super("cart.ftl");
		this.cart = cart;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}
}
