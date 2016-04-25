package net.mancke.microcart;

import com.codahale.metrics.annotation.Timed;
import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.OrderData;
import net.mancke.microcart.model.Position;
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
import javax.ws.rs.core.StreamingOutput;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    /**
     * returns download one article
     * @return
     * @throws URISyntaxException
     */
    @Timed
    @GET
    @Path("/{orderId}/{articleId}")
    //@Produces({"text/html; charset=utf-8"})
    public Response getDownload(
            @PathParam("orderId") String orderId,
            @PathParam("articleId") String articleId) {

        try {
            try {

                Cart cart = cartService.getOrder(orderId);
                Optional<Position> position = cart.getPositions().stream().filter(p -> p.getArticleId().equals(articleId)).findFirst();

                if (!cart.isAllowDownload() || !position.isPresent()) {
                    return Response.seeOther(new URI("/shop/download/" + orderId)).build();
                }
                java.nio.file.Path path = Paths.get(configuration.getDownloadFileDirectory(), position.get().getDownloadLink());
                String mimeType = Files.probeContentType(path);

                StreamingOutput stream = new StreamingOutput() {
                    @Override
                    public void write(OutputStream output) throws IOException {
                        try {
                            Files.copy(path, output);

                            Cart cart = cartService.getOrder(orderId);
                            cart.getPostProcessing().setDownloadCounter(cart.getPostProcessing().getDownloadCounter() + 1);
                            cartService.saveOrderToBackend(cart);
                        } catch (org.eclipse.jetty.io.EofException eof) {
                            // browser canceled
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };

                return Response.ok(stream, mimeType)
                        .header("Content-Disposition", "attachment; filename="+ path.getFileName().toString())
                        .build();

            } catch (HttpClientErrorException httpError) {
                if (httpError.getStatusCode().value() == 404) {
                    return Response.seeOther(new URI("/shop/download/" + orderId)).build();
                } else {
                    throw httpError;
                }
            } catch (IOException e) {
                throw new RuntimeException("error reading file for orderId="+orderId +" articleId="+articleId, e);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
