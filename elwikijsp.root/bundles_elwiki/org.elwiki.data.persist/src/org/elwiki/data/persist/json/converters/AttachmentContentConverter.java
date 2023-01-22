package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.GregorianCalendar;

import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AttachmentContentConverter extends DeserialiseStuff
		implements JsonSerializer<AttachmentContent>, JsonDeserializer<AttachmentContent> {

	String VERSION = "version";
	String CREATION_DATE = "creationDate";
	String AUTHOR = "author";
	String CHANGE_NOTE = "changeNote";
	String PLACE = "place";
	String SIZE = "size";

	@Override
	public JsonElement serialize(AttachmentContent attachmentContent, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(VERSION, attachmentContent.getVersion());
		Date creationDate = attachmentContent.getCreationDate();
		if (creationDate == null) {
			creationDate = new GregorianCalendar(1972, 1, 12).getTime(); //:FVK: workaround.
		}
		result.add(CREATION_DATE, context.serialize(creationDate, Date.class));
		result.addProperty(AUTHOR, attachmentContent.getAuthor());
		result.addProperty(CHANGE_NOTE, attachmentContent.getChangeNote());
		result.addProperty(PLACE, attachmentContent.getPlace());
		result.addProperty(SIZE, attachmentContent.getSize());

		return result;
	}

	@Override
	public AttachmentContent deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		AttachmentContent attachmentContent = Elwiki_dataFactory.eINSTANCE.createAttachmentContent();

		int version = getInt(jsonObject, VERSION);
		Date creationDate = getDate(jsonObject, CREATION_DATE, context);
		String author = getString(jsonObject, AUTHOR);
		String changeNote = getString(jsonObject, CHANGE_NOTE);
		String content = getString(jsonObject, PLACE);
		long size = getLong(jsonObject, SIZE);

		attachmentContent.setVersion(version);
		attachmentContent.setCreationDate(creationDate);
		attachmentContent.setAuthor(author);
		attachmentContent.setChangeNote(changeNote);
		attachmentContent.setPlace(content);
		attachmentContent.setSize(size);

		return attachmentContent;
	}

}
