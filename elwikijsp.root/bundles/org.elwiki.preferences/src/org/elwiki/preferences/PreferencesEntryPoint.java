package org.elwiki.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;

import org.apache.wiki.api.core.Context;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.auth.AuthorizationManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;
import org.eclipse.rap.rwt.scripting.ClientListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.elwiki.api.authorization.IAuthorizer;
import org.elwiki.api.authorization.WrapGroup;
import org.elwiki.services.ServicesRefs;

/**
 * Creates and populate main Shell of RAP.
 * 
 * @author vfedorov
 */
public class PreferencesEntryPoint extends AbstractEntryPoint {

	private static final long serialVersionUID = -823917493561120773L;
	private Table table;

	@SuppressWarnings("restriction")
	@Override
	protected Shell createShell(Display display) {
		Shell shell = super.createShell(display);
		// :FVK: Shell shell = new Shell( display, SWT.NO_TRIM | SWT.RESIZE );
		// shell.setMaximized( true );

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

	String[] titles = { "Group Name", "Group Members", "Created on", "Created by", "Date Modified", "Modified by",
			"Actions" };
	int[] bounds = { 170, 170, 170, 170, 170, 170, 170 };
	private TableViewer tableViewer;
	private Composite composite_1;

	/**
	 * Create contents of the view part.
	 * 
	 * @wbp.parser.entryPoint
	 */
	@PostConstruct
	@Override
	protected void createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));

		{
			composite_1 = new Composite(composite, SWT.NONE);
			composite_1.setLayout(new GridLayout(3, false));

			createTable(composite_1);
			{
				Composite composite_2 = new Composite(composite_1, SWT.NONE);
				RowLayout rl_composite_2 = new RowLayout(SWT.VERTICAL);
				rl_composite_2.spacing = 15;
				rl_composite_2.marginRight = 30;
				rl_composite_2.center = true;
				rl_composite_2.marginLeft = 30;
				composite_2.setLayout(rl_composite_2);
				GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
				gd_composite_2.widthHint = 126;
				composite_2.setLayoutData(gd_composite_2);

				Button btnCreate = new Button(composite_2, SWT.NONE);
				btnCreate.setLayoutData(new RowData(65, SWT.DEFAULT));
				btnCreate.setText("Create");

				Button btnEdit = new Button(composite_2, SWT.NONE);
				btnEdit.setLayoutData(new RowData(65, SWT.DEFAULT));
				//btnEdit.setSize(292, 429);
				btnEdit.setText("Edit");
				addCustomBehavior(btnEdit);

				Button btnDelete = new Button(composite_2, SWT.NONE);
				btnDelete.setLayoutData(new RowData(65, SWT.DEFAULT));
				btnDelete.setText("Delete");
			}
		}

	}

	@SuppressWarnings("serial")
	private void createTable(Composite composite) {
		new Label(composite_1, SWT.NONE);
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		TableViewerColumn col;

		// Group name.
		col = createTableViewerColumn(0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WrapGroup wrapGroup = (WrapGroup) element;
				return wrapGroup.getName();
			}
		});

		// Members of group.
		col = createTableViewerColumn(1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WrapGroup wrapGroup = (WrapGroup) element;
				String str = Arrays.stream(wrapGroup.members()).map(Object::toString).collect(Collectors.joining(","));
				return str;
			}
		});

		// Creation date.
		col = createTableViewerColumn(2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WrapGroup wrapGroup = (WrapGroup) element;
				return wrapGroup.getCreated().toGMTString();
			}
		});

		// Creator.
		col = createTableViewerColumn(3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WrapGroup wrapGroup = (WrapGroup) element;
				return wrapGroup.getCreator();
			}
		});

		// Modification date.
		col = createTableViewerColumn(4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WrapGroup wrapGroup = (WrapGroup) element;
				return wrapGroup.getLastModified().toGMTString();
			}
		});

		// Modifier of group.
		col = createTableViewerColumn(5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WrapGroup wrapGroup = (WrapGroup) element;
				return wrapGroup.getModifier();
			}
		});

		{// prepare groups list.
			AuthorizationManager authMgr = ServicesRefs.getAuthorizationManager();
			IAuthorizer groupMgr = ServicesRefs.getGroupManager();
			List<org.osgi.service.useradmin.Group> groups1 = groupMgr.getRoles();
			List<WrapGroup> wrapGroups = new ArrayList<WrapGroup>();

			for (org.osgi.service.useradmin.Group group2 : groups1) {
				if (group2 instanceof org.osgi.service.useradmin.Group) {
					WrapGroup group = new WrapGroup(group2);
					wrapGroups.add(group);
				}
			}

			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setInput(wrapGroups);
		}
	}

	private TableViewerColumn createTableViewerColumn(int colNumber) {
		String title = titles[colNumber];
		int bound = bounds[colNumber];
		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
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
