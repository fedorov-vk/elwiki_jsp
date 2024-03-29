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
package org.apache.wiki.ui.admin;

import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.parser0.WikiDocument;
import org.apache.wiki.render0.RenderingManager;
import org.apache.wiki.ui.admin0.AdminBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 *  This class is still experimental.
 *
 */
public abstract class WikiFormAdminBean implements AdminBean {

    public abstract String getForm( WikiContext context );
    
    public abstract void handleResponse( WikiContext context, Map< ?, ? > params );

    @Override
    public String doGet( final WikiContext context ) {
        String result = "";
        final String wikiMarkup = getForm( context );
        final RenderingManager mgr = context.getEngine().getManager(RenderingManager.class);
        final WikiDocument doc;
        try {
            doc = mgr.getParser( context, wikiMarkup ).parse();
            result = mgr.getHTML( context, doc );
        } catch( final IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return result;
    }

    public String handlePost( final WikiContext context, final HttpServletRequest req, final HttpServletResponse resp ) {
        // FIXME: Not yet implemented
        return null;
    }

}
