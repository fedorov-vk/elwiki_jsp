package org.apache.wiki.api.core;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import org.apache.wiki.api.ui.EditorManager;

public class ContextUtil {

	/**
	 * Returns a string with context definitions separated by the '|' character.
	 *
	 * @param contexts context definitions given by strings. 
	 * @return a delimited string.
	 * @throws IllegalArgumentException if no arguments can be found.  
	 */
	public static String compose(String... contexts) throws IllegalArgumentException {
		if (contexts.length < 1) {
			throw new IllegalArgumentException("Missed context definitions.");
		}
		StringBuilder result = new StringBuilder(40);
		for (int i = 0; i < contexts.length; i++) {
			if (i != 0) {
				result.append("|");
			}
			result.append(contexts[i]);
		}
		return result.toString();
	}

	/**
	 * This method can be used to find the WikiContext programmatically from a JSP PageContext. We
	 * check the request context. The wiki context, if it exists, is looked up using the key
	 * {@link #ATTR_WIKI_CONTEXT}.
	 *
	 * @since 2.4
	 * @param pageContext the JSP page context
	 * @return Current WikiContext, or null, of no context exists.
	 */
	public static Context findContext(PageContext pageContext) {
		final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		return (Context) request.getAttribute(Context.ATTR_WIKI_CONTEXT);
	}

	public static Context findContext(HttpServletRequest request) {
		return (Context) request.getAttribute(Context.ATTR_WIKI_CONTEXT);
	}

	// (::FVK: from EditorManager)
	/**
	 * Convenience function which examines the current context and attempts to figure out whether
	 * the edited text is in the HTTP request parameters or somewhere in the session.
	 *
	 * @param ctx the JSP page context
	 * @return the edited text, if present in the session page context or as a parameter
	 */
	public static String getEditedText(PageContext ctx) {
		ServletRequest servletRequest = ctx.getRequest();
		String usertext = servletRequest.getParameter(EditorManager.REQ_EDITEDTEXT);
		if (usertext == null) {
			usertext = (String) ctx.findAttribute(EditorManager.ATTR_EDITEDTEXT);
		}

		return usertext;
	}

}
