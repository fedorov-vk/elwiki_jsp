package org.elwiki.preferences.ui.internal;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;

public class WikiPreferenceRapApp implements ApplicationConfiguration {

	@Override
	public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "ElWiki Preferences");
        application.addEntryPoint("/configuration", WikiPreferenceEntryPoint.class, properties);
	}

}
