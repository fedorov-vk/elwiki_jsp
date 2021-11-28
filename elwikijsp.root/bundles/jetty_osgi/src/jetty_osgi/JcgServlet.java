package jetty_osgi;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

@Component(
	    service=Servlet.class,
	    property= "osgi.http.whiteboard.servlet.pattern=/test",
	    scope=ServiceScope.PROTOTYPE)
public class JcgServlet extends HttpServlet {

	private static final long serialVersionUID = 369886070676036502L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().println("Hello JCG, Hello OSGi");
	}

	public JcgServlet() {
		super();
	}

}
