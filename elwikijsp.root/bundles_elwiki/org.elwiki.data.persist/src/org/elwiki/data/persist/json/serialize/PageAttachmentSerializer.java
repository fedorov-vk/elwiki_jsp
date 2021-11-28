package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;

import org.elwiki_data.PageAttachment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageAttachmentSerializer implements JsonSerializer<PageAttachment> {

	@Override
	public JsonElement serialize(PageAttachment pageAttachment, Type arg1, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();

		result.addProperty("version", pageAttachment.getVersion());
		result.addProperty("lastModify", pageAttachment.getLastModify().toString());
		result.addProperty("author", pageAttachment.getAuthor());
		result.addProperty("changeNote", pageAttachment.getChangeNote());
		result.addProperty("id", pageAttachment.getId());
		result.addProperty("name", pageAttachment.getName());
		result.addProperty("cacheable", pageAttachment.isCacheable());
		result.addProperty("place", pageAttachment.getPlace());
		result.addProperty("size", pageAttachment.getSize());

		return null;
	}

}
