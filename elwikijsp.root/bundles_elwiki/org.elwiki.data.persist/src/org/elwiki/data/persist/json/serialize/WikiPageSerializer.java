package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.elwiki_data.Acl;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WikiPageSerializer implements JsonSerializer<WikiPage> {

	@Override
	public JsonElement serialize(WikiPage wikiPage, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty("id", wikiPage.getId());
		result.addProperty("name", wikiPage.getName());
		result.addProperty("description", wikiPage.getDescription());
		result.addProperty("alias", wikiPage.getAlias());
		result.addProperty("redirect", wikiPage.getRedirect());
		result.addProperty("viewCount", wikiPage.getViewCount());
		result.addProperty("wiki", wikiPage.getWiki());

		JsonArray references = new JsonArray();
		result.add("references", references);
		for (PageReference reference : wikiPage.getPageReferences()) {
			references.add(new JsonPrimitive(reference.getPageId()));
		}

		JsonArray children = new JsonArray();
		result.add("children", children);
		for (WikiPage child : wikiPage.getChildren()) {
			children.add(context.serialize(child, WikiPage.class));
						//new JsonPrimitive(child.getId()));
		}

		JsonArray attributes = new JsonArray();
		result.add("attributes", attributes);
		for (Entry<String, Object> attribute : wikiPage.getAttributes()) {
			JsonObject jsonAttribute = new JsonObject();
			jsonAttribute.addProperty(attribute.getKey(), attribute.getValue().toString());
			attributes.add(jsonAttribute);
		}

		JsonArray contents = new JsonArray();
		result.add("contents", contents);
		for (PageContent content : wikiPage.getPageContents()) {
			contents.add(context.serialize(content, PageContent.class));
		}

		JsonArray attachments = new JsonArray();
		result.add("attachments", attachments);
		for (PageAttachment attachment : wikiPage.getAttachments()) {
			attachments.add(context.serialize(attachment, PageAttachment.class));
		}

		Acl pageAcl = wikiPage.getAcl();
		result.add("acl", context.serialize(pageAcl, Acl.class));

		return result;
	}

}
