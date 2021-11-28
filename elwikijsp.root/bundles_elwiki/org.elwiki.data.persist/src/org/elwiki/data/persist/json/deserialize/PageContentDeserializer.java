package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;

import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageContent;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PageContentDeserializer extends DeserialiseStuff implements JsonDeserializer<PageContent> {

	@SuppressWarnings("unused")
	@Override
	public PageContent deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		PageContent pageContent = Elwiki_dataFactory.eINSTANCE.createPageContent();

		int version = getInt(jsonObject, "version");
		String lastModify = getString(jsonObject, "lastModify");
		String author = getString(jsonObject, "author");
		String changeNote = getString(jsonObject, "changeNote");
		String content = getString(jsonObject, "content");

		pageContent.setVersion(version);
		//TODO: pageContent.setLastModify(lastModify);
		pageContent.setAuthor(author);
		pageContent.setChangeNote(changeNote);
		pageContent.setContent(content);		
		
		return pageContent;
	}

}
