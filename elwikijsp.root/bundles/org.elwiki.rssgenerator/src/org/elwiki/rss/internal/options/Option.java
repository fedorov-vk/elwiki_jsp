package org.elwiki.rss.internal.options;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.osgi.service.prefs.BackingStoreException;

public abstract class Option<T> implements ICallbackAction, IJspCode {

	private String prefsId;
	private String label;
	private String info;
	private WikiAjaxServlet jsonTracker;
	private final String id;

	T currentValue;

	public Option(String prefsId, String label, String info, WikiAjaxServlet jsonTracker) {
		this.prefsId = prefsId;
		this.label = label;
		this.info = info;
		this.jsonTracker = jsonTracker;

		id = "id" + String.valueOf(this.hashCode());
		currentValue = getInstanceValue();
	}

	abstract String getDefaultJsCode();

	abstract T getDefaultValue();

	abstract T getInstanceValue();

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
		return info;
	}

	public WikiAjaxServlet getJsonTracker() {
		return jsonTracker;
	}

}
