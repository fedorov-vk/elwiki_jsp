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
package org.apache.wiki.tasks;

import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.tasks.TasksManager;
import org.apache.wiki.tasks.auth.SaveUserProfileTask;
import org.apache.wiki.tasks.pages.PreSaveWikiPageTask;
import org.apache.wiki.tasks.pages.SaveWikiPageTask;
import org.apache.wiki.ui.TemplateManager;
import org.apache.wiki.workflow0.Step;
import org.osgi.service.component.annotations.Component;

import java.util.Locale;

/**
 * Default implementation for {@link TasksManager}.
 */
@Component(name = "elwiki.DefaultTasksManager", service = TasksManager.class, //
		factory = "elwiki.TasksManager.factory")
public class DefaultTasksManager implements TasksManager {

    /**
     * {@inheritDoc}
     */
    @Override
    public Step buildPreSaveWikiPageTask( final Context context, final String proposedText ) {
        return new PreSaveWikiPageTask( context, proposedText );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Step buildSaveWikiPageTask( final Context context, String author, String changenote ) {
        return new SaveWikiPageTask( context, author, changenote );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Step buildSaveUserProfileTask( final Engine engine, final Locale loc ) {
        return new SaveUserProfileTask( engine, loc );
    }
    
}
