package org.elwiki.manager.wikiscope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.ajax.AjaxUtil;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.exceptions.ProviderException;
import org.eclipse.emf.common.util.EList;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;

public class JSONWikiScopeTracker implements WikiAjaxServlet {

	private static final Logger log = Logger.getLogger(JSONWikiScopeTracker.class);

	ServicesRefs engine;

	private List<Map<String, Object>> listSpecialPages = new ArrayList<>();

	/**
	 * Creates instance of JSONWikiScopeTracker.
	 */
	public JSONWikiScopeTracker() {
		super();
		engine = ServicesRefs.Instance; //:FVK: workaround - hard coding for getting engine.
	}
	
	@Override
	public String getServletMapping() {
		return WikiScopeManager.JSON_WIKISCOPE;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response, String actionName,
			List<String> params) throws ServletException, IOException {
		Collection<WikiPage> upperPages = null;
		try {
			upperPages = ServicesRefs.getPageManager().getUpperPages();
		} catch (final ProviderException pe) {
			log.error("Unable to retrieve list of upper pages", pe);
			return;
		}

		listSpecialPages.clear();
		List<Map<String, Object>> list = new ArrayList<>();
		// adding hierarchy of all pages.
		for (WikiPage page : upperPages) {
			HashMap<String, Object> treeNode = preparePage(page);
			if (!page.isInternalPage()) {
				list.add(treeNode);
			}
		}
		// here added list of special pages.
		if (listSpecialPages.size() > 0) {
			Map<String, Object> specialPages = new HashMap<>(Map.of("id", "0-x-0", "text", "Special pages"));
			specialPages.put("children", listSpecialPages);
			list.add(specialPages);
		}

		String result = AjaxUtil.toJson(list);
		response.getWriter().print(result);
	}

	private HashMap<String, Object> preparePage(WikiPage page) {
		HashMap<String, Object> hm = new HashMap<>();
		String pageId = page.getId();
		String pageName = page.getName();
		if (page.isInternalPage()) {
			hm.put("id", pageId);
			hm.put("text", pageName);
			listSpecialPages.add(hm);
		} else {
			hm.put("id", pageId);
			hm.put("text", pageName);
		}
		List<Map<String, Object>> children = new ArrayList<>();
		List<WikiPage> childrenPages = page.getChildren();
		for (WikiPage childPage : childrenPages) {
			HashMap<String, Object> treeNode = preparePage(childPage);
			children.add(treeNode);
		}
		if (children.size() > 0) {
			hm.put("children", children);
		}

		return hm;
	}

}
