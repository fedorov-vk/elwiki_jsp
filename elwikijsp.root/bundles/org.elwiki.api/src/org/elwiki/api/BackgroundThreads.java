package org.elwiki.api;

public interface BackgroundThreads {

	public abstract class Actor {
		/**
		 * Abstract method that performs the actual work for this background thread; subclasses must
		 * implement this method.
		 * 
		 * @throws Exception Any exception can be thrown
		 */
		public abstract void backgroundTask() throws Exception;

		/**
		 * Executes a task just after the thread's {@link Thread#run()} method starts, but before the
		 * {@link #backgroundTask()} task executes. By default, this method does nothing; override it to
		 * implement custom functionality.
		 * 
		 * @throws Exception Any exception can be thrown.
		 */
		public void startupTask() throws Exception {
		}

		/**
		 * Executes a task after shutdown signal was detected. By default, this method does nothing;
		 * override it to implement custom functionality.
		 * 
		 * @throws Exception Any exception can be thrown.
		 */
		public void shutdownTask() throws Exception {
		}
	}

	/**
	 * Constructs a new instance of this background thread with a specified sleep interval, and adds the
	 * new instance to the wiki engine's event listeners.
	 * @param name thread name.
	 * @param sleepInterval the interval between invocations of the thread's {@link Thread#run()}
	 *                      method, in seconds.
	 * @param actor instance.
	 * 
	 * @return Created thread instance.
	 */
	Thread createThread(String name, int sleepInterval, Actor actor);
}
