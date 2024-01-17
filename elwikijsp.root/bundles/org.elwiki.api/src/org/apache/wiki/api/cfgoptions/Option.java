package org.apache.wiki.api.cfgoptions;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.eclipse.core.runtime.preferences.BundleDefaultsScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

public abstract class Option<T> implements ICallbackAction, IJspCode {

	private String prefsId;
	private String label;
	private String info;
	private WikiAjaxServlet jsonTracker;

	private final String id;

	T currentValue;

	private IEclipsePreferences defaultPrefs;
	private IEclipsePreferences instancePrefs;

	public Option(BundleContext bundleContext, String prefsId, String label, String info, WikiAjaxServlet jsonTracker) {
		this.prefsId = prefsId;
		this.label = label;
		this.info = info;
		this.jsonTracker = jsonTracker;

		String bundleName = bundleContext.getBundle().getSymbolicName();
		defaultPrefs = BundleDefaultsScope.INSTANCE.getNode(bundleName);
		instancePrefs = InstanceScope.INSTANCE.getNode(bundleName);

		id = "id" + String.valueOf(this.hashCode());
		currentValue = getInstanceValue();
	}

	abstract String getDefaultJsCode();

	public abstract T getDefaultValue();

	public abstract T getInstanceValue();

	abstract void restoreDefault();

	abstract void applyValue() throws BackingStoreException;

	boolean isDirty() {
		return currentValue != getInstanceValue();
	}

	@Override
	public String getId() {
		return id;
	}

	public String getPrefsId() {
		return prefsId;
	}

	public String getLabel() {
		return label;
	}

	public String getInfo() {
		String jspInfo = "";
		if(info != null && !info.isBlank()) {
			jspInfo = String.format(
"""
    <label class="dropdown" style="display:inline-block; vertical-align:top;" >
      &#9432;
      <ul class="dropdown-menu" data-hover-parent=".dropdown">
        <li class="dropdown" style="width:700px;"> %s </li>
      </ul>
    </label>
""", info); 
		}

		return jspInfo;
	}

	public WikiAjaxServlet getJsonTracker() {
		return jsonTracker;
	}

	protected IEclipsePreferences getDefaultPrefs() {
		return defaultPrefs;
	}

	protected IEclipsePreferences getInstancePrefs() {
		return instancePrefs;
	}

}
