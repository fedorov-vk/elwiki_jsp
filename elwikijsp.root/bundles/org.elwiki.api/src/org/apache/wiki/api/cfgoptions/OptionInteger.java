package org.apache.wiki.api.cfgoptions;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

public class OptionInteger extends Option<Integer> {

	public OptionInteger(BundleContext bundleContext, String prefsId, String label, String info,
			WikiAjaxServlet jsonTracker) {
		super(bundleContext, prefsId, label, info, jsonTracker);
	}

	@Override
	public String getJsp() {
//@formatter:off
		String textJsp = String.format(
"""
  <div class="form-group">
    <label class="control-label form-col-20"> %s </label>
    <input oninput="Wiki.jsonrpc('/%s',event.target.value)"
       id="%s" type="number" class="form-control form-col-20" value="%s" />
    %s
  </div>""",
  			getLabel(),
  			getJsonTracker().getServletMapping() + "/" + getId(),
  			getId(),
  			getInstanceValue(),
  			getInfo());
//@formatter:on

		return textJsp;
	}

	@Override
	public Integer getDefaultValue() {
		return getDefaultPrefs().getInt(getPrefsId(), 3600);
	}

	@Override
	public Integer getInstanceValue() {
		return getInstancePrefs().getInt(getPrefsId(), getDefaultValue());
	}

	@Override
	void restoreDefault() {
		currentValue = getDefaultValue();
	}

	@Override
	void applyValue() throws BackingStoreException {
		IEclipsePreferences instancePrefs = getInstancePrefs();
		instancePrefs.putInt(getPrefsId(), currentValue);
		instancePrefs.flush();
	}

	@Override
	String getDefaultJsCode() {
		// $('idCookieAssertions').value='%s';
		String result = String.format("$('%s').value='%s';", //
				getId(), getDefaultValue());

		return result;
	}

	@Override
	public void callBack(String string) {
		currentValue = Integer.valueOf(string);
	}

}
