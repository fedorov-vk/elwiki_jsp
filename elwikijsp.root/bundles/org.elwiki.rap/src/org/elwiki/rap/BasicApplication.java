package org.elwiki.rap;

import java.util.HashMap;
import java.util.Map;

import org.apache.wiki.api.core.Engine;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(name = "elwiki.PreferencesApplication", service = ApplicationConfiguration.class)
public class BasicApplication implements ApplicationConfiguration {

	@Reference
	private Engine engine;

	static BasicApplication instance;
	
    public void configure(Application application) {
    	instance = this;

        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Hello RAP");
        application.addEntryPoint("/preferences", BasicEntryPoint.class, properties);
    }

    public static Engine getEngine() {
    	return instance.engine;
    }
}
