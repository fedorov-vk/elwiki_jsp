package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;

import org.elwiki.data.persist.json.PageAttachmentAttributes;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class PageAttachmentSerializer implements JsonSerializer<PageAttachment> {

	@Override
	public JsonElement serialize(PageAttachment pageAttachment, Type arg1, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(PageAttachmentAttributes.NAME, pageAttachment.getName());
		result.addProperty(PageAttachmentAttributes.LAST_VERSION, pageAttachment.getLastVersion());

		JsonArray references = new JsonArray();
		result.add(PageAttachmentAttributes.ATTACH_CONTENTS, references);
		for (AttachmentContent attchmentContent : pageAttachment.getAttachContents()) {
			references.add(context.serialize(attchmentContent, AttachmentContent.class));
		}

		return result;
	}

}
