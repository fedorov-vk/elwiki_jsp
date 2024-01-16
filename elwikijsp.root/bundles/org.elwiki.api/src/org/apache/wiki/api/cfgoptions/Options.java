package org.apache.wiki.api.cfgoptions;

import org.apache.wiki.api.core.Engine;
import org.osgi.framework.BundleContext;

public abstract class Options {

	abstract public void initialize(BundleContext bundleContext, Engine engine);

	abstract public String getConfigurationJspPage();

}
