package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;

import org.elwiki.data.persist.json.PageAttachmentAttributes;
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

		String name = getString(jsonObject, PageAttachmentAttributes.NAME);
		short lastVersion = getShort(jsonObject, PageAttachmentAttributes.LAST_VERSION);

		pageAttachment.setName(name);
		pageAttachment.setLastVersion(lastVersion);

		return pageAttachment;
	}

}
