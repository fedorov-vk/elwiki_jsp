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
package org.apache.wiki.spi;

import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.apache.wiki.api.spi.ContentsSPI;
import org.elwiki_data.PageAttachment;


/**
 * Default implementation for {@link ContentsSPI}
 *
 * @see ContentsSPI
 */
public class ContentsSPIDefaultImpl implements ContentsSPI {

    /**
     * {@inheritDoc}
     */
    @Override
    public PageAttachment attachment( final Engine engine, final String parentPage, final String fileName ) {
         PageAttachment pageAttachment = Elwiki_dataFactory.eINSTANCE.createPageAttachment();
        //:FVK: new Attachment( engine, parentPage, fileName );
         pageAttachment.setName(fileName);
         return pageAttachment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WikiPage page( final Engine engine, final String name ) {
    	WikiPage wikiPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();
    	//:FVK: new WikiPage( engine, name );
    	wikiPage.setName(name);
        return wikiPage;
    }
}
