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
package org.apache.wiki.api.filters;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.engine.Initializable;
import org.apache.wiki.api.exceptions.FilterException;
import org.apache.wiki.api.exceptions.WikiException;

import java.lang.reflect.Method;
import java.util.Properties;

import static org.apache.wiki.api.filters.FilterSupportOperations.executePageFilterPhase;
import static org.apache.wiki.api.filters.FilterSupportOperations.methodOfNonPublicAPI;


/**
 * Provides a base implementation of a PageFilter.  None of the callbacks do anything, so it is a good idea for you to extend from this
 * class and implement only methods that you need.
 */
public class BasePageFilter implements PageFilter {

    protected Engine m_engine;

    /**
     * Is called whenever the a new PageFilter is instantiated and reset.
     * If you override this, you should call super.initialize() first.
     * {@inheritDoc}
     *  
     *  @param engine The Engine which owns this PageFilter
     *  @param properties The properties ripped from filters.xml.
     *  @throws WikiException If the filter could not be initialized. If this is thrown, the filter is not added to the internal queues.
     */
    @Override
    public void initialize( final Engine engine ) throws WikiException {
        m_engine = engine;
        final Method m = methodOfNonPublicAPI( this, "initialize", "org.apache.wiki.WikiEngine", "java.util.Properties" );
        executePageFilterPhase( () -> null, m, this, engine );
    }

}
