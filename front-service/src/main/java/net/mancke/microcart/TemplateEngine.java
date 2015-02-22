package net.mancke.microcart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.mancke.microcart.model.Cart;
import net.mancke.microcart.model.Order;

public class TemplateEngine {
	
	private FrontConfiguration configuration;

	public TemplateEngine(FrontConfiguration configuration) {
		this.configuration = configuration;
	}

	public String renderTemplate(String templateName, Cart cart, String paymentInfo ) {
        try {
			Configuration cfg = new Configuration(Configuration.VERSION_2_3_21);
			 cfg.setClassForTemplateLoading(TemplateEngine.class, "");
	        Template template = cfg.getTemplate(templateName);
	         
	        Map<String, Object> data = new HashMap<String, Object>();
	        data.put("cart", cart);
	        data.put("paymentInfo", paymentInfo);
	                     
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Writer out = new OutputStreamWriter(bos);
			template.process(data, out);
	        return bos.toString();
		} catch (TemplateException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String renderPrecashPaymentInfo(Cart cart, String orderId) {
		if (Cart.PRECASH.equals(cart.getOrderData().getPaymentType())) {			
			return configuration.getPrecashPaymentInfo()
				.replace("{amount}", String.format(Locale.GERMAN, "%.2f", cart.getTotalPrice()))
    		    .replace("{orderId}", orderId.substring(0, 5));
		}
		return "";		
	}
}
