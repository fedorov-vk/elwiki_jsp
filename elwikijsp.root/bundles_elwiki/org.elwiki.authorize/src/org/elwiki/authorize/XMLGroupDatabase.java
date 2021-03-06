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
package org.elwiki.authorize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.WikiSecurityException;
import org.eclipse.core.runtime.IPath;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.IWikiEngine;
import org.elwiki.api.authorization.IGroupWiki;
import org.elwiki.api.authorization.authorize.GroupDatabase;
//import org.elwiki.api.exceptions.NoRequiredPropertyException;
//import org.elwiki.api.exceptions.NoSuchPrincipalException;
//import org.elwiki.api.exceptions.WikiSecurityException;
import org.elwiki.authorize.user.GroupWiki;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.authorize.WikiPrincipal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
//:FVK:import org.xml.sax.SAXException;

/**
 * <p>
 * GroupDatabase implementation for loading, persisting and storing wiki groups, using an XML file
 * for persistence. Group entries are simple <code>&lt;group&gt;</code> elements under the root.
 * Each group member is representated by a <code>&lt;member&gt;</code> element. For example:
 * </p>
 * <blockquote><code>
 * &lt;groups&gt;<br/>
 * &nbsp;&nbsp;&lt;group name="TV" created="Jun 20, 2006 2:50:54 PM" lastModified="Jan 21, 2006 2:50:54 PM"&gt;<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;member principal="Archie Bunker" /&gt;<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;member principal="BullwinkleMoose" /&gt;<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;member principal="Fred Friendly" /&gt;<br/>
 * &nbsp;&nbsp;&lt;/group&gt;<br/>
 * &nbsp;&nbsp;&lt;group name="Literature" created="Jun 22, 2006 2:50:54 PM" lastModified="Jan 23, 2006 2:50:54 PM"&gt;<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;member principal="Charles Dickens" /&gt;<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;member principal="Homer" /&gt;<br/>
 * &nbsp;&nbsp;&lt;/group&gt;<br/>
 * &lt;/groups&gt;
 * </code></blockquote>
 * 
 * @since 2.4.17
 */
public class XMLGroupDatabase implements GroupDatabase {

	protected static final Logger log = Logger.getLogger(XMLGroupDatabase.class);

	/**
	 * The jspwiki.properties property specifying the file system location of the group database.
	 */
	public static final String PROP_DATABASE = "jspwiki.xmlGroupDatabaseFile";

	private static final String DEFAULT_DATABASE = "groupdatabase.xml";

	private static final String CREATED = "created";

	private static final String CREATOR = "creator";

	private static final String GROUP_TAG = "group";

	private static final String GROUP_NAME = "name";

	private static final String LAST_MODIFIED = "lastModified";

	private static final String MODIFIER = "modifier";

	private static final String MEMBER_TAG = "member";

	private static final String PRINCIPAL = "principal";

	private Document m_dom = null;

	private DateFormat m_defaultFormat = DateFormat.getDateTimeInstance();

	private DateFormat m_format = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss:SSS z");

	private File m_file = null;

	private Engine m_engine = null;

	private Map<String, IGroupWiki> m_groups = new HashMap<>();

	//:FVK: private IApplicationSession applicationSession;

	/**
	 * No-op method that in previous versions of JSPWiki was intended to atomically commit changes to
	 * the user database. Now, the {@link #save(GroupWiki, Principal)} and {@link #delete(GroupWiki)} methods
	 * are atomic themselves.
	 * 
	 * @throws WikiSecurityException
	 *             never...
	 * @deprecated there is no need to call this method because the save and delete methods contain
	 *             their own commit logic
	 */
	@Override
	public void commit() throws WikiSecurityException {
	}

	/**
	 * Looks up and deletes a {@link GroupWiki} from the group database. If the group database does not
	 * contain the supplied Group. this method throws a {@link NoSuchPrincipalException}. The method
	 * commits the results of the delete to persistent storage.
	 * 
	 * @param group
	 *            the group to remove
	 * @throws WikiSecurityException
	 *             if the database does not contain the supplied group (thrown as
	 *             {@link NoSuchPrincipalException}) or if the commit did not succeed
	 */
	@Override
	public void delete(IGroupWiki group) throws WikiSecurityException {
		String index = group.getName();
		boolean exists = this.m_groups.containsKey(index);

		if (!exists) {
			throw new NoSuchPrincipalException("Not in database: " + group.getName());
		}

		this.m_groups.remove(index);

		// Commit to disk
		saveDOM();
	}

	/**
	 * Returns all wiki groups that are stored in the GroupDatabase as an array of Group objects. If the
	 * database does not contain any groups, this method will return a zero-length array. This method
	 * causes back-end storage to load the entire set of group; thus, it should be called infrequently
	 * (e.g., at initialization time).
	 * 
	 * @return the wiki groups
	 * @throws WikiSecurityException
	 *             if the groups cannot be returned by the back-end
	 */
	@Override
	public IGroupWiki[] groups() throws WikiSecurityException {
		buildDOM();
		Collection<IGroupWiki> groups = this.m_groups.values();
		return groups.toArray(new GroupWiki[groups.size()]);
	}

	/* //:FVK: 
	@Override
	public void initialize(IApplicationSession applicationSession1)
			throws NoRequiredPropertyException, WikiSecurityException {
		this.applicationSession = applicationSession1;
		this.m_engine = this.applicationSession.getWikiEngine();

		IWikiConfiguration wikiConfiguration = this.applicationSession.getWikiConfiguration();
		File defaultFile = null;
		IPath rootPath = wikiConfiguration.getWorkspacePath();
		if (rootPath.isEmpty()) {
			log.fatal("Cannot identify ElWiki workspace path.");
			// :FVK: defaultFile = new File("WEB-INF/" + DEFAULT_DATABASE).getAbsoluteFile();
		} else {
			defaultFile = rootPath.append(DEFAULT_DATABASE).toFile();
		}

		// Get database file location
		String file = wikiConfiguration.getWikiPreferences().getString(PROP_DATABASE);
		if (file.length() == 0) {
			log.warn("XML group database property " + PROP_DATABASE + " not found; trying :: " + defaultFile);
			this.m_file = defaultFile;
		} else {
			this.m_file = new File(file);
		}

		log.info("XML group database at :: " + this.m_file.getAbsolutePath());

		// Read DOM
		buildDOM();
	}
	*/

	/**
	 * Saves a Group to the group database. Note that this method <em>must</em> fail, and throw an
	 * <code>IllegalArgumentException</code>, if the proposed group is the same name as one of the
	 * built-in Roles: e.g., Admin, Authenticated, etc. The database is responsible for setting
	 * create/modify timestamps, upon a successful save, to the Group. The method commits the results of
	 * the delete to persistent storage.
	 * 
	 * @param group
	 *            the Group to save
	 * @param modifier
	 *            the user who saved the Group
	 * @throws WikiSecurityException
	 *             if the Group could not be saved successfully
	 */
	@Override
	public void save(IGroupWiki group, Principal modifier) throws WikiSecurityException {
		if (group == null || modifier == null) {
			throw new IllegalArgumentException("Group or modifier cannot be null.");
		}

		checkForRefresh();

		String index = group.getName();
		boolean isNew = !(this.m_groups.containsKey(index));
		Date modDate = new Date(System.currentTimeMillis());
		if (isNew) {
			// If new, set created info
			group.setCreated(modDate);
			group.setCreator(modifier.getName());
		}
		group.setModifier(modifier.getName());
		group.setLastModified(modDate);

		// Add the group to the 'saved' list
		this.m_groups.put(index, group);

		// Commit to disk
		saveDOM();
	}

	private void buildDOM() throws WikiSecurityException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setExpandEntityReferences(false);
		factory.setIgnoringComments(true);
		factory.setNamespaceAware(false);
		try {
			this.m_dom = factory.newDocumentBuilder().parse(this.m_file);
			log.debug("Database successfully initialized");
			this.m_lastModified = this.m_file.lastModified();
			this.m_lastCheck = System.currentTimeMillis();
		} catch (ParserConfigurationException e) {
			log.error("Configuration error: " + e.getMessage());
		} catch (FileNotFoundException e) {
			log.info("Group database not found; creating from scratch...");
		} catch (IOException e) {
			log.error("IO error: " + e.getMessage());
		} catch (Exception e) { //SAXException
			log.error("SAX error: " + e.getMessage());
		}
		if (this.m_dom == null) {
			try {
				//
				// Create the DOM from scratch
				//
				this.m_dom = factory.newDocumentBuilder().newDocument();
				this.m_dom.appendChild(this.m_dom.createElement("groups"));
			} catch (ParserConfigurationException e) {
				log.fatal("Could not create in-memory DOM");
			}
		}

		// Ok, now go and read this sucker in
		if (this.m_dom != null) {
			NodeList groupNodes = this.m_dom.getElementsByTagName(GROUP_TAG);
			for (int i = 0; i < groupNodes.getLength(); i++) {
				Element groupNode = (Element) groupNodes.item(i);
				String groupName = groupNode.getAttribute(GROUP_NAME);
				if (groupName == null) {
					log.warn("Detected null group name in XMLGroupDataBase. Check your group database.");
				} else {
					IGroupWiki group = buildGroup(groupNode, groupName);
					this.m_groups.put(groupName, group);
				}
			}
		}
	}

	private long m_lastCheck = 0;
	private long m_lastModified = 0;

	private void checkForRefresh() {
		long time = System.currentTimeMillis();

		if (time - this.m_lastCheck > 60 * 1000L) {
			long lastModified = this.m_file.lastModified();

			if (lastModified > this.m_lastModified) {
				try {
					buildDOM();
				} catch (WikiSecurityException e) {
					log.error("Could not refresh DOM", e);
				}
			}
		}
	}

	/**
	 * Constructs a Group based on a DOM group node.
	 * 
	 * @param groupNode
	 *            the node in the DOM containing the node
	 * @param name
	 *            the name of the group
	 * @throws NoSuchPrincipalException
	 * @throws WikiSecurityException
	 */
	private IGroupWiki buildGroup(Element groupNode, String name) {
		// It's an error if either param is null (very odd)
		if (groupNode == null || name == null) {
			throw new IllegalArgumentException("DOM element or name cannot be null.");
		}

		// Construct a new group
		GroupWiki group = new GroupWiki(name, this.m_engine.getWikiConfiguration().getApplicationName());

		// Get the users for this group, and add them
		NodeList members = groupNode.getElementsByTagName(MEMBER_TAG);
		for (int i = 0; i < members.getLength(); i++) {
			Element memberNode = (Element) members.item(i);
			String principalName = memberNode.getAttribute(PRINCIPAL);
			Principal member = new WikiPrincipal(principalName);
			group.add(member);
		}

		// Add the created/last-modified info
		String creator = groupNode.getAttribute(CREATOR);
		String created = groupNode.getAttribute(CREATED);
		String modifier = groupNode.getAttribute(MODIFIER);
		String modified = groupNode.getAttribute(LAST_MODIFIED);
		try {
			group.setCreated(this.m_format.parse(created));
			group.setLastModified(this.m_format.parse(modified));
		} catch (ParseException e) {
			// If parsing failed, use the platform default
			try {
				group.setCreated(this.m_defaultFormat.parse(created));
				group.setLastModified(this.m_defaultFormat.parse(modified));
			} catch (ParseException e2) {
				log.warn("Could not parse 'created' or 'lastModified' " + "attribute for " + " group'" + group.getName()
						+ "'." + " It may have been tampered with.");
			}
		}
		group.setCreator(creator);
		group.setModifier(modifier);
		return group;
	}

	private void saveDOM() throws WikiSecurityException {
		if (this.m_dom == null) {
			log.fatal("Group database doesn't exist in memory.");
		}

		File newFile = new File(this.m_file.getAbsolutePath() + ".new");
		try (BufferedWriter io = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "UTF-8"));) {

			// Write the file header and document root
			io.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			io.write("<groups>\n");

			// Write each profile as a <group> node
			for (IGroupWiki group : this.m_groups.values()) {
				io.write("  <" + GROUP_TAG + " ");
				io.write(GROUP_NAME);
				io.write("=\"" + StringEscapeUtils.escapeXml(group.getName()) + "\" ");
				io.write(CREATOR);
				io.write("=\"" + StringEscapeUtils.escapeXml(group.getCreator()) + "\" ");
				io.write(CREATED);
				io.write("=\"" + this.m_format.format(group.getCreated()) + "\" ");
				io.write(MODIFIER);
				io.write("=\"" + group.getModifier() + "\" ");
				io.write(LAST_MODIFIED);
				io.write("=\"" + this.m_format.format(group.getLastModified()) + "\"");
				io.write(">\n");

				// Write each member as a <member> node
				for (Principal member : group.members()) {
					io.write("    <" + MEMBER_TAG + " ");
					io.write(PRINCIPAL);
					io.write("=\"" + StringEscapeUtils.escapeXml(member.getName()) + "\" ");
					io.write("/>\n");
				}

				// Close tag
				io.write("  </" + GROUP_TAG + ">\n");
			}
			io.write("</groups>");
			io.close();
		} catch (IOException e) {
			throw new WikiSecurityException(e.getLocalizedMessage(), e);
		}

		// Copy new file over old version
		File backup = new File(this.m_file.getAbsolutePath() + ".old");
		if (backup.exists() && !backup.delete()) {
			log.error("Could not delete old group database backup: " + backup);
		}
		if (!this.m_file.renameTo(backup)) {
			log.error("Could not create group database backup: " + backup);
		}
		if (!newFile.renameTo(this.m_file)) {
			log.error("Could not save database: " + backup + " restoring backup.");
			if (!backup.renameTo(this.m_file)) {
				log.error("Restore failed. Check the file permissions.");
			}
			log.error("Could not save database: " + this.m_file + ". Check the file permissions");
		}
	}

	@Override
	public void initialize(Engine engine) throws NoRequiredPropertyException, WikiSecurityException {
		m_engine = engine;
		// TODO Auto-generated method stub
		
	}

}
