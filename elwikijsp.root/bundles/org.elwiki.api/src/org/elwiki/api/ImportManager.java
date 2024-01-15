package org.elwiki.api;

import org.apache.wiki.api.core.WikiContext;
import org.elwiki.api.component.WikiManager;

public interface ImportManager extends WikiManager {

	void ImportPages(WikiContext wikiContext) throws Exception;

}
