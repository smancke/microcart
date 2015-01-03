package net.mancke.microcart;

import io.dropwizard.Configuration;
import net.mancke.microcart.osiam.OsiamLoginConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FrontConfiguration extends Configuration {

	@JsonProperty
    private String backendURL = "http://127.0.0.1:5001";
    
    @JsonProperty
    private OsiamLoginConfiguration osiamLogin;
    
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

}
