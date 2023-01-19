package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.GregorianCalendar;

import org.elwiki.data.persist.json.PageContentAttributes;
import org.elwiki_data.PageContent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageContentSerializer implements JsonSerializer<PageContent> {

	@Override
	public JsonElement serialize(PageContent pageContent, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(PageContentAttributes.VERSION, pageContent.getVersion());
		Date creationDate = pageContent.getCreationDate();
		if (creationDate == null) {
			creationDate = new GregorianCalendar(1972, 1, 12).getTime(); //:FVK: workaround.
		}
		result.addProperty(PageContentAttributes.CREATION_DATE, creationDate.toString());
		result.addProperty(PageContentAttributes.AUTHOR, pageContent.getAuthor());
		result.addProperty(PageContentAttributes.CHANGE_NOTE, pageContent.getChangeNote());
		result.addProperty(PageContentAttributes.CONTENT, pageContent.getContent());

		return result;
	}

}
