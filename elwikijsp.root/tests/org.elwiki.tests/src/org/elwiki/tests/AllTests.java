package org.elwiki.tests;

//import org.junit.platform.suite.api.SelectPackages;
//import org.junit.platform.suite.api.Suite;
//import org.junit.platform.suite.api.SuiteDisplayName;
//import org.junit.runner.RunWith;
import org.junit.runners.Suite;
//import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

//@formatter:off
//@Suite
//@SelectPackages({"org.elwiki.tests"})
//@SuiteDisplayName("A demo Test Suite")
@RunWith(Suite.class)
@SuiteClasses({
	WikiSessionTests.class,
	ServletTest.class
})
//@formatter:on
public class AllTests {
//	@Test
//	void test1() {
//		//fail("Not yet implemented");
//		System.out.println("test() - assertTrue");
//		assertTrue(1==(2-1), "assertTrue message");
//	}
//
//	@Test
//	void test2() {
//		//fail("Not yet implemented");
//		System.out.println("test() - assertTrue");
//		assertTrue(1==(2-1), "assertTrue message");
//	}
}

//@RunWith(JUnitPlatform.class)
//@SelectClasses({WikiSessionTest.class})
