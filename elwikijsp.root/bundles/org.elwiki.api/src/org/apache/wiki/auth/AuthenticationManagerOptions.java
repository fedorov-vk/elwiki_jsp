package org.apache.wiki.auth;

import javax.security.auth.spi.LoginModule;

public interface AuthenticationManagerOptions {
	
	/** If this preferences.ini property is <code>true</code>, allow cookies to be used to assert identities. */
	String PROP_ALLOW_COOKIE_ASSERTIONS = "cookieAssertions";

    /** If this preferences.ini property is <code>true</code>, allow cookies to be used for authentication. */
    String PROP_ALLOW_COOKIE_AUTH = "cookieAuthentication";

    /** Whether logins should be throttled to limit brute-forcing attempts. Defaults to true. */
    String PROP_LOGIN_THROTTLING = "login.throttling";
    
    /** The {@link LoginModule} to use for custom authentication. */
    String PROP_LOGIN_MODULE = "loginModule.class";

	boolean isCookieAuthentication();

	boolean isCookieAssertions();
	
	boolean isLoginThrottling();

	/**
	 * Supply the name JAAS LoginModule class used for custom authentication.
	 * 
	 * @return name JAAS LoginModule class.
	 */
	String getLoginModuleClass();

}
