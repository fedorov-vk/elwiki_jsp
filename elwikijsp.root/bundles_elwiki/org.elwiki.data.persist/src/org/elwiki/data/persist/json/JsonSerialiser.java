package org.elwiki.data.persist.json;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.data.persist.internal.PluginActivator;
import org.elwiki.data.persist.json.converters.AclInfoConverter;
import org.elwiki.data.persist.json.converters.AttachmentContentConverter;
import org.elwiki.data.persist.json.converters.DateConverter;
import org.elwiki.data.persist.json.converters.PageAttachmentConverter;
import org.elwiki.data.persist.json.converters.PageContentConverter;
import org.elwiki.data.persist.json.converters.PageReferenceConverter;
import org.elwiki.data.persist.json.converters.PagesStoreConverter;
import org.elwiki.data.persist.json.converters.WikiPageConverter;
import org.elwiki_data.AclInfo;
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
				//.setVersion(1.0)
				.registerTypeAdapter(PagesStore.class, new PagesStoreConverter())
				.registerTypeAdapter(WikiPage.class, new WikiPageConverter())
				.registerTypeAdapter(PageReference.class, new PageReferenceConverter())
				.registerTypeAdapter(PageContent.class, new PageContentConverter())
				.registerTypeAdapter(PageAttachment.class, new PageAttachmentConverter())
				.registerTypeAdapter(AttachmentContent.class, new AttachmentContentConverter())
				.registerTypeAdapter(AclInfo.class, new AclInfoConverter())
				.registerTypeAdapter(Date.class, new DateConverter())
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
