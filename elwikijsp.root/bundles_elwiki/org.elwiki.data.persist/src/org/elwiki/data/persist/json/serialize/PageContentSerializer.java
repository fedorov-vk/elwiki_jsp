package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.GregorianCalendar;

import org.elwiki_data.PageContent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageContentSerializer implements JsonSerializer<PageContent> {

	@Override
	public JsonElement serialize(PageContent pageContent, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty("version", pageContent.getVersion());
		Date lastModify = pageContent.getLastModify();
		if (lastModify == null) {
			lastModify = new GregorianCalendar(1972, 1, 12).getTime();
		}
		result.addProperty("lastModify", lastModify.toString());
		result.addProperty("author", pageContent.getAuthor());
		result.addProperty("changeNote", pageContent.getChangeNote());
		result.addProperty("content", pageContent.getContent());

		return result;
	}

}
