package org.elwiki.pageprovider.cdo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.log4j.Logger;
import org.elwiki_data.PageContent;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.exceptions.NoRequiredPropertyException;
import org.apache.wiki.api.exceptions.ProviderException;
import org.apache.wiki.api.exceptions.RepositoryModifiedException;
import org.apache.wiki.api.providers.PageProvider;
import org.apache.wiki.api.providers.WikiProvider;
import org.apache.wiki.api.search.QueryItem;
import org.apache.wiki.api.search.SearchResult;
import org.apache.wiki.auth.AuthorizationManager;
import org.apache.wiki.auth.WikiSecurityException;
import org.apache.wiki.auth.acl.AclManager;
import org.apache.wiki.util.FileUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOQuery;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
//import org.eclipse.jface.dialogs.MessageDialog;
//import org.elwiki.api.IApplicationSession;
//import org.elwiki.api.WikiProvider;
//import org.elwiki.api.acl.AclManager;
//import org.elwiki.api.exceptions.ProviderException;
//import org.elwiki.api.exceptions.RepositoryModifiedException;
//import org.elwiki.api.exceptions.WikiSecurityException;
//import org.elwiki.api.log.DbCoreLog;
//import org.elwiki.api.pageprovider.IWikiPageProvider;
//import org.elwiki.api.search.QueryItem;
//import org.elwiki.api.search.SearchResult;
import org.elwiki.configuration.IWikiConfiguration;
import org.elwiki.pageprovider.cdo.internal.bundle.PageProviderCdoActivator;
//import org.elwiki.pageprovider.jspwiki.Attachment;
//import org.elwiki.pageprovider.jspwiki.JdbcAttachmentProvider;
//import org.elwiki.pageprovider.jspwiki.JdbcPageProvider;
import org.elwiki.permissions.PermissionFactory;
import org.elwiki.services.ServicesRefs;
//import org.elwiki.utils.FileUtil;
import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

public class CdoWikiPageProvider implements PageProvider {

	private static final Logger log = Logger.getLogger(CdoWikiPageProvider.class);

	@Deprecated
	private String rootPath1;

	//private IApplicationSession applicationSession;

	private IWikiConfiguration wikiConfiguration;

	// == CODE ================================================================

	/**
	 * Default constructor.
	 */
	public CdoWikiPageProvider() {
		super();
	}

	protected IWikiConfiguration getWikiConfiguration() {
		return this.wikiConfiguration;
	}

	/*:FVK:
	@Override
	public void initialize(IApplicationSession applicationSession1) throws FileNotFoundException {
		this.applicationSession = applicationSession1;
		this.wikiConfiguration = applicationSession1.getWikiConfiguration();
		this.rootPath1 = null; // getWikiConfiguration().getRootPath();
	}*/

	@Override
	public void initialize(Engine engine) throws NoRequiredPropertyException, IOException {
		// TODO Auto-generated method stub

	}

	// ------------------------------------------------------------------------

	@Override
	public String getProviderInfo() {
		return "CDO provider";
	}

	/**
	 * ?????????? PageContent ???????????????? ?? ?????????? ?????????????? ??????????????.
	 * 
	 * @param page ?????????????????? ????????????????.
	 * @return ???????????????? <code>null</code>.
	 */
	private PageContent getMaximalVersionContent(WikiPage page) {
		PageContent result = null;
		int currentVersion = -1;
		for (PageContent pageContent : page.getPagecontents()) {
			int contentVersion = pageContent.getVersion();
			if (currentVersion < contentVersion) {
				result = pageContent;
				currentVersion = contentVersion;
			}
		}
		return result;
	}

	@Override
	public void putPageText(WikiPage page, String text, String author, String changenote) throws ProviderException {
		// PageContent[] ca = page.getCa();
		// ca[0] = Elwiki_dataFactory.eINSTANCE.createPageContent();

		// ???????????????? ???????????? (?????????????????? ????????????????????).
		PageContent pc = getMaximalVersionContent(page);
		int version = (pc != null) ? pc.getVersion() + 1 : 1;
		pc = Elwiki_dataFactory.eINSTANCE.createPageContent();
		pc.setVersion(version);
		pc.setAuthor(author);
		pc.setChangeNote(changenote);
		pc.setLastModify(new Date()); // TODO: :FVK: ???????????????????? ????????, ???????????????? ???????? ????????????????????.
		pc.setContent(text);
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		WikiPage page1 = transaction.getObject(page);
		page1.getPagecontents().add(pc);
		try {
			transaction.commit();
		} catch (CommitException ex) {
			ex.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		// ???????????????? ???????????? ?????????????????? ???????????? ????????????????.
		try {
			updatePageLinks(page, text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * ?????????????????? ???????????? ?????????????????? ???????????? ????????????????, ???????????????? ?????????????????? ????????????.
	 *
	 * @param page
	 * @param pageText
	 * @throws Exception
	 */
	private void updatePageLinks(WikiPage argWikiPage, String pageText) throws Exception {
		Set<String> referredPagesId = new HashSet<>(10);

		//
		// Get link's identifiers from text of page.
		//
		String regexp = "@\\d+"; // regular expression of link.
		Pattern pattern;
		try { // Create a Pattern object
			pattern = Pattern.compile(regexp);
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		// Now create matcher object.
		Matcher matcher = pattern.matcher(pageText);
		boolean isFound;
		do {
			if (isFound = matcher.find()) {
				String pageId = matcher.group().substring(1);
				if (!referredPagesId.contains(pageId)) {
					if (getPageById(pageId) != null) {
						referredPagesId.add(pageId);
					}
				}
			}
		} while (isFound);

		//
		// Save change of references.
		//
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		try {
			// Refresh list of references from page.
			WikiPage wikiPage = transaction.getObject(argWikiPage);
			EList<PageReference> pageReferences = wikiPage.getPageReferences();

			// Remove unused references.
			for (Iterator<PageReference> iter = wikiPage.getPageReferences().iterator(); iter.hasNext();) {
				PageReference pageReference = iter.next();
				String pageId = pageReference.getPageId();
				if (referredPagesId.contains(pageId)) {
					referredPagesId.remove(pageId); // remove exiting pageId from set of references-id. 
				} else {
					iter.remove(); // remove unused reference.
				}
			}
			for (String pageId : referredPagesId) {
				PageReference pageReference = Elwiki_dataFactory.eINSTANCE.createPageReference();
				pageReference.setPageId(pageId);
				pageReferences.add(pageReference); // Adding reference into list.
			}
			transaction.commit();
		} catch (CommitException ex) {
			ex.printStackTrace();
		} finally {
			if (transaction != null && !transaction.isClosed()) {
				transaction.close();
			}
		}
	}

	@Override
	public boolean pageExistsById(String pageId) {
		if (pageId == null || pageId.isBlank()) {
			return false;
		}
		boolean result = false;

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		EClass eClassWikiPage = Elwiki_dataPackage.eINSTANCE.getWikiPage();
		CDOQuery query = transaction.createQuery("ocl",
				"WikiPage.allInstances()->select(p:WikiPage|p.id='" + pageId + "')", eClassWikiPage, false);
		query.setParameter("cdoLazyExtents", Boolean.FALSE);

		try {
			List<WikiPage> pages = query.getResult();
			if (pages.size() > 0) {
				result = true;
			}
		} catch (Exception e) {
			//:FVK: page missed... // e.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}
		return result;
	}

	@Override
	public boolean pageExistsByName(String pageName) {
		if (pageName == null) {
			return false;
		}
		boolean result = false;

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		EClass eClassWikiPage = Elwiki_dataPackage.eINSTANCE.getWikiPage();
		CDOQuery query = transaction.createQuery("ocl",
				"WikiPage.allInstances()->select(p:WikiPage|p.name='" + pageName + "')", eClassWikiPage, false);
		query.setParameter("cdoLazyExtents", Boolean.FALSE);

		try {
			List<WikiPage> pages = query.getResult();
			if (pages.size() > 0) {
				result = true;
			}
		} catch (Exception e) {
			//:FVK: page missed... // e.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}
		return result;
	}

	@Override
	public boolean pageExists(String pageName1, int version) {
		if (pageName1 == null) {
			return false;
		}

		PagesStore pages = PageProviderCdoActivator.getStorageCdo().getPagesStore();
		for (WikiPage page : pages.getWikipages()) {
			String pageName = page.getName();
			if (pageName1.equals(pageName)) {
				if (version == WikiProvider.LATEST_VERSION) {
					return true;
				}
				for (PageContent pageContent : page.getPagecontents()) {
					if (pageContent.getVersion() == version) {
						return true;
					}
				}
				return false;
			}
		}

		return false;

	}

	@Override
	public Collection<SearchResult> findPages(QueryItem[] query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<WikiPage> getAllPages() throws ProviderException {
		// :FVK: workaround - ???????????????????????? ???????????? '????????????????' ????????????????.
		PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();

		//initializeRepositoryContentFromText(); //:FVK: WORKAROUND

		EList<WikiPage> pages = pagesStore.getWikipages();

		return pages;
	}

	@Override
	public Collection<WikiPage> getAllChangedSince(Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPageCount() throws ProviderException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<PageContent> getVersionHistory(WikiPage page) throws ProviderException {
		WikiPage wikiPage = getPageById(page.getId());
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WikiPage getPageInfo(String page, int version) throws ProviderException {
		WikiPage wikiPage = getPageByName(page);
		return wikiPage;
	}

	@Override
	//public String getPageText(WikiPage wikiPage, int version) throws ProviderException {
	public String getPageText(String page, int version) throws ProviderException {
		WikiPage wikiPage = getPageByName(page);
		if (wikiPage == null) {
			return "";
		}

		// :FVK: WORKAROUND !!! - ?????? - ???????????????????? ???????????????? ???? ????????????????????????????.
		//WikiPage page1 = getPageById(wikiPage.getId());
		WikiPage page1 = wikiPage;

		String content = "";
		if (version == WikiProvider.LATEST_VERSION) {
			PageContent pc = getMaximalVersionContent(page1);
			if (pc != null) {
				content = pc.getContent();
			}
		} else {
			for (PageContent pageContent : page1.getPagecontents()) {
				if (pageContent.getVersion() == version) {
					content = pageContent.getContent();
					break;
				}
			}
		}
		return content;
	}

	@Override
	public void deleteVersion(String pageName, int version) throws ProviderException {
		// TODO Auto-generated method stub
		Assert.isTrue(false, "Code missed!");
	}

	@Override
	//public void deletePage(WikiPage wikiPage) throws ProviderException {
	public void deletePage(String pageName) throws ProviderException {
		WikiPage wikiPage = getPageByName(pageName);
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		WikiPage removedPage = transaction.getObject(wikiPage);

		removedPage.getPageReferences().clear();
		removedPage.getPagecontents().clear();
		for (PageAttachment att : removedPage.getAttachments()) {
			String fileName = att.getPlace();
			File file = new File(fileName);
			file.delete();
		}

		WikiPage parentPage = removedPage.getParent();
		if (parentPage != null) {
			removedPage.setParent(null);
		} else {
			PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();
			pagesStore = transaction.getObject(pagesStore);
			pagesStore.getWikipages().remove(removedPage);
		}

		try {
			transaction.commit();
		} catch (CommitException e) {
			log.error("????????????", e);
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}
	}

	@Override
	public void movePage(String from, String to) throws ProviderException {
		// TODO Auto-generated method stub

	}

	//:FVK: @Override
	public void renamePage(String newName, WikiPage renamedPage1) {
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		WikiPage renamedPage = transaction.getObject(renamedPage1);
		renamedPage.setName(newName);

		try {
			transaction.commit();
		} catch (CommitException e) {
			log.error("????????????", e);
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}
	}

	/*:FVK:*/static boolean flag = true;

	//:FVK: @Override
	public WikiPage getPageByName(String pageName) throws RepositoryModifiedException {
		/*:FVK:*/
		/*if (flag) {
			List<String> exists = new ArrayList();
			System.out.println("Pages:");
			String data = null;
			for (WikiPage page : getPages()) {
				data = page.getName();
				exists.add(data);
				System.out.println(data);
				data = page.getLastContent().getContent();
			}
			flag = false;
		}*/

		// TODO: ?????????? ???????????? ???? ?????????????????????? - ???????????????????? ??????, ?????????????? ???????????????? ???????? ??????????.
		if (pageName == null) {
			return null;
		}

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		CDOQuery query;
		EClass eClassWikiPage = Elwiki_dataPackage.eINSTANCE.getWikiPage();
		query = transaction.createQuery("ocl", "WikiPage.allInstances()->select(p:WikiPage|p.name='" + pageName + "')",
				eClassWikiPage, false);
		query.setParameter("cdoLazyExtents", Boolean.FALSE);

		WikiPage wikiPage = null;
		try {
			List<WikiPage> pages = query.getResult();
			if (pages.size() > 0) {
				wikiPage = pages.get(0);
				CDOView view = PageProviderCdoActivator.getStorageCdo().getView();
				wikiPage = (WikiPage) view.getObject(wikiPage.cdoID());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!transaction.isClosed()) {
			transaction.close();
		}

		return wikiPage;
	}

	//:FVK: @Override
	public List<PageReference> getPageReferencesById(String pageId) throws RepositoryModifiedException {
		// TODO: ?????????? ???????????? ???? ?????????????????????? - ???????????????????? ??????, ?????????????? ???????????????? ???????? ??????????.
		if (pageId == null) {
			return null;
		}

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		CDOQuery query;
		EClass eClassWikiPage = Elwiki_dataPackage.eINSTANCE.getWikiPage();
		query = transaction.createQuery("ocl",
				"PageReference.allInstances()->select(p:PageReference|p.pageId='" + pageId + "')", eClassWikiPage,
				false);
		query.setParameter("cdoLazyExtents", Boolean.FALSE);

		List<PageReference> references = new ArrayList<>();
		try {
			CDOView view = PageProviderCdoActivator.getStorageCdo().getView();
			for (Object ref : query.getResult()) {
				PageReference pageReference = (PageReference) view.getObject(((PageReference) ref).cdoID());
				references.add(pageReference);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		return references;
	}

	@Override
	public WikiPage getPageById(String pageId) {
		if (pageId == null) {
			return null;
		}

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		CDOQuery query;
		EClass eClassWikiPage = Elwiki_dataPackage.eINSTANCE.getWikiPage();
		query = transaction.createQuery("ocl", "WikiPage.allInstances()->select(p:WikiPage|p.id='" + pageId + "')",
				eClassWikiPage, false);
		query.setParameter("cdoLazyExtents", Boolean.FALSE);

		WikiPage wikiPage = null;
		try {
			List<WikiPage> pages = query.getResult();
			if (pages.size() > 0) {
				wikiPage = pages.get(0);
				CDOView view = PageProviderCdoActivator.getStorageCdo().getView();
				wikiPage = (WikiPage) view.getObject(wikiPage.cdoID());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!transaction.isClosed()) {
			transaction.close();
		}

		return wikiPage;
	}

	/* ?????????????? ????????????, ?????? OCL
	 * -- ?????????????????
	 */
	@Deprecated
	public WikiPage _getPage(String pageName1, int version) throws RepositoryModifiedException {
		if (pageName1 == null) {
			return null;
		}

		PagesStore pages = PageProviderCdoActivator.getStorageCdo().getPagesStore();
		for (WikiPage page : pages.getWikipages()) {
			String pageName = page.getName();
			if (pageName1.equals(pageName)) {
				if (version == WikiProvider.LATEST_VERSION) {
					return page;
				}
				for (PageContent pageContent : page.getPagecontents()) {
					if (pageContent != null && pageContent.getVersion() == version) {
						String content = pageContent.getContent();
						if (content != null) {
							return page;
						} else {
							return null;
						}
					}
				}
				return null;
			}
		}

		return null;
	}

	//:FVK: @Override
	@Deprecated
	public void initializeRepositoryContentFromText() {
		IPath rootPath; //:FVK: = new Path(this.rootPath1 + "/pages_text/");
		rootPath = new Path("/home/vfedorov/jspwiki-files/");

		File dir = rootPath.toFile();
		if (dir.isDirectory()) {
			String[] fileNames = dir.list();
			for (String fileName : fileNames) {
				String pageName = fileName.substring(0, fileName.lastIndexOf('.'));
				File file = new File(rootPath.toOSString(), fileName);
				createPageFromFile(pageName, file);
			}
		}
	}

	private WikiPage createPageFromFile(String pageName, File file) {
		StringBuilder content = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new FileReader(file));) {
			String line;
			while ((line = in.readLine()) != null) {
				content.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		WikiPage wikiPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();
		wikiPage.setName(pageName);
		wikiPage.setWiki("elwiki");
		PageContent pageContent = Elwiki_dataFactory.eINSTANCE.createPageContent();
		pageContent.setContent(content.toString());
		pageContent.setLastModify(new Date());

		PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();

		pagesStore = transaction.getObject(pagesStore);

		pagesStore.getWikipages().add(wikiPage);
		wikiPage.getPagecontents().add(pageContent);
		try {
			transaction.commit();
		} catch (ConcurrentAccessException ex) {
			log.error("???????????????? ???????????? ???????????????????? (?????????????????????? ???????????????????????? ????????????) - ???????????????? ????????????????????.");
			transaction.rollback();
		} catch (CommitException e) {
			log.error(e.getMessage());
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		// == ?????????????????? 'Main' ????????????????.
		if ("Main".equals(pageName)) {
			// pagesStore.setMainPageId();
			String pageId = wikiPage.getId();

			transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
			pagesStore = transaction.getObject(pagesStore);
			pagesStore.setMainPageId(pageId);
			try {
				transaction.commit();
			} catch (CommitException e) {
				log.error("????????????", e);
			} finally {
				if (!transaction.isClosed()) {
					transaction.close();
				}
			}
		}

		return wikiPage;
	}

	/**
	 * ???????????????????????????? JSPwiki ?????????????? ?? ???????????? ElWiki, ??.??. ???????????????? ???????????????????? ????????????. </br>
	 * ?????????????????????????? ?????????????? ?????????????????? ????????????. </br>
	 * ?????????????????? ???????????? ???????????? ?? ???????? ???????????????? ???? ????????????.
	 */
	//:FVK: @Override
	boolean doneFlag = false;

	public void convertRepositoryContent() {
		if (doneFlag)
			return;
		doneFlag = true;
		TextChanger textChanger = new TextChanger();

		List<WikiPage> wikiPages = getWikiPages(null);

		// ?????????????? ????????????????????. 
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		for (WikiPage page : wikiPages) {
			@SuppressWarnings("unused")
			String pageName = page.getName();
			if (page.getPageReferences().size() > 0) { // ???? ???????????????????????? ???????????????? ?? ???????????????? - ?????? ?????? ????????????????.
				continue;
			}

			EList<PageContent> pageContents = page.getPagecontents();
			if (pageContents.size() == 0) { // ???? ???????????????????????? ???????????????? ?????? ??????????????????????.
				continue;
			}

			// ???????????????????? ???????????? ?????????? ?????????????????? (????????????????????????) ???????????? ??????????????????????.
			PageContent pageContent = null;
			int currentVer = -1;
			for (PageContent pc : pageContents) {
				int pcVersion = pc.getVersion();
				if (pcVersion > currentVer) {
					currentVer = pcVersion;
					pageContent = pc;
				}
			}

			Assert.isNotNull(pageContent, "Internal error!"); //:FVK:

			String oldContent = pageContent.getContent();
			try {
				Set<String> setRefPagesId = new HashSet<>(); // ?????????? ???????????? ???? ??????????????. 
				String newContent = textChanger.changeText(oldContent, setRefPagesId, wikiPages);
				if (!oldContent.equals(newContent)) { // ???????? ???????????????????? ???????????????????? - ???????????????? ??????.
					pageContent = transaction.getObject(pageContent);
					pageContent.setContent(newContent);
					if (setRefPagesId.size() > 0) {
						page = transaction.getObject(page);
						EList<PageReference> pageReferences = page.getPageReferences();
						pageReferences.clear();
						for (String pageId : setRefPagesId) {
							PageReference pageReference = Elwiki_dataFactory.eINSTANCE.createPageReference();
							pageReference.setPageId(pageId);
							pageReferences.add(pageReference);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// ???????????????? ???????????? (?????????????????? ????????????????????).
		try {
			transaction.commit();
		} catch (CommitException ex) {
			ex.printStackTrace();
		} finally {
			if (transaction != null && !transaction.isClosed()) {
				transaction.close();
			}
		}
	}

	/**
	 * ???????????????? ???????????? ?????????? ????????????????????, ???????????????? ???? ?????????????? ?????????????????????? ??????????????.
	 * 
	 * @see org.elwiki.api.pageprovider.IWikiPageProvider#fvkPagesRefreshReferences()
	 */
	//:FVK: @Override
	public void fvkPagesRefreshReferences() {
		TextChanger textChanger = new TextChanger();
		// ?????????????? ????????????????????. 

		for (WikiPage page : getPages()) {
			// :FVK: "tmp"			CDOID nativeCdoId0 = CDOIDUtil.createLong(Long.parseLong("22"));
			//			EObject eObj0 = UtilDataSupport.getEObject(nativeCdoId0);
			//			page = (WikiPage) eObj0;

			// ?????????????????????? ?????? ???????????? ???????????? ?????????? ?????????????????? (????????????????????????) ???????????? ??????????????????????.
			EList<PageContent> pageContents = page.getPagecontents();
			PageContent pageContent = null;
			int currentVer = -1;
			for (PageContent pc : pageContents) { // ???? ???????????????????????? ???????????????? ?????? ??????????????????????.
				int pcVersion = pc.getVersion();
				if (pcVersion > currentVer) {
					currentVer = pcVersion;
					pageContent = pc;
				}
			}

			Assert.isNotNull(pageContent, "Internal error!"); //:FVK:

			Set<String> setRefs = textChanger.getReferences(pageContent.getContent());
			// ???????????????? ???????????? (?????????????????? ????????????????????).
			CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
			WikiPage thisWikiPage = transaction.getObject(page);
			try {
				EList<PageReference> pageReferences = thisWikiPage.getPageReferences();
				pageReferences.clear();
				if (setRefs.size() > 0) {
					// :FVK: ?? ownWikiPage.getRefPages().clear();
					for (String pageId : setRefs) {
						PageReference pageReference = Elwiki_dataFactory.eINSTANCE.createPageReference();
						pageReference.setPageId(pageId);
						pageReferences.add(pageReference); // ???????????????? ???????????? ?? ???????????? ????????????.
					}
				}
				transaction.commit();
			} catch (CommitException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (!transaction.isClosed()) {
					transaction.close();
				}
			}
		}

		System.out.println(":FVK: Done.");
	}

	/**
	 * @return ???????????? ???????? WikiPage.
	 */
	private List<WikiPage> getPages() {
		List<WikiPage> wikiPages = new ArrayList<>();

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		CDOQuery query;
		EClass eClassWikiPage = Elwiki_dataPackage.eINSTANCE.getWikiPage();
		query = transaction.createQuery("ocl", "WikiPage.allInstances()", eClassWikiPage, false);
		query.setParameter("cdoLazyExtents", Boolean.FALSE);

		try {
			CDOView view = PageProviderCdoActivator.getStorageCdo().getView();
			for (Object page : query.getResult()) {
				//@formatter:off
				wikiPages.add((WikiPage) view.getObject(
						((WikiPage) page).cdoID()
						)
				);
				//@formatter:on
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		return wikiPages;
	}

	/**
	 * @return ???????????? ???????? WikiPage.
	 */
	private List<WikiPage> getWikiPages(String wikiName) {
		List<WikiPage> wikiPages = new ArrayList<>();

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		CDOQuery query;
		EClass eClassWikiPage = Elwiki_dataPackage.eINSTANCE.getWikiPage();
		if (wikiName == null || wikiName.isEmpty()) {
			query = transaction.createQuery("ocl", "WikiPage.allInstances()", eClassWikiPage, false);
		} else {
			query = transaction.createQuery("ocl",
					"WikiPage.allInstances()->select(p:WikiPage|p.wiki='" + wikiName + "')", eClassWikiPage, false);
		}
		query.setParameter("cdoLazyExtents", Boolean.FALSE);

		try {
			CDOView view = PageProviderCdoActivator.getStorageCdo().getView();
			for (Object page : query.getResult()) {
				//@formatter:off
				wikiPages.add((WikiPage) view.getObject(
						((WikiPage) page).cdoID()
						)
				);
				//@formatter:on
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		return wikiPages;
	}

	@Override
	public String getMainPageId() {
		PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();
		String mainPageId = pagesStore.getMainPageId();

		return mainPageId;
	}

	//:FVK: @Override
	public WikiPage createPage(String name, String content, WikiPage parentPage) {
		String wikiName = "ELwiki";
		String pageAuthor = "Victor Fedorov"; // :FVK: WORKAROUND.
		if (parentPage != null) {
			wikiName = parentPage.getWiki();
		}

		WikiPage wikiPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();
		wikiPage.setName(name);
		wikiPage.setWiki(wikiName);

		PageContent pageContent = Elwiki_dataFactory.eINSTANCE.createPageContent();
		pageContent.setContent(content);
		pageContent.setAuthor(pageAuthor);

		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();
		pagesStore = transaction.getObject(pagesStore);
		if (parentPage != null) {
			WikiPage owner = transaction.getObject(parentPage);
			owner.getChildren().add(wikiPage);
		} else {
			pagesStore.getWikipages().add(wikiPage);
		}
		wikiPage.getPagecontents().add(pageContent);

		// ???????????? '????????????????????' pageId.
		String strPageId = pagesStore.getNextPageId();
		int pageId;
		try {
			pageId = Integer.parseInt(strPageId);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			pageId = 1;
		}
		wikiPage.setId(String.valueOf(pageId));
		pageId++;
		pagesStore.setNextPageId(String.valueOf(pageId));

		try {
			transaction.commit();
		} catch (CommitException e) {
			log.error("????????????", e);
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		CDOView view = PageProviderCdoActivator.getStorageCdo().getView();
		wikiPage = (WikiPage) view.getObject(wikiPage.cdoID());

		return wikiPage;
	}

	//:FVK: @Override
	public List<WikiPage> getRootPages() {
		PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();
		return pagesStore.getWikipages();
	}

	//:FVK: @Override
	public void changeHierarchy(WikiPage master1, List<WikiPage> children) {
		WikiPage master;
		// ???????????????? ???????????? (?????????????????? ????????????????????).
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		master = transaction.getObject(master1);
		// String[] oldParents = new String[] { "p1", "p2" }; // :FVK:
		// master.setOldParents(oldParents);
		for (WikiPage child : children) {
			master.getChildren().add(transaction.getObject(child));
		}
		try {
			transaction.commit();
		} catch (CommitException ex) {
			ex.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}
	}

	//:FVK: @Override
	public String getPageId(WikiPage wikiPage) {
		return wikiPage.getId();
	}

	//:FVK: @Override
	public void putAttachment(WikiPage page, PageAttachment att) {
		// ???????????????? ???????????? (?????????????????? ????????????????????).
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		WikiPage page1 = transaction.getObject(page);
		page1.getAttachments().add(att);
		try {
			transaction.commit();
		} catch (CommitException ex) {
			ex.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}
	}

	/**
	 * ???????????? ?????????????????????????? ???????????????? (?????? ???? ????????).
	 */
	//:FVK: @Override
	public void fvkPagesIdAdjust() {
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();
		int pageId = Integer.parseInt(pagesStore.getNextPageId());
		pagesStore = transaction.getObject(pagesStore);

		for (WikiPage page : getPages()) {
			String wikiPageId = page.getId();
			if (wikiPageId == null || wikiPageId.trim().length() == 0) {
				System.out.printf("???????????? ID: %-13s, \"%s\"\n", page.getWiki(), page.getName());
				WikiPage wikiPage = transaction.getObject(page);
				wikiPage.setId(String.valueOf(pageId));
				pageId++;
			}

			/* WORKAROUND -- DEPRECATED.
			// :FVK: "tmp"			CDOID nativeCdoId0 = CDOIDUtil.createLong(Long.parseLong("22"));
			//			EObject eObj0 = UtilDataSupport.getEObject(nativeCdoId0);
			//			page = (WikiPage) eObj0;
			
			// ?????????????????????? ?????? ???????????? ???????????? ?????????? ?????????????????? (????????????????????????) ???????????? ??????????????????????.
			EList<PageContent> pageContents = page.getPagecontents();
			PageContent pageContent = null;
			int currentVer = -1;
			for (PageContent pc : pageContents) { // ???? ???????????????????????? ???????????????? ?????? ??????????????????????.
				int pcVersion = pc.getVersion();
				if (pcVersion > currentVer) {
					currentVer = pcVersion;
					pageContent = pc;
				}
			}
			
			 */
		}
		pagesStore.setNextPageId(String.valueOf(pageId));
		// ???????????????? ???????????? (?????????????????? ????????????????????).
		try {
			transaction.commit();
		} catch (CommitException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}

		System.out.println(":FVK: Done.");
	}

	//:FVK: @Override
	public void redefinePageIds(Map<String, String> oldId2NewId) {
		for (WikiPage page : getPages()) {
			// :FVK: "tmp"			CDOID nativeCdoId0 = CDOIDUtil.createLong(Long.parseLong("22"));
			//			EObject eObj0 = UtilDataSupport.getEObject(nativeCdoId0);
			//			page = (WikiPage) eObj0;
			String oldPageId = page.getId();
			String newPageId = oldId2NewId.get(oldPageId);
			if (newPageId == null) {
				System.out.println("ERROR page id, oldId=" + oldPageId);
				continue;
			}

			// ???????????????? ???????????? (?????????????????? ????????????????????).
			CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
			WikiPage thisWikiPage = transaction.getObject(page);
			try {
				//
				// ?????????????????????? ?????? ???????????? ???????????? ?????????? ?????????????????? (????????????????????????) ???????????? ??????????????????????.
				EList<PageContent> pageContents = thisWikiPage.getPagecontents();
				PageContent pageContent = null;
				int currentVer = -1;
				for (PageContent pc : pageContents) { // ???? ???????????????????????? ???????????????? ?????? ??????????????????????. ?
					int pcVersion = pc.getVersion();
					if (pcVersion > currentVer) {
						currentVer = pcVersion;
						pageContent = pc;
					}
				}

				//
				// :FVK: ???????????????? ????????????????????;
				if (pageContent == null) {
					System.out.println(
							"ERROR page content == null. Page id=" + oldPageId + ", name: " + thisWikiPage.getName());
				}
				Assert.isNotNull(pageContent, "Internal error!"); //:FVK:

				String newPageText = TextChanger.fixTextForReferences(pageContent.getContent(), oldId2NewId);
				pageContent.setContent(newPageText);

				//
				// ???????????????????????? ????????????.
				EList<PageReference> references = thisWikiPage.getPageReferences();
				for (PageReference reference : references) {
					String oldRefId = reference.getPageId();
					String newRefId = oldId2NewId.get(oldRefId);
					if (newRefId == null) {
						System.out.println("ERROR ref id, oldId=" + oldRefId);
						continue;
					}
					reference.setPageId(newRefId);
				}

				thisWikiPage.setId(newPageId);
				transaction.commit();
			} catch (CommitException ex) {
				ex.printStackTrace();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (!transaction.isClosed()) {
					transaction.close();
				}
			}
		}
	}

	private static final String ATTFILE_PREFIX = "att-";

	private static final String ATTFILE_SUFFIX = ".dat";

	private List<String> unhandledPages = new ArrayList<>();

	private Engine m_engine;

	{
		this.unhandledPages.add("ApprovalRequiredForPageChanges");
		this.unhandledPages.add("ApprovalRequiredForUserProfiles");
		this.unhandledPages.add("CopyrightNotice");
		this.unhandledPages.add("EditPageHelp");
		this.unhandledPages.add("FullRecentChanges");
		this.unhandledPages.add("LeftMenu");
		this.unhandledPages.add("LeftMenuFooter");
		this.unhandledPages.add("LoginHelp");
		this.unhandledPages.add("MoreMenu");
		this.unhandledPages.add("PageAliases");
		this.unhandledPages.add("PageIndex");
		this.unhandledPages.add("RecentChanges");
		this.unhandledPages.add("RejectedMessage");
		this.unhandledPages.add("SandBox");
		this.unhandledPages.add("SearchPageHelp");
		this.unhandledPages.add("SystemInfo");
		this.unhandledPages.add("TextFormattingRules");
		this.unhandledPages.add("TitleBox");
		this.unhandledPages.add("UndefinedPages");
		this.unhandledPages.add("UnusedPages");
		this.unhandledPages.add("UserPreferences");
		this.unhandledPages.add("Wiki.About");
		this.unhandledPages.add("Wiki.AdminPage");
		this.unhandledPages.add("Wiki.Category");
		this.unhandledPages.add("Wiki.EditFindAndReplaceHelp");
		this.unhandledPages.add("Wiki.MainPage");
		this.unhandledPages.add("Wiki.Name");
		this.unhandledPages.add("Wiki.PageFilters");
		this.unhandledPages.add("Wiki.Wiki");
		this.unhandledPages.add("Wiki.?????????????????????????????????? ????????????????");
		this.unhandledPages.add("Wiki.????????????");
		this.unhandledPages.add("??????????????????");
		this.unhandledPages.add("Category.Wiki_??????????????????????????????????");
		this.unhandledPages.add("Category.Wiki_????????????????");
		this.unhandledPages.add("Category.????????????????????????");
		this.unhandledPages.add("Category.????????????????????????????");
	};

	/*:FVK:
	@Override
	public void initializeRepositoryContentFromSql() {
		// see. "/home/vfedorov/dev/dev_jspwiki_orig/developing/dev_pageprovider/JdbcPageProvider"
	
		try {
			JdbcPageProvider jdbcPageProvider = new JdbcPageProvider();
			JdbcAttachmentProvider jdbcAttachmentProvider = new JdbcAttachmentProvider();
			jdbcPageProvider.initialize(this.applicationSession);
			jdbcAttachmentProvider.initialize(this.applicationSession);
	
			CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
	
			PagesStore pagesStore = PageProviderCdoActivator.getStorageCdo().getPagesStore();
			int pageId = Integer.parseInt(pagesStore.getNextPageId());
			pageId++;
			pagesStore = transaction.getObject(pagesStore);
	
			// -- WORKAROUND ----------------------------------------
			String WikiName = "Arduino";
			WikiPage rootPage;
			{
				// create Root WikiPage.
				rootPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();
				rootPage.setName("Arduino");
				rootPage.setWiki(WikiName);
				rootPage.setId(String.valueOf(pageId));
				pageId++;
				pagesStore.getWikipages().add(rootPage);
			}
	
			Collection<org.elwiki.pageprovider.jspwiki.JSPwikiPage> pages = jdbcPageProvider.getAllPages();
			for (org.elwiki.pageprovider.jspwiki.JSPwikiPage pageV : pages) {
				String pageName = pageV.getName();
				if (this.unhandledPages.contains(pageName)) {
					continue;
				}
				System.out.printf("pageName: \"%s\"\n", pageName);
	
				// create WikiPage.
				WikiPage wikiPage = Elwiki_dataFactory.eINSTANCE.createWikiPage();
				wikiPage.setName(pageName);
				wikiPage.setWiki(WikiName);
				wikiPage.setId(String.valueOf(pageId));
				pageId++;
	
				// ?????????????????? ?????? ???????????? ???????????????? (???? ?????????????????????? ??????????)
				int maxVersion = pageV.getVersion();
				StringBuilder description = new StringBuilder();
				for (int ver = 1; ver <= maxVersion; ver++) {
					org.elwiki.pageprovider.jspwiki.JSPwikiPage page = jdbcPageProvider.getPageInfo(pageV.getName(),
							ver);
	
					String changenote = (String) page
							.getAttribute(org.elwiki.pageprovider.jspwiki.JSPwikiPage.CHANGENOTE);
					String author = page.getAuthor();
					String pageText = jdbcPageProvider.getPageText(pageName, ver);
					Date lastModified = page.getLastModified();
					String descr = (String) page.getAttribute(org.elwiki.pageprovider.jspwiki.JSPwikiPage.DESCRIPTION);
					if (descr != null && descr.trim().length() > 0) {
						description.append(descr);
					}
	
					// create PageContent.
					PageContent pageContent = Elwiki_dataFactory.eINSTANCE.createPageContent();
					pageContent.setVersion(ver);
					pageContent.setLastModify(lastModified);
					pageContent.setAuthor(author);
					pageContent.setChangeNote(changenote);
					pageContent.setContent(pageText);
					wikiPage.getPagecontents().add(pageContent);
				}
				wikiPage.setDescription(description.toString());
	
				//
				// ???????????????? ?? ????????????????.
				//
				//IAttachmentManager attMgr = this.applicationSession.getAttachmentManager();
	
				Collection<Attachment> attachments = jdbcAttachmentProvider.listAttachments(pageV);
				for (Attachment att : attachments) {
					// String attachmentFile = att.getFileName();
					PageAttachment pageAttachment = Elwiki_dataFactory.eINSTANCE.createPageAttachment();
					pageAttachment.setName(att.getFileName());
					pageAttachment.setVersion(att.getVersion());
					pageAttachment.setLastModify(att.getLastModified());
					pageAttachment.setAuthor(att.getAuthor());
					pageAttachment.setChangeNote("");
	
					// ?????????????????????? ????????????
					// IWikiConfiguration wikiConfiguration = ServicesRefs.getConfiguration();
					IPath dstPath = getWikiConfiguration().getAttachmentPath();
	
					File outputFile = File.createTempFile(ATTFILE_PREFIX, ATTFILE_SUFFIX, dstPath.toFile());
					try (OutputStream outputStream = new FileOutputStream(outputFile);
							InputStream inputStream = jdbcAttachmentProvider.getAttachmentData(att);) {
	
						FileUtil.copyContents(inputStream, outputStream);
						outputStream.close();
						inputStream.close();
	
						pageAttachment.setPlace(outputFile.getName());
						pageAttachment.setSize(outputFile.length());
						wikiPage.getAttachments().add(pageAttachment);
					} catch (Exception e) {
						// ignored.
						System.out.println("ERROR: " + e.getMessage());
					}
				} // ~~end~~for~~ - page`s attachment.
	
				rootPage.getChildren().add(wikiPage);
	
			} // ~~end~~for~~ - wiki page.
	
			pagesStore.setNextPageId(String.valueOf(pageId));
	
			try {
				transaction.commit();
			} catch (CommitException ex) {
				ex.printStackTrace();
			} finally {
				if (!transaction.isClosed()) {
					transaction.close();
				}
			}
	
			// == ?????????????????? ?????????????????? 'Main' ???????????????? =========================
			/ *
			IWikiEngine engine = this.applicationSession.getWikiEngine();
			WikiPage wikiPage = engine.getPage("Main");
			String cdoId = UtilDataSupport.getId(wikiPage);
			
			transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
			pagesStore = transaction.getObject(pagesStore);
			pagesStore.setMainPageId(cdoId);
			try {
				transaction.commit();
			} catch (CommitException e) {
				MessageDialog.openError(null, "????????????", e.getMessage());
				DbCoreLog.logError(e);
			} finally {
				transaction.close();
			}
			* /
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		// createPageFromSql(pageName, file);
	}
	*/

	//:FVK: @Override
	public void putAcl(WikiPage page, String aclString) {
		//:FVK: 
		AclManager am = ServicesRefs.getAclManager();

		org.elwiki_data.Acl acl = null;
		try {
			acl = am.parseAcl(page, aclString);
			//acl = parseAcl(page, aclString);
		} catch (WikiSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ???????????????? ???????????? (?????????????????? ????????????????????).
		CDOTransaction transaction = PageProviderCdoActivator.getStorageCdo().getTransactionCDO();
		WikiPage page1 = transaction.getObject(page);
		//:FVK:page1.setAccessList(acl);
		page1.setAcl(acl);

		try {
			transaction.commit();
		} catch (CommitException ex) {
			ex.printStackTrace();
		} finally {
			if (!transaction.isClosed()) {
				transaction.close();
			}
		}
	}

	@Deprecated
	private org.elwiki_data.Acl parseAcl(WikiPage page, String ruleLine) {
		Acl acl = page.getAcl();
		if (acl == null) {
			acl = Elwiki_dataFactory.eINSTANCE.createAcl();
		}

		try {
			StringTokenizer fieldToks = new StringTokenizer(ruleLine);
			fieldToks.nextToken();
			String actions = fieldToks.nextToken();

			while (fieldToks.hasMoreTokens()) {
				String principalName = fieldToks.nextToken(",").trim();
				Principal principal = ServicesRefs.getAuthorizationManager()
						.resolvePrincipal(principalName); //:FVK: Principal principal = this.m_auth.resolvePrincipal(principalName);
				AclEntry oldEntry = acl.getEntry(principal);

				if (oldEntry != null) {
					log.debug("Adding to old acl list: " + principal + ", " + actions);
					oldEntry.getPermission().add(PermissionFactory.getPagePermission(page, actions));
				} else {
					log.debug("Adding new acl entry for " + actions);
					AclEntry entry = Elwiki_dataFactory.eINSTANCE.createAclEntry();

					entry.setPrincipal(principal);
					entry.getPermission().add(PermissionFactory.getPagePermission(page, actions));

					acl.getAclEntries().add(entry);
				}
			}

			// :FVK: page.setAccessList(acl);

			log.debug(acl.toString());
		} catch (NoSuchElementException nsee) {
			log.warn("Invalid access rule: " + ruleLine + " - defaults will be used.");
			// :FVK: throw new WikiSecurityException("Invalid access rule: " + ruleLine, nsee);
		} catch (IllegalArgumentException iae) {
			// :FVK: throw new WikiSecurityException("Invalid permission type: " + ruleLine, iae);
		}

		return acl;
	}
	
}