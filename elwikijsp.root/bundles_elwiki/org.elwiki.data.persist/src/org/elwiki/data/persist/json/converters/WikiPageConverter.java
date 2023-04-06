package org.elwiki.data.persist.json.converters;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EList;
import org.elwiki_data.AclInfo;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WikiPageConverter extends DeserialiseStuff
		implements JsonSerializer<WikiPage>, JsonDeserializer<WikiPage> {

	private static String ID = "id";
	private static String NAME = "name";
	private static String DESCRIPTION = "description";
	private static String ALIAS = "alias";
	private static String REDIRECT = "redirect";
	private static String VIEW_COUNT = "viewCount";
	private static String WIKI = "wiki";
	private static String REFERENCES = "references";
	private static String CHILDREN = "children";
	private static String ATTRIBUTES = "attributes";
	private static String CONTENTS = "contents";
	private static String ATTACHMENTS = "attachments";
	private static String ACL_INFOS = "aclInfos";

	@Override
	public JsonElement serialize(WikiPage wikiPage, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();

		result.addProperty(ID, wikiPage.getId());
		result.addProperty(NAME, wikiPage.getName());
		result.addProperty(DESCRIPTION, wikiPage.getDescription());
		result.addProperty(ALIAS, wikiPage.getAlias());
		result.addProperty(REDIRECT, wikiPage.getRedirect());
		result.addProperty(VIEW_COUNT, wikiPage.getViewCount());
		result.addProperty(WIKI, wikiPage.getWiki());

		JsonArray references = new JsonArray();
		result.add(REFERENCES, references);
		for (PageReference reference : wikiPage.getPageReferences()) {
			references.add(context.serialize(reference, PageReference.class));
		}

		JsonArray children = new JsonArray();
		result.add(CHILDREN, children);
		for (WikiPage child : wikiPage.getChildren()) {
			children.add(context.serialize(child, WikiPage.class));
		}

		JsonArray attributes = new JsonArray();
		result.add(ATTRIBUTES, attributes);
		for (Entry<String, Object> attribute : wikiPage.getAttributes()) {
			JsonObject jsonAttribute = new JsonObject();
			jsonAttribute.addProperty(attribute.getKey(), attribute.getValue().toString());
			attributes.add(jsonAttribute);
		}

		JsonArray contents = new JsonArray();
		result.add(CONTENTS, contents);
		for (PageContent content : wikiPage.getPageContents()) {
			contents.add(context.serialize(content, PageContent.class));
		}

		JsonArray attachments = new JsonArray();
		result.add(ATTACHMENTS, attachments);
		for (PageAttachment attachment : wikiPage.getAttachments()) {
			attachments.add(context.serialize(attachment, PageAttachment.class));
		}

		JsonArray aclInfos = new JsonArray();
		result.add(ACL_INFOS, aclInfos);
		for (AclInfo aclInfo : wikiPage.getAclInfos()) {
			aclInfos.add(context.serialize(aclInfo, AclInfo.class));
		}

		return result;
	}

	@Override
	public WikiPage deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		WikiPage wikiPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();

		String id = getString(jsonObject, ID);
		String name = getString(jsonObject, NAME);
		String description = getString(jsonObject, DESCRIPTION);
		String alias = getString(jsonObject, ALIAS);
		String redirect = getString(jsonObject, REDIRECT);
		int viewCount = getInt(jsonObject, VIEW_COUNT);
		String wiki = getString(jsonObject, WIKI);

		wikiPage.setId(id);
		wikiPage.setName(name);
		wikiPage.setDescription(description);
		wikiPage.setAlias(alias);
		wikiPage.setRedirect(redirect);
		wikiPage.setViewCount(viewCount);
		wikiPage.setWiki(wiki);

		EList<PageReference> pageReferences = wikiPage.getPageReferences();
		for (JsonElement jsonRef : getArray(jsonObject, REFERENCES)) {
			PageReference pageReference = context.deserialize(jsonRef, PageReference.class);
			if (pageReference != null) {
				pageReferences.add(pageReference);
			}
		}

		EList<WikiPage> children = wikiPage.getChildren();
		for (JsonElement jsonPage : getArray(jsonObject, CHILDREN)) {
			WikiPage childWikiPage = context.deserialize(jsonPage, WikiPage.class);
			children.add(childWikiPage);
		}

		for (JsonElement jsonAttrib : getArray(jsonObject, ATTRIBUTES)) {
			for (Map.Entry<String, JsonElement> entry : jsonAttrib.getAsJsonObject().entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			//TODO: :FVK: JSON restore attributes.
		}

		EList<PageContent> contents = wikiPage.getPageContents();
		for (JsonElement jsonContent : getArray(jsonObject, CONTENTS)) {
			PageContent pageContent = context.deserialize(jsonContent, PageContent.class);
			contents.add(pageContent);
		}

		EList<PageAttachment> attachments = wikiPage.getAttachments();
		for (JsonElement jsonAttachment : getArray(jsonObject, ATTACHMENTS)) {
			PageAttachment pageAttachment = context.deserialize(jsonAttachment, PageAttachment.class);
			if (pageAttachment != null) {
				attachments.add(pageAttachment);
			}
		}

		EList<AclInfo> aclInfos = wikiPage.getAclInfos();
		for (JsonElement jsonAclInfos : getArray(jsonObject, ACL_INFOS)) {
			AclInfo aclInfo = context.deserialize(jsonAclInfos, AclInfo.class);
			if (aclInfo != null) {
				aclInfos.add(aclInfo);
			}
		}

		return wikiPage;
	}

}
