package org.elwiki.api;

public interface BackgroundThreads {

	/**
	 * Abstract subclass that operates in the background; a subclassed class are controlled by
	 * BackgroundThreads component. When component detects the {@link SHUTDOWN} event, it terminates all
	 * background tasks.
	 * <p>
	 * Subclasses of this method need only implement the method {@link #backgroundTask()}, instead of
	 * the normal {@link Thread#run()}. The sleep interval is specified for BackgroundThreads for
	 * creating thread instance.
	 * <p>
	 * This class is thread-safe.
	 */
	public abstract class Actor {

		private volatile boolean isAlive = true;

		final public boolean isAlive() {
			return isAlive;
		};

		/**
		 * Requests the shutdown of this background thread. Note that the shutdown is not immediate.
		 */
		final protected void shutdown() {
			this.isAlive = false;
		}

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
	 * Constructs a new instance of this background thread with a specified sleep interval.
	 *
	 * @param name          thread name.
	 * @param sleepInterval the interval between invocations of the thread's {@link Thread#run()}
	 *                      method, in seconds.
	 * @param actor         instance.
	 * 
	 * @return Created thread instance.
	 */
	Thread createThread(String name, int sleepInterval, Actor actor);
}
