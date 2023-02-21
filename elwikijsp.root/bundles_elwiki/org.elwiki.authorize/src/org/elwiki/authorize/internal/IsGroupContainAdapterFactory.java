package org.elwiki.authorize.internal;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.util.ThreadUtil;
import org.eclipse.core.runtime.IAdapterFactory;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.elwiki.permissions.GroupPermission;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

public class IsGroupContainAdapterFactory implements IAdapterFactory {

	// use a static final field so that the adapterList is only instantiated once.
	private static final Class<?>[] adapterList = new Class<?>[] { Boolean.class };

	private UserAdmin userAdminService;

	/**
	 * Constructs this adapter factory.
	 */
	public IsGroupContainAdapterFactory() {
		super();

		BundleContext context = AuthorizePluginActivator.getDefault().getBundle().getBundleContext();
		ServiceReference<?> ref = context.getServiceReference(UserAdmin.class.getName());
		if (ref != null) {
			this.userAdminService = (UserAdmin) context.getService(ref);
		}
	}

	@Override
	public Class<?>[] getAdapterList() {
		return adapterList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == Boolean.class) {
			GroupPermission groupPermission = (GroupPermission) adaptableObject;
			String groupName = groupPermission.getGroup();
			Role role = userAdminService.getUser(AccountRegistry.GROUP_NAME, groupName);
			String groupUid = (role != null) ? role.getName() : "";

			WikiContext wikiContext = (WikiContext) ThreadUtil.getCurrentRequest().getAttribute(WikiContext.ATTR_WIKI_CONTEXT);
			User user = wikiContext.getWikiSession().getUser();
			Authorization authorization = userAdminService.getAuthorization(user);
			boolean status = authorization.hasRole(groupUid);

			return (T) (status ? Boolean.TRUE : Boolean.FALSE);
		}
		return (T) Boolean.FALSE;
	}

}
