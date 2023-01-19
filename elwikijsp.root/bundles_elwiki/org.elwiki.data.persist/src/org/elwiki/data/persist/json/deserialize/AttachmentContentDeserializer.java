package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;

import org.elwiki.data.persist.json.AttachmentContentAttributes;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class AttachmentContentDeserializer extends DeserialiseStuff implements JsonDeserializer<AttachmentContent> {

	@SuppressWarnings("unused")
	@Override
	public AttachmentContent deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		AttachmentContent attachmentContent = Elwiki_dataFactory.eINSTANCE.createAttachmentContent();

		short version = getShort(jsonObject, AttachmentContentAttributes.VERSION);
		String creationDate = getString(jsonObject, AttachmentContentAttributes.CREATION_DATE);
		String author = getString(jsonObject, AttachmentContentAttributes.AUTHOR);
		String changeNote = getString(jsonObject, AttachmentContentAttributes.CHANGE_NOTE);
		String content = getString(jsonObject, AttachmentContentAttributes.PLACE);
		long size = getLong(jsonObject, AttachmentContentAttributes.SIZE);

		attachmentContent.setVersion(version);
		//TODO: attachmentContent.setLastModify(lastModify);
		attachmentContent.setAuthor(author);
		attachmentContent.setChangeNote(changeNote);
		attachmentContent.setPlace(content);
		attachmentContent.setSize(size);

		return attachmentContent;
	}

}
