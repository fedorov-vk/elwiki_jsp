package org.elwiki.preferences.ui.internal;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogMessageArea;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceContentProvider;
import org.eclipse.jface.preference.PreferenceLabelProvider;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

/**
 * This class is ported from the PreferenceDialog class and its superclasses, from the bundle
 * org.eclipse.rap.jface v3.27.0.
 */
@SuppressWarnings("restriction")
public class WikiPreferenceEntryPoint extends AbstractEntryPoint
		implements IPreferencePageContainer, IPageChangeProvider {
	private static final long serialVersionUID = 1L;

	/**
	 * Layout for the page container.
	 */
	private class PageLayout extends Layout {
		private static final long serialVersionUID = 1L;

		public Point computeSize(Composite composite, int wHint, int hHint, boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT) {
				return new Point(wHint, hHint);
			}
			int x = minimumPageSize.x;
			int y = minimumPageSize.y;
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				Point size = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
				x = Math.max(x, size.x);
				y = Math.max(y, size.y);
			}

			// As pages can implement thier own computeSize
			// take it into account
			if (currentPage != null) {
				Point size = currentPage.computeSize();
				x = Math.max(x, size.x);
				y = Math.max(y, size.y);
			}

			if (wHint != SWT.DEFAULT) {
				x = wHint;
			}
			if (hHint != SWT.DEFAULT) {
				y = hHint;
			}
			return new Point(x, y);
		}

		public void layout(Composite composite, boolean force) {
			Rectangle rect = composite.getClientArea();
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				children[i].setSize(rect.width, rect.height);
			}
		}
	}

	/**
	 * The minimum page size; 400 by 400 by default.
	 * 
	 * @see #setMinimumPageSize(Point)
	 */
	private Point minimumPageSize = new Point(400, 400);

	// The attribute name of store for the last page Id that was selected.
	private static String LAST_PREFERENCE_ID = WikiPreferenceEntryPoint.class.getName() + "#lastPreferenceId"; //$NON-NLS-1$

	// The last known tree width
	private static int lastTreeWidth = 200;

	/** The current preference page, or <code>null</code> if there is none. */
	private IPreferencePage currentPage;

	private DialogMessageArea messageArea;

	private Point lastShellSize;

	private IPreferenceNode lastSuccessfulNode;

	/** The Composite in which a page is shown. */
	private Composite pageContainer;

	/** Button width in pixels. */
	private final static int BUTTON_WIDTH = 70;

	/** The OK button. */
	private Button okButton;
	/** The Cancel button. */
	private Button cancelButton;

	/** Flag for the presence of the error message. */
	private boolean showingError = false;

	/** The tree viewer. */
	private TreeViewer treeViewer;

	private Composite titleArea;

	private ListenerList<IPageChangedListener> pageChangedListeners = new ListenerList<>();

	/** Composite with a FormLayout to contain the title area */
	private Composite formTitleComposite;

	/**
	 * Preference store, initially <code>null</code> meaning none.
	 * 
	 * @see #setPreferenceStore
	 */
	private IPreferenceStore preferenceStore;

	private ScrolledComposite scrolled;

	/**
	 * Creates instance of the WikiPreferenceEntryPoint.
	 */
	public WikiPreferenceEntryPoint() {
		super();
	}

	/**
	 * Returns the preference manager used by this preference dialog.
	 * 
	 * @return the preference manager
	 */
	public PreferenceManager getPreferenceManager() {
		return WikiPreferenceManager.Instance();
	}

	/**
	 * Get the last known right side width.
	 * 
	 * @return the width.
	 */
	protected int getLastRightWidth() {
		return lastTreeWidth;
	}

	/**
	 * Returns the currentPage.
	 * 
	 * @return IPreferencePage
	 * @since 1.0
	 */
	protected IPreferencePage getCurrentPage() {
		return currentPage;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected void createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = layout.marginWidth = 0;
		layout.verticalSpacing = layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		Control treeControl = createTreeAreaContents(composite);
		createSash(composite, treeControl);

		Label vertsep = new Label(composite, SWT.SEPARATOR | SWT.VERTICAL);
		GridData verGd = new GridData(GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);

		vertsep.setLayoutData(verGd);
		vertsep.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

		Composite pageAreaComposite = new Composite(composite, SWT.NONE);
		pageAreaComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		layout = new GridLayout(1, true);
		layout.marginHeight = layout.marginWidth = 0;
		layout.verticalSpacing = /*layout.horizontalSpacing =*/ 0;
		pageAreaComposite.setLayout(layout);

		formTitleComposite = new Composite(pageAreaComposite, SWT.NONE);
		FormLayout titleLayout = new FormLayout();
		titleLayout.marginWidth = 0;
		titleLayout.marginHeight = 0;
		formTitleComposite.setLayout(titleLayout);

		GridData titleGridData = new GridData(GridData.FILL_HORIZONTAL);
		titleGridData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
		formTitleComposite.setLayoutData(titleGridData);

		// Build the title area and separator line
		Composite titleComposite = new Composite(formTitleComposite, SWT.NONE);
		layout = new GridLayout();
		layout.marginBottom = 5;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		titleComposite.setLayout(layout);

		FormData titleFormData = new FormData();
		titleFormData.top = new FormAttachment(0, 0);
		titleFormData.left = new FormAttachment(0, 0);
		titleFormData.right = new FormAttachment(100, 0);
		titleFormData.bottom = new FormAttachment(100, 0);

		titleComposite.setLayoutData(titleFormData);
		createTitleArea(titleComposite);

		Label separator = new Label(pageAreaComposite, SWT.HORIZONTAL | SWT.SEPARATOR);

		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		// Build the Page container
		pageContainer = createPageContainer(pageAreaComposite);
		GridData pageContainerData = new GridData(GridData.FILL_BOTH);
		pageContainerData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
		pageContainer.setLayoutData(pageContainerData);
		// Build the separator line
		Label bottomSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		bottomSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		createButtonsForButtonBar(composite);

		// Add the first page
		selectSavedItem();
	}

	/**
	 * Creates the inner page container.
	 * 
	 * @param parent
	 * @return Composite
	 */
	protected Composite createPageContainer(Composite parent) {

		Composite outer = new Composite(parent, SWT.NONE);

		GridData outerData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		outerData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;

		outer.setLayout(new GridLayout());
		outer.setLayoutData(outerData);

		// Create an outer composite for spacing
		scrolled = new ScrolledComposite(outer, SWT.V_SCROLL | SWT.H_SCROLL);

		// always show the focus control
		scrolled.setShowFocusedControl(true);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);

		scrolled.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

		Composite result = new Composite(scrolled, SWT.NONE);

		GridData resultData = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);

		result.setLayout(getPageLayout());
		result.setLayoutData(resultData);

		scrolled.setContent(result);

		return result;
	}

	/**
	 * Return the layout for the composite that contains the pages.
	 * 
	 * @return PageLayout
	 * 
	 * @since 1.0
	 */
	protected Layout getPageLayout() {
		return new PageLayout();
	}

	/**
	 * Creates the wizard's title area.
	 * 
	 * @param parent the SWT parent for the title area composite.
	 * @return the created title area composite.
	 */
	protected Composite createTitleArea(Composite parent) {
		// Create the title area which will contain
		// a title, message, and image.
		int margins = 2;
		titleArea = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		layout.marginHeight = 0;
		layout.marginWidth = margins;
		titleArea.setLayout(layout);

		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		layoutData.verticalAlignment = SWT.TOP;
		titleArea.setLayoutData(layoutData);

		// Message label
		messageArea = new DialogMessageArea();
		messageArea.createContents(titleArea);

		titleArea.addControlListener(new ControlAdapter() {
			private static final long serialVersionUID = 1L;

			public void controlResized(ControlEvent e) {
				updateMessage();
			}
		});

		final IPropertyChangeListener fontListener = new IPropertyChangeListener() {
			private static final long serialVersionUID = 1L;

			public void propertyChange(PropertyChangeEvent event) {
				if (JFaceResources.BANNER_FONT.equals(event.getProperty())) {
					updateMessage();
				}
				if (JFaceResources.DIALOG_FONT.equals(event.getProperty())) {
					updateMessage();
					Font dialogFont = JFaceResources.getDialogFont();
					updateTreeFont(dialogFont);
					/*:FVK: TODO: here - update of dialog buttons bar font. */
				}
			}
		};

		titleArea.addDisposeListener(new DisposeListener() {
			private static final long serialVersionUID = 1L;

			public void widgetDisposed(DisposeEvent event) {
				JFaceResources.getFontRegistry().removeListener(fontListener);
			}
		});
		JFaceResources.getFontRegistry().addListener(fontListener);
		messageArea.setTitleLayoutData(createMessageAreaData());
		messageArea.setMessageLayoutData(createMessageAreaData());
		return titleArea;
	}

	/**
	 * Create the layout data for the message area.
	 * 
	 * @return FormData for the message area.
	 */
	private FormData createMessageAreaData() {
		FormData messageData = new FormData();
		messageData.top = new FormAttachment(0);
		messageData.bottom = new FormAttachment(100);
		messageData.right = new FormAttachment(100);
		messageData.left = new FormAttachment(0);
		return messageData;
	}

	protected Control createTreeAreaContents(Composite parent) {
		// :FVK: from FilteredPreferenceDialog
		Composite leftArea = new Composite(parent, SWT.NONE);
		leftArea.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		leftArea.setFont(parent.getFont());
		GridLayout leftLayout = new GridLayout();
		leftLayout.numColumns = 1;
		leftLayout.marginHeight = 0;
		leftLayout.marginTop = 7; // FVK: IDialogConstants.VERTICAL_MARGIN;
		leftLayout.marginWidth = 1; // FVK: IDialogConstants.HORIZONTAL_MARGIN;
		leftLayout.horizontalSpacing = 0;
		leftLayout.verticalSpacing = 0;
		leftArea.setLayout(leftLayout);

		// Build the tree and put it into the composite.
		TreeViewer viewer = createTreeViewer(leftArea);
		viewer.setInput(getPreferenceManager());
		setTreeViewer(viewer);

		updateTreeFont(JFaceResources.getDialogFont());
		GridData viewerData = new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL);
		leftArea.setLayoutData(viewerData);

		layoutTreeAreaControl(leftArea);

		return leftArea;
	}

	/**
	 * Create a new <code>TreeViewer</code>.
	 * 
	 * @param parent the parent <code>Composite</code>.
	 * @return the <code>TreeViewer</code>.
	 * @since 1.0
	 */
	protected TreeViewer createTreeViewer(Composite parent) {
		TreeViewer tree = new TreeViewer(parent, SWT.SINGLE);
		addListeners(tree);
		tree.setLabelProvider(new PreferenceLabelProvider());
		tree.setContentProvider(new PreferenceContentProvider());

		tree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleTreeSelectionChanged(event);
			}
		});

		return tree;
	}

	/**
	 * A selection has been made in the tree.
	 * 
	 * @param event SelectionChangedEvent
	 */
	protected void handleTreeSelectionChanged(SelectionChangedEvent event) {
		// Do nothing by default. :FVK: is from FilteredPreferenceDialog?
	}

	/**
	 * Creates buttons for button bar.
	 * 
	 * @param parent
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		Composite compositeButtonBar = new Composite(parent, SWT.NONE);
		compositeButtonBar.setLayout(new GridLayout(2, false));
		GridData gd_compositeButtonBar = new GridData(SWT.RIGHT, SWT.FILL, true, false, 4, 1);
		gd_compositeButtonBar.heightHint = 42;
		gd_compositeButtonBar.minimumHeight = 42;
		compositeButtonBar.setLayoutData(gd_compositeButtonBar);

		okButton = new Button(compositeButtonBar, SWT.NONE);
		okButton.addMouseListener(new MouseAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void mouseUp(MouseEvent e) {
				okPressed();
			}
		});
		okButton.setText("Ok");
		setButtonLayoutData(okButton);

		cancelButton = new Button(compositeButtonBar, SWT.NONE);
		cancelButton.addMouseListener(new MouseAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void mouseUp(MouseEvent e) {
				cancelPressed();
			}
		});
		cancelButton.setText("Cancel");
		setButtonLayoutData(cancelButton);
	}

	/**
	 * Set the layout data of the button to a GridData with appropriate heights and widths.
	 * 
	 * @param button
	 */
	protected void setButtonLayoutData(Button button) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = BUTTON_WIDTH;
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}

	/**
	 * @return the <code>TreeViewer</code> for this dialog.
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

	/**
	 * Set the treeViewer.
	 * 
	 * @param treeViewer
	 */
	protected void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	/**
	 * Add the listeners to the tree viewer.
	 * 
	 * @param viewer
	 * 
	 * @since 1.0
	 */
	protected void addListeners(final TreeViewer viewer) {
		viewer.addPostSelectionChangedListener(new ISelectionChangedListener() {
			private void handleError() {
				try {
					// remove the listener temporarily so that the events caused
					// by the error handling dont further cause error handling to occur.
					viewer.removePostSelectionChangedListener(this);
					showPageFlippingAbortDialog();
					selectCurrentPageAgain();
					clearSelectedNode();
				} finally {
					viewer.addPostSelectionChangedListener(this);
				}
			}

			public void selectionChanged(SelectionChangedEvent event) {
				final IPreferenceNode selection = getSingleSelection(event.getSelection());
				BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
					public void run() {
						if (!isCurrentPageValid()) {
							handleError();
						} else if (!showPage((IPreferenceNode) selection)) {
							// Page flipping wasn't successful
							handleError();
						} else {
							// Everything went well
							lastSuccessfulNode = (IPreferenceNode) selection;
						}
					}
				});
			}
		});
		((Tree) viewer.getControl()).addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			public void widgetDefaultSelected(final SelectionEvent event) {
				ISelection selection = viewer.getSelection();
				if (selection.isEmpty()) {
					return;
				}
				IPreferenceNode singleSelection = getSingleSelection(selection);
				boolean expanded = viewer.getExpandedState(singleSelection);
				viewer.setExpandedState(singleSelection, !expanded);
			}
		});
		// Register help listener on the tree to use context sensitive help
		viewer.getControl().addHelpListener(new HelpListener() {
			private static final long serialVersionUID = 1L;

			public void helpRequested(HelpEvent event) {
				if (currentPage == null) { // no current page? open dialog's help
					openDialogHelp();
					return;
				}
				// A) A typical path: the current page has registered its own help link
				// via WorkbenchHelpSystem#setHelp().
				// When just call it and let it handle the help request.
				Control pageControl = currentPage.getControl();
				if (pageControl != null && pageControl.isListening(SWT.Help)) {
					currentPage.performHelp();
					return;
				}

				// B) Less typical path: no standard listener has been created for the page.
				// In this case we may or may not have an override of page's #performHelp().
				// 1) Try to get default help opened for the dialog;
				openDialogHelp();
				// 2) Next call currentPage's #performHelp(). If it was overridden, it might
				// switch help to something else.
				currentPage.performHelp();
			}

			private void openDialogHelp() {
				if (pageContainer == null)
					return;
				for (Control currentControl = pageContainer; currentControl != null; currentControl = currentControl
						.getParent()) {
					if (currentControl.isListening(SWT.Help)) {
						currentControl.notifyListeners(SWT.Help, new Event());
						break;
					}
				}
			}
		});
	}

	/**
	 * Create the sash with right control on the right. Note that this method assumes GridData for the
	 * layout data of the rightControl.
	 * 
	 * @param composite
	 * @param rightControl
	 * @return Sash
	 * 
	 * @since 1.0
	 */
	private Sash createSash(Composite composite, Control rightControl) {
		final Sash sash = new Sash(composite, SWT.VERTICAL);
		GridData gd_sash = new GridData(GridData.FILL_VERTICAL);
		gd_sash.grabExcessVerticalSpace = false;
		sash.setLayoutData(gd_sash);
		// :FVK: sash.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		sash.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_GREEN));

		// the following listener resizes the tree control based on sash deltas.
		// If necessary, it will also grow/shrink the dialog.
		sash.addListener(SWT.Selection, new Listener() {
			private static final long serialVersionUID = 1L;

			public void handleEvent(Event event) {
				if (event.detail == SWT.DRAG) {
					return;
				}

				int shift = event.x - sash.getBounds().x;
				GridData data = (GridData) rightControl.getLayoutData();
				int newWidthHint = data.widthHint + shift;
				if (newWidthHint < 20) {
					return;
				}
				Point computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
				Point currentSize = getShell().getSize();
				// if the dialog wasn't of a custom size we know we can shrink
				// it if necessary based on sash movement.
				boolean customSize = !computedSize.equals(currentSize);
				data.widthHint = newWidthHint;
				setLastTreeWidth(newWidthHint);
				composite.layout(true);
				// recompute based on new widget size
				computedSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
				// if the dialog was of a custom size then increase it only if necessary.
				if (customSize) {
					computedSize.x = Math.max(computedSize.x, currentSize.x);
				}
				computedSize.y = Math.max(computedSize.y, currentSize.y);
				if (computedSize.equals(currentSize)) {
					return;
				}
				setShellSize(computedSize.x, computedSize.y);
				lastShellSize = getShell().getSize();
			}
		});
		return sash;
	}

	/**
	 * @param selection the <code>ISelection</code> to examine.
	 * @return the first element, or null if empty.
	 */
	protected IPreferenceNode getSingleSelection(ISelection selection) {
		if (!selection.isEmpty()) {
			IStructuredSelection structured = (IStructuredSelection) selection;
			if (structured.getFirstElement() instanceof IPreferenceNode) {
				return (IPreferenceNode) structured.getFirstElement();
			}
		}
		return null;
	}

	/**
	 * Returns whether the current page is valid.
	 * 
	 * @return <code>false</code> if the current page is not valid, or or <code>true</code> if the
	 *         current page is valid or there is no current page
	 */
	protected boolean isCurrentPageValid() {
		if (currentPage == null) {
			return true;
		}
		return currentPage.isValid();
	}

	/**
	 * @param control the <code>Control</code> to lay out.
	 */
	protected void layoutTreeAreaControl(Control control) {
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = getLastRightWidth();
		gd.verticalSpan = 1;
		control.setLayoutData(gd);
	}

	/**
	 * Save the currently selected node.
	 */
	private void setSelectedNode() {
		String storeValue = null;
		IStructuredSelection selection = (IStructuredSelection) getTreeViewer().getSelection();
		if (selection.size() == 1) {
			IPreferenceNode node = (IPreferenceNode) selection.getFirstElement();
			storeValue = node.getId();
		}
		setSelectedNodePreference(storeValue);
	}

	/**
	 * Sets the name of the selected item preference. Public equivalent to
	 * <code>setSelectedNodePreference</code>.
	 * 
	 * @param pageId The identifier for the page
	 */
	public void setSelectedNode(String pageId) {
		setSelectedNodePreference(pageId);
	}

	/**
	 * Get the name of the selected item preference
	 * 
	 * @return String
	 */
	protected String getSelectedNodePreference() {
		// Keep Id of last preference page in the RAP session store.
		return (String) ContextProvider.getUISession().getAttribute(LAST_PREFERENCE_ID);
	}

	/**
	 * Sets the name of the selected item preference.
	 * 
	 * @param pageId The identifier for the page
	 */
	protected void setSelectedNodePreference(String pageId) {
		// Keep Id of last preference page in the RAP session store.
		ContextProvider.getUISession().setAttribute(LAST_PREFERENCE_ID, pageId);
	}

	/**
	 * Selects the page determined by <code>lastSuccessfulNode</code> in the page hierarchy.
	 */
	void selectCurrentPageAgain() {
		if (lastSuccessfulNode == null) {
			return;
		}
		getTreeViewer().setSelection(new StructuredSelection(lastSuccessfulNode));
		currentPage.setVisible(true);
	}

	/**
	 * Selects the saved item in the tree of preference pages. If it cannot do this it saves the first
	 * one.
	 */
	protected void selectSavedItem() {
		IPreferenceNode node = findNodeMatching(getSelectedNodePreference());
		if (node == null) {
			IPreferenceNode[] nodes = getPreferenceManager().getRootSubNodes();
			ViewerComparator comparator = getTreeViewer().getComparator();
			if (comparator != null) {
				comparator.sort(null, nodes);
			}
			ViewerFilter[] filters = getTreeViewer().getFilters();
			for (int i = 0; i < nodes.length; i++) {
				IPreferenceNode selectedNode = nodes[i];
				// See if it passes all filters
				for (int j = 0; j < filters.length; j++) {
					IPreferenceNode root = ((WikiPreferenceManager) getPreferenceManager()).getRoot();
					if (!filters[j].select(getTreeViewer(), root, selectedNode)) {
						selectedNode = null;
						break;
					}
				}
				// if it passes all filters select it
				if (selectedNode != null) {
					node = selectedNode;
					break;
				}
			}
		}
		if (node != null) {
			getTreeViewer().setSelection(new StructuredSelection(node), true);
			// Keep focus in tree. See bugs 2692, 2621, and 6775.
			getTreeViewer().getControl().setFocus();
		}
	}

	/**
	 * Find the <code>IPreferenceNode</code> that has data the same id as the supplied value.
	 * 
	 * @param nodeId the id to search for.
	 * @return <code>IPreferenceNode</code> or <code>null</code> if not found.
	 */
	protected IPreferenceNode findNodeMatching(String nodeId) {
		List<?> nodes = getPreferenceManager().getElements(PreferenceManager.POST_ORDER);
		for (Iterator<?> i = nodes.iterator(); i.hasNext();) {
			IPreferenceNode node = (IPreferenceNode) i.next();
			if (node.getId().equals(nodeId)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * Changes the shell size to the given size, ensuring that it is no larger than the display bounds.
	 * 
	 * @param width  the shell width
	 * @param height the shell height
	 */
	private void setShellSize(int width, int height) {
		Rectangle preferred = getShell().getBounds();
		preferred.width = width;
		preferred.height = height;
		Rectangle bounds = new Rectangle(0, 0, 700, 600); // :FVK: getConstrainedShellBounds(preferred)
		getShell().setBounds(bounds);
	}

	/**
	 * Save the last known tree width.
	 * 
	 * @param width the width.
	 */
	private void setLastTreeWidth(int width) {
		lastTreeWidth = width;
	}

	/**
	 * Shows the "Page Flipping abort" dialog.
	 */
	void showPageFlippingAbortDialog() {
		// TODO: make RAP-safety error message.
		// And test error supporting for page with incorrect fields (that has not preference store). 
		/*
		MessageDialog.open(MessageDialog.ERROR, getShell(), //
				JFaceResources.getString("AbortPageFlippingDialog.title"), //$NON-NLS-1$
				JFaceResources.getString("AbortPageFlippingDialog.message"), SWT.SHEET); //$NON-NLS-1$
		 */
	}

	/**
	 * Shows the preference page corresponding to the given preference node. Does nothing if that page
	 * is already current.
	 * 
	 * @param node the preference node, or <code>null</code> if none
	 * @return <code>true</code> if the page flip was successful, and <code>false</code> is unsuccessful
	 */
	protected boolean showPage(IPreferenceNode node) {
		if (node == null) {
			return false;
		}
		// Create the page if nessessary
		if (node.getPage() == null) {
			createPage(node);
		}
		if (node.getPage() == null) {
			return false;
		}
		IPreferencePage newPage = getPage(node);
		if (newPage == currentPage) {
			return true;
		}
		if (currentPage != null) {
			if (!currentPage.okToLeave()) {
				return false;
			}
		}
		IPreferencePage oldPage = currentPage;
		currentPage = newPage;
		// Set the new page's container
		currentPage.setContainer(this);
		// Ensure that the page control has been created
		// (this allows lazy page control creation)
		if (currentPage.getControl() == null) {
			final boolean[] failed = { false };
			SafeRunnable.run(new ISafeRunnable() {
				public void handleException(Throwable e) {
					failed[0] = true;
				}

				public void run() {
					createPageControl(currentPage, pageContainer);
				}
			});
			if (failed[0]) {
				return false;
			}
			// the page is responsible for ensuring the created control is
			// accessable
			// via getControl.
			Assert.isNotNull(currentPage.getControl());
		}
		// Force calculation of the page's description label because
		// label can be wrapped.
		final Point[] size = new Point[1];
		final Point failed = new Point(-1, -1);
		SafeRunnable.run(new ISafeRunnable() {
			public void handleException(Throwable e) {
				size[0] = failed;
			}

			public void run() {
				size[0] = currentPage.computeSize();
			}
		});
		if (size[0].equals(failed)) {
			return false;
		}
		Point contentSize = size[0];
		// Do we need resizing. Computation not needed if the
		// first page is inserted since computing the dialog's
		// size is done by calling dialog.open().
		// Also prevent auto resize if the user has manually resized
		Shell shell = getShell();
		Point shellSize = shell.getSize();
		if (oldPage != null) {
			Rectangle rect = pageContainer.getClientArea();
			Point containerSize = new Point(rect.width, rect.height);
			int hdiff = contentSize.x - containerSize.x;
			int vdiff = contentSize.y - containerSize.y;
			if ((hdiff > 0 || vdiff > 0) && shellSize.equals(lastShellSize)) {
				hdiff = Math.max(0, hdiff);
				vdiff = Math.max(0, vdiff);
				setShellSize(shellSize.x + hdiff, shellSize.y + vdiff);
				lastShellSize = shell.getSize();
				if (currentPage.getControl().getSize().x == 0) {
					currentPage.getControl().setSize(containerSize);
				}

			} else {
				currentPage.setSize(containerSize);
			}
		}

		scrolled.setMinSize(contentSize);
		// Ensure that all other pages are invisible
		// (including ones that triggered an exception during
		// their creation).
		Control[] children = pageContainer.getChildren();
		Control currentControl = currentPage.getControl();
		for (int i = 0; i < children.length; i++) {
			if (children[i] != currentControl) {
				children[i].setVisible(false);
			}
		}
		// Make the new page visible
		currentPage.setVisible(true);
		if (oldPage != null) {
			oldPage.setVisible(false);
		}
		// update the dialog controls
		update();
		return true;
	}

	/**
	 * Create the page control for the supplied page.
	 * 
	 * @param page   - the preference page to be shown
	 * @param parent - the composite to parent the page
	 */
	protected void createPageControl(IPreferencePage page, Composite parent) {
		page.createControl(parent);
	}

	/**
	 * Create the page for the node.
	 * 
	 * @param node
	 */
	protected void createPage(IPreferenceNode node) {
		node.createPage();
	}

	/**
	 * Get the page for the node.
	 * 
	 * @param node
	 * @return IPreferencePage
	 */
	protected IPreferencePage getPage(IPreferenceNode node) {
		return node.getPage();
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	/**
	 * Updates this dialog's controls to reflect the current page.
	 */
	protected void update() {
		// Update the title bar
		updateTitle();
		// Update the message line
		updateMessage();
		// Update the buttons
		updateButtons();
		// Saved the selected node in the preferences
		setSelectedNode();
		firePageChanged(new PageChangedEvent(this, getCurrentPage()));
	}

	@Override
	public void updateButtons() {
		okButton.setEnabled(isCurrentPageValid());
	}

	@Override
	public void updateMessage() {
		String message = null;
		String errorMessage = null;
		if (currentPage != null) {
			message = currentPage.getMessage();
			errorMessage = currentPage.getErrorMessage();
		}
		int messageType = IMessageProvider.NONE;
		if (message != null && currentPage instanceof IMessageProvider) {
			messageType = ((IMessageProvider) currentPage).getMessageType();
		}

		if (errorMessage == null) {
			if (showingError) {
				// we were previously showing an error
				showingError = false;
			}
		} else {
			message = errorMessage;
			messageType = IMessageProvider.ERROR;
			if (!showingError) {
				// we were not previously showing an error
				showingError = true;
			}
		}
		messageArea.updateText(message, messageType);
	}

	@Override
	public void updateTitle() {
		if (currentPage == null) {
			return;
		}
		messageArea.showTitle(currentPage.getTitle(), currentPage.getImage());
	}

	/**
	 * Update the tree to use the specified <code>Font</code>.
	 * 
	 * @param dialogFont the <code>Font</code> to use.
	 * @since 1.0
	 */
	protected void updateTreeFont(Font dialogFont) {
		getTreeViewer().getControl().setFont(dialogFont);
	}

	@Override
	public Object getSelectedPage() {
		return getCurrentPage();
	}

	@Override
	public void addPageChangedListener(IPageChangedListener listener) {
		pageChangedListeners.add(listener);
	}

	@Override
	public void removePageChangedListener(IPageChangedListener listener) {
		pageChangedListeners.remove(listener);
	}

	/**
	 * Notifies any selection changed listeners that the selected page has changed. Only listeners
	 * registered at the time this method is called are notified.
	 *
	 * @param event a selection changed event
	 *
	 * @see IPageChangedListener#pageChanged
	 * 
	 * @since 1.0
	 */
	protected void firePageChanged(final PageChangedEvent event) {
		Object[] listeners = pageChangedListeners.getListeners();
		for (Object listener : listeners) {
			final IPageChangedListener changeListener = (IPageChangedListener) listener;
			SafeRunnable.run(new SafeRunnable() {
				private static final long serialVersionUID = 1L;

				public void run() {
					changeListener.pageChanged(event);
				}
			});
		}
	}

	/**
	 * The preference dialog implementation of this <code>Dialog</code> framework method sends
	 * <code>performOk</code> to all pages of the preference dialog, then calls <code>handleSave</code>
	 * on this dialog to save any state, and then calls <code>close</code> to close this dialog.
	 */
	protected void okPressed() {
		SafeRunnable.run(new SafeRunnable() {
			private static final long serialVersionUID = 1L;

			private boolean errorOccurred;

			public void run() {
				okButton.setEnabled(false);
				errorOccurred = false;
				boolean hasFailedOK = false;
				try {
					// Notify all the pages and give them a chance to abort
					Iterator<?> nodes = getPreferenceManager().getElements(PreferenceManager.PRE_ORDER).iterator();
					while (nodes.hasNext()) {
						IPreferenceNode node = (IPreferenceNode) nodes.next();
						IPreferencePage page = node.getPage();
						if (page != null) {
							if (!page.performOk()) {
								hasFailedOK = true;
								return;
							}
						}
					}
				} catch (Exception e) {
					handleException(e);
				} finally {
					// Don't bother closing if the OK failed. (TODO: :FVK: to improve this functionality)
					if (hasFailedOK) {
						okButton.setEnabled(true);
						return;
					}

					if (!errorOccurred) {
						// Give subclasses the choice to save the state of the preference pages.
						handleSave();
					}
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.core.runtime.ISafeRunnable#handleException(java.lang.Throwable)
			 */
			public void handleException(Throwable e) {
				errorOccurred = true;

				Policy.getLog().log(new Status(IStatus.ERROR, Policy.JFACE, 0, e.toString(), e));

				clearSelectedNode();
				String message = JFaceResources.getString("SafeRunnable.errorMessage"); //$NON-NLS-1$

				Policy.getStatusHandler().show(new Status(IStatus.ERROR, Policy.JFACE, message, e),
						JFaceResources.getString("Error")); //$NON-NLS-1$

			}
		});
	}

	/**
	 * Save the values specified in the pages.
	 * <p>
	 * The default implementation of this framework method saves all pages of type
	 * <code>PreferencePage</code> (if their store needs saving and is a <code>PreferenceStore</code>).
	 * </p>
	 * <p>
	 * Subclasses may override.
	 * </p>
	 */
	protected void handleSave() {
		Iterator<?> nodes = getPreferenceManager().getElements(PreferenceManager.PRE_ORDER).iterator();
		while (nodes.hasNext()) {
			IPreferenceNode node = (IPreferenceNode) nodes.next();
			IPreferencePage page = node.getPage();
			if (page instanceof PreferencePage preferencePage) {
				// Save now in case tbe wiki does not shutdown cleanly.
				IPreferenceStore store = preferencePage.getPreferenceStore();
				if (store != null && store.needsSaving()
						&& store instanceof IPersistentPreferenceStore persistentStore) {
					try {
						persistentStore.save();
					} catch (IOException e) {
						String message = JFaceResources.format("PreferenceDialog.saveErrorMessage", //$NON-NLS-1$
								new Object[] { page.getTitle(), e.getMessage() });
						Policy.getStatusHandler().show(new Status(IStatus.ERROR, Policy.JFACE, message, e),
								JFaceResources.getString("PreferenceDialog.saveErrorTitle")); //$NON-NLS-1$
					}
				}
			}
		}
	}

	protected void cancelPressed() {
		// Inform all pages that we are cancelling.
		Iterator<?> nodes = getPreferenceManager().getElements(PreferenceManager.PRE_ORDER).iterator();
		while (nodes.hasNext()) {
			final IPreferenceNode node = (IPreferenceNode) nodes.next();
			if (getPage(node) != null) {
				SafeRunnable.run(new SafeRunnable() {
					private static final long serialVersionUID = 1L;

					public void run() {
						if (!getPage(node).performCancel()) {
							return;
						}
					}
				});
			}
		}

		// Give subclasses the choice to save the state of the preference pages if needed
		handleSave();
	}

	/**
	 * Clear the last selected node. This is so that we not chache the last selection in case of an
	 * error.
	 */
	void clearSelectedNode() {
		setSelectedNodePreference(null);
	}

}
