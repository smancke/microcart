package net.mancke.microcart;

import java.nio.charset.Charset;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class MailHelper {

	public static void postPlainText(String uri, String body, Object... uriArgs) {
		RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", Charset.forName("UTF-8")));
        restTemplate.postForLocation(uri, new HttpEntity<String>(body, headers), uriArgs);
	}
}
