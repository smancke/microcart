package net.mancke.microcart.osiam;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OsiamLoginConfiguration {

	@NotEmpty
	@JsonProperty
	private String authServerEndpoint;

	@NotEmpty
	@JsonProperty
	private String resourceServerEndpoint;

	@NotEmpty
	@JsonProperty
	private String clientId;
	
	@NotEmpty
	@JsonProperty
	private String clientSecret;
	
	@NotEmpty
	@JsonProperty
	private String sessionCookieSecret;
	
	@NotEmpty
	@JsonProperty
	private int sessionLifetimeMinutes;

	public String getAuthServerEndpoint() {
		return authServerEndpoint;
	}

	public void setAuthServerEndpoint(String authServerEndpoint) {
		this.authServerEndpoint = authServerEndpoint;
	}

	public String getResourceServerEndpoint() {
		return resourceServerEndpoint;
	}

	public void setResourceServerEndpoint(String resourceServerEndpoint) {
		this.resourceServerEndpoint = resourceServerEndpoint;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getSessionCookieSecret() {
		return sessionCookieSecret;
	}

	public void setSessionCookieSecret(String sessionCookieSecret) {
		this.sessionCookieSecret = sessionCookieSecret;
	}

	public int getSessionLifetimeMinutes() {
		return sessionLifetimeMinutes;
	}

	public void setSessionLifetimeMinutes(int sessionLifetimeMinutes) {
		this.sessionLifetimeMinutes = sessionLifetimeMinutes;
	}	
}
