package org.apache.wiki.auth;

import javax.security.auth.spi.LoginModule;

public interface AuthenticationManagerOptions {

	/** The {@link LoginModule} to use for custom authentication. */
	String PROP_LOGIN_MODULE_CLASS = "loginModule.class";
	
	boolean isCookieAssertions();

	boolean isCookieAuthentication();

	boolean isLoginThrottling();

	/**
	 * Supply the name JAAS LoginModule class used for custom authentication.
	 * 
	 * @return name JAAS LoginModule class.
	 */
	String getLoginModuleClass();

}
