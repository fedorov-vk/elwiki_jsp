package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.GregorianCalendar;

import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageContent;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageContentConverter extends DeserialiseStuff
		implements JsonSerializer<PageContent>, JsonDeserializer<PageContent> {

	private static String VERSION = "version";
	private static String CREATION_DATE = "creationDate";
	private static String AUTHOR = "author";
	private static String CHANGE_NOTE = "changeNote";
	private static String CONTENT = "content";

	@Override
	public JsonElement serialize(PageContent pageContent, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(VERSION, pageContent.getVersion());
		Date creationDate = pageContent.getCreationDate();
		if (creationDate == null) {
			creationDate = new GregorianCalendar(1972, 1, 12).getTime(); //:FVK: workaround.
		}
		result.add(CREATION_DATE, context.serialize(creationDate, Date.class));
		result.addProperty(AUTHOR, pageContent.getAuthor());
		result.addProperty(CHANGE_NOTE, pageContent.getChangeNote());
		result.addProperty(CONTENT, pageContent.getContent());

		return result;
	}

	@Override
	public PageContent deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		PageContent pageContent = Elwiki_dataFactory.eINSTANCE.createPageContent();

		short version = getShort(jsonObject, VERSION);
		Date creationDate = getDate(jsonObject, CREATION_DATE, context);
		String author = getString(jsonObject, AUTHOR);
		String changeNote = getString(jsonObject, CHANGE_NOTE);
		String content = getString(jsonObject, CONTENT);

		pageContent.setVersion(version);
		pageContent.setCreationDate(creationDate);
		pageContent.setAuthor(author);
		pageContent.setChangeNote(changeNote);
		pageContent.setContent(content);

		return pageContent;
	}

}
