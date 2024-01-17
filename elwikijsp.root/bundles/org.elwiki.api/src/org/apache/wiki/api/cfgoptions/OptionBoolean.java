package org.apache.wiki.api.cfgoptions;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

public class OptionBoolean extends Option<Boolean> {

	public OptionBoolean(BundleContext bundleContext, String prefsId, String label, String info,
			WikiAjaxServlet jsonTracker) {
		super(bundleContext, prefsId, label, info, jsonTracker);
	}

	@Override
	public String getJsp() {
//@formatter:off
		String textJsp = String.format(
"""
  <div class="form-group form-inline ">
    <label class="control-label form-col-20"> %s </label>
    <label class="form-control form-switch xpref-appearance">
      <!--<fmt:message key="prefs.user.appearance.light"/>-->
      <input oninput="Wiki.jsonrpc('/%s',event.target.checked)"
          id="%s" type="checkbox" class="" %s>
      <!--<fmt:message key="prefs.user.appearance.dark"/>-->
    </label>
    %s
  </div>""",
  			getLabel(),
  			getJsonTracker().getServletMapping() + "/" + getId(),
  			getId(),
  			getInstanceValue() ? "checked='true'" : "",
  			getInfo());
//@formatter:on

		return textJsp;
	}

	@Override
	public Boolean getDefaultValue() {
		return getDefaultPrefs().getBoolean(getPrefsId(), false);
	}

	@Override
	public Boolean getInstanceValue() {
		return getInstancePrefs().getBoolean(getPrefsId(), getDefaultValue());
	}

	@Override
	void restoreDefault() {
		currentValue = getDefaultValue();
	}

	@Override
	void applyValue() throws BackingStoreException {
		IEclipsePreferences instancePrefs = getInstancePrefs();
		instancePrefs.putBoolean(getPrefsId(), currentValue);
		instancePrefs.flush();
	}

	@Override
	String getDefaultJsCode() {
		// $('idCookieAssertions').checked='%s';
		String result = String.format("$('%s').checked='%s';", //
				getId(), //
				getDefaultValue() ? "true" : "");

		return result;
	}

	@Override
	public void callBack(String string) {
		currentValue = Boolean.valueOf(string);
	}

}
