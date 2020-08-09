/**
 */
package org.elwiki_data.impl;

import java.lang.Cloneable;
import java.lang.Comparable;
import java.lang.Object;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import org.eclipse.emf.common.util.EList;

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
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getRedirect <em>Redirect</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getViewCount <em>View Count</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getPagecontents <em>Pagecontents</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getAttachments <em>Attachments</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getWiki <em>Wiki</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getOldParents <em>Old Parents</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getPageReferences <em>Page References</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getTotalAttachment <em>Total Attachment</em>}</li>
 *   <li>{@link org.elwiki_data.impl.WikiPageImpl#getAcl <em>Acl</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WikiPageImpl extends ComparableImpl implements WikiPage {

	/**
	 * @generated NOT
	 */
	private final HashMap<String, Object> m_attributes = new HashMap<String, Object>();

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
	public EList<PageContent> getPagecontents() {
		return (EList<PageContent>)eGet(Elwiki_dataPackage.Literals.WIKI_PAGE__PAGECONTENTS, true);
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
	 * @generated NOT
	 */
	public int getTotalAttachment() {
		EList<PageAttachment> attachments = getAttachments();
		return attachments.size();
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
	public Date getLastModified() {
		EList<PageContent> pageContent = this.getPagecontents();
		int listSize = pageContent.size();
		return (listSize>0)? pageContent.get(listSize-1).getLastModify() : new Date(1972,2,12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttribute(final String key, final Object attribute) {
		this.m_attributes.put( key, attribute );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getAttribute(final String key) {
		return this.m_attributes.get( key );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAuthor() {
		String author = null; // "unknown";
		EList<PageContent> pageContents = getPagecontents();
		if (!pageContents.isEmpty()) {
			PageContent content = pageContents.get(pageContents.size() - 1);
			author = content.getAuthor();
		}
		return author;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PageContent getLastContent() {
		PageContent result = null;
		int currentVersion = -1;
		for (PageContent pageContent : this.getPagecontents()) {
			int contentVersion = pageContent.getVersion();
			if (currentVersion < contentVersion) {
				result = pageContent;
				currentVersion = contentVersion;
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
			case Elwiki_dataPackage.WIKI_PAGE___GET_LAST_MODIFIED:
				return getLastModified();
			case Elwiki_dataPackage.WIKI_PAGE___SET_ATTRIBUTE__STRING_OBJECT:
				setAttribute((String)arguments.get(0), (Object)arguments.get(1));
				return null;
			case Elwiki_dataPackage.WIKI_PAGE___GET_ATTRIBUTE__STRING:
				return getAttribute((String)arguments.get(0));
			case Elwiki_dataPackage.WIKI_PAGE___GET_AUTHOR:
				return getAuthor();
			case Elwiki_dataPackage.WIKI_PAGE___GET_LAST_CONTENT:
				return getLastContent();
			case Elwiki_dataPackage.WIKI_PAGE___GET_VERSION:
				return getVersion();
		}
		return super.eInvoke(operationID, arguments);
	}

} //WikiPageImpl
