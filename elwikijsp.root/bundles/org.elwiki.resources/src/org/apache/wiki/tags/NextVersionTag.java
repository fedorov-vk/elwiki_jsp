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

import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.providers.WikiProvider;

/**
 * Writes the version number of the next version of the page.
 *
 * @since 2.2
 */
public class NextVersionTag extends BaseWikiTag {

	private static final long serialVersionUID = 8072981822263682521L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int doWikiStartTag() throws IOException, ProviderException, JspTagException {
		int version = getWikiContext().getPageVersion();
		if (version != WikiProvider.LATEST_VERSION) {
			version++;
		}

		pageContext.getOut().print(version);
		return SKIP_BODY;
	}

}
