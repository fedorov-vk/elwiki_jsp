/**
 */
package org.elwiki_data.impl;

import java.lang.Cloneable;
import java.lang.Comparable;
import java.lang.Object;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EClass;

import org.elwiki_data.Acl;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Wiki Page</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getLastVersion <em>Last Version</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getRedirect <em>Redirect</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getViewCount <em>View Count</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getPageContents <em>Page Contents</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getAttachments <em>Attachments</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getWiki <em>Wiki</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getOldParents <em>Old Parents</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getPageReferences <em>Page References</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getAcl <em>Acl</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#isWebLog <em>Web Log</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getAttributes <em>Attributes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WikiPageImpl extends ComparableImpl implements WikiPage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WikiPageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.WIKI_PAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getId() {
		return (String)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__ID, newId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
		return (String)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__NAME, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__NAME, newName);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDescription() {
		return (String)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__DESCRIPTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDescription(String newDescription) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__DESCRIPTION, newDescription);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAlias() {
		return (String)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__ALIAS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAlias(String newAlias) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__ALIAS, newAlias);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRedirect() {
		return (String)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__REDIRECT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRedirect(String newRedirect) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__REDIRECT, newRedirect);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getViewCount() {
		return (Integer)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__VIEW_COUNT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setViewCount(int newViewCount) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__VIEW_COUNT, newViewCount);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<PageContent> getPageContents() {
		return (EList<PageContent>)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__PAGE_CONTENTS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<PageAttachment> getAttachments() {
		return (EList<PageAttachment>)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__ATTACHMENTS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getWiki() {
		return (String)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__WIKI, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWiki(String newWiki) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__WIKI, newWiki);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<WikiPage> getChildren() {
		return (EList<WikiPage>)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__CHILDREN, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WikiPage getParent() {
		return (WikiPage)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__PARENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParent(WikiPage newParent) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__PARENT, newParent);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String[] getOldParents() {
		return (String[])eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__OLD_PARENTS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setOldParents(String[] newOldParents) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__OLD_PARENTS, newOldParents);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EList<PageReference> getPageReferences() {
		return (EList<PageReference>)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__PAGE_REFERENCES, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getLastVersion() {
		return (Integer)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__LAST_VERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLastVersion(int newLastVersion) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__LAST_VERSION, newLastVersion);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Acl getAcl() {
		return (Acl)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__ACL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAcl(Acl newAcl) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__ACL, newAcl);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isWebLog() {
		return (Boolean)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__WEB_LOG, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWebLog(boolean newWebLog) {
		eSet(Elwiki_dataPackage.Literals.WIKI_PAGE__WEB_LOG, newWebLog);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public EMap<String, Object> getAttributes() {
		return (EMap<String, Object>)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__ATTRIBUTES, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int compareTo(final Object obj) {
		if (obj instanceof WikiPage) {
			WikiPage page = (WikiPage) obj;
			String obj1 = this.getName(), obj2 = page.getName();
			if (obj1 == null) {
				return -1;
			}
			if (obj2 == null) {
				return 1;
			}
			if (obj1.equals(obj2)) {
				return 0;
			}
				return obj1.compareTo(obj2);
		}
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object clone() {
		Object o = new Object(); // :FVK:
		return o;
		
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getLastModifiedDate() {
		Date lastModifiedDate;
		
		List<PageContent> contents = new ArrayList<>(this.getPageContents());
		if (contents.size() > 0) {
			contents.sort(new Comparator<PageContent>() {
				@Override
				public int compare(PageContent o1, PageContent o2) {
					Date date1 = o1.getCreationDate();
					Date date2 = o2.getCreationDate();
					if (date1 != null) {
						return date1.compareTo(date2);
					} else if (date2 != null) {
						return -1;
					}
		
					return 0;
				}
			});
			lastModifiedDate = contents.get(contents.size() - 1).getCreationDate();
		} else {
			lastModifiedDate = new GregorianCalendar(1972, 2, 12).getTime(); //:FVK: workaround.
		}
		
		return lastModifiedDate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAuthor() {
		PageContent content = getLastContent();
		return (content != null) ? content.getAuthor() : "";
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PageContent getLastContent() {
		PageContent result = null;
		int version = 0;
		EList<PageContent> attachContents = getPageContents();
		for (PageContent pageContent : attachContents) {
			int contentVersion = pageContent.getVersion();
			if (version < contentVersion) {
				version = contentVersion;
				result = pageContent;
			}
		}
		
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getVersion() {
		PageContent content = getLastContent();
		return (content != null) ? content.getVersion() : -1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getAttribute(final String name) {
		return getAttributes().get(name);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttribute(final String name, final Object value) {
		getAttributes().put( name, value );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isInternalPage() {
		String pageId = this.getId();
		return (pageId.startsWith("w"))? true : false;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<PageContent> getPageContentsReversed() {
		List<PageContent> contents = new ArrayList<>(this.getPageContents());
		contents.sort(Comparator.comparing(PageContent::getVersion, Comparator.reverseOrder()));
		
		return contents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedOperationID(int baseOperationID, Class<?> baseClass) {
		if (baseClass == Comparable.class) {
			switch (baseOperationID) {
				case Elwiki_dataPackage.COMPARABLE___COMPARE_TO__OBJECT: return Elwiki_dataPackage.WIKI_PAGE___COMPARE_TO__OBJECT;
				default: return super.eDerivedOperationID(baseOperationID, baseClass);
			}
		}
		if (baseClass == Cloneable.class) {
			switch (baseOperationID) {
				case Elwiki_dataPackage.CLONEABLE___CLONE: return Elwiki_dataPackage.WIKI_PAGE___CLONE;
				default: return -1;
			}
		}
		return super.eDerivedOperationID(baseOperationID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case Elwiki_dataPackage.WIKI_PAGE___COMPARE_TO__OBJECT:
				return compareTo((Object)arguments.get(0));
			case Elwiki_dataPackage.WIKI_PAGE___CLONE:
				return clone();
			case Elwiki_dataPackage.WIKI_PAGE___GET_LAST_MODIFIED_DATE:
				return getLastModifiedDate();
			case Elwiki_dataPackage.WIKI_PAGE___GET_AUTHOR:
				return getAuthor();
			case Elwiki_dataPackage.WIKI_PAGE___GET_LAST_CONTENT:
				return getLastContent();
			case Elwiki_dataPackage.WIKI_PAGE___GET_VERSION:
				return getVersion();
			case Elwiki_dataPackage.WIKI_PAGE___GET_ATTRIBUTE__STRING:
				return getAttribute((String)arguments.get(0));
			case Elwiki_dataPackage.WIKI_PAGE___SET_ATTRIBUTE__STRING_OBJECT:
				setAttribute((String)arguments.get(0), arguments.get(1));
				return null;
			case Elwiki_dataPackage.WIKI_PAGE___IS_INTERNAL_PAGE:
				return isInternalPage();
			case Elwiki_dataPackage.WIKI_PAGE___GET_PAGE_CONTENTS_REVERSED:
				return getPageContentsReversed();
		}
		return super.eInvoke(operationID, arguments);
	}

	public String toString() {
		String cdoId = (this.cdoID() != null)? this.cdoID().toString() : "noCDOid";
		return this.getName() + " / " + this.getId() + " : " + cdoId;
	}
} //WikiPageImpl
