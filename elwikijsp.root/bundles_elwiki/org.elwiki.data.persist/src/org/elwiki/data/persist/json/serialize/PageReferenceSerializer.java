package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;

import org.elwiki.data.persist.json.PageReferenceAttributes;
import org.elwiki_data.PageReference;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageReferenceSerializer implements JsonSerializer<PageReference> {

	@Override
	public JsonElement serialize(PageReference pageReference, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(PageReferenceAttributes.PAGE_ID, pageReference.getPageId());

		return result;
	}

}
