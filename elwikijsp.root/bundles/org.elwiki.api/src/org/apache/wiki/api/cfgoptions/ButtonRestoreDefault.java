package org.apache.wiki.api.cfgoptions;

import java.util.List;

import org.apache.wiki.ajax.WikiAjaxServlet;

public class ButtonRestoreDefault implements IJspCode, ICallbackAction {

	private WikiAjaxServlet jsonTracker;
	private List<Option<?>> options;
	private String id;

	public ButtonRestoreDefault(List<Option<?>> options, WikiAjaxServlet jsonTracker) {
		this.options = options;
		this.jsonTracker = jsonTracker;
		this.id = "idRestore_" + String.valueOf(this.hashCode());
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
			String text = option.getDefaultJsCode();
			text = text.replaceAll("\n", "\\\\n");
			text = text.replaceAll("'", "\\'");
			textRestoreDefault += text + "\n";
		}

		textRestoreDefault += """
      })">
      <!-- <fmt:message key='prefs.save.prefs.submit'/> -->
      Restore Defaults
    </button>
  </span>""";
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
