package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.GregorianCalendar;

import org.elwiki.data.persist.json.AttachmentContentAttributes;
import org.elwiki_data.AttachmentContent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AttachmentContentSerializer implements JsonSerializer<AttachmentContent> {

	@Override
	public JsonElement serialize(AttachmentContent attachmentContent, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(AttachmentContentAttributes.VERSION, attachmentContent.getVersion());
		Date creationDate = attachmentContent.getCreationDate();
		if (creationDate == null) {
			creationDate = new GregorianCalendar(1972, 1, 12).getTime(); //:FVK: workaround.
		}
		result.addProperty(AttachmentContentAttributes.CREATION_DATE, creationDate.toString());
		result.addProperty(AttachmentContentAttributes.AUTHOR, attachmentContent.getAuthor());
		result.addProperty(AttachmentContentAttributes.CHANGE_NOTE, attachmentContent.getChangeNote());
		result.addProperty(AttachmentContentAttributes.PLACE, attachmentContent.getPlace());
		result.addProperty(AttachmentContentAttributes.SIZE, attachmentContent.getSize());

		return result;
	}

}
