package net.mancke.microcart;

import com.codahale.metrics.annotation.Timed;
import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.OrderData;
import net.mancke.microcart.osiam.LoginHandler;
import net.mancke.microcart.paypal.PayPalClient;
import org.springframework.web.client.HttpClientErrorException;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
@Path("/shop/download")
public class DownloadResource {

	/**
     * The global configuration.
     */
    private FrontConfiguration configuration;

	private CartService cartService;

    /**
     */
    @Inject
    public DownloadResource(final FrontConfiguration cfg, CartService cartService) {
        this.configuration = cfg;
        this.cartService = cartService;
    }

    /**
     * returns shows all downloads for the order
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/{orderId}")
    @Produces({"text/html; charset=utf-8"})
    public OrderView getDownloadOverview(
    		@PathParam("orderId") String orderId) {

		try {

			Cart cart = cartService.getOrder(orderId);
			return new OrderView("downloadOverview.ftl", cart, configuration);

		} catch (HttpClientErrorException httpError) {
			if (httpError.getStatusCode().value() == 404) {
				return new OrderView("noSuchOrder.ftl", null, configuration);
			} else {
				throw httpError;
			}
		}
    }
    
}
