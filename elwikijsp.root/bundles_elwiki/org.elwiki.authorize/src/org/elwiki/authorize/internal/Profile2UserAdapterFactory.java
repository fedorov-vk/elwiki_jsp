package org.elwiki.authorize.internal;

import org.apache.wiki.auth.UserProfile;
import org.eclipse.core.runtime.IAdapterFactory;
import org.elwiki.authorize.internal.bundle.AuthorizePluginActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;

public class Profile2UserAdapterFactory implements IAdapterFactory {

	private UserAdmin userAdmin;

	/**
	 * Constructs this adapter factory.
	 */
	public Profile2UserAdapterFactory() {
		super();

		BundleContext context = AuthorizePluginActivator.getDefault().getBundle().getBundleContext();
		ServiceReference<?> ref = context.getServiceReference(UserAdmin.class.getName());
		if (ref != null) {
			this.userAdmin = (UserAdmin) context.getService(ref);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == User.class) {
			UserProfile userProfile = (UserProfile) adaptableObject;
			String userId = userProfile.getUid();
			return (T) this.userAdmin.getRole(userId);
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class[] { User.class };
	}
}
