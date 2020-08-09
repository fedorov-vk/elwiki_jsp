package org.elwiki.storage.cdo.internal.bundle;

import org.apache.wiki.api.IStorageCdo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

@SuppressWarnings("unused")
@Deprecated
public class StorageCdoActivator implements BundleActivator {

	public static String PLIGIN_ID = "org.elwiki.storage.cdo";

	// The shared instance
	private static StorageCdoActivator instance;

	private static BundleContext context;

	private static IStorageCdo storageCdo;

	// == CODE ================================================================

	/**
	 * Default Constructor.
	 */
	public StorageCdoActivator() {
		super();
		instance = this;
	}

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		StorageCdoActivator.context = bundleContext;

		/*
		Job job = Job.createSystem(new ICoreRunnable() { // :FVK: delayed CDO connection.																																																																																																																																																																	
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				System.out.println("--FVK-------------------------:: invoke activateCDOconnection().");
				activateCDOconnection();
			}
		});
		job.schedule(15000L);
		*/
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		deactivateCDOconnection();

		StorageCdoActivator.context = null;
	}

	/**
	 * Activates connection with CDO repository.
	 */
	private void activateCDOconnection() {
		try {
			Lifecycle storageCdo1 = (Lifecycle) getStorageCdo();
			// makes this before invokes secureLogin(). Due to this is initialisation the CDO access.
			storageCdo1.activate();
		} catch (Exception e) {
			//:FVK:			MessageDialog.openError(Display.getDefault().getActiveShell(), "Ошибка",
			//					"Ошибка связи с сервером данных.\n\n" + e.getMessage());
			// DbCoreLog.logError(e);
			System.out.println("Ошибка связи с сервером данных.\n\n" + e.getMessage());
			System.exit(0); // TODO01: workaround exit.
		}
	}

	/**
	 * Deactivates connection with CDO repository.
	 */
	private void deactivateCDOconnection() {
		Lifecycle storageCdo1 = (Lifecycle) getStorageCdo();
		if (storageCdo1.isActive()) {
			try {
				storageCdo1.deactivate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
	}

	public static StorageCdoActivator getDefault() {
		return StorageCdoActivator.instance;
	}

	public IStorageCdo getStorageCdo() {
		return storageCdo;
	}

	public static void setStorageCdo(IStorageCdo storageCdo) {
		StorageCdoActivator.storageCdo = storageCdo;
	}
}
