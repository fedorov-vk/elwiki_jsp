package org.elwiki.data.persist.json.serialize;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import org.elwiki.data.persist.json.PageAttributes;
import org.elwiki_data.Acl;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WikiPageSerializer implements JsonSerializer<WikiPage> {

	@Override
	public JsonElement serialize(WikiPage wikiPage, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(PageAttributes.ID, wikiPage.getId());
		result.addProperty(PageAttributes.LAST_VERSION, wikiPage.getLastVersion());
		result.addProperty(PageAttributes.NAME, wikiPage.getName());
		result.addProperty(PageAttributes.DESCRIPTION, wikiPage.getDescription());
		result.addProperty(PageAttributes.ALIAS, wikiPage.getAlias());
		result.addProperty(PageAttributes.REDIRECT, wikiPage.getRedirect());
		result.addProperty(PageAttributes.VIEW_COUNT, wikiPage.getViewCount());
		result.addProperty(PageAttributes.WIKI, wikiPage.getWiki());

		JsonArray references = new JsonArray();
		result.add(PageAttributes.REFERENCES, references);
		for (PageReference reference : wikiPage.getPageReferences()) {
			references.add(context.serialize(reference, PageReference.class));
		}

		JsonArray children = new JsonArray();
		result.add(PageAttributes.CHILDREN, children);
		for (WikiPage child : wikiPage.getChildren()) {
			children.add(context.serialize(child, WikiPage.class));
		}

		JsonArray attributes = new JsonArray();
		result.add(PageAttributes.ATTRIBUTES, attributes);
		for (Entry<String, Object> attribute : wikiPage.getAttributes()) {
			JsonObject jsonAttribute = new JsonObject();
			jsonAttribute.addProperty(attribute.getKey(), attribute.getValue().toString());
			attributes.add(jsonAttribute);
		}

		JsonArray contents = new JsonArray();
		result.add(PageAttributes.CONTENTS, contents);
		for (PageContent content : wikiPage.getPageContents()) {
			contents.add(context.serialize(content, PageContent.class));
		}

		JsonArray attachments = new JsonArray();
		result.add(PageAttributes.ATTACHMENTS, attachments);
		for (PageAttachment attachment : wikiPage.getAttachments()) {
			attachments.add(context.serialize(attachment, PageAttachment.class));
		}

		Acl pageAcl = wikiPage.getAcl();
		result.add("acl", context.serialize(pageAcl, Acl.class));

		return result;
	}

}
