package org.elwiki.data.persist.json.deserialize;

import java.lang.reflect.Type;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.elwiki.data.persist.json.PageAttributes;
import org.elwiki_data.Acl;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class WikiPageDeserializer extends DeserialiseStuff implements JsonDeserializer<WikiPage> {

	@Override
	public WikiPage deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		WikiPage wikiPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();

		String id = getString(jsonObject, PageAttributes.ID);
		short lastVersion = getShort(jsonObject, PageAttributes.LAST_VERSION);
		String name = getString(jsonObject, PageAttributes.NAME);
		String description = getString(jsonObject, PageAttributes.DESCRIPTION);
		String alias = getString(jsonObject, PageAttributes.ALIAS);
		String redirect = getString(jsonObject, PageAttributes.REDIRECT);
		int viewCount = getInt(jsonObject, PageAttributes.VIEW_COUNT);
		String wiki = getString(jsonObject, PageAttributes.WIKI);

		wikiPage.setId(id);
		wikiPage.setLastVersion(lastVersion);
		wikiPage.setName(name);
		wikiPage.setDescription(description);
		wikiPage.setAlias(alias);
		wikiPage.setRedirect(redirect);
		wikiPage.setViewCount(viewCount);
		wikiPage.setWiki(wiki);

		EList<PageReference> pageReferences = wikiPage.getPageReferences();
		for (JsonElement jsonRef : getArray(jsonObject, PageAttributes.REFERENCES)) {
			PageReference pageReference = context.deserialize(jsonRef, PageReference.class);
			if (pageReference != null) {
				pageReferences.add(pageReference);
			}
		}

		EList<WikiPage> children = wikiPage.getChildren();
		for (JsonElement jsonPage : getArray(jsonObject, PageAttributes.CHILDREN)) {
			WikiPage childWikiPage = context.deserialize(jsonPage, WikiPage.class);
			children.add(childWikiPage);
		}

		for (JsonElement jsonAttrib : getArray(jsonObject, PageAttributes.ATTRIBUTES)) {
			for (Map.Entry<String, JsonElement> entry : jsonAttrib.getAsJsonObject().entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			//TODO: :FVK: JSON restore attributes.
		}

		EList<PageContent> contents = wikiPage.getPageContents();
		for (JsonElement jsonContent : getArray(jsonObject, PageAttributes.CONTENTS)) {
			PageContent pageContent = context.deserialize(jsonContent, PageContent.class);
			contents.add(pageContent);
		}

		EList<PageAttachment> attachments = wikiPage.getAttachments();
		for (JsonElement jsonAttachment : getArray(jsonObject, PageAttributes.ATTACHMENTS)) {
			PageAttachment pageAttachment = context.deserialize(jsonAttachment, PageAttachment.class);
			if (pageAttachment != null) {
				attachments.add(pageAttachment);
			}
		}

		if (jsonObject.has("acl")) {
			Acl acl = context.deserialize(jsonObject.get("acl"), Acl.class);
			wikiPage.setAcl(acl);
		}

		return wikiPage;
	}

}
