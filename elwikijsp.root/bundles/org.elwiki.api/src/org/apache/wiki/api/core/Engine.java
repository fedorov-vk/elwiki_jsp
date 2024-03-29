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
package org.apache.wiki.api.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.wiki.api.exceptions.ProviderException;
import org.eclipse.jdt.annotation.NonNull;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.WikiPage;

/**
 *  Provides Wiki services to the JSP page.
 *
 *  <P>
 *  This is the main interface through which everything should go.
 *
 *  <p>
 *  There's basically only a single Engine for each web application, and you should always get it using either the
 *  {@code WikiContext#getEngine()} method or through OSGi {@code ServiceReference}.
 */
public interface Engine {

    /** This property defines the inline image pattern.  It's current value is {@value} */
    String PROP_INLINEIMAGEPTRN = "jspwiki.translatorReader.inlinePattern";

    /** The name of the cookie that gets stored to the user browser. */
    String PREFS_COOKIE_NAME = "JSPWikiUserProfile";


    /** Property name for setting the url generator instance */
    String PROP_URLCONSTRUCTOR = "jspwiki.urlConstructor";

    /** The name of the property containing the ACLManager implementing class. The value is {@value}. */
    String PROP_ACL_MANAGER_IMPL = "jspwiki.aclManager";

    /**
     * Adapt Engine to a concrete type.
     *
     * @param cls class denoting the type to adapt to.
     * @param <E> type to adapt to.
     * @return engine instance adapted to the requested type. Might throw an unchecked exception if the instance cannot be adapted to requested type!
     */
    @SuppressWarnings( "unchecked" )
    default < E extends Engine > E adapt( final Class< E > cls ) {
        return ( E )this;
    }

    /**
     * Retrieves the object instantiated by the Engine matching the requested type.
     *
     * @param manager requested object instantiated by the Engine.
     * @param <T> type of the requested object.
     * @return requested object instantiated by the Engine, {@code null} if not available.
     */
    @NonNull
    < T > T getManager( Class< T > manager );

    /**
     * Retrieves the objects instantiated by the Engine that can be assigned to the requested type.
     *
     * @param manager requested objectx instantiated by the Engine.
     * @param <T> type of the requested object.
     * @return collection of requested objects instantiated by the Engine, {@code empty} list if none available.
     */
    < T > List< T > getManagers( Class< T > manager );

    /**
     * check if the Engine has been configured.
     *
     * @return {@code true} if it has, {@code false} otherwise.
     */
    boolean isConfigured();

    /**
     *  Returns the moment when this engine was started.
     *
     *  @since 2.0.15.
     *  @return The start time of this wiki.
     */
    Date getStartTime();

    /**
     *  Returns the URL of the global RSS file.  May be null, if the RSS file generation is not operational.
     *
     *  @since 1.7.10
     *  @return The global RSS url
     */
    String getGlobalRSSURL();

    /**
     *  Returns an URL if a WikiContext is not available.
     *
     *  @param context The WikiContext (VIEW, EDIT, etc...)
     *  @param pageName Name of the page, as usual
     *  @param params List of parameters. May be null, if no parameters.
     *  @return An URL (absolute or relative).
     */
    String getURL( String context, String pageName, String params );

    /**
     *  Returns the ServletContext that this particular Engine was initialized with. <strong>It may return {@code null}</strong>,
     *  if the Engine is not running inside a servlet container!
     *
     *  @since 1.7.10
     *  @return ServletContext of the Engine, or {@code null}.
     */
    ServletContext getServletContext();

    /**
     * Looks up and obtains a configuration file inside the WEB-INF folder of a wiki webapp.
     *
     * @param name the file to obtain, <em>e.g.</em>, <code>jspwiki.policy</code>
     * @return the URL to the file
     */
    @Deprecated
    default URL findConfigFile( final String name ) {
        Logger.getLogger( Engine.class ).info( "looking for " + name + " inside WEB-INF " );
        // Try creating an absolute path first
        File defaultFile = null;
        if( getRootPath() != null ) {
            defaultFile = new File( getRootPath() + "/WEB-INF/" + name );
        }
        if ( defaultFile != null && defaultFile.exists() ) {
            try {
                return defaultFile.toURI().toURL();
            } catch ( final MalformedURLException e ) {
                // Shouldn't happen, but log it if it does
                Logger.getLogger( Engine.class ).warn( "Malformed URL: " + e.getMessage() );
            }
        }

        // Ok, the absolute path didn't work; try other methods
        URL path = null;

        if( getServletContext() != null ) {
        	//TODO: этот код устарел (в ElWiki инициализация относительно OSGi, а не сервлета. ServletContext==null). 
            final File tmpFile;
            try {
                tmpFile = File.createTempFile( "temp." + name, "" );
            } catch( final IOException e ) {
                Logger.getLogger( Engine.class ).error( "unable to create a temp file to load onto the policy", e );
                return null;
            }
            tmpFile.deleteOnExit();
            Logger.getLogger( Engine.class ).info( "looking for /" + name + " on classpath" );
            //  create a tmp file of the policy loaded as an InputStream and return the URL to it
            try( final InputStream is = Engine.class.getResourceAsStream( "/" + name );
                    final OutputStream os = new FileOutputStream( tmpFile ) ) {
                if( is == null ) {
                    throw new FileNotFoundException( name + " not found" );
                }
                final URL url = getServletContext().getResource( "/WEB-INF/" + name );
                if( url != null ) {
                    return url;
                }

                final byte[] buff = new byte[1024];
                int bytes;
                while( ( bytes = is.read( buff ) ) != -1 ) {
                    os.write( buff, 0, bytes );
                }

                path = tmpFile.toURI().toURL();
            } catch( final MalformedURLException e ) {
                // This should never happen unless I screw up
                Logger.getLogger( Engine.class ).fatal( "Your code is b0rked.  You are a bad person.", e );
            } catch( final IOException e ) {
                Logger.getLogger( Engine.class ).error( "failed to load security policy from file " + name + ",stacktrace follows", e );
            }
        }
        return path;
    }

    /**
     *  Returns the root path.  The root path is where the Engine is located in the file system.
     *
     *  @since 2.2
     *  @return A path to where the Wiki is installed in the local filesystem.
     */
    String getRootPath();

    /**
     *  Returns the correct page name, or null, if no such page can be found.  Aliases are considered. This method simply delegates to
     *  {@link org.apache.wiki.ui.CommandResolver#getFinalPageName(String)}.
     *
     *  @since 2.0
     *  @param page Page name.
     *  @return The rewritten page name, or null, if the page does not exist.
     *  @throws ProviderException If something goes wrong in the backend.
     */
    String getFinalPageName( String page ) throws ProviderException;

    /**
     * Adds an attribute to the engine for the duration of this engine.  The value is not persisted.
     *
     * @since 2.4.91
     * @param key the attribute name
     * @param value the value
     */
    void setAttribute( String key, Object value );

    /**
     *  Gets an attribute from the engine.
     *
     *  @param key the attribute name
     *  @return the value
     */
    < T > T getAttribute( String key );

    /**
     *  Removes an attribute.
     *
     *  @param key The key of the attribute to remove.
     *  @return The previous attribute, if it existed.
     */
    < T > T removeAttribute( String key );

	IWikiConfiguration getWikiConfiguration();

    /**
     *  :FVK: WORKAROUND.
     *
     *  Returns the IANA name of the character set encoding we're supposed to be using right now.
     *
     *  @return The content encoding (either UTF-8, ISO-8859-1 or any specified in the preferences).
     */
    Charset getContentEncoding();


	/**
	 * Turns a WikiName into something that can be called through using an URL.
	 *
	 * @since 1.4.1
	 * @param pagename A name. Can be actually any string.
	 * @return A properly encoded name.
	 * @throws Exception;
	 * @see #decodeName(String)
	 */
	String encodeName(String pagename) throws IOException;

	/**
	 * Decodes a URL-encoded request back to regular life. This properly heeds the encoding as defined
	 * in the settings file.
	 *
	 * @param pagerequest The URL-encoded string to decode
	 * @return A decoded string.
	 * @see #encodeName(String)
	 */
	String decodeName(String pagerequest) throws IOException;
    
}
