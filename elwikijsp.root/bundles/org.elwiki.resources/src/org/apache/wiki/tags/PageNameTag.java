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
package org.apache.wiki.tags;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.render0.RenderingManager;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 *  Returns the currently requested page name.
 *
 *  @since 2.0
 */
public class PageNameTag extends BaseWikiTag {

    private static final long serialVersionUID = 0L;

    @Override
    public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
        final WikiPage page = wikiContext.getPage();

        if( page != null ) {
            if( page instanceof PageAttachment ) {
            	//:FVK: было -- pageContext.getOut().print( TextUtil.replaceEntities( ((PageAttachment)page).getFileName() ) );
            	// TODO: реализовать ...
            } else {
                pageContext.getOut().print(wikiContext.getName());
            }
        }

        return SKIP_BODY;
    }

}
