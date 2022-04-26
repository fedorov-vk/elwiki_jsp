package org.elwiki.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;

import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;

public class ServletTest {

//    @Mock
//    private HttpServletRequest request;
//    @Mock
//    private HttpServletResponse response;
//    @Mock
//    private ServletConfig servletConfig;
//    @Mock
//    private ServletContext servletContext;
//    private FilterContextPart instance;
 
//    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
//        instance = new UserServlet ();
   }

	@Test
	public void test02() {
		//fail("Not yet implemented");
		System.out.println(">>> test(2) - assertTrue");
		Assertions.assertEquals( 2 , 1+1);
	}
    
}
