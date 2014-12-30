package net.mancke.microcart;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TrackingIdFilter implements Filter {

	public static final String TRACKING_COOKIE_KEY = "trck";
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		String trackingId = readCookie((HttpServletRequest) req);
		if (trackingId == null) {
			trackingId = UUID.randomUUID().toString();
			writeCookie((HttpServletResponse)res, trackingId);
		}

		chain.doFilter(req, res);
	}

	private void writeCookie(HttpServletResponse res, String trackingId) {
		Cookie cookie = new Cookie(TRACKING_COOKIE_KEY, trackingId);
		cookie.setMaxAge(Integer.MAX_VALUE);
		cookie.setPath("/");
		res.addCookie(cookie);
	}

	private String readCookie(HttpServletRequest req) {
		Cookie[] cookies = ((HttpServletRequest) req).getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(TRACKING_COOKIE_KEY)) {
					return cookies[i].getValue();
				}
			}
		}
		return null;
	}
	
	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
