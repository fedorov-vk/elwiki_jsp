package org.apache.wiki.search.lucene;

import org.apache.wiki.api.search.SearchResult;
import org.elwiki_data.WikiPage;

class SearchResultImpl implements SearchResult {

	private WikiPage m_page;
	private int m_score;
	private String[] m_contexts;

	public SearchResultImpl(final WikiPage page, final int score, final String[] contexts) {
		m_page = page;
		m_score = score;
		m_contexts = contexts != null ? contexts.clone() : null;
	}

	@Override
	public WikiPage getPage() {
		return m_page;
	}

	@Override
	public String getPageId() {
		return m_page.getId();
	}

	/* (non-Javadoc)
	 * @see org.apache.wiki.SearchResult#getScore()
	 */
	@Override
	public int getScore() {
		return m_score;
	}

	@Override
	public String[] getContexts() {
		return m_contexts;
	}

}
