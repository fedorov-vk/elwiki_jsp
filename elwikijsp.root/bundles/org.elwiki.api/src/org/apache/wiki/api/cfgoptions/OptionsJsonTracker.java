package org.apache.wiki.api.cfgoptions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.ajax.WikiAjaxServlet;

public class OptionsJsonTracker implements WikiAjaxServlet {

	private final String servletMapping;
	private final List<ICallbackAction> actions;

	public OptionsJsonTracker(String id, List<ICallbackAction> actions) {
		servletMapping = id + "_" + this.hashCode();
		this.actions = actions;
	}

	@Override
	public String getServletMapping() {
		return servletMapping;
	}

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response, String actionName,
			List<String> params) throws ServletException, IOException {
		String parameter = String.join(", ", params); // :FVK: workaround, due to
														// WikiAjaxDispatcherServlet.performAction split by ','
		for (ICallbackAction actions : actions) {
			if (actionName.equals(actions.getId())) {
				actions.callBack(parameter);
				return;
			}
		}
	}

}
