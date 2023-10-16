package org.elwiki.api;

import org.apache.wiki.api.core.WikiContext;

public interface ImportManager {

	void ImportPages(WikiContext wikiContext) throws Exception;

}
