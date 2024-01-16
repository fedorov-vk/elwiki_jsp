package org.elwiki.services;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.api.component.WikiPrefs;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

//@formatter:off
@Component(
	name = "elwiki.GlobalPreferences",
	service = { GlobalPreferences.class, WikiManager.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class GlobalPreferencesImpl implements GlobalPreferences, WikiPrefs {

	@WikiServiceReference
	private Engine engine = null;

	GlobalPreferencesOptions options;

	// -- OSGi service handling ----------------------(start)--

	@Activate
	protected void startup(BundleContext bundleContext) {
		this.options = new GlobalPreferencesOptions(bundleContext);
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		options.initialize(engine);
	}

	// -- OSGi service handling ------------------------(end)--

	@Override
	public String getConfigurationEntry() {
		String jspItems = options.getConfigurationJspPage();
		return jspItems;
	}

	@Override
	public String getApplicationName() {
		return options.getApplicationName();
	}

	
}
