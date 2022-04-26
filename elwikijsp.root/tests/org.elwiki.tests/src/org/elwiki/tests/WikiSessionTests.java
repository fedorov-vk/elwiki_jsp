package org.elwiki.tests;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * @author v.fedorov
 */
public class WikiSessionTests {

//	/**
//	 * @throws java.lang.Exception
//	 */
//	@BeforeAll
//	static void setUpBeforeClass() throws Exception {
//		System.out.println("BeforeAll");
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@AfterAll
//	static void tearDownAfterClass() throws Exception {
//		System.out.println("AfterAll");
//	}

	@Test
	public void test() {
		//fail("Not yet implemented");
		System.out.println(">>> test(4) - assertTrue");
		//assertTrue("assertTrue message", 1==1);
		Assertions.assertEquals( 4 , 2+2);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	void tttt() {
		
	}
}
