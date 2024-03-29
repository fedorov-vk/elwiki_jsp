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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.wiki.ui.admin0.AdminBean;
import org.apache.wiki.ui.admin0.AdminBeanManager;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Provides an iterator for all AdminBeans of a given type.
 *
 */
public class AdminBeanIteratorTag extends BaseIteratorTag<AdminBean> {

	private static final long serialVersionUID = -2043505811106523815L;

	private int m_type;

	/**
	 * Set the type of the bean.
	 * 
	 * @param type Type to set
	 */
	public void setType(final String type) {
		@NonNull
		AdminBeanManager adminBeanManager = getWikiContext().getEngine().getManager(AdminBeanManager.class);
		m_type = adminBeanManager.getTypeFromString(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetIterator() {
		@NonNull
		AdminBeanManager adminBeanManager = getWikiContext().getEngine().getManager(AdminBeanManager.class);
		final Collection<AdminBean> beans = adminBeanManager.getAllBeans();
		final ArrayList<AdminBean> typedBeans = new ArrayList<>();
		for (final AdminBean ab : beans) {
			if (ab.getType() == m_type) {
				typedBeans.add(ab);
			}
		}

		setList(typedBeans);
	}

}
