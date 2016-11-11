package net.mancke.microcart;

import io.dropwizard.views.View;
import net.mancke.microcart.model.Cart;

import java.nio.charset.StandardCharsets;

public class CartLinkView extends View {

	private String cartLabel;

	protected CartLinkView( String cartLabel) {
		super("cartLink.ftl", StandardCharsets.UTF_8);
		this.cartLabel = cartLabel;
	}

	public String getCartLabel() {
		return cartLabel;
	}

	public void setCartLabel(String cartLabel) {
		this.cartLabel = cartLabel;
	}
}
