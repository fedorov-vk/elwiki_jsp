package org.elwiki.test;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wiki.api.exceptions.WikiException;
import org.apache.wiki.pages0.PageManager;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.elwiki.test.internal.bundle.TestUtilsActivator;
import org.elwiki.test.utils.WikiSessionTools;
import org.elwiki_data.WikiPage;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.rules.TestName;

abstract public class ElWikiTest {

	protected static final Logger log = Logger.getLogger(ElWikiTest.class);

	@ClassRule
	public static TestContext context = new TestContext();

	@ClassRule
	public static TestContext context2 = new TestContext();

	@Rule
	public TestName testName = new TestName();

//	private static IApplicationSession appSession;
	private static PageManager pageManager;
//	private static IElWikiSession wikiSession;
//	private static IElWikiSession wikiSession2;

	private static Map<String, WikiPage> createdPages = new HashMap<>();

	// == CODE ================================================================

	@BeforeClass
	public static void testClassSetUp() throws Exception {

	}

	@AfterClass
	public static void testClassCleanUp() throws Exception {
		for (WikiPage page : createdPages.values()) {
			pageManager.deletePage(page);
		}
	}

	/**
	 * Default Constructor.
	 */
	public ElWikiTest() {
		try {
			setUp();
		} catch (Exception e) {
			assertTrue(e.getLocalizedMessage(), false);
		}
	}

	abstract protected void setUp() throws Exception;

	public PageManager getPageManager() {
		return pageManager;
	}

	public WikiPage createPage(String name, String... content) throws WikiException {
		return null;
	}

	public void deletePage(WikiPage page) throws WikiException {
		createdPages.remove(page.getId());
		getPageManager().deletePage(page);
	}

	protected void testStarted() {
		log.debug("♦start " + this.getClass().getSimpleName() + "." + testName.getMethodName() + "()");
	}

}