package net.mancke.microcart.paypal;

import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PayPalConfiguration {

	@NotEmpty
	@JsonProperty
	private String apiUrl;
	
	@NotEmpty
	@JsonProperty
	private String redirectUrl;
	
	@NotEmpty
	@JsonProperty
	private String user;
	
	@NotEmpty
	@JsonProperty
	private String pwd;
	
	@NotEmpty
	@JsonProperty
	private String signature;

	@NotEmpty
	@JsonProperty
	private String cancelBaseUrl;

	@NotEmpty
	@JsonProperty
	private String returnBaseUrl;
	
	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redurectUrl) {
		this.redirectUrl = redurectUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getCancelBaseUrl() {
		return cancelBaseUrl;
	}

	public void setCancelBaseUrl(String cancelBaseUrl) {
		this.cancelBaseUrl = cancelBaseUrl;
	}

	public String getReturnBaseUrl() {
		return returnBaseUrl;
	}

	public void setReturnBaseUrl(String successBaseUrl) {
		this.returnBaseUrl = successBaseUrl;
	}
	
}
