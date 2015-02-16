package net.mancke.microcart;

import io.dropwizard.Configuration;
import net.mancke.microcart.osiam.OsiamLoginConfiguration;
import net.mancke.microcart.paypal.PayPalConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FrontConfiguration extends Configuration {

	@JsonProperty
    private String backendURL = "http://127.0.0.1:5001";
	
	@JsonProperty
	private String precashPaymentInfo;
    
    @JsonProperty
    private OsiamLoginConfiguration osiamLogin;
    
    @JsonProperty
    private PayPalConfiguration payPal;
    
	public String getBackendURL() {
		return backendURL;
	}

	public void setBackendURL(String backendURL) {
		this.backendURL = backendURL;
	}

	public OsiamLoginConfiguration getOsiamLogin() {
		return osiamLogin;
	}

	public void setOsiamLogin(OsiamLoginConfiguration osiamLogin) {
		this.osiamLogin = osiamLogin;
	}

	public PayPalConfiguration getPayPal() {
		return payPal;
	}

	public void setPayPal(PayPalConfiguration payPal) {
		this.payPal = payPal;
	}

	public String getPrecashPaymentInfo() {
		return precashPaymentInfo;
	}

	public void setPrecashPaymentInfo(String precashPaymentInfo) {
		this.precashPaymentInfo = precashPaymentInfo;
	}

}
