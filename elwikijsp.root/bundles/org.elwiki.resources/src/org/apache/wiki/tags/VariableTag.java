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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;

import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoSuchVariableException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.variables.VariableManager;
import org.apache.wiki.pages0.PageManager;
import org.apache.wiki.util.TextUtil;

/**
 * Returns the value of an Wiki variable.
 *
 * <p>
 * <b>Attributes</b>
 * </p>
 * <ul>
 * <li>var - Name of the variable. Required.
 * <li>default - Revert to this value, if the value of "var" is null. If left
 * out, this tag will produce a concise error message if the named variable is
 * not found. Set to empty (default="") to hide the message.
 * </ul>
 *
 * <p>
 * A default value implies <i>failmode='quiet'</i>.
 *
 * @since 2.0
 */
public class VariableTag extends BaseWikiTag {

	private static final long serialVersionUID = -2120534004669429093L;

	private String m_var = null;
	private String m_default = null;

	@Override
	public void initTag() {
		super.initTag();
		m_var = m_default = null;
	}

	public String getVar() {
		return m_var;
	}

	public void setVar(final String arg) {
		m_var = arg;
	}

	public void setDefault(final String arg) {
		m_default = arg;
	}

	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		WikiContext wikiContext = getWikiContext();
		Engine engine = wikiContext.getEngine();
		VariableManager variableManager = engine.getManager(VariableManager.class);

		JspWriter out = pageContext.getOut();
		String msg = null;
		String value = null;

		try {
			value = variableManager.getValue(wikiContext, getVar());
		} catch (final NoSuchVariableException e) {
			msg = "No such variable: " + e.getMessage();
		} catch (final IllegalArgumentException e) {
			msg = "Incorrect variable name: " + e.getMessage();
		}

		if (value == null) {
			value = m_default;
		}

		if (value == null) {
			value = msg;
		}

		out.write(TextUtil.replaceEntities(value));
		return SKIP_BODY;
	}

}
