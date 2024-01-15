package org.elwiki.rss.internal.options;

import org.apache.wiki.ajax.WikiAjaxServlet;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.elwiki.rss.internal.bundle.ActivatorRssGenerator;
import org.osgi.service.prefs.BackingStoreException;

public class BooleanOption extends Option<Boolean> {

	public BooleanOption(String prefsId, String label, String info, WikiAjaxServlet jsonTracker) {
		super(prefsId, label, info, jsonTracker);
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
  			getInstanceValue() ? "checked='true'" : "",
  			getInfo());
//@formatter:on

		return textJsp;
	}

	@Override
	Boolean getDefaultValue() {
		return ActivatorRssGenerator.getDefault().getDefaultPrefs().getBoolean(getPrefsId(), false);
	}

	@Override
	Boolean getInstanceValue() {
		return ActivatorRssGenerator.getDefault().getInstancePrefs().getBoolean(getPrefsId(), getDefaultValue());
	}

	@Override
	void restoreDefault() {
		currentValue = getDefaultValue();
	}

	@Override
	void applyValue() throws BackingStoreException {
		IEclipsePreferences instancePrefs = ActivatorRssGenerator.getDefault().getInstancePrefs();
		instancePrefs.putBoolean(getPrefsId(), currentValue);
		instancePrefs.flush();
	}

	@Override
	String getDefaultJsCode() {
		// $('idCookieAssertions').checked='%s';
		String result = String.format("$('%s').checked='%s'", //
				getId(), //
				getDefaultValue() ? "true" : "");

		return result;
	}

	@Override
	public void callBack(String string) {
		// TODO Auto-generated method stub
		System.out.println(string);
		currentValue = Boolean.valueOf(string);
	}

}
