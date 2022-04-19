package org.elwiki.test.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

public class WikiSessionTools {

	//:FVK: - возможно это излишний код.
	static HttpServletRequest getHttpRequest(Principal[] roles) {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getParameter("username")).thenReturn("me");
		when(request.getParameter("password")).thenReturn("secret");

		/*:FVK:
		Set<String> r = new HashSet<String>();
		for (Principal role : roles) {
			r.add(role.getName());
		}
		when(request.getParameter("password")).thenReturn("secret");
		*/
		
		return request;
	}

}