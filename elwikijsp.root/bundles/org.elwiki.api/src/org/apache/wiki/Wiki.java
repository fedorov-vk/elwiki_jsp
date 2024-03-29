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
package org.apache.wiki;

import org.apache.wiki.api.spi.ContentsDSL;
import org.apache.wiki.api.spi.ContentsSPI;
import org.apache.wiki.api.spi.ContextDSL;
import org.apache.wiki.api.spi.ContextSPI;

public class Wiki {

	private static ContextSPI contextSPI;

	/**
	 * Access to {@link ContentsSPI} operations.
	 *
	 * @return {@link ContentsSPI} operations.
	 */
	public static ContentsDSL contents() {
		return new ContentsDSL();
	}

	/**
	 * Access to {@link ContextSPI} operations.
	 *
	 * @return {@link ContextSPI} operations.
	 */
	public static ContextDSL context() {
		return new ContextDSL(contextSPI);
	}

	// -- OSGi service handling ----------------------(start)--

	public void setContextSPI(ContextSPI contextSPI1) {
		contextSPI = contextSPI1;
	}

	public void startup() {
		//
	}

	public void shutdown() {
		//
	}

	// -- OSGi service handling ------------------------(end)--

}