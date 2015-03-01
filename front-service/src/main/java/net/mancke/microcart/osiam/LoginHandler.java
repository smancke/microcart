package net.mancke.microcart.osiam;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.osiam.client.OsiamConnector;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.exception.UnauthorizedException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.GroupRef;
import org.osiam.resources.scim.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The LoginHandler is able to login with osiam an manage a crypted cookie for 
 * authentication of the web session. It can be used directly of injected as JAX-RS BeanProperty.
 * 
 * @author smancke
 *
 */
public class LoginHandler {

    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    private static final String COOKIE_NAME = "okmsdc";

    private static final String ALGO = "AES";
    
    private OsiamLoginConfiguration config;

    @Context 
    private HttpServletRequest req;

    @Context
    private HttpServletResponse res;

    private AuthCookie authCookie;

    public LoginHandler() {    	
    }
    
    public LoginHandler(OsiamLoginConfiguration config, HttpServletRequest req, HttpServletResponse res) {
        this.req = req;
        this.res = res;
        this.config = config;
    }

    public boolean verifyLogin() {
    	if (authCookie == null) {
    		authCookie = readAuthCookie();
    	}
        if (authCookie != null
                && authCookie.getAgeMinutes() < config.getSessionLifetimeMinutes()) {

            // refresh if older than 1 minute
            if (authCookie.getAgeMinutes() > 1) {
                writeCookie(authCookie);
            }

            return true;
        }
        return false;
    }
    
    /**
     * Returs the osiam user object to the current session.
     * @return null, if no valid session is available.
     */
	public User getUser() {
		if (getAuthCookie() == null)
			return null;
		OsiamConnector osiam = osiam();
		AccessToken accessToken = osiam.retrieveAccessToken(Scope.GET);
		return osiam.getUser(getAuthCookie().getUserId(), accessToken);
	}

    public AuthCookie getAuthCookie() {
    	if (authCookie == null) {
    		authCookie = readAuthCookie();
    	}
        return authCookie;
    }

    public boolean doLogin() {
        OsiamConnector osiam = osiam();

        try {
            AccessToken accessToken = osiam
                    .retrieveAccessToken(req.getParameter("username"), req.getParameter("password"), Scope.GET);
            User user = osiam.getCurrentUser(accessToken);
            List<GroupRef> groups = user.getGroups();

            writeCookie(user, groups);
        } catch (UnauthorizedException uae) {
        	logger.info("loin failed (UnauthorizedException) for "+req.getParameter("username"));
        	return false;
        } catch (ConnectionInitializationException cie) {
        	if (cie.getMessage().contains("doesn't exist") || cie.getMessage().contains("Bad credentials") ) {
        		return false;
        	}
        	throw new RuntimeException("error on login for "+req.getParameter("username"), cie);
        }
        return true;
    }

	private OsiamConnector osiam() {
		OsiamConnector osiam = new OsiamConnector.Builder()
                .setAuthServerEndpoint(config.getAuthServerEndpoint())
                .setResourceServerEndpoint(config.getResourceServerEndpoint())
                .setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret())
                .build();
		return osiam;
	}

    public void doLogout() {
        clearCookie();
    }

    public String encrypt(String data) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key());
        byte[] encVal = c.doFinal(data.getBytes());
        return new String(Base64Utils.encode(encVal));
    }

    public String decrypt(String encryptedData) throws Exception {
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key());
        byte[] decordedValue = Base64Utils.decode(encryptedData.getBytes());
        byte[] decValue = c.doFinal(decordedValue);
        return new String(decValue);
    }

    private SecretKeySpec key() {
    	if (config.getSessionCookieSecret() == null || config.getSessionCookieSecret().isEmpty())
    		throw new RuntimeException("no session cookie secret is configured");
    	
        return new SecretKeySpec(config.getSessionCookieSecret().getBytes(), ALGO);
    }

    private AuthCookie readAuthCookie() {
        try {
            Cookie cookie = readCookie(COOKIE_NAME);
            if (cookie == null || cookie.getValue().isEmpty())
                return null;
            String decryptedValue = decrypt(cookie.getValue());
            AuthCookie authCookie = new ObjectMapper().readValue(decryptedValue, AuthCookie.class);
            return authCookie;
        } catch (Exception e) {
            logger.trace("invalid session cookie", e);
            return null;
        }
    }

    private Cookie readCookie(String cookieName) {
		for (Cookie cookie : req.getCookies()) {
			if (cookieName.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	private void clearCookie() {
		Cookie cookie = readCookie(COOKIE_NAME);
        if (cookie == null)
            return;
        cookie.setMaxAge(0);
        res.addCookie(cookie);
    }

    private void writeCookie(User user, List<GroupRef> groups) {
        AuthCookie authCookie = new AuthCookie();
        authCookie.setUserId(user.getId());
        authCookie.setLastSeen(System.currentTimeMillis());
        authCookie.setUserName(user.getUserName());
        authCookie.setDisplayName(user.getDisplayName());
        if (authCookie.getDisplayName() == null || authCookie.getDisplayName().isEmpty()) {
            if (user.getName() != null) {
                authCookie.setDisplayName(user.getName().getGivenName() + " " + user.getName().getFamilyName());
            } else if (user.getUserName() != null) {
                authCookie.setDisplayName(user.getUserName());
            }
        }
        authCookie.setGroups(new ArrayList<String>());
        for (GroupRef groupRef : groups) {
            authCookie.addGrpup(groupRef.getDisplay());
        }

        writeCookie(authCookie);
    }

    private void writeCookie(AuthCookie authCookie) {
        this.authCookie = authCookie;
        ObjectMapper mapper = new ObjectMapper();
        String cookieBytes;
        try {
            cookieBytes = mapper.writeValueAsString(authCookie);
            cookieBytes = encrypt(cookieBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Cookie cookie = new Cookie(COOKIE_NAME, cookieBytes);
        cookie.setPath("/");
        cookie.setMaxAge(60 * config.getSessionLifetimeMinutes());
        cookie.setHttpOnly(true);
        res.addCookie(cookie);
    }

	public OsiamLoginConfiguration getConfig() {
		return config;
	}

	public void setConfig(OsiamLoginConfiguration config) {
		this.config = config;
	}

	public HttpServletRequest getReq() {
		return req;
	}

	public void setReq(HttpServletRequest req) {
		this.req = req;
	}

	public HttpServletResponse getRes() {
		return res;
	}

	public void setRes(HttpServletResponse res) {
		this.res = res;
	}
}
