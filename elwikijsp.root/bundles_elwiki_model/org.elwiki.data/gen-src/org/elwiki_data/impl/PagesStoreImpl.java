/**
 */
package org.elwiki_data.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Pages Store</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.PagesStoreImpl#getWikipages <em>Wikipages</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PagesStoreImpl#getMainPageId <em>Main Page Id</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PagesStoreImpl#getNextPageId <em>Next Page Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PagesStoreImpl extends CDOObjectImpl implements PagesStore {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PagesStoreImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.PAGES_STORE;
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
	@SuppressWarnings("unchecked")
	@Override
	public EList<WikiPage> getWikipages() {
		return (EList<WikiPage>)eGet(Elwiki_dataPackage.Literals.PAGES_STORE__WIKIPAGES, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMainPageId() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGES_STORE__MAIN_PAGE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMainPageId(String newMainPageId) {
		eSet(Elwiki_dataPackage.Literals.PAGES_STORE__MAIN_PAGE_ID, newMainPageId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getNextPageId() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGES_STORE__NEXT_PAGE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNextPageId(String newNextPageId) {
		eSet(Elwiki_dataPackage.Literals.PAGES_STORE__NEXT_PAGE_ID, newNextPageId);
	}

} //PagesStoreImpl
