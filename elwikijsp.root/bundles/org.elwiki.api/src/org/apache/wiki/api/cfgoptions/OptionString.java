package org.apache.wiki.api.cfgoptions;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

public class OptionString extends Option<String> {

	public OptionString(BundleContext bundleContext, String prefsId, String label, String info,
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
       id="%s" type="text" class="form-control form-col-25" value="%s" />
    <label class="dropdown" style="display:inline-block; vertical-align:top;" >
      &#9432;
      <ul class="dropdown-menu" data-hover-parent=".dropdown">
        <li class="dropdown" style="width:700px;"> %s </li>
      </ul>
    </label>
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
	public String getDefaultValue() {
		return getDefaultPrefs().get(getPrefsId(), "");
	}

	@Override
	public String getInstanceValue() {
		return getInstancePrefs().get(getPrefsId(), getDefaultValue());
	}

	@Override
	void restoreDefault() {
		currentValue = getDefaultValue();
	}

	@Override
	void applyValue() throws BackingStoreException {
		IEclipsePreferences instancePrefs = getInstancePrefs();
		instancePrefs.put(getPrefsId(), currentValue);
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
		currentValue = string;
	}

}
