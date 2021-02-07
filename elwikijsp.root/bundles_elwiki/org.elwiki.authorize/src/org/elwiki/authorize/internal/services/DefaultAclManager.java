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
package org.elwiki.authorize.internal.services;

import java.security.Permission;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.acl.AclManager;
import org.apache.wiki.pages0.PageLock;
import org.apache.wiki.pages0.PageManager;
//import org.eclipse.rap.rwt.RWT;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.IAuthorizationManager;
//import org.elwiki.api.IRenderingManager;
//import org.elwiki.api.IWikiConstants;
//import org.elwiki.api.IWikiContext;
//import org.elwiki.api.IWikiEngine;
//import org.elwiki.api.PageLock;
//import org.elwiki.api.acl.AclManager;
//import org.elwiki.api.exceptions.ProviderException;
//import org.elwiki.api.exceptions.WikiSecurityException;
//import org.elwiki.api.pagemanager.IPageManager;
//import org.elwiki.api.release.FactoryWikiContext;
import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.PrincipalComparator;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.PermissionFactory;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * Default implementation that parses Acls from wiki page markup.
 *
 * @since 2.3
 */
public class DefaultAclManager implements AclManager {

	private static final Logger log = Logger.getLogger(DefaultAclManager.class);

	private AuthorizationManager m_auth = null;
	private Engine m_engine = null;

	private IWikiConfiguration wikiConfiguration;

	/**
	 * A helper method for parsing textual AccessControlLists.</br>
	 * The line is in form "ALLOW <permission> <principal>, <principal>, <principal>". This method was
	 * moved from Authorizer.
	 *
	 * @param page
	 *            The current wiki page. If the page already has an ACL, it will be used as a basis for
	 *            this ACL in order to avoid the creation of a new one.
	 * @param ruleLine
	 *            The rule line, as described above.
	 * @return A valid Access Control List. May be empty.
	 * @throws WikiSecurityException
	 *             if the ruleLine was faulty somehow.
	 * @since 2.1.121
	 */
	@Override
	public Acl parseAcl(WikiPage page, String ruleLine) throws WikiSecurityException {
		Acl acl = page.getAcl();
		if (acl == null) {
			acl = Elwiki_dataFactory.eINSTANCE.createAcl();
		}

		try {
			StringTokenizer fieldToks = new StringTokenizer(ruleLine);
			fieldToks.nextToken();
			String actions = fieldToks.nextToken();

			while (fieldToks.hasMoreTokens()) {
				String principalName = fieldToks.nextToken(",").trim();
				Principal principal = this.m_auth.resolvePrincipal(principalName);
				AclEntry oldEntry = acl.getEntry(principal);

				if (oldEntry != null) {
					log.debug("Adding to old acl list: " + principal + ", '" + actions + "'");
					oldEntry.getPermission().add(PermissionFactory.getPagePermission(page, actions));
				} else {
					log.debug("Adding new acl entry for '" + actions + "'");
					AclEntry entry = Elwiki_dataFactory.eINSTANCE.createAclEntry();

					entry.setPrincipal(principal);
					entry.getPermission().add(PermissionFactory.getPagePermission(page, actions));

					acl.getAclEntries().add(entry);
				}
			}

			// :FVK: page.setAccessList(acl);

			log.debug(acl.toString());
		} catch (NoSuchElementException nsee) {
			log.warn("Invalid access rule: " + ruleLine + " - defaults will be used.");
			throw new WikiSecurityException("Invalid access rule: " + ruleLine, nsee);
		} catch (IllegalArgumentException iae) {
			throw new WikiSecurityException("Invalid permission type: " + ruleLine, iae);
		}

		return acl;
	}

	/**
	 * Returns the access control list for the page. If the ACL has not been parsed yet, it is done
	 * on-the-fly. If the page has a parent page, then that is tried also. This method was moved from
	 * Authorizer; it was consolidated with some code from AuthorizationManager. This method is
	 * guaranteed to return a non-<code>null</code> Acl.
	 *
	 * @param page
	 *            the page
	 * @return the Acl representing permissions for the page
	 * @since 2.2.121
	 */
	@Override
	public Acl getPermissions(WikiPage page1) {
		WikiPage page = page1;
		//
		//  Does the page already have cached ACLs?
		//
		Acl acl = page.getAcl();
		log.debug("page=" + page.getName() + "\n" + acl);

		if (acl == null) {
			//
			//  If null, try the parent.
			//
			if (page instanceof PageAttachment) {
/* :FVK: этот код не выполнится никогда - так как PageAttachment не наследуется от  WikiPage. */
				PageAttachment attachment = (PageAttachment) page;
				//WikiPage parent = this.m_engine.getPage(((Attachment) page).getParentName());
				WikiPage parent = attachment.getWikipage();

				acl = getPermissions(parent);
			} else {
				/*
				//
				//  Or, try parsing the page
				//
				IWikiContext ctx = // :FVK: new WikiContext(this.m_engine, page);
						FactoryWikiContext.createWikiContext(RWT.getUISession(), this.m_engine, page);
				
				ctx.setVariable(IRenderingManager.VAR_EXECUTE_PLUGINS, Boolean.FALSE);
				
				this.m_engine.getHtml(ctx, page);
				
				// :FVK: page = this.m_engine.getPage(page.getName(), page.getVersion());
				page = this.m_engine.getPage(page.getName()); // взять последнюю версию. ?
				acl = page.getAccessList();
				
				if (acl == null) {
					acl = Elwiki_dataFactory.eINSTANCE.createAcl();
					page.setAccessList(acl);
				}
				*/
				acl = Elwiki_dataFactory.eINSTANCE.createAcl();
			}
		}

		return acl;
	}

	/**
	 * Sets the access control list for the page and persists it by prepending it to the wiki page
	 * markup and saving the page. When this method is called, all other ACL markup in the page is
	 * removed. This method will forcibly expire locks on the wiki page if they exist. Any
	 * ProviderExceptions will be re-thrown as WikiSecurityExceptions.
	 *
	 * @param page
	 *            the wiki page
	 * @param acl
	 *            the access control list
	 * @throws WikiSecurityException
	 *             of the Acl cannot be set
	 * @since 2.5
	 */
	@Override
	public void setPermissions(WikiPage page, Acl acl) throws WikiSecurityException {
		final PageManager pageManager = m_engine.getManager( PageManager.class );

		// Forcibly expire any page locks
		PageLock lock = pageManager.getCurrentLock(page);
		if (lock != null) {
			pageManager.unlockPage(lock);
		}

		// Remove all of the existing ACLs.
		String pageText = ":FVK:"; //:FVK: this.m_engine.getPureText(page);
		Matcher matcher = DefaultAclManager.ACL_PATTERN.matcher(pageText);
		String cleansedText = matcher.replaceAll("");
		String newText = DefaultAclManager.printAcl(null/*:FVK: page.getAcl()*/) + cleansedText;
		try {
			pageManager.putPageText(page, newText, "author", "changenote"); // FIXME: здесь надо не текст, а ACL задать.
		} catch (ProviderException e) {
			throw new WikiSecurityException("Could not set Acl. Reason: ProviderExcpetion " + e.getMessage(), e);
		}
	}

	/**
	 * Generates an ACL string for inclusion in a wiki page, based on a supplied Acl object. All of the
	 * permissions in this Acl are assumed to apply to the same page scope. The names of the pages are
	 * ignored; only the actions and principals matter.
	 *
	 * @param acl
	 *            the ACL
	 * @return the ACL string
	 */
	protected static String printAcl(Acl acl) {
		// Extract the ACL entries into a Map with keys == permissions, values == principals
		Map<String, List<Principal>> permissionPrincipals = new TreeMap<String, List<Principal>>();
		for (AclEntry entry : acl.getAclEntries()) {
			Principal principal = entry.getPrincipal();
			for (Permission permission : entry.getPermission()) {
				List<Principal> principals = permissionPrincipals.get(permission.getActions());
				if (principals == null) {
					principals = new ArrayList<Principal>();
					String action = permission.getActions();
					if (action.indexOf(',') != -1) {
						throw new IllegalStateException("AclEntry permission cannot have multiple targets.");
					}
					permissionPrincipals.put(action, principals);
				}
				principals.add(principal);
			}
		}

		// Now, iterate through each permission in the map and generate an ACL string

		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, List<Principal>> entry : permissionPrincipals.entrySet()) {
			String action = entry.getKey();
			List<Principal> principals = entry.getValue();
			Collections.sort(principals, new PrincipalComparator());
			s.append("[{ALLOW ");
			s.append(action);
			s.append(" ");
			for (int i = 0; i < principals.size(); i++) {
				Principal principal = principals.get(i);
				s.append(principal.getName());
				if (i < (principals.size() - 1)) {
					s.append(",");
				}
			}
			s.append("}]\n");
		}
		return s.toString();
	}

	// -- service support ---------------------------------

	public synchronized void startup() {
		//
	}

	public synchronized void shutdown() {
		//
	}

    /** {@inheritDoc} */
    @Override
    public void initialize( final Engine engine) {
        m_auth = engine.getManager( AuthorizationManager.class );
        m_engine = engine;
    }
	
}
