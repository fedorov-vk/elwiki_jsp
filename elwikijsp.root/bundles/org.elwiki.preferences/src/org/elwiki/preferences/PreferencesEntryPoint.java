package org.elwiki.preferences;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.elwiki.services.ServicesRefs;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.ui.TemplateManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.custom.ScrolledComposite;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;

import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Session;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.elwiki.services.ServicesRefs;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class PreferencesEntryPoint extends AbstractEntryPoint {

	private static final long serialVersionUID = -823917493561120773L;

	@SuppressWarnings("restriction")
	@Override
	protected Shell createShell(Display display) {
		Shell shell = super.createShell(display);
		//:FVK: Shell shell = new Shell( display, SWT.NO_TRIM | SWT.RESIZE );
		//shell.setMaximized( true );

		String $el = "$el";
		exec("rap.getObject( '", org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId(shell), "' ).", $el,
				".css( 'overflow', null );");

		return shell;
	}

	private static void exec(String... strings) {
		StringBuilder builder = new StringBuilder();
		for (String str : strings) {
			builder.append(str);
		}

		JavaScriptExecutor executor = RWT.getClient().getService(JavaScriptExecutor.class);
		executor.execute(builder.toString());
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@PostConstruct
	@Override
	protected void createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new GridLayout(1, false));
		
		Button btnSave = new Button(composite, SWT.NONE);
		btnSave.setText("Save");
		addCustomBehavior(btnSave); //:FVK:
	}

	public static void addCustomBehavior(Control control) {
		ClientListener listener = MyClientListener.getInstance();
		control.addListener(SWT.MouseDown, listener);
	}

	private void populateWithData() {
		HttpSession httpSession = RWT.getUISession().getHttpSession();
		Session ssession = ServicesRefs.getSessionMonitor().findSession(httpSession);
		// String httpSessionId = httpSession.getId();

		Context prCtx = ServicesRefs.getCurrentContext(); // :FVK: ContextUtil.findContext( pageContext );
		/*
		TemplateManager t = ServicesRefs.getTemplateManager();
		skins = t.listSkins(pageContext, prCtx.getTemplate() );
		*/
		// String template = prCtx.getTemplate();
		// System.err.println();
	}

	@PreDestroy
	public void dispose() {
	}
}
