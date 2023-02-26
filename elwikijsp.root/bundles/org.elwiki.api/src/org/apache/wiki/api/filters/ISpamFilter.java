package org.apache.wiki.api.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.auth.UserProfile;
import org.elwiki_data.WikiPage;

public interface ISpamFilter {

	/**
	 *  Checks whether the UserProfile matches certain checks.
	 *
	 *  @param profile The profile to check
	 *  @param context The WikiContext
	 *  @return False, if this userprofile is suspect and should not be allowed to be added.
	 *  @since 2.6.1
	 */
	boolean isValidUserProfile(final WikiContext context, final UserProfile profile);

	/**
	 *  Returns the name of the hash field to be used in this request. The value is unique per session, and once 
	 *  the session has expired, you cannot edit anymore.
	 *
	 *  @param request The page request
	 *  @return The name to be used in the hash field
	 *  @since  2.6
	 */
	String getHashFieldName(final HttpServletRequest request);

	/**
	 *  This method is used to calculate an unique code when submitting the page to detect edit conflicts.  
	 *  It currently incorporates the last-modified date of the page, and the IP address of the submitter.
	 *
	 *  @param page The WikiPage under edit
	 *  @param request The HTTP Request
	 *  @since 2.6
	 *  @return A hash value for this page and session
	 */
	String getSpamHash(final WikiPage page, final HttpServletRequest request);

	/**
	 * This helper method adds all the input fields to your editor that the SpamFilter requires
	 * to check for spam.  This <i>must</i> be in your editor form if you intend to use the SpamFilter.
	 *  
	 * @param pageContext The PageContext
	 * @return A HTML string which contains input fields for the SpamFilter.
	 */
	String insertInputFields(final PageContext pageContext);

	/**
	 * Returns a static string which can be used to detect spambots which just wildly fill in all the fields.
	 *
	 * @return A string
	 */
	String getBotFieldName();

}
