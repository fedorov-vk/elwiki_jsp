package org.apache.wiki.api.cfgoptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.osgi.framework.BundleContext;

public abstract class Options {

	protected final List<Option<?>> options = new ArrayList<>();
	protected final List<ICallbackAction> actions = new ArrayList<>();

	protected WikiAjaxServlet jsonTracker;
	
	abstract public void initialize(BundleContext bundleContext, Engine engine);

	abstract public String getConfigurationJspPage();

}
