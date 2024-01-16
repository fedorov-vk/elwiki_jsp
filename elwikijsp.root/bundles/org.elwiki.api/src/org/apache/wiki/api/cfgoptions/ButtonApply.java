package org.apache.wiki.api.cfgoptions;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.osgi.service.prefs.BackingStoreException;

public class ButtonApply implements IJspCode, ICallbackAction {

	private static final Logger log = Logger.getLogger(ButtonApply.class);

	private WikiAjaxServlet jsonTracker;
	private List<Option<?>> options;
	private String id;

	public ButtonApply(List<Option<?>> options, WikiAjaxServlet jsonTracker) {
		this.options = options;
		this.jsonTracker = jsonTracker;
		this.id = "idApply_" + String.valueOf(this.hashCode());
	}

	@Override
	public String getJsp() {
//@formatter:off
		String textApply = String.format("""
  &nbsp;
  <span class="dropdown" style="display:inline-block" >
    <button class="btn btn-info" name="apply"
      onclick="Wiki.jsonrpc('/%s',0)">
      <!-- <fmt:message key='prefs.cancel.submit'/> -->
      Apply
    </button>
  </span>""", jsonTracker.getServletMapping() + "/" + getId());
//@formatter:on

		return textApply;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void callBack(String string) {
		for (Option<?> option : options) {
			if (option.isDirty()) {
				try {
					option.applyValue();
				} catch (BackingStoreException e) {
					log.error("Failed assigning preference option.", e);
				}
			}
		}
	}

}
