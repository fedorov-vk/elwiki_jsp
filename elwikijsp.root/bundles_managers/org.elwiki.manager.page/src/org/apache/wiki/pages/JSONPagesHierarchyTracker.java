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

/**
 * Подготавливает иерархический JSON список страниц. Каждый элемент списка:
 * 
 * <pre>
 * {
 *   name: имя_страницы,
 *   id: идентификатор_страницы
 *   children : [список подчиненных страницы]
 * }
 * </pre>
 * 
 * Специальные страницы - включены в отдельную ветку.
 * 
 * @author v.fedorov
 */
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

	private List<Map<String, Object>> listSpecialPages = new ArrayList<>();

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

		Map<String, Object> specialPages = new HashMap<>(Map.of("name", "Special pages", "id", "0"));
		listSpecialPages.clear();

		List<Map<String, Object>> list = new ArrayList<>();
		list.add(specialPages);
		// adding hierarchy of all pages.
		for (WikiPage page : upperPages) {
			HashMap<String, Object> data = preparePage(page);
			if (!page.isInternalPage()) {
				list.add(data);
			}
		}
		// here added list of special pages.
		if (listSpecialPages.size() > 0) {
			specialPages.put("children", listSpecialPages);
		}

		String result = AjaxUtil.toJson(list);
		response.getWriter().print(result);
	}

	private HashMap<String, Object> preparePage(WikiPage page) {
		HashMap<String, Object> hm = new HashMap<>();
		String pageId = page.getId();
		String pageName = page.getName();
		if (page.isInternalPage()) {
			hm.put("name", pageName);
			hm.put("id", pageId);
			listSpecialPages.add(hm);
		} else {
			//:FVK: "<a href=\"http://localhost:8088/cmd.view?pageId=" + page.getId() + "\">" + page.getName() + "</a>";
			String link = "<a href=\"" + engine.getURL(ContextEnum.PAGE_VIEW.getRequestContext(), pageId, null) + "\">"
					+ pageName + "</a>";
			hm.put("name", link);
			hm.put("id", pageId);
		}
		List<Map<String, Object>> children = new ArrayList<>();
		for (WikiPage childPage : page.getChildren()) {
			HashMap<String, Object> pageMap = preparePage(childPage);
			children.add(pageMap);
		}
		if (children.size() > 0) {
			hm.put("children", children);
		}

		return hm;
	}

}
