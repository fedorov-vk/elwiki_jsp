package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;

import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class PageAttachmentDeserializer extends DeserialiseStuff implements JsonDeserializer<PageAttachment> {

	@SuppressWarnings("unused")
	@Override
	public PageAttachment deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		PageAttachment ageAttachment = Elwiki_dataFactory.eINSTANCE.createPageAttachment();
		
		int version = getInt(jsonObject, "version");
		String lastModify = getString(jsonObject, "lastModify");
		String author = getString(jsonObject, "author");
		String changeNote = getString(jsonObject, "changeNote");
		String id = getString(jsonObject, "id");
		String name = getString(jsonObject, "name");
		boolean cacheable = getBoolean(jsonObject, "cacheable");
		String place = getString(jsonObject, "place");
		long size = getLong(jsonObject, "size");

		ageAttachment.setVersion(version);
		//TODO: pageContent.setLastModify(lastModify);
		ageAttachment.setAuthor(author);
		ageAttachment.setChangeNote(changeNote);
		ageAttachment.setId(id);
		ageAttachment.setName(name);
		ageAttachment.setCacheable(cacheable);
		ageAttachment.setPlace(place);
		ageAttachment.setSize(size);
		
		return ageAttachment;
	}

}
