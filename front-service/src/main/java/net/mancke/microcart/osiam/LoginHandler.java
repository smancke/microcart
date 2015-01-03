package net.mancke.microcart.osiam;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osiam.client.OsiamConnector;
import org.osiam.client.exception.ConnectionInitializationException;
import org.osiam.client.oauth.AccessToken;
import org.osiam.client.oauth.Scope;
import org.osiam.resources.scim.GroupRef;
import org.osiam.resources.scim.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginHandler {

    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    private static final String COOKIE_NAME = "okmsdc";

    private static final String ALGO = "AES";
    private OsiamLoginConfiguration config;

    private HttpServletRequest req;

    private HttpServletResponse res;

    private AuthCookie authCookie;

    public LoginHandler(OsiamLoginConfiguration config, HttpServletRequest req, HttpServletResponse res) {
        this.req = req;
        this.res = res;
        this.config = config;
        this.authCookie = readAuthCookie();
    }

    public boolean verifyLogin() {

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

    public AuthCookie getAuthCookie() {
        return authCookie;
    }

    public boolean doLogin() {
        OsiamConnector osiam = new OsiamConnector.Builder()
                .setAuthServerEndpoint(config.getAuthServerEndpoint())
                .setResourceServerEndpoint(config.getResourceServerEndpoint())
                .setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret())
                .build();

        try {
            AccessToken accessToken = osiam
                    .retrieveAccessToken(req.getParameter("username"), req.getParameter("password"), Scope.GET);
            User user = osiam.getCurrentUser(accessToken);
            List<GroupRef> groups = user.getGroups();

            writeCookie(user, groups);
        } catch (ConnectionInitializationException cie) {
            if (cie.getCause() != null) {
                throw new RuntimeException("error on login", cie.getCause());
            }
            return false;
        }
        return true;
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
            logger.info("invalid session cookie", e);
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
        cookie.setMaxAge(60 * 60 * 24 * 365);
        cookie.setHttpOnly(true);
        res.addCookie(cookie);
    }
}
