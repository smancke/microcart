package net.mancke.microcart;

import io.dropwizard.Configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class FrontConfiguration extends Configuration {

    @NotEmpty
    @JsonProperty
    private String message;

    @JsonProperty
    private String backendURL = "http://127.0.0.1:5001";
    
	public String getBackendURL() {
		return backendURL;
	}

	public void setBackendURL(String backendURL) {
		this.backendURL = backendURL;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
