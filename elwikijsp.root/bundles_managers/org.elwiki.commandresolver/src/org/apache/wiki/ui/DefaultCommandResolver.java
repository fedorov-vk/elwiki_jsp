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
package org.apache.wiki.ui;

import org.apache.log4j.Logger;
import org.apache.wiki.InternalWikiException;
import org.apache.wiki.Wiki;
import org.apache.wiki.api.core.Command;
import org.apache.wiki.api.core.Engine;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.wiki.api.engine.Initializable;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.ui.AllCommands;
import org.apache.wiki.api.ui.CommandResolver;
import org.apache.wiki.api.ui.GroupCommand;
import org.apache.wiki.api.ui.PageCommand;
import org.apache.wiki.api.ui.RedirectCommand;
import org.apache.wiki.api.ui.WikiCommand;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.parser0.MarkupParser;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.TextUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.GroupPrincipal;
import org.elwiki.services.ServicesRefs;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Default implementation for {@link CommandResolver}
 * </p>
 *
 * @since 2.4.22
 */
@Component(name = "elwiki.DefaultCommandResolver", service = CommandResolver.class, //
		factory = "elwiki.CommandResolver.factory")
public final class DefaultCommandResolver implements CommandResolver, Initializable {

	private static final Logger LOG = Logger.getLogger(DefaultCommandResolver.class);

	private static final String URL_CMD_PREFIX = "cmd.";

	/** Private map with request contexts as keys, Commands as values */
	private static final Map<String, Command> CONTEXTS;

	/* Store the JSP-to-Command and context-to-Command mappings */
	static {
		CONTEXTS = new HashMap<>();
		final Command[] commands = AllCommands.get();
		for (final Command command : commands) {
			CONTEXTS.put(command.getRequestContext(), command);
		}
	}

	/**
	 * Create instance of DefaultCommandResolver.
	 */
	public DefaultCommandResolver() {
		super();
	}

	// -- service handling ---------------------------(start)--

	private Engine m_engine;

	/** Stores configuration. */
	@Reference //(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private PageManager pageManager;

	@WikiServiceReference
	private URLConstructor urlConstructor;

	/**
	 * This component activate routine. Does all the real initialization.
	 * 
	 * @param componentContext passed the Engine.
	 * @throws WikiException
	 */
	@Activate
	protected void startup(ComponentContext componentContext) throws WikiException {
		Object obj = componentContext.getProperties().get(Engine.ENGINE_REFERENCE);
		if (obj instanceof Engine engine) {
			initialize(engine);
		}
	}

	// -- service handling -----------------------------(end)--

	@Override
	public void initialize(Engine wikiEngine) throws WikiException {
		m_engine = wikiEngine;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Command findCommand(final HttpServletRequest request, final String defaultContext) {
		// Corner case if request is null
		if (request == null) {
			return CommandResolver.findCommand(defaultContext);
		}

		Command command = null;

		// Determine the name of the page (which may be null)
		String pageName = extractPageFromParameter(defaultContext, request);

		// If we haven't found a matching command yet, extract the JSP path and compare to our list of
		// special pages
		/*:FVK:if (command == null)*/ {
			command = extractCommandFromPath(request);

			// Otherwise: use the default context
			if (command == null) {
				command = CONTEXTS.get(defaultContext);
				if (command == null) {
					throw new IllegalArgumentException("Wiki context '" + defaultContext + "' is illegal.");
				}
			}
		}

		// For PageCommand.VIEW, default to front page if a page wasn't supplied
		if (PageCommand.VIEW.equals(command) && pageName == null) {
			pageName = this.wikiConfiguration.getFrontPage();
		}
		
		//:FVK: workaround - for /attach/ request.
		if(PageCommand.ATTACH.equals(command)) {
			pageName = request.getParameter("pageId");
			if (pageName != null) {
				WikiPage page = null;
				try {
					page = this.pageManager.getPageById(pageName);
				} catch (ProviderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (page != null)
					return command.targetedCommand(page);
			}
		}

		// These next blocks handle targeting requirements

		// If we were passed a page parameter, try to resolve it
		if (command instanceof PageCommand && pageName != null) {
			// If there's a matching WikiPage, "wrap" the command
			final WikiPage page = resolvePage(request, pageName);
			return command.targetedCommand(page);
		}

		// If "create group" command, target this wiki
		final String wiki = this.wikiConfiguration.getApplicationName();
		if (WikiCommand.CREATE_GROUP.equals(command)) {
			return WikiCommand.CREATE_GROUP.targetedCommand(wiki);
		}

		// If group command, see if we were passed a group name
		if (command instanceof GroupCommand) {
			String groupName = request.getParameter("group");
			groupName = TextUtil.replaceEntities(groupName);
			if (groupName != null && groupName.length() > 0) {
				final GroupPrincipal group = new GroupPrincipal(groupName);
				return command.targetedCommand(group);
			}
		}

		// No page provided; return an "ordinary" command
		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated //:FVK: this method now obfuscates wiki functionality - now page names are fixed, and can be repeated.
	@Override
	public String getFinalPageName(final String page) throws ProviderException {
		boolean isThere = this.pageManager.pageExists(page);
		String finalName = page;

		if (!isThere) {
			finalName = MarkupParser.wikifyLink(page);
			isThere = this.pageManager.pageExists(finalName);
		}

		return isThere ? finalName : null;
	}

	/**
	 * Extracts a Command based on the path of an HTTP request. If the requested path matches a
	 * Command's <code>getRequestContext()</code> value, that Command is returned.
	 *
	 * @param request the HTTP request
	 * @return the resolved Command, or <code>null</code> if not found
	 */
	protected Command extractCommandFromPath(HttpServletRequest request) {
		// Take everything between of initial left / and first right # or ?
		String requestCtx = request.getRequestURI(); // takes, for example: "/cmd.view"

		if (requestCtx == null) {
			return null;
		}

		// remove first '/' and first "cmd." //:FVK: - following is my workaround?
		if (requestCtx.startsWith("/")) {
			requestCtx = requestCtx.substring(1);
		}
		if (requestCtx.startsWith(URL_CMD_PREFIX)) {
			requestCtx = requestCtx.substring(URL_CMD_PREFIX.length());
		}

		if (requestCtx.length() == 0) {
			return null;
		}

		// Still haven't found a matching command?
		// Ok, see if we match against our standard list of CONTEXTs
		return CONTEXTS.get(requestCtx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String extractPageFromParameter(final String requestContext, final HttpServletRequest request) {
		// Extract the page name/number from the URL directly
		try {
			String page = this.urlConstructor.parsePage(requestContext, request,
					this.wikiConfiguration.getContentEncodingCs());
			if (page != null) {
				/*:FVK: - getFinalPageName() COMMENTED - the call of actions with the compilation of the page name. 
				try {
					// Look for singular/plural variants; if one not found, take the one the user supplied
					final String finalPage = getFinalPageName(page);
					if (finalPage != null) {
						page = finalPage;
					}
				} catch (final ProviderException e) {
					// FIXME: Should not ignore!
				}
				*/
				return page;
			} else {
				// extract page number.
				String pageId = this.urlConstructor.parsePageId(request);
				WikiPage wikiPage = this.pageManager.getPageById(pageId);
				if (wikiPage != null) {
					return wikiPage.getName();
				}
			}
		} catch (final IOException | ProviderException e) {
			LOG.error("Unable to create context", e);
			throw new InternalWikiException("Big internal booboo, please check logs.", e);
		}

		// Didn't resolve; return null
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WikiPage resolvePage(final HttpServletRequest request, String page) {
		// See if the user included a version parameter
		int version = WikiProvider.LATEST_VERSION;
		final String rev = request.getParameter("version");
		if (rev != null) {
			try {
				version = Integer.parseInt(rev);
			} catch (final NumberFormatException e) {
				// This happens a lot with bots or other guys who are trying to test if we are vulnerable to
				// e.g. XSS attacks. We catch
				// it here so that the admin does not get tons of mail.
			}
		}

		WikiPage wikipage = this.pageManager.getPage(page, version);
		if (wikipage == null) {
			page = MarkupParser.cleanLink(page);
			wikipage = Wiki.contents().page(page);
		}
		return wikipage;
	}

}
