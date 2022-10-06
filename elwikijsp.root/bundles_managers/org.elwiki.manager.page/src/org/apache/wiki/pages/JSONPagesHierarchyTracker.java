package org.apache.wiki.pages;

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
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.exceptions.ProviderException;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.WikiPage;

public class JSONPagesHierarchyTracker implements WikiAjaxServlet {

	private static final Logger log = Logger.getLogger(JSONPagesHierarchyTracker.class);

	ServicesRefs engine;

	public JSONPagesHierarchyTracker() {
		super();
		engine = ServicesRefs.Instance; //:FVK: workaround - hard coding for getting engine.
	}

	@Override
	public String getServletMapping() {
		return DefaultPageManager.JSON_PAGESHIERARCHY;
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

		List<Map<String, Object>> list = new ArrayList<>();
		for (WikiPage page : upperPages) {
			list.add(preparePage(page));
		}
		String result = AjaxUtil.toJson(list);
		response.getWriter().print(result);
	}

	private HashMap<String, Object> preparePage(WikiPage page) {
		HashMap<String, Object> hm = new HashMap<>();
		String pageId = page.getId();
		//:FVK: "<a href=\"http://localhost:8088/cmd.view?pageId=" + page.getId() + "\">" + page.getName() + "</a>";
		String link = "<a href=\"" + engine.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), pageId, null) + "\">"
				+ page.getName() + "</a>";
		hm.put("name", link);
		hm.put("id", pageId);
		List<Map<String, Object>> children = new ArrayList<>();
		for (WikiPage childPage : page.getChildren()) {
			HashMap<String, Object> pageMap = preparePage(childPage);
			children.add(pageMap);
		}
		return hm;
	}

}
