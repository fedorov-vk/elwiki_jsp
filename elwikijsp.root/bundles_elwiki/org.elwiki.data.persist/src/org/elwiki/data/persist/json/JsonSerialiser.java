package org.elwiki.data.persist.json;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.persist.internal.PluginActivator;
import org.elwiki.data.persist.json.serialize.AclSerializer;
import org.elwiki.data.persist.json.serialize.AttachmentContentSerializer;
import org.elwiki.data.persist.json.serialize.PageAttachmentSerializer;
import org.elwiki.data.persist.json.serialize.PageContentSerializer;
import org.elwiki.data.persist.json.serialize.PageReferenceSerializer;
import org.elwiki.data.persist.json.serialize.PagesStoreSerializer;
import org.elwiki.data.persist.json.serialize.WikiPageSerializer;
import org.elwiki.services.ServicesRefs;
import org.elwiki_data.Acl;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonSerialiser {

	@SuppressWarnings("unused")
	private IWikiConfiguration wikiConfiguration;
	private IPath workPath;

	public JsonSerialiser(IWikiConfiguration wikiConfiguration) {
		this.wikiConfiguration = wikiConfiguration;
		this.workPath = wikiConfiguration.getWorkDir();
	}

	public void SaveData() throws IOException {
		// Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
		Gson gson = new GsonBuilder()
				.setPrettyPrinting() //@formatter:off
				.registerTypeAdapter(PagesStore.class, new PagesStoreSerializer())
				.registerTypeAdapter(WikiPage.class, new WikiPageSerializer())
				.registerTypeAdapter(PageReference.class, new PageReferenceSerializer())
				.registerTypeAdapter(PageContent.class, new PageContentSerializer())
				.registerTypeAdapter(PageAttachment.class, new PageAttachmentSerializer())
				.registerTypeAdapter(AttachmentContent.class, new AttachmentContentSerializer())
				.registerTypeAdapter(Acl.class, new AclSerializer())
				.create(); //@formatter:on

		PagesStore pagesStore = PluginActivator.getDefault().getDataStore().getPagesStore();
		@SuppressWarnings("unused")
		EList<WikiPage> pages = pagesStore.getWikipages();

		try (Writer writer = new FileWriter(workPath.append(PersistConstants.FILE_NAME).toFile())) {
			gson.toJson(pagesStore, PagesStore.class, writer);
			writer.close();
		}
	}
}
