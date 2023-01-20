package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;

import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageReference;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageReferenceConverter extends DeserialiseStuff
		implements JsonSerializer<PageReference>, JsonDeserializer<PageReference> {

	private static String PAGE_ID = "id";

	@Override
	public JsonElement serialize(PageReference pageReference, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(PAGE_ID, pageReference.getPageId());

		return result;
	}

	@Override
	public PageReference deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		PageReference pageReference = Elwiki_dataFactory.eINSTANCE.createPageReference();

		String pageId = getString(jsonObject, PAGE_ID);

		pageReference.setPageId(pageId);

		return pageReference;
	}

}
