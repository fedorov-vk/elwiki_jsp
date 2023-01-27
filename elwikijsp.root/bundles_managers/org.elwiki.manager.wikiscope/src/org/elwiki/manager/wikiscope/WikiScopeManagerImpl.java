package org.elwiki.manager.wikiscope;

import java.lang.reflect.Type;
import java.util.Dictionary;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.engine.Initializable;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.auth.ISessionMonitor;
import org.apache.wiki.auth.user0.UserDatabase;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiScopeManager;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component(name = "elwiki.WikiScopeManager", service = WikiScopeManager.class, //
		factory = "elwiki.WikiScopeManager.factory")
public class WikiScopeManagerImpl implements WikiScopeManager, Initializable {

	private static final Logger log = Logger.getLogger(WikiScopeManagerImpl.class);

	private static Gson gson = new Gson();

	private Engine m_engine;	

	/**
	 * Defines the scope for wiki sessions:
	 * <code>&lt;session, &lt;pageId, selectionStatus&gt;&gt;</code>. For the session, In the case
	 * of defining an scope area - a map of page ids is stored.
	 * <p>
	 * For example, map of pages <code>&lt;pageId, selectionStatus&gt;</code>:
	 * <code>{1000:1, 1002:2, 1003:2, 1004:2, 1013:2}</code><br/>
	 * The selection status is: =2 - page is selected; =1 - only some children of page are selected for scope.
	 */
	private Map<Session, Map<String, Integer>> scopePages = new WeakHashMap();

	/**
	 * Creates instance of WikiScopeManager.
	 */
	public WikiScopeManagerImpl() {
		super();
	}

	// -- service handling ---------------------------(start)--

	/**
	 * This component activate routine. Does all the real initialization.
	 * 
	 * @param componentContext passed the Engine.
	 * @throws WikiException
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		Object obj = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
		if (obj instanceof Engine engine) {
			initialize(engine);
		}
	}

	@Override
	public void initialize(Engine engine) throws WikiException {
		m_engine = engine;
		IPreferenceStore props = engine.getWikiPreferences();

		WikiAjaxDispatcher wikiAjaxDispatcher = engine.getManager(WikiAjaxDispatcher.class);
		wikiAjaxDispatcher.registerServlet(JSONWikiScopeTracker.JSON_WIKISCOPE, new JSONWikiScopeTracker());
	}

	// -- service handling -----------------------------(end)--

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void saveScope() {

	}

	@Override
	public String[] getScopeList(HttpServletRequest request) {
		ISessionMonitor sessionMonitor = this.m_engine.getManager(ISessionMonitor.class);
		Session session = sessionMonitor.getWikiSession(request);
		org.osgi.service.useradmin.User user = session.getUser();
		if (user != null) {
			Dictionary<String, Object> props = user.getProperties();
			String userUid = (String) props.get(UserDatabase.UID);
		}

		return new String[] { "Books", "EMF", "BIRT&amp;Я", "MooTools&x", "12345 6789012345\67890" }; //:FVK: workaround.
		//		return new String[] {}; //:FVK: workaround.
	}

	@Override
	public void ReinitScope(Context wikiContext, String scopeArea, String scopeName, String scopes) {
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
}
