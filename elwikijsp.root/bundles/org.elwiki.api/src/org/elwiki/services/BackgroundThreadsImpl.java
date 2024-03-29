/* 
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */
package org.elwiki.services;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.WikiException;
import org.elwiki.api.BackgroundThreads;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiComponent;
import org.elwiki.api.event.EngineEvent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * This component creates and operates of threads presented by subclassing Actor. When it detects
 * the {@link SHUTDOWN} event, it terminates all threads.
 */
//@formatter:off
@Component(
	name = "elwiki.BackgroundThreads",
	service = { BackgroundThreads.class, WikiComponent.class, EventHandler.class },
	property = {
		EventConstants.EVENT_TOPIC + "=" + EngineEvent.Topic.SHUTDOWN,
	},
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class BackgroundThreadsImpl implements BackgroundThreads, WikiComponent, EventHandler {

	private static final Logger log = Logger.getLogger(BackgroundThreadsImpl.class);

	private static final long POLLING_INTERVAL = 1_000L;

	/** List of created threads. */
	private final List<WeakReference<BackgrounThread>> listThreads = new ArrayList<>();

	Lock listThreadsLock = new ReentrantLock();

	// -- OSGi service handling --------------------( start )--

	@WikiServiceReference
	Engine engine;

	/**
	 * This component deactivate routine.
	 */
	@Deactivate
	protected void shutdown() {
		listThreadsLock.lock();
		try {
			for (WeakReference<BackgrounThread> ref : listThreads) {
				BackgrounThread thread = ref.get();
				if (thread != null) {
					thread.shutdown();
				}
			}
			listThreads.clear();
		} finally {
			listThreadsLock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void initialize() throws WikiException {
		// nothing to do.
	}

	// -- OSGi service handling ----------------------( end )--

	@Override
	public Thread createThread(String name, int sleepInterval, Actor actor) {
		BackgrounThread thread = new BackgrounThread(sleepInterval, actor);
		thread.setName(name);
		thread.setDaemon(false);

		listThreadsLock.lock();
		try {
			listThreads.add(new WeakReference<>(thread));
		} finally {
			listThreadsLock.unlock();
		}

		return thread;
	}

	protected class BackgrounThread extends Thread {

		private final int m_interval;
		private final Actor actor;
		private volatile boolean m_killMe = false;

		/**
		 * @param sleepInterval the interval between invocations of task action.
		 * @param actor
		 */
		BackgrounThread(int sleepInterval, Actor actor) {
			this.m_interval = sleepInterval;
			this.actor = actor;
		}

		void shutdown() {
			log.warn("Detected wiki engine shutdown: killing " + getName() + ".");
			m_killMe = true;
		}

		/**
		 * Runs the background thread's {@link #backgroundTask()} method at the interval specified at
		 * construction. <br/>
		 * The thread will initially pause for a full sleep interval before starting, after which it will
		 * execute {@link #startupTask()}. This method will cleanly terminate the thread if it has
		 * previously been marked as dead, before which it will execute {@link #shutdownTask()}.
		 * <p/>
		 * If any of the three methods return an exception, it will be re-thrown as a
		 * {@link org.apache.wiki.InternalWikiException}.
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public final void run() {
			try {
				// Perform the initial startup task
				final String name = getName();
				log.warn("Starting up background thread: " + name + ".");
				actor.startupTask();

				// Perform the background task; check every second for thread death
				while (!m_killMe && actor.isAlive()) {
					// Perform the background task
					// log.debug( "Running background task: " + name + "." );
					actor.backgroundTask();

					// Sleep for the interval we're supposed to, but wake up every POLLING_INTERVAL to see if thread
					// should die
					boolean interrupted = false;
					try {
						for (int i = 0; i < m_interval; i++) {
							Thread.sleep(POLLING_INTERVAL);
							if (m_killMe) {
								interrupted = true;
								log.warn("Interrupted background thread: " + name + ".");
								break;
							}
						}
						if (interrupted) {
							break;
						}
					} catch (final Throwable t) {
						log.error("Background thread error: (stack trace follows)", t);
					}
				}

				// Perform the shutdown task
				actor.shutdownTask();
			} catch (final Throwable t) {
				log.error("Background thread error: (stack trace follows)", t);
				throw new InternalWikiException(t.getMessage(), t);
			}
		}
	}

	@Override
	public void handleEvent(Event event) {
		String topic = event.getTopic();
		switch (topic) {
		case EngineEvent.Topic.SHUTDOWN: {
			shutdown();
		}
			break;
		}
	}

}
