package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;

import org.eclipse.emf.common.util.EList;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageAttachmentConverter extends DeserialiseStuff
		implements JsonSerializer<PageAttachment>, JsonDeserializer<PageAttachment> {

	private static String ID = "id";
	private static String NAME = "name";
	private static String LAST_VERSION = "lastVersion";
	private static String ATTACH_CONTENTS = "attachContents";

	@Override
	public JsonElement serialize(PageAttachment pageAttachment, Type arg1, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(ID, pageAttachment.getId());
		result.addProperty(NAME, pageAttachment.getName());
		result.addProperty(LAST_VERSION, pageAttachment.getLastVersion());

		JsonArray references = new JsonArray();
		result.add(ATTACH_CONTENTS, references);
		for (AttachmentContent attchmentContent : pageAttachment.getAttachContents()) {
			references.add(context.serialize(attchmentContent, AttachmentContent.class));
		}

		return result;
	}

	@Override
	public PageAttachment deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		PageAttachment pageAttachment = Elwiki_dataFactory.eINSTANCE.createPageAttachment();

		String id = getString(jsonObject, ID);
		String name = getString(jsonObject, NAME);
		int lastVersion = getInt(jsonObject, LAST_VERSION);

		EList<AttachmentContent> contents = pageAttachment.getAttachContents();
		for (JsonElement jsonContent : getArray(jsonObject, ATTACH_CONTENTS)) {
			AttachmentContent attachmentContent = context.deserialize(jsonContent, AttachmentContent.class);
			contents.add(attachmentContent);
		}

		pageAttachment.setId(id);
		pageAttachment.setName(name);
		pageAttachment.setLastVersion(lastVersion);

		return pageAttachment;
	}

}
