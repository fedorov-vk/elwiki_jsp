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

		PageAttachment pageAttachment = Elwiki_dataFactory.eINSTANCE.createPageAttachment();

		short version = (short) getInt(jsonObject, "version");
		String lastModify = getString(jsonObject, "lastModify");
		String author = getString(jsonObject, "author");
		String changeNote = getString(jsonObject, "changeNote");
		String id = getString(jsonObject, "id");
		String name = getString(jsonObject, "name");
		boolean cacheable = getBoolean(jsonObject, "cacheable");
		String place = getString(jsonObject, "place");
		long size = getLong(jsonObject, "size");

		/*:FVK:
		pageAttachment.setVersion(version);
		//TODO: pageContent.setLastModify(lastModify);
		pageAttachment.setAuthor(author);
		pageAttachment.setChangeNote(changeNote);
		pageAttachment.setId(id);
		pageAttachment.setName(name);
		pageAttachment.setCacheable(cacheable);
		pageAttachment.setPlace(place);
		pageAttachment.setSize(size);
		*/
		
		return pageAttachment;
	}

}
