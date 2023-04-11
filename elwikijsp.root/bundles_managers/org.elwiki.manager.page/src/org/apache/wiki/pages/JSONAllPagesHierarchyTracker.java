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
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.pages0.PageManager.PageMotionType;
import org.elwiki_data.WikiPage;

/**
 * Подготавливает иерархический JSON список всех страниц. Каждый элемент списка:
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
public class JSONAllPagesHierarchyTracker implements WikiAjaxServlet {

	private static final Logger log = Logger.getLogger(JSONAllPagesHierarchyTracker.class);

	Engine engine;
	PageManager pageManager;

	/**
	 * Creates instance of JSONPagesHierarchyTracker.
	 */
	public JSONAllPagesHierarchyTracker(Engine engine) {
		super();
		this.engine = engine;
		this.pageManager = this.engine.getManager(PageManager.class);
	}

	@Override
	public String getServletMapping() {
		return DefaultPageManager.JSON_ALLPAGESHIERARCHY;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response, String actionName,
			List<String> params) throws ServletException, IOException {
		String requestType = "";
		if (params.size() > 0) {
			requestType = params.get(0);
		}

		switch (requestType) {
		case "getPages": {
			Collection<WikiPage> upperPages = null;
			try {
				upperPages = this.pageManager.getUpperPages();
			} catch (Exception ex) {
				log.error("Unable to retrieve list of upper pages", ex);
				return;
			}
			List<Map<String, Object>> list = new ArrayList<>();

			// adding a hierarchy of only regular wiki pages.
			for (WikiPage page : upperPages) {
				if (!page.isInternalPage()) {
					HashMap<String, Object> data = preparePage(page);
					list.add(data);
				}
			}

			String result = AjaxUtil.toJson(list);
			response.getWriter().print(result);

		}
			break;
		case "move": {
			if (params.size() >= 4) {
				PageMotionType motionType = switch (params.get(1)) {
				case "after" -> PageMotionType.AFTER;
				case "before" -> PageMotionType.BEFORE;
				case "bottom" -> PageMotionType.BOTTOM;
				default -> null;
				};
				if (motionType != null) {
					String target = params.get(2);
					String dragged = params.get(3);
					try {
						this.pageManager.movePage(motionType, target, dragged);
					} catch (ProviderException ex) {
						log.error("Unable to move page", ex);
					}
				}
			}
		}
			break;
		}
	}

	private HashMap<String, Object> preparePage(WikiPage page) {
		HashMap<String, Object> hm = new HashMap<>();
		hm.put("name", page.getName());
		hm.put("id", page.getId());

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
