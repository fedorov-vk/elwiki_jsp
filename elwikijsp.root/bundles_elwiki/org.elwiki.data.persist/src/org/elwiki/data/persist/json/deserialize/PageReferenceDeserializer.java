package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;

import org.elwiki.data.persist.json.PageReferenceAttributes;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageReference;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PageReferenceDeserializer extends DeserialiseStuff implements JsonDeserializer<PageReference> {

	@Override
	public PageReference deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		PageReference pageReference = Elwiki_dataFactory.eINSTANCE.createPageReference();

		String pageId = getString(jsonObject, PageReferenceAttributes.PAGE_ID);

		pageReference.setPageId(pageId);

		return pageReference;
	}

}
