package org.elwiki.authorize.internal.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wiki.ajax.WikiAjaxDispatcher;
import org.apache.wiki.ajax.WikiAjaxServlet;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.auth.AuthenticationManagerOptions;
import org.eclipse.core.runtime.preferences.BundleDefaultsScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.osgi.service.prefs.BackingStoreException;

public class AuthenticationManagerOptionsImpl implements AuthenticationManagerOptions {

	private class JSONtracker implements WikiAjaxServlet {
		private static final String SERVLET_AM_CFG = "CfgAuthenticationManager";

		@Override
		public String getServletMapping() {
			return SERVLET_AM_CFG;
		}

		@Override
		public void service(HttpServletRequest request, HttpServletResponse response, String actionName,
				List<String> params) throws ServletException, IOException {
			switch (actionName) {
			case ACT_COOKIE_ASSERTIONS:
				actCookieAssertions(params);
				break;
			case ACT_COOKIE_AUTHENTICATION:
				actCookieAuthentication(params);
				break;
			case ACT_LOGIN_THROTTLING:
				actLoginThrottling(params);
				break;
			case ACT_RESTORE_DEFAULT:
				actRestoreDefault();
				break;
			case ACT_APPLY:
				try {
					actApply();
				} catch (BackingStoreException e) {
					throw new IOException(e);
				}
				break;
			}
		}
	}

	class Option<T> {
		private T oldValue;
		private T newValue;

		public Option(T oldValue) {
			this.oldValue = oldValue;
			this.newValue = oldValue;
		}

		boolean isDirty() {
			return oldValue != newValue;
		}

		void setValue(T newValue) {
			this.newValue = newValue;
		}

		public T getNewValue() {
			return this.newValue;
		}
	}

	private static final String ACT_COOKIE_ASSERTIONS = "cookieAssertions";
	private static final String ACT_COOKIE_AUTHENTICATION = "cookieAuthentication";
	private static final String ACT_LOGIN_THROTTLING = "loginThrottling";
	private static final String ACT_RESTORE_DEFAULT = "restoreDefault";
	private static final String ACT_APPLY = "apply";

	private final JSONtracker jsonTracker = new JSONtracker();

	Map<String, Option<?>> values = new HashMap<>();

	protected void actCookieAssertions(List<String> params) {
		@SuppressWarnings("unchecked")
		Option<Boolean> option = (Option<Boolean>) values.get(ACT_COOKIE_ASSERTIONS);
		option.setValue(Boolean.valueOf(params.get(0)));
	}

	protected void actCookieAuthentication(List<String> params) {
		@SuppressWarnings("unchecked")
		Option<Boolean> option = (Option<Boolean>) values.get(ACT_COOKIE_AUTHENTICATION);
		option.setValue(Boolean.valueOf(params.get(0)));
	}

	protected void actLoginThrottling(List<String> params) {
		@SuppressWarnings("unchecked")
		Option<Boolean> option = (Option<Boolean>) values.get(ACT_LOGIN_THROTTLING);
		option.setValue(Boolean.valueOf(params.get(0)));
	}

	@SuppressWarnings("unchecked")
	protected void actRestoreDefault() {
		IEclipsePreferences defaultPrefs = getDefaultPrefs();
		boolean defValue;
		Option<Boolean> option;

		option = (Option<Boolean>) values.get(ACT_COOKIE_ASSERTIONS);
		defValue = defaultPrefs.getBoolean(PROP_ALLOW_COOKIE_ASSERTIONS, true);
		option.setValue(defValue);

		option = (Option<Boolean>) values.get(ACT_COOKIE_AUTHENTICATION);
		defValue = defaultPrefs.getBoolean(PROP_ALLOW_COOKIE_AUTH, false);
		option.setValue(defValue);

		option = (Option<Boolean>) values.get(ACT_LOGIN_THROTTLING);
		defValue = defaultPrefs.getBoolean(PROP_LOGIN_THROTTLING, true);
		option.setValue(defValue);
	}

	@SuppressWarnings("unchecked")
	public void actApply() throws BackingStoreException {
		Option<Boolean> option;
		boolean isDirty = false;
		IEclipsePreferences instancePrefs = getInstancePrefs();

		option = (Option<Boolean>) values.get(ACT_COOKIE_ASSERTIONS);
		if (option.isDirty()) {
			instancePrefs.putBoolean(PROP_ALLOW_COOKIE_ASSERTIONS, option.getNewValue());
			isDirty = true;
		}

		option = (Option<Boolean>) values.get(ACT_COOKIE_AUTHENTICATION);
		if (option.isDirty()) {
			instancePrefs.putBoolean(PROP_ALLOW_COOKIE_AUTH, option.getNewValue());
			isDirty = true;
		}

		option = (Option<Boolean>) values.get(ACT_LOGIN_THROTTLING);
		if (option.isDirty()) {
			instancePrefs.putBoolean(PROP_LOGIN_THROTTLING, option.getNewValue());
			isDirty = true;
		}

		if (isDirty) {
			instancePrefs.flush();
		}
	}

	void initialize(Engine engine) {
		WikiAjaxDispatcher wikiAjaxDispatcher = engine.getManager(WikiAjaxDispatcher.class);
		wikiAjaxDispatcher.registerServlet(jsonTracker);
	}

	private IEclipsePreferences getDefaultPrefs() {
		String bundleName = AuthorizePluginActivator.getDefault().getName();
		return BundleDefaultsScope.INSTANCE.getNode(bundleName);
	}

	private IEclipsePreferences getInstancePrefs() {
		String bundleName = AuthorizePluginActivator.getDefault().getName();
		return InstanceScope.INSTANCE.getNode(bundleName);
	}

	public String getConfigurationEntry() {
		values.clear();
		values.put(ACT_COOKIE_ASSERTIONS, new Option<Boolean>(isCookieAssertions()));
		values.put(ACT_COOKIE_AUTHENTICATION, new Option<Boolean>(isCookieAuthentication()));
		values.put(ACT_LOGIN_THROTTLING, new Option<Boolean>(isLoginThrottling()));

//@formatter:off
		String infoCookieAssertions = """
If this value is set to "true", then JSPWiki <br/>
 will allow you to "assert" an identity using a cookie. <br/>
It's still considered to be unsafe, <br/>
 just like no login at all, but it is useful <br/>
 when you have no need to force everyone to login. """;
		String textCookieAssertions = String.format("""
  <div class="form-group form-inline ">
    <label class="control-label form-col-20"> Cookie assertions </label>
    <label class="form-control form-switch xpref-appearance">
      <!--<fmt:message key="prefs.user.appearance.light"/>-->
      <input oninput="Wiki.jsonrpc('/%s',event.target.checked)"
          id="idCookieAssertions" type="checkbox" class="" %s>
      <!--<fmt:message key="prefs.user.appearance.dark"/>-->
    </label>
    <label class="dropdown" style="display:inline-block; vertical-align:top;" >
      &#9432;
      <ul class="dropdown-menu" data-hover-parent=".dropdown">
        <li class="dropdown" style="width:700px;"> %s </li>
      </ul>
    </label>
  </div>""", jsonTracker.getServletMapping() + "/" + ACT_COOKIE_ASSERTIONS,
  			isCookieAssertions()? "checked='true'" : "", infoCookieAssertions);

		String infoCookieAuthentication = ":FVK:foo";
		String textCookieAuthentication = String.format("""
  <div class="form-group form-inline ">
    <label class="control-label form-col-20"> Cookie authentication </label>
    <label class="form-control form-switch xpref-appearance">
      <!--<fmt:message key="prefs.user.appearance.light"/>-->
      <input oninput="Wiki.jsonrpc('/%s',event.target.checked)"
          id="idCookieAuthentication" type="checkbox" class="" %s>
      <!--<fmt:message key="prefs.user.appearance.dark"/>-->
    </label>
    <label class="dropdown" style="display:inline-block; vertical-align:top;" >
      &#9432;
      <ul class="dropdown-menu" data-hover-parent=".dropdown">
        <li class="dropdown" style="width:700px;"> %s </li>
      </ul>
    </label>
  </div>""", jsonTracker.getServletMapping() + "/" + ACT_COOKIE_AUTHENTICATION,
  			isCookieAuthentication()? "checked='true'" : "", infoCookieAuthentication);

		String infoLoginThrottling = "Whether logins should be throttled to limit bruce-force attempts.";
		String textLoginThrottling = String.format("""
  <div class="form-group form-inline ">
    <label class="control-label form-col-20"> Login throttling </label>
    <label class="form-control form-switch xpref-appearance">
      <!--<fmt:message key="prefs.user.appearance.light"/>-->
      <input oninput="Wiki.jsonrpc('/%s',event.target.checked)"
          id="idLoginThrottling" type="checkbox" class="" %s>
      <!--<fmt:message key="prefs.user.appearance.dark"/>-->
    </label>
    <label class="dropdown" style="display:inline-block; vertical-align:top;" >
      &#9432;
      <ul class="dropdown-menu" data-hover-parent=".dropdown">
        <li class="dropdown" style="width:700px;"> %s </li>
      </ul>
    </label>
  </div>""", jsonTracker.getServletMapping() + "/" + ACT_LOGIN_THROTTLING,
  			isLoginThrottling()? "checked='true'" : "", infoLoginThrottling);

		String textRestoreDefault = String.format("""
  <span class="dropdown" style="display:inline-block" >
    <button class="btn btn-info" name="restoreDefault"
      onclick="Wiki.jsonrpc('/%s',0,function(result){
          $('idCookieAssertions').checked='%s';
          $('idCookieAuthentication').checked='%s';
          $('idLoginThrottling').checked='%s';
      })">
      <!-- <fmt:message key='prefs.save.prefs.submit'/> -->
      Restore Defaults
    </button>
  </span>""", jsonTracker.getServletMapping() + "/" + ACT_RESTORE_DEFAULT,
  			getDefaultPrefs().getBoolean(PROP_ALLOW_COOKIE_ASSERTIONS, true)? "true" : "",
  			getDefaultPrefs().getBoolean(PROP_ALLOW_COOKIE_AUTH, false)? "true" : "",
  			getDefaultPrefs().getBoolean(PROP_LOGIN_THROTTLING, true)? "true" : ""
		);

		String textApply = String.format("""
  &nbsp;
  <span class="dropdown" style="display:inline-block" >
    <button class="btn btn-info" name="apply"
      onclick="Wiki.jsonrpc('/%s',0)">
      <!-- <fmt:message key='prefs.cancel.submit'/> -->
      Apply
    </button>
  </span>""", jsonTracker.getServletMapping() + "/" + ACT_APPLY);

		String text =
"<h4>Authentication manager</h4>" +
textCookieAssertions +
textCookieAuthentication +
textLoginThrottling + """
  <div class="form-group form-inline">
    <br/><span class="form-col-20 control-label"></span>""" +
textRestoreDefault +
textApply +
  "</div>";
//@formatter:on		
		return text;
	}

	@Override
	public boolean isCookieAssertions() {
		boolean defaultValue = getDefaultPrefs().getBoolean(PROP_ALLOW_COOKIE_ASSERTIONS, true);
		return getInstancePrefs().getBoolean(PROP_ALLOW_COOKIE_ASSERTIONS, defaultValue);
	}

	@Override
	public boolean isCookieAuthentication() {
		boolean defaultValue = getDefaultPrefs().getBoolean(PROP_ALLOW_COOKIE_AUTH, false);
		return getInstancePrefs().getBoolean(PROP_ALLOW_COOKIE_AUTH, defaultValue);
	}

	@Override
	public boolean isLoginThrottling() {
		boolean defaultValue = getDefaultPrefs().getBoolean(PROP_LOGIN_THROTTLING, true);
		return getInstancePrefs().getBoolean(PROP_LOGIN_THROTTLING, defaultValue);
	}

	@Override
	public String getLoginModuleClass() {
		String defaultValue = getDefaultPrefs().get(PROP_LOGIN_MODULE, "");
		return getInstancePrefs().get(PROP_LOGIN_MODULE, defaultValue);
	}

}
