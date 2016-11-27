package net.mancke.microcart.voucher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.mancke.microcart.ArticleService;
import net.mancke.microcart.CartService;
import net.mancke.microcart.FrontConfiguration;
import net.mancke.microcart.MailHelper;
import net.mancke.microcart.model.Position;
import net.mancke.microcart.model.Voucher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Path("/admin/voucher")
@Consumes("application/json")
@Produces("application/json")
public class VoucherService {
	
	private static final Logger logger = LoggerFactory.getLogger(VoucherService.class);

	private static final String VOUCHER_RESOURCE = "/shop/voucher";
	
	private FrontConfiguration configuration;

	public VoucherService(FrontConfiguration cfg) {
		this.configuration = cfg;
	}
	
	@POST
	@Path("/mail")
	public List<String> sendVouchersByMail(VoucherMailRequest voucherMailRequest) {
		List<String> resultStatus = new ArrayList<>();
		for (String to : voucherMailRequest.getTo()) {
			String result = to;
			try {
				Voucher voucher = create(voucherMailRequest.getVoucher());
				String text = voucherMailRequest.getTemplate().replace("{voucher}", voucher.getId());			
				String uri = configuration.getBackendURL() + "/mail/self/{to}/{subject}";
				MailHelper.postPlainText(uri, text, to, voucherMailRequest.getSubject());
				result += "ok";
			} catch (Exception e) {
				logger.error("error sending voucher email to "+ to, e);
				result += " error: "+ e.getMessage();
			}
			resultStatus.add(result);
		}
		return resultStatus;
	}

	@POST
	public Voucher create(VoucherRequest voucherRequest) {
		Voucher voucher = new Voucher();
		voucher.setId(UUID.randomUUID().toString().substring(0,8));
		voucher.setVoucherType(voucherRequest.getType());
		voucher.setVoucherAmount(voucherRequest.getAmount());
		voucher.setInitialAmount(voucherRequest.getAmount());
		voucher.setComment(voucherRequest.getComment());
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, voucherRequest.getExpiryDays());
		voucher.setExpiryDate(c.getTime());
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForLocation(configuration.getBackendURL() + VOUCHER_RESOURCE, voucher);
		
		return voucher;		
	}
	
	public boolean verify(String voucherId) {
		Voucher voucher = find(voucherId);
		return verify(voucher);
	}

	private boolean verify(Voucher voucher) {
		return voucher != null
				&& ! voucher.isUsed()
				&& voucher.getExpiryDate() != null
				&& voucher.getExpiryDate().after(new Date());
	}

	public Voucher find(String voucherId) {		
		RestTemplate restTemplate = new RestTemplate();
		try {
			return restTemplate.getForObject(configuration.getBackendURL() + VOUCHER_RESOURCE + "/{voucherId}", Voucher.class, voucherId);
		} catch (HttpClientErrorException ex) {
			return null;
		}
	}

	public void use(String voucherId, String usedBy) {
		Voucher voucher = find(voucherId);
		voucher.setUsed(true);
		voucher.setUsedBy(usedBy);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.put(configuration.getBackendURL() + VOUCHER_RESOURCE + "/" + voucherId, voucher);
	}

	public Position positionFromVoucher(String voucherId) {
		Position position = new Position();
		position.setType(Position.TYPE_VOUCHER);
    	position.setArticleId(voucherId);
    	
    	Voucher voucher = find(voucherId);
    	if (!verify(voucher))
    		return null;
    	
		position.setTitle("Gutscheincode "+ voucherId);
    	position.setQuantity(1);
    	position.setDiscountPercent(0);
    	position.setPricePerUnit(-1*voucher.getVoucherAmount());
		return position;

    	
/**
 		if (voucher == null)  {
 
			position.setTitle("Ungültiger Gutscheincode.");
		} else if (voucher.isUsed())  {
			position.setTitle("Gutscheincode bereits eingelöst.");
		} else if (voucher.getExpiryDate().before(new Date()))  {
			position.setTitle("Gutscheincode abgelaufen.");
		} else {
			position.setTitle("Gutscheincode "+ voucherId);
	    	position.setQuantity(1);
	    	position.setDiscountPercent(0);
	    	position.setPricePerUnit(-1*voucher.getVoucherAmount());
		}
		*/
	}
}
