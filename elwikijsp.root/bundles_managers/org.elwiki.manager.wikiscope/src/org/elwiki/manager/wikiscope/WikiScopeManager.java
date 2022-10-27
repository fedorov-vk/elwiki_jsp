package org.elwiki.manager.wikiscope;

import org.apache.log4j.Logger;
import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.engine.Initializable;
import org.apache.wiki.api.exceptions.WikiException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiScope;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(name = "elwiki.WikiScopeManager", service = WikiScope.class, //
		factory = "elwiki.WikiScopeManager.factory")
public class WikiScopeManager implements WikiScope, Initializable {

	private static final Logger log = Logger.getLogger(WikiScopeManager.class);

	static final String JSON_WIKISCOPE = "wikiscope";

	private Engine m_engine;

	/**
	 * Creates instance of WikiScopeManager.
	 */
	public WikiScopeManager() {
		super();
	}

	// -- service handling ---------------------------(start)--

	/**
	 * This component activate routine. Does all the real initialization.
	 * 
	 * @param componentContext passed the Engine.
	 */
	@Activate
	protected void startup(ComponentContext componentContext) {
		try {
			Object engine = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
			if (engine instanceof Engine) {
				initialize((Engine) engine);
			}
		} catch (WikiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(Engine engine) throws WikiException {
		m_engine = engine;
		IPreferenceStore props = engine.getWikiPreferences();

		WikiAjaxDispatcher wikiAjaxDispatcher = engine.getManager(WikiAjaxDispatcher.class);
		wikiAjaxDispatcher.registerServlet(JSON_WIKISCOPE, new JSONWikiScopeTracker());
	}

	// -- service handling -----------------------------(end)--

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

}
