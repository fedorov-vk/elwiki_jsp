package org.elwiki.rap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;

public class BasicEntryPoint extends AbstractEntryPoint {

	private static final long serialVersionUID = 1L;
	private Text text;
	private Table table;

	public BasicEntryPoint() {
	}

	/**
	 * Create contents of the view part.
	 */
	@PostConstruct
	@Override
	protected void createContents(Composite parent) {
	//public void createControls(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(parent, SWT.NONE);
		
		TabItem tbtmPreferences = new TabItem(tabFolder, SWT.NONE);
		tbtmPreferences.setText("Preferences");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmPreferences.setControl(composite_1);
		GridLayout gl_composite_1 = new GridLayout(2, false);
		gl_composite_1.verticalSpacing = 15;
		composite_1.setLayout(gl_composite_1);
		
		Label columnHolder_1 = new Label(composite_1, SWT.HORIZONTAL | SWT.CENTER);
		GridData gd_columnHolder_1 = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_columnHolder_1.heightHint = 12;
		gd_columnHolder_1.widthHint = 106;
		columnHolder_1.setLayoutData(gd_columnHolder_1);
		
		Composite composite_1_1 = new Composite(composite_1, SWT.NONE);
		composite_1_1.setLayout(new GridLayout(4, false));
		composite_1_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSaveUserPreferences = new Button(composite_1_1, SWT.NONE);
		btnSaveUserPreferences.setBounds(0, 0, 75, 25);
		btnSaveUserPreferences.setText("Save User Preferences");
		
		Button btnClearUserPreferences = new Button(composite_1_1, SWT.NONE);
		btnClearUserPreferences.setText("Clear User Preferences");
		
		Label label = new Label(composite_1_1, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnCancel = new Button(composite_1_1, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText("Cancel");
		
		Label lblName = new Label(composite_1, SWT.NONE);
		lblName.setText("Name");
		
		text = new Text(composite_1, SWT.BORDER);
		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 300;
		text.setLayoutData(gd_text);
		
		Label lblEditor = new Label(composite_1, SWT.NONE);
		lblEditor.setText("Editor");
		
		Combo comboEditor = new Combo(composite_1, SWT.NONE);
		GridData gd_comboEditor = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_comboEditor.widthHint = 96;
		comboEditor.setLayoutData(gd_comboEditor);
		
		Label lblSectionEditing = new Label(composite_1, SWT.NONE);
		lblSectionEditing.setText("Section Editing");
		
		Button btnSectionEditing = new Button(composite_1, SWT.CHECK);
		btnSectionEditing.setText("Enable");
		
		Label lblAppearance = new Label(composite_1, SWT.NONE);
		lblAppearance.setText("Appearance");
		
		Button btnDark = new Button(composite_1, SWT.CHECK);
		btnDark.setText("Dark");
		
		Label lblLanguage = new Label(composite_1, SWT.NONE);
		lblLanguage.setText("Language");
		
		Combo comboLanguage = new Combo(composite_1, SWT.NONE);
		GridData gd_comboLanguage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_comboLanguage.widthHint = 149;
		comboLanguage.setLayoutData(gd_comboLanguage);
		
		Label lblPageLayout = new Label(composite_1, SWT.NONE);
		lblPageLayout.setText("Page Layout");
		
		Composite composite_1_2 = new Composite(composite_1, SWT.NONE);
		GridLayout gl_composite_1_2 = new GridLayout(3, false);
		gl_composite_1_2.horizontalSpacing = 25;
		composite_1_2.setLayout(gl_composite_1_2);
		composite_1_2.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
		
		Group grpWidth = new Group(composite_1_2, SWT.NONE);
		grpWidth.setText("Width");
		grpWidth.setBounds(0, 0, 70, 82);
		grpWidth.setLayout(new RowLayout(SWT.VERTICAL));
		
		Button btnFullWidth = new Button(grpWidth, SWT.RADIO);
		btnFullWidth.setText("Full width");
		
		Button btnRadioButton = new Button(grpWidth, SWT.RADIO);
		btnRadioButton.setText("Fixed width");
		
		Group groupArrange = new Group(composite_1_2, SWT.NONE);
		groupArrange.setText("Arrange");
		groupArrange.setLayout(new RowLayout(SWT.VERTICAL));
		groupArrange.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Button btnSidebarContent = new Button(groupArrange, SWT.RADIO);
		btnSidebarContent.setText("Sidebar / Content");
		
		Button btnContentSidebar = new Button(groupArrange, SWT.RADIO);
		btnContentSidebar.setText("Content / Sidebar");
		new Label(composite_1_2, SWT.NONE);
		
		Label lblTimeFormat = new Label(composite_1, SWT.NONE);
		lblTimeFormat.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTimeFormat.setText("Time Format");
		
		Combo comboTimeFormat = new Combo(composite_1, SWT.NONE);
		GridData gd_comboTimeFormat = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_comboTimeFormat.widthHint = 219;
		comboTimeFormat.setLayoutData(gd_comboTimeFormat);
		
		Label lblTimeZone = new Label(composite_1, SWT.NONE);
		lblTimeZone.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTimeZone.setText("Time Zone");
		
		Combo comboTimeZone = new Combo(composite_1, SWT.NONE);
		GridData gd_comboTimeZone = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_comboTimeZone.widthHint = 299;
		comboTimeZone.setLayoutData(gd_comboTimeZone);
		
		Label lblSeparator = new Label(composite_1, SWT.SEPARATOR | SWT.HORIZONTAL);
		lblSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		lblSeparator.setText("separator_2");
		
		Label lblPageCookies = new Label(composite_1, SWT.NONE);
		lblPageCookies.setText("Page Cookies");
		
		TableViewer tableViewer = new TableViewer(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		table.setHeaderVisible(true);
	    table.setLinesVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnCookieType = tableViewerColumn.getColumn();
		tblclmnCookieType.setWidth(100);
		tblclmnCookieType.setText("Cookie Type");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnPage = tableViewerColumn_1.getColumn();
		tblclmnPage.setWidth(251);
		tblclmnPage.setText("Page");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnActions = tableViewerColumn_2.getColumn();
		tblclmnActions.setWidth(100);
		tblclmnActions.setText("Actions");
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Profile");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_2);
		composite_2.setLayout(new RowLayout(SWT.VERTICAL));
		
		Label lblNewLabel_1 = new Label(composite_2, SWT.NONE);
		lblNewLabel_1.setText("New Label 2.1");
		
		Button btnNewButton_1 = new Button(composite_2, SWT.NONE);
		btnNewButton_1.setText("New Button 2.2");
		
		Label lblNewLabel_2 = new Label(composite_2, SWT.NONE);
		lblNewLabel_2.setText("New Label 2.3");
		
		Button btnNewButton_2 = new Button(composite_2, SWT.NONE);
		btnNewButton_2.setText("New Button 2.4");
		
		Label lblNewLabel_3 = new Label(composite_2, SWT.NONE);
		lblNewLabel_3.setText("New Label 2.5");
		
		Button btnNewButton_3 = new Button(composite_2, SWT.NONE);
		btnNewButton_3.setText("New Button 2.6");
		
		Label lblNewLabel_4 = new Label(composite_2, SWT.NONE);
		lblNewLabel_4.setText("New Label 2.7");
		
		Button btnNewButton_4 = new Button(composite_2, SWT.NONE);
		btnNewButton_4.setText("New Button 2.8");
	}

	@PreDestroy
	public void dispose() {
	}
}
