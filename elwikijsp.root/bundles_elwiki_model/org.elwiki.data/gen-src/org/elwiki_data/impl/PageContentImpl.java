/**
 */
package org.elwiki_data.impl;

import java.lang.reflect.InvocationTargetException;

import java.util.Date;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageContent;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.PageContentImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageContentImpl#getLastModify <em>Last Modify</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageContentImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageContentImpl#getChangeNote <em>Change Note</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageContentImpl#getContent <em>Content</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageContentImpl#getWikipage <em>Wikipage</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PageContentImpl extends CDOObjectImpl implements PageContent {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.PAGE_CONTENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected int eStaticFeatureCount() {
		return 0;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getVersion() {
		return (Integer)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__VERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setVersion(int newVersion) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__VERSION, newVersion);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Date getLastModify() {
		return (Date)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__LAST_MODIFY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLastModify(Date newLastModify) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__LAST_MODIFY, newLastModify);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAuthor() {
		return (String)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__AUTHOR, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAuthor(String newAuthor) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__AUTHOR, newAuthor);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getChangeNote() {
		return (String)eGet(Elwiki_dataPackage.Literals.IHISTORY_INFO__CHANGE_NOTE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setChangeNote(String newChangeNote) {
		eSet(Elwiki_dataPackage.Literals.IHISTORY_INFO__CHANGE_NOTE, newChangeNote);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContent() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGE_CONTENT__CONTENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContent(String newContent) {
		eSet(Elwiki_dataPackage.Literals.PAGE_CONTENT__CONTENT, newContent);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WikiPage getWikipage() {
		return (WikiPage)eGet(Elwiki_dataPackage.Literals.PAGE_CONTENT__WIKIPAGE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWikipage(WikiPage newWikipage) {
		eSet(Elwiki_dataPackage.Literals.PAGE_CONTENT__WIKIPAGE, newWikipage);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getLength() {
		return getContent().length();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
			case Elwiki_dataPackage.PAGE_CONTENT___GET_LENGTH:
				return getLength();
		}
		return super.eInvoke(operationID, arguments);
	}

} //PageContentImpl
