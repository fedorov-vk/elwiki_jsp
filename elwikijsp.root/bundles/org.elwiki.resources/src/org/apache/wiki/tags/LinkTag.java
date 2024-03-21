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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import org.apache.log4j.Logger;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.pages0.PageManager;
import org.elwiki.api.GlobalPreferences;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.WikiPage;

/**
 * Provides a generic link tag for all kinds of linking purposes.
 * <p>
 * If parameter <i>path</i> is defined, constructs a URL pointing to the specified path of URL
 * (command, file), under the baseURL known by the Engine. Any ParamTag name-value pairs contained
 * in the body are added to this URL to provide support for arbitrary JSP calls.
 * <p>
 */
public class LinkTag extends BaseWikiLinkTag implements ParamHandler, BodyTag {

	private static final long serialVersionUID = -1659028657261152566L;
	private static final Logger log = Logger.getLogger(LinkTag.class);

	private String m_version = null;
	private String m_cssClass = null;
	private String m_datamodal = null;

	private String m_path = null;
	private String m_style = null;
	private String m_title = null;
	private String m_target = null;
	private String m_compareToVersion = null;
	private String m_rel = null;
	private String m_ref = null;
	private String m_context = WikiContext.PAGE_VIEW;
	private String m_accesskey = null;
	private String m_tabindex = null;
	private String m_templatefile = null;

	private Map<String, String> m_containedParams;

	private BodyContent m_bodyContent;

	@Override
	public void initTag() {
		super.initTag();
		m_version = m_cssClass = m_style = m_title = m_target = null;
		m_compareToVersion = m_rel = m_path = m_ref = m_accesskey = null;
		m_tabindex = m_templatefile = null;
		m_context = ContextEnum.PAGE_VIEW.getRequestContext();
		m_containedParams = new HashMap<>();
	}

	public String getDatamodal() {
		return m_datamodal;
	}

	public void setDatamodal(String arg) {
		m_datamodal = arg;
	}

	public void setPath(final String path) {
		m_path = path;
	}

	public void setTemplatefile(final String key) {
		m_templatefile = key;
	}

	public void setAccessKey(final String key) {
		m_accesskey = key;
	}

	public String getVersion() {
		return m_version;
	}

	public void setVersion(final String arg) {
		m_version = arg;
	}

	public void setCssClass(final String arg) {
		m_cssClass = arg;
	}

	public void setStyle(final String style) {
		m_style = style;
	}

	public void setTitle(final String title) {
		m_title = title;
	}

	public void setTarget(final String target) {
		m_target = target;
	}

	public void setTabindex(final String tabindex) {
		m_tabindex = tabindex;
	}

	public void setCompareToVersion(final String ver) {
		m_compareToVersion = ver;
	}

	public void setRel(final String rel) {
		m_rel = rel;
	}

	public void setRef(final String ref) {
		m_ref = ref;
	}

	public void setContext(final String context) {
		m_context = context;
	}

	/**
	 * Support for ParamTag supplied parameters in body.
	 */
	@Override
	public void setContainedParameter(final String name, final String value) {
		if (name != null) {
			if (m_containedParams == null) {
				m_containedParams = new HashMap<>();
			}
			m_containedParams.put(name, value);
		}
	}

	/**
	 * This method figures out what kind of an URL should be output. It mirrors heavily on
	 * JSPWikiMarkupParser.handleHyperlinks();
	 *
	 * @return the URL
	 * @throws ProviderException
	 */
	private String figureOutURL() throws ProviderException {
		WikiContext wikiContext = getWikiContext();
		Engine engine = wikiContext.getEngine();
		GlobalPreferences globalPreferences = engine.getManager(GlobalPreferences.class);
		PageManager pageManager = engine.getManager(PageManager.class);
		String url = null;

		String params = (m_version != null) ? "version=" + getVersion() : null;
		params = addParamsForRecipient(params, m_containedParams);
		if (m_ref != null) {
			params += "#" + m_ref;
		}

		if (m_templatefile != null) {
			String template = globalPreferences.getTemplateDir();
			url = engine.getURL(ContextEnum.PAGE_NONE.getRequestContext(), "shapes/" + template + "/" + m_templatefile,
					params);
		} else if (m_path != null) {
			url = engine.getURL(ContextEnum.PAGE_NONE.getRequestContext(), m_path, params);
		} else if (getPageId() == null) {
			// Here - Id of page is not specified.
			url = makeBasicURL(m_context, pageManager.getMainPageId(), params);
		} else {
			// Here - Id of page is specified.
			url = makeBasicURL(m_context, getPageId(), params);
		}

		return url;
	}

	private String addParamsForRecipient(final String addTo, final Map<String, String> params) {
		if (params == null || params.size() == 0) {
			return addTo;
		}
		final StringBuilder buf = new StringBuilder();
		final Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
		while (it.hasNext()) {
			final Map.Entry<String, String> e = it.next();
			final String n = e.getKey();
			final String v = e.getValue();
			buf.append(n);
			buf.append("=");
			buf.append(v);
			if (it.hasNext()) {
				buf.append("&amp;");
			}
		}
		if (addTo == null) {
			return buf.toString();
		}
		if (!addTo.endsWith("&amp;")) {
			return addTo + "&amp;" + buf.toString();
		}
		return addTo + buf.toString();
	}

	private String makeBasicURL(final String context, String pageId, String parms) {
		WikiContext wikiContext = getWikiContext();
		final Engine engine = wikiContext.getEngine();
		PageManager pageManager = engine.getManager(PageManager.class);

		if (context.equals(ContextEnum.PAGE_DIFF.getRequestContext())) {
			int r1;
			int r2;

			if (DiffLinkTag.VER_LATEST.equals(getVersion())) {
				final WikiPage latest = pageManager.getPage(pageId, WikiProvider.LATEST_VERSION);

				r1 = latest.getLastVersion();
			} else if (DiffLinkTag.VER_PREVIOUS.equals(getVersion())) {
				r1 = wikiContext.getPageVersion() - 1;
				r1 = Math.max(r1, 1);
			} else if (DiffLinkTag.VER_CURRENT.equals(getVersion())) {
				r1 = wikiContext.getPageVersion();
			} else {
				r1 = Integer.parseInt(getVersion());
			}

			if (DiffLinkTag.VER_LATEST.equals(m_compareToVersion)) {
				final WikiPage latest = pageManager.getPage(pageId, WikiProvider.LATEST_VERSION);

				r2 = latest.getLastVersion();
			} else if (DiffLinkTag.VER_PREVIOUS.equals(m_compareToVersion)) {
				r2 = wikiContext.getPageVersion() - 1;
				r2 = Math.max(r2, 1);
			} else if (DiffLinkTag.VER_CURRENT.equals(m_compareToVersion)) {
				r2 = wikiContext.getPageVersion();
			} else {
				r2 = Integer.parseInt(m_compareToVersion);
			}

			parms = "r1=" + r1 + "&amp;r2=" + r2;
		}

		/*
		{//TODO: here should remove the addressing by the page name. The page ID should be used.
			//:FVK: workaround - вообще, в результате портирования - работа с именем страницы, как с параметром адресации - будет исключена.
			if (m_pageName != null) {
				return engine.getURL(m_context, m_pageName, parms);
			}
			return engine.getURL(m_context, getPageId(), parms);
		}
		*/

		// !!! NEW CODE --
		return engine.getURL(m_context, pageId, parms);
	}

	@Override
	public int doWikiStartTag() throws ProviderException, IOException, JspTagException {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() {
		try {
			final JspWriter out = super.pageContext.getOut();
			final String url = figureOutURL();
			final StringBuilder sb = new StringBuilder(20);

			BiConsumer<String, String> appender = (option, var) -> {
				if (var != null)
					sb.append(option + '"' + var + "\" ");
			};
			appender.accept("class=", m_cssClass);
			appender.accept("data-modal=", m_datamodal);
			appender.accept("style=", m_style);
			appender.accept("target=", m_target);
			appender.accept("title=", m_title);
			appender.accept("rel=", m_rel);
			appender.accept("accesskey=", m_accesskey);
			appender.accept("tabindex=", m_tabindex);

			// TODO: пересмотреть код - страница не наследует Attach
			PageManager pageManager = getWikiContext().getEngine().getManager(PageManager.class);
			AttachmentManager attachmentManager = getWikiContext().getEngine().getManager(AttachmentManager.class);
			if (m_pageName != null && pageManager.getPage(m_pageName) instanceof PageAttachment) {
				sb.append(attachmentManager.forceDownload(m_pageName) ? "download " : "");
			}

			switch (m_format) {
			case URL:
				out.print(url);
				break;
			default:
			case ANCHOR:
				out.print("<a " + sb.toString() + " href=\"" + url + "\">");
				break;
			}

			// Add any explicit body content. This is not the intended use of LinkTag,
			// but happens to be the way it has worked previously.
			if (m_bodyContent != null) {
				final String linktext = m_bodyContent.getString().trim();
				out.write(linktext);
			}

			// Finish off by closing opened anchor
			if (m_format == LinkFormat.ANCHOR) {
				out.print("</a>");
			}
		} catch (Exception e) {
			// Yes, we want to catch all exceptions here, including RuntimeExceptions
			log.error("Tag failed", e);
		}

		return EVAL_PAGE;
	}

	@Override
	public void setBodyContent(final BodyContent bc) {
		m_bodyContent = bc;
	}

	@Override
	public void doInitBody() {
	}

}
