package org.elwiki.rss.internal.options;

import java.util.List;

import org.apache.wiki.ajax.WikiAjaxServlet;

public class ButtonRestoreDefault implements IJspCode, ICallbackAction {

	private WikiAjaxServlet jsonTracker;
	private List<Option<?>> options;
	private String id;

	public ButtonRestoreDefault(List<Option<?>> options, WikiAjaxServlet jsonTracker) {
		this.options = options;
		this.jsonTracker = jsonTracker;
		this.id = "id" + String.valueOf(this.hashCode());
	}

	@Override
	public String getJsp() {
//@formatter:off
		String textRestoreDefault = String.format("""
  <span class="dropdown" style="display:inline-block" >
    <button class="btn btn-info" name="restoreDefault"
      onclick="Wiki.jsonrpc('/%s',0,function(result){
""", jsonTracker.getServletMapping() + "/" + getId());

		for(Option<?> option:options) {
			textRestoreDefault += option.getDefaultJsCode();
		}

		textRestoreDefault += """
      })">
      <!-- <fmt:message key='prefs.save.prefs.submit'/> -->
      Restore Defaults
    </button>
  </span>""";
/*
  , jsonTracker.getServletMapping() + "/" + getId(),
  			getDefaultPrefs().getBoolean(PROP_ALLOW_COOKIE_ASSERTIONS, true)? "true" : "",
  			getDefaultPrefs().getBoolean(PROP_ALLOW_COOKIE_AUTH, false)? "true" : "",
  			getDefaultPrefs().getBoolean(PROP_LOGIN_THROTTLING, true)? "true" : ""
		);
*/
//@formatter:on

		return textRestoreDefault;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void callBack(String string) {
		for (Option<?> option : options) {
			option.restoreDefault();
		}		
	}

}
