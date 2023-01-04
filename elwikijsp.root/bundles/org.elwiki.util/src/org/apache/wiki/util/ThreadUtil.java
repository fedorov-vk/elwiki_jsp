package org.apache.wiki.util;

import javax.servlet.http.HttpServletRequest;

public final class ThreadUtil {

	private static ThreadLocal<HttpServletRequest> thHttpServletRequest = new ThreadLocal<>();

	/** Private constructor to prevent direct instantiation. */
	private ThreadUtil() {
	}

	/**
	 * Returns HttpServletRequest for the current process.
	 *
	 * @return HttpServletRequest for the current process.
	 */
	public static HttpServletRequest getCurrentRequest() {
		return thHttpServletRequest.get();
	}

	/**
	 * Sets HttpServletRequest for the current process.
	 * 
	 * @param httpServletRequest the HttpServletRequest of servlet.
	 */
	public static void setCurrentRequest(HttpServletRequest httpServletRequest) {
		thHttpServletRequest.set(httpServletRequest);
	}

	public static void removeCurrentRequest() {
		thHttpServletRequest.remove();
	}

}
