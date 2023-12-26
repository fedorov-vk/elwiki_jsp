package org.elwiki.manager.wikiscope;

import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.ISessionMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiScopeManager;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//@formatter:off
@Component(
	name = "elwiki.WikiScopeManager",
	service = { WikiScopeManager.class, WikiManager.class, EventHandler.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class WikiScopeManagerImpl implements WikiScopeManager, WikiManager, EventHandler {

	private static final Logger log = Logger.getLogger(WikiScopeManagerImpl.class);

	private static Gson gson = new Gson();

	/**
	 * Defines the scope for wiki sessions:
	 * <code>&lt;session, &lt;pageId, selectionStatus&gt;&gt;</code>. For the session, In the case of
	 * defining an scope area - a map of page ids is stored.
	 * <p>
	 * For example, map of pages <code>&lt;pageId, selectionStatus&gt;</code>:
	 * <code>{1000:1, 1002:2, 1003:2, 1004:2, 1013:2}</code><br/>
	 * The selection status is: =2 - page is selected; =1 - only some children of page are selected for
	 * scope.
	 */
	private Map<Session, Map<String, Integer>> scopePages = new WeakHashMap();

	/**
	 * Creates instance of WikiScopeManager.
	 */
	public WikiScopeManagerImpl() {
		super();
	}

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine m_engine;

	@WikiServiceReference
	WikiAjaxDispatcher wikiAjaxDispatcher;

	/**
	 * Initializes WikiScopeManager.
	 * 
	 * @throws WikiException
	 */
	@Override
	public void initialize() throws WikiException {
		wikiAjaxDispatcher.registerServlet(JSONWikiScopeTracker.JSON_WIKISCOPE, new JSONWikiScopeTracker(m_engine));
	}

	// -- OSGi service handling ------------------------(end)--

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void saveScope() {
		//
	}

	@Override
	public String[] getScopeList(HttpServletRequest request) {
		ISessionMonitor sessionMonitor = this.m_engine.getManager(ISessionMonitor.class);
		Session session = sessionMonitor.getWikiSession(request);
		org.osgi.service.useradmin.User user = session.getUser();
		if (user != null) {
			Dictionary<String, Object> props = user.getProperties();
			String userUid = (String) props.get(AccountRegistry.UID);
		}

		return new String[] { "Books", "EMF", "BIRT&amp;Я", "MooTools&x", "12345 6789012345\67890" }; //:FVK: workaround.
		//		return new String[] {}; //:FVK: workaround.
	}

	@Override
	public void ReinitScope(WikiContext wikiContext, String scopeArea, String scopeName, String scopes) {
		Session session = wikiContext.getWikiSession();

		if ("all".equals(scopeArea)) {
			scopePages.put(session, null);
			System.out.println("Set scope: all");
		} else {
			try {
				// get JSON scope definition by its name.
				Type typeScopesMap = new TypeToken<Map<String, String>>() {
				}.getType();
				Map<String, String> allScopes = gson.fromJson(scopes, typeScopesMap);
				String jsonScopeDefinition = allScopes.get(scopeName);
				// get pages of scope definition from JSON.
				if (jsonScopeDefinition != null) {
					Type typePagesMap = new TypeToken<Map<String, Integer>>() {
					}.getType();
					Map<String, Integer> pages = gson.fromJson(jsonScopeDefinition, typePagesMap);
					scopePages.put(session, pages);
				}
			} catch (Exception e) {
				log.error("Failed scope definition decoding: " + e.getMessage());
				scopePages.put(session, null);
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		/*String topic = event.getTopic();
		switch (topic) {
			break;
		}*/		
	}

}
