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
package org.apache.wiki.ui.admin.beans;

import java.util.Date;

import javax.management.NotCompliantMBeanException;
import javax.servlet.http.HttpServletRequest;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiSession;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.ui.admin.SimpleAdminBean;
import org.apache.wiki.ui.admin0.AdminBean;

public class UserBean extends SimpleAdminBean {

	Engine engine;
	
    public UserBean( final Engine engine ) throws NotCompliantMBeanException  {
        super();
        this.engine = engine;
    }

    @Override
    public String[] getAttributeNames()
    {
        return new String[0];
    }

    // FIXME: We don't yet support MBean for this kind of stuff.
    @Override
    public String[] getMethodNames()
    {
        return new String[0];
    }

    @Override
    public String doPost( final WikiContext context ) {
        final HttpServletRequest request = context.getHttpRequest();
        final WikiSession session = context.getWikiSession();
        //:FVK: final AccountManager accountManager = super.m_engine.getManager(AccountManager.class);
        AccountRegistry accountRegistry = this.engine.getManager(AccountRegistry.class);
        final String loginid = request.getParameter( "loginid" );
        final String loginname = request.getParameter( "loginname" );
        final String fullname = request.getParameter( "fullname" );
        final String password = request.getParameter( "password" );
        final String password2 = request.getParameter( "password2" );
        final String email = request.getParameter( "email" );

        if( request.getParameter( "action" ).equalsIgnoreCase( "remove" ) ) {
            try {
            	accountRegistry.deleteByLoginName( loginid );
                session.addMessage( "User profile " + loginid + " (" + fullname + ") has been deleted" );
            } catch( final NoSuchPrincipalException e ) {
                session.addMessage( "User profile has already been removed" );
            } catch( final WikiSecurityException e ) {
                session.addMessage( "Security problem: " + e );
            }
            return "";
        }

        if( password != null && password.length() > 0 && !password.equals( password2 ) ) {
            session.addMessage( "Passwords do not match!" );
            return "";
        }

        final UserProfile p;

        if( loginid.equals( "--New--" ) ) {
            // Create new user

            p = accountRegistry.newProfile();
            p.setCreated( new Date() );
        } else {
            try {
                p = accountRegistry.findByLoginName( loginid );
            } catch( final NoSuchPrincipalException e ) {
                session.addMessage( "I could not find user profile " + loginid );
                return "";
            }
        }

        p.setEmail( email );
        p.setFullname( fullname );
        if( password != null && password.length() > 0 ) {
            p.setPassword( password );
        }
        p.setLoginName( loginname );

        try {
        	accountRegistry.save( p );
        } catch( final WikiSecurityException e ) {
            session.addMessage( "Unable to save " + e.getMessage() );
        }

        session.addMessage("User profile has been updated");

        return "";
    }

    @Override
    public String getTitle() {
        return "User administration";
    }

    @Override
    public int getType() {
        return AdminBean.UNKNOWN;
    }

}
