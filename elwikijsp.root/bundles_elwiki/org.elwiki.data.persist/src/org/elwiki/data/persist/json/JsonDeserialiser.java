package org.elwiki.data.persist.json;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.elwiki.api.GlobalPreferences;
import org.elwiki.data.persist.json.converters.AttachmentContentConverter;
import org.elwiki.data.persist.json.converters.DateConverter;
import org.elwiki.data.persist.json.converters.PageAclEntryConverter;
import org.elwiki.data.persist.json.converters.PageAttachmentConverter;
import org.elwiki.data.persist.json.converters.PageContentConverter;
import org.elwiki.data.persist.json.converters.PageReferenceConverter;
import org.elwiki.data.persist.json.converters.PagesStoreConverter;
import org.elwiki.data.persist.json.converters.WikiPageConverter;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.PageAclEntry;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonDeserialiser {

	@SuppressWarnings("unused")
	private GlobalPreferences globalPreferences;
	private IPath workPath;

	public JsonDeserialiser(GlobalPreferences globalPreferences) {
		this.globalPreferences = globalPreferences;
		this.workPath = globalPreferences.getWorkDir();
	}

	public void LoadData() throws IOException {
		Gson gson = new GsonBuilder()
				.setPrettyPrinting() //@formatter:off
				//.setVersion(1.0)
				.registerTypeAdapter(PagesStore.class, new PagesStoreConverter())
				.registerTypeAdapter(WikiPage.class, new WikiPageConverter())
				.registerTypeAdapter(PageReference.class, new PageReferenceConverter())
				.registerTypeAdapter(PageContent.class, new PageContentConverter())
				.registerTypeAdapter(PageAttachment.class, new PageAttachmentConverter())
				.registerTypeAdapter(AttachmentContent.class, new AttachmentContentConverter())
				.registerTypeAdapter(PageAclEntry.class, new PageAclEntryConverter())
				.registerTypeAdapter(Date.class, new DateConverter())
				.create(); //@formatter:on

		try (Reader input = new FileReader(workPath.append(PersistConstants.FILE_NAME).toFile())) {
			gson.fromJson(input, PagesStore.class);
		}
	}

}
