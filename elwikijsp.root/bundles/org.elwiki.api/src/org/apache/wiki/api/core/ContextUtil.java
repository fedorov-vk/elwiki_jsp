package org.apache.wiki.api.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.wiki.api.ui.EditorManager;

public class ContextUtil {

	/**
	 * This method can be used to find the WikiContext programmatically from a JSP PageContext. We
	 * check the request context. The wiki context, if it exists, is looked up using the key
	 * {@link #ATTR_CONTEXT}.
	 *
	 * @since 2.4
	 * @param pageContext the JSP page context
	 * @return Current WikiContext, or null, of no context exists.
	 */
	public static Context findContext(final PageContext pageContext) {
		final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		return (Context) request.getAttribute(Context.ATTR_CONTEXT);
	}

	// (::FVK: from EditorManager)
	/**
	 * Convenience function which examines the current context and attempts to figure out whether
	 * the edited text is in the HTTP request parameters or somewhere in the session.
	 *
	 * @param ctx the JSP page context
	 * @return the edited text, if present in the session page context or as a parameter
	 */
	public static String getEditedText(final PageContext ctx) {
		String usertext = ctx.getRequest().getParameter(EditorManager.REQ_EDITEDTEXT);
		if (usertext == null) {
			usertext = (String) ctx.findAttribute(EditorManager.ATTR_EDITEDTEXT);
		}

		return usertext;
	}

}
