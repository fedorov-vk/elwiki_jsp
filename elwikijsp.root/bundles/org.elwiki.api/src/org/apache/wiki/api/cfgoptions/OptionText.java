package org.apache.wiki.api.cfgoptions;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

public class OptionText extends Option<String> {

	public OptionText(BundleContext bundleContext, String prefsId, String label, String info,
			WikiAjaxServlet jsonTracker) {
		super(bundleContext, prefsId, label, info, jsonTracker);
	}

	@Override
	public String getJsp() {
//@formatter:off
		String textJsp = String.format(
"""
  <div class="form-group">
    <label class="control-label form-col-20" style="vertical-align:top;"> %s </label>
    <textarea oninput="Wiki.jsonrpc('/%s',event.target.value)"
        id="%s" style="width:50%%; display:inline-block;
                       min-width:200px; min-height:30px;
                       max-width:600px; max-height:150px;">
      %s
    </textarea>
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
