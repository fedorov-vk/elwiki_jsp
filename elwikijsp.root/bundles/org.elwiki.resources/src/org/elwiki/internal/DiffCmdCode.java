package org.elwiki.internal;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.apache.log4j.Logger;
import org.apache.wiki.WatchDog;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.preferences.Preferences;
import org.apache.wiki.tags.BreadcrumbsTag;
import org.apache.wiki.tags.BreadcrumbsTag.FixedQueue;
import org.apache.wiki.tags.InsertDiffTag;
import org.apache.wiki.util.HttpUtil;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;

public class DiffCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(DiffCmdCode.class);

	WatchDog w;

	protected DiffCmdCode() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		Context wikiContext = ContextUtil.findContext(httpRequest);

		if (!ServicesRefs.getAuthorizationManager().hasAccess(wikiContext, response))
			return;
		if (wikiContext.getCommand().getTarget() == null) {
			response.sendRedirect(wikiContext.getURL(wikiContext.getRequestContext(), wikiContext.getName()));
			return;
		}
		String pagereq = wikiContext.getName();

		/*:FVK:
		w = WatchDog.getCurrentWatchDog( ServicesRefs.Instance );
		w.enterState("Generating INFO response",60);
		*/

		// Notused ?
		// String pageurl = wiki.encodeName( pagereq );

		// If "r1" is null, then assume current version (= -1)
		// If "r2" is null, then assume the previous version (=current version-1)

		// FIXME: There is a set of unnecessary conversions here: InsertDiffTag
		// does the String->int conversion anyway.

		WikiPage wikipage = wikiContext.getPage();

		String srev1 = httpRequest.getParameter("r1");
		String srev2 = httpRequest.getParameter("r2");

		int ver1 = -1, ver2 = -1;

		if (srev1 != null) {
			ver1 = Integer.parseInt(srev1);
		}

		if (srev2 != null) {
			ver2 = Integer.parseInt(srev2);
		} else {
			int lastver = wikipage.getVersion();
			if (lastver > 1) {
				ver2 = lastver - 1;
			}
		}

		// :FVK: эти атрибуты - установлены скорее всего не верно, так как надо их задавать в JSP PageContext.
		// см. ниже.
		httpRequest.setAttribute(InsertDiffTag.ATTR_OLDVERSION, Integer.valueOf(ver1));
		httpRequest.setAttribute(InsertDiffTag.ATTR_NEWVERSION, Integer.valueOf(ver2));
		/*
		pageContext.setAttribute( InsertDiffTag.ATTR_OLDVERSION, Integer.valueOf(ver1), PageContext.REQUEST_SCOPE );
		pageContext.setAttribute( InsertDiffTag.ATTR_NEWVERSION, Integer.valueOf(ver2), PageContext.REQUEST_SCOPE );
		*/

//		log.debug("Request for page diff for '" + pagereq + "' from " + HttpUtil.getRemoteAddress(httpRequest + " by "
//				+ httpRequest.getRemoteUser() + ".  R1=" + ver1 + ", R2=" + ver2);
	}

	public void applyEpilogue() {
		/*:FVK:
		w.exitState();
		*/
	}

}
