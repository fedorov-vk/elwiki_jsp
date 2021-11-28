package org.elwiki.data.persist.internal;

import org.eclipse.core.runtime.jobs.Job;
import org.elwiki.data.persist.IDataStore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PluginActivator implements BundleActivator {

	private static PluginActivator instance;

	private static BundleContext context;

	private DataStore dataStore = new DataStore();

	/**
	 * Default Constructor.
	 */
	public PluginActivator() {
		super();
		instance = this;
	}

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		PluginActivator.context = bundleContext;
		activateRepository();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		deactivateRepository();
		PluginActivator.context = null;
	}

	Job jobActivating;

	private void activateRepository() {
		/*
		jobActivating = Job.createSystem("activateRepository", new ICoreRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				PluginActivator.this.dataStore.doConnect();
			}
		});
		jobActivating.setPriority(Job.LONG);
		jobActivating.schedule();
		*/

//		Thread thread = new Thread(() -> {
		//PluginActivator.this.dataStore.doConnect();
		//System.out.println("--- Repository Activated. ---");
//		});
//		thread.start();
	}

	private void deactivateRepository() {
		/*
		// Try cancel Activating Job, if it is running.
		jobActivating.cancel();
		int counter = 5;
		while (jobActivating.getState() == Job.RUNNING && --counter > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
		if (jobActivating.getState() != Job.NONE)
			jobActivating.cancel();
		
		Job jobDeactivating = Job.createSystem("deActivateRepository", new ICoreRunnable() {
			@Override
			public void run(IProgressMonitor monitor) {
				PluginActivator.this.dataStore.doDisconnect();
				System.out.println("--- Repository Deactivated. ---");
			}
		});
		jobDeactivating.setPriority(Job.LONG);
		jobDeactivating.schedule();
		*/
		Thread thread = new Thread(() -> {
			PluginActivator.this.dataStore.doDisconnect();
			System.out.println("--- Repository Deactivated. ---");
		});
		thread.start();

	}

	public static PluginActivator getDefault() {
		return PluginActivator.instance;
	}

	public IDataStore getDataStore() {
		return this.dataStore;
	}

}
