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
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.PluginException;
import org.elwiki.api.plugin.PluginManager;

/**
 * Inserts any Wiki plugin. The body of the tag becomes then the body for the
 * plugin.
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>plugin - name of the plugin you want to insert.
 * <li>args - An argument string for the tag.
 * </ul>
 *
 * @since 2.0
 */
public class PluginTag extends BaseWikiBodyTag {

	private static final long serialVersionUID = -4260389289439790369L;
	private static final Logger log = Logger.getLogger(PluginTag.class);

	private String m_plugin;
	private String m_args;

	private boolean m_evaluated = false;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		super.release();
		m_plugin = m_args = null;
		m_evaluated = false;
	}

	/**
	 * Set the name of the plugin to execute.
	 * 
	 * @param p Name of the plugin.
	 */
	public void setPlugin(final String p) {
		m_plugin = p;
	}

	/**
	 * Set the argument string to the plugin.
	 * 
	 * @param a Arguments string.
	 */
	public void setArgs(final String a) {
		m_args = a;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int doWikiStartTag() throws JspException, IOException {
		m_evaluated = false;
		return EVAL_BODY_BUFFERED;
	}

	private String executePlugin(final String plugin, final String args, final String body)
			throws PluginException, IOException {
		WikiContext wikiContext = getWikiContext();
		final Engine engine = wikiContext.getEngine();
		final PluginManager pluginManager = engine.getManager(PluginManager.class);

		m_evaluated = true;

		final Map<String, String> argmap = pluginManager.parseArgs(args);

		if (body != null) {
			argmap.put("_body", body);
		}

		return pluginManager.execute(wikiContext, plugin, argmap);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int doEndTag() throws JspException {
		if (!m_evaluated) {
			try {
				pageContext.getOut().write(executePlugin(m_plugin, m_args, null));
			} catch (final Exception e) {
				log.error("Failed to insert plugin", e);
				throw new JspException("Tag failed, check logs: " + e.getMessage());
			}
		}
		return EVAL_PAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int doAfterBody() throws JspException {
		try {
			final BodyContent bc = getBodyContent();

			getPreviousOut().write(executePlugin(m_plugin, m_args, (bc != null) ? bc.getString() : null));
		} catch (final Exception e) {
			log.error("Failed to insert plugin", e);
			throw new JspException("Tag failed, check logs: " + e.getMessage());
		}

		return SKIP_BODY;
	}

}
