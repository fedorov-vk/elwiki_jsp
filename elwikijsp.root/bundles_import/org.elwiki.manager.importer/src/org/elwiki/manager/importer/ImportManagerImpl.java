package org.elwiki.manager.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.apache.wiki.api.attachment.AttachmentManager;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.emf.common.util.EList;
import org.elwiki.api.ImportManager;
import org.elwiki.api.WikiServiceReference;
import org.elwiki.api.component.WikiManager;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.WikiPage;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

import dwedata.Attachment;
import dwedata.DB;
import dwedata.Page;
import dwedata.PageVersion;

/**
 * @author v.fedorov
 *
 */
//@formatter:off
@Component(
	name = "elwiki.ImportManager",
	service = { ImportManager.class, WikiManager.class },
	scope = ServiceScope.SINGLETON)
//@formatter:on
public class ImportManagerImpl implements ImportManager, WikiManager {

	private static final Logger log = Logger.getLogger(ImportManagerImpl.class);

	// -- OSGi service handling ----------------------(start)--

	/** Stores configuration. */
	@Reference
	private IWikiConfiguration wikiConfiguration;

	@WikiServiceReference
	private Engine m_engine;

	@WikiServiceReference
	private AttachmentManager attachmentManager;

	// -- OSGi service handling ------------------------(end)--

	private List<String> reservedPages = new ArrayList<String>(Arrays.asList(new String[] { "Main",
			"Category.Wiki_Введение", "SearchPageHelp", "EditPageHelp", "TextFormattingRules", "Wiki.Name",
			"Wiki.About", "Wiki.Этикет", "Wiki.Wiki", "Wiki.AdminPage", "Wiki.EditFindAndReplaceHelp", "Wiki.MainPage",
			"Wiki.Category", "LoginHelp", "SandBox", "Песочница", "Category.Wiki_Администрирование",
			"Wiki.Зарезервированные страницы", "PageIndex", "RecentChanges", "FullRecentChanges", "UndefinedPages",
			"UnusedPages", "LeftMenu", "LeftMenuFooter", "MoreMenu", "SystemInfo", "Wiki.PageFilters", "TitleBox",
			"PageAliases", "Category.Документация", "CopyrightNotice", "ApprovalRequiredForUserProfiles",
			"ApprovalRequiredForPageChanges", "RejectedMessage" }));

	private DB db;

	/**
	 * Creates instance of ImportManager.
	 */
	public ImportManagerImpl() {
		super();
	}

	@Override
	public void initialize() throws WikiException {
		// TODO Auto-generated method stub

	}

	Map<WikiPage, String> mapPageId = new HashMap<>();
	Map<String, WikiPage> mapIdPage = new HashMap<>();
	Map<String, String> mapPageNameId = new HashMap<>();
	Map<WikiPage, Page> mapNewOld = new HashMap<>();

	@Override
	public void ImportPages(WikiContext wikiContext) throws Exception {
		ImportJspWikiData wikiDataImporter = new ImportJspWikiData(wikiConfiguration.getWorkspacePath());
		wikiDataImporter.readXmlData();
		db = wikiDataImporter.getDb();

		/*
		IPath fileName = wikiConfiguration.getWorkspacePath().append("names.txt");
		Path filePath = Path.of(fileName.toOSString());
		try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, //
				StandardOpenOption.CREATE, StandardOpenOption.APPEND);) {
			for (Page oldPage : db.getPages()) {
				writer.write(oldPage.getName() + "\n");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/

		/* Извлечение страниц, составление карт соответствия.
		 */
		mapPageId.clear();
		mapIdPage.clear();
		mapPageNameId.clear();
		mapNewOld.clear();

		PageManager pm = m_engine.getManager(PageManager.class);

		WikiPage emfMainPage = pm.createPage("EMFbook", pm.getMainPageId());
		String emfMainPageId = emfMainPage.getId();
		for (Page oldPage : db.getPages()) {
			if (reservedPages.contains(oldPage.getName()))
				continue;
			String pageName = oldPage.getName();
			WikiPage wikiPage = pm.createPage(pageName, emfMainPageId);

			mapPageId.put(wikiPage, wikiPage.getId());
			mapIdPage.put(wikiPage.getId(), wikiPage);
			mapPageNameId.put(pageName, wikiPage.getId());
			mapNewOld.put(wikiPage, oldPage);
		}

		/* Обработка:
		  - настройка ссылок в тексте страниц
		  - запись настроенного текста страниц
		 */
		for (Entry<WikiPage, String> pair : mapPageId.entrySet()) {
			WikiPage wikiPage = pair.getKey();

			Page jspWikiPage = mapNewOld.get(wikiPage);
			PageVersion pageVersion = getPageVersionData(jspWikiPage);

			String content = changePageText(pageVersion.getContent());
			//XMLGregorianCalendar modified = pageVersion.getModified();
			String author = pageVersion.getAuthor();
			String changeNote = pageVersion.getChangenote();
			//String description = pageVersion.getDescription();
			/* запись контента страницы. */
			pm.putPageText(wikiPage, content, author, changeNote);
		}

		/* Портирование присоединений. */
		for (Entry<WikiPage, String> pair : mapPageId.entrySet()) {
			WikiPage wikiPage = pair.getKey();

			Page jspWikiPage = mapNewOld.get(wikiPage);
			for (Attachment attachment : jspWikiPage.getAttachments()) {
				String fileName = attachment.getFilename();
				String author = attachment.getModifiedBy();
				XMLGregorianCalendar modifiedDate = attachment.getModified();
				int contentLength = attachment.getLength();

				AttachmentContent attContent = Elwiki_dataFactory.eINSTANCE.createAttachmentContent();
				attContent.setCreationDate(modifiedDate.toGregorianCalendar().getTime());
				attContent.setSize(contentLength);
				attContent.setAuthor(author);

				String dataFileName = attachment.getDatafile();
				try {
					InputStream data = wikiDataImporter.importAttachmentData(dataFileName);
					attachmentManager.storeAttachment(wikiPage, attContent, fileName, data);
				} catch (Exception e) {
					log.error("Importing attached data file '" + dataFileName + "'", e);
				}
			}

		}
	}

	/**
	 * Изменяет wiki-ссылки в тексте исходной страницы JSPwiki. <br/>
	 * Записывает текст в страницу ElWiki wiki.
	 * 
	 * @param page страница ElWiki.
	 */
	private String changePageText(String pageText) {
		final Pattern linkPattern = Pattern.compile("\\[([^\\[{].+?)\\]");

		Matcher linkMatcher = linkPattern.matcher(pageText);
		StringBuffer sb = new StringBuffer();
		while (linkMatcher.find()) {
			String baseStr = linkMatcher.group(0);
			String str = linkMatcher.group(1);
			String[] res = str.split("\\|");
			String replacer = null;
			if (res.length > 1) {
				String link = getPageLink(res[1]);
				if (link != null) {
					replacer = String.format("%s | %s", res[0], link);
					for (int n = 2; n < res.length - 1; n++) {
						replacer = replacer.concat(" | " + res[n]);
					}
				}
			} else {
				// Прямая ссылка на страницу или протокол.
				String link = getPageLink(res[0]);
				if (link != null)
					replacer = String.format("%s", link);
			}

			if (replacer != null) {
				linkMatcher.appendReplacement(sb, "[ " + replacer + " ]");
			} else {
				linkMatcher.appendReplacement(sb, baseStr);
			}
		}
		linkMatcher.appendTail(sb);

		return sb.toString();
	}

	private String getPageLink(String string) {
		// Ссылка на страницу или протокол, с наименованием и?? атрибутами.
		String link = string.trim();
		String pageId = null;
		if (link.indexOf('#') == 0) {
			// Ссылка внутрь текущей страницы - ничего не менять.
		} else if (link.indexOf('#') >= 1) {
			// Ссылка внутрь некоторой страницы - получить ее индекс.
			String name1 = link.substring(0, link.indexOf('#') - 1);
			pageId = mapPageNameId.get(name1);
			if (pageId != null) {
				link = "@" + pageId + link.substring(link.indexOf('#'));
			} else {
				// name = null;
			}
		} else if (link.indexOf('#') < 0) {
			pageId = mapPageNameId.get(link);
			if (pageId != null) {
				link = "@" + pageId;
			} else {
				link = null;
			}
		}
		return link;
	}

	private PageVersion getPageVersionData(Page oldPage) {
		PageVersion pageVersion = null;
		EList<PageVersion> versions = oldPage.getVersions();
		if (versions.size() > 0) {
			pageVersion = versions.get(versions.size() - 1);
			/*String author = pageVersion.getAuthor();
			String changeNote = pageVersion.getChangenote();
			pm.putPageText(wikiPage, content, author, changeNote);*/
		}
		return pageVersion;
	}

}