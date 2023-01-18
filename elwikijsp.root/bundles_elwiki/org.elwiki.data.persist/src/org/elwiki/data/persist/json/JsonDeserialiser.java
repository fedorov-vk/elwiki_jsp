package org.elwiki.data.persist.json;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.eclipse.core.runtime.IPath;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.persist.json.deserialize.AclDeserializer;
import org.elwiki.data.persist.json.deserialize.AttachmentContentDeserializer;
import org.elwiki.data.persist.json.deserialize.PageAttachmentDeserializer;
import org.elwiki.data.persist.json.deserialize.PageContentDeserializer;
import org.elwiki.data.persist.json.deserialize.PageReferenceDeserializer;
import org.elwiki.data.persist.json.deserialize.PagesStoreDeserializer;
import org.elwiki.data.persist.json.deserialize.WikiPageDeserializer;
import org.elwiki_data.Acl;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonDeserialiser {

	@SuppressWarnings("unused")
	private IWikiConfiguration wikiConfiguration;
	private IPath workPath;

	public JsonDeserialiser(IWikiConfiguration wikiConfiguration) {
		this.wikiConfiguration = wikiConfiguration;
		this.workPath = wikiConfiguration.getWorkDir();
	}

	public void LoadData() throws IOException {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting() //@formatter:off
				.registerTypeAdapter(PagesStore.class, new PagesStoreDeserializer())
				.registerTypeAdapter(WikiPage.class, new WikiPageDeserializer())
				.registerTypeAdapter(PageReference.class, new PageReferenceDeserializer())
				.registerTypeAdapter(PageContent.class, new PageContentDeserializer())
				.registerTypeAdapter(PageAttachment.class, new PageAttachmentDeserializer())
				.registerTypeAdapter(AttachmentContent.class, new AttachmentContentDeserializer())
				.registerTypeAdapter(Acl.class, new AclDeserializer())
				.create(); //@formatter:on

		// Writer writer = new FileWriter(JsonConstants.FILE_NAME);

		try (Reader input = new FileReader(workPath.append(PersistConstants.FILE_NAME).toFile())) {
			gson.fromJson(input, PagesStore.class);
		}
	}

}
