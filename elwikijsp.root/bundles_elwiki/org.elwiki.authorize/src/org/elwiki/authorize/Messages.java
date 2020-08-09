package org.elwiki.authorize;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS  {

	//:FVK: private static final String BUNDLE_NAME = "org.elwiki.authorize.messages"; //$NON-NLS-1$
	private static final String BUNDLE_NAME = Messages.class.getName().toLowerCase();

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String notification_createUserProfile_accept_content;
	public static String notification_createUserProfile_accept_subject;
	public static String notification_createUserProfile_reject;

	public static String security_error_noaccess_logged;
	public static String security_error_noaccess;

	public static String security_error_createprofilebeforelogin;
	public static String security_error_blankpassword;
	public static String security_error_passwordnomatch;

	public static String security_error_illegalfullname;
	public static String security_error_illegalloginname;

	public static String security_error_email_taken;

	public static String security_user_loginname;
	public static String security_user_fullname;
	public static String security_user_email;

}