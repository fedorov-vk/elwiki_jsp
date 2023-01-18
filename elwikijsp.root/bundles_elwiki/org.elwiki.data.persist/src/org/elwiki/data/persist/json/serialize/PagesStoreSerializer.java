package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;

import org.elwiki.data.persist.json.PagesStoreAttributes;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PagesStoreSerializer implements JsonSerializer<PagesStore> {

	@Override
	public JsonElement serialize(PagesStore pagesStore, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(PagesStoreAttributes.MAIN_PAGE_ID, pagesStore.getMainPageId());
		result.addProperty(PagesStoreAttributes.NEXT_PAGE_ID, pagesStore.getNextPageId());

		JsonArray pages = new JsonArray();
		result.add(PagesStoreAttributes.PAGES, pages);
		for (WikiPage page : pagesStore.getWikipages()) {
			pages.add(context.serialize(page, WikiPage.class));
		}

		return result;
	}

}
