package org.elwiki.preferences;

import java.util.HashMap;
import java.util.Map;

import org.apache.wiki.api.core.Engine;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(name = "elwiki.PreferencesApplication", service = ApplicationConfiguration.class)
public class PreferencesApplication implements ApplicationConfiguration {

	@Reference
	private Engine engine;

	static PreferencesApplication instance;
	
    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_OVERFLOW, "scroll");
        properties.put(WebClient.BODY_HTML, "<h1>Hi, Victor!</h1>"); //:FVK: workaround stub.
        //TODO: properties.put(WebClient.THEME_ID, "foo");
        application.addEntryPoint("/preferences", PreferencesEntryPoint.class, properties);
    }

    //:FVK: workaround.
    public static Engine getEngine() {
    	return instance.engine;
    }
    
}
