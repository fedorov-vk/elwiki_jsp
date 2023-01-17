/**
 */
package org.elwiki_data.impl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageReference;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.impl.PageReferenceImpl#getPageId <em>Page Id</em>}</li>
 *   <li>{@link org.elwiki_data.impl.PageReferenceImpl#getWikipage <em>Wikipage</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PageReferenceImpl extends CDOObjectImpl implements PageReference {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PageReferenceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return Elwiki_dataPackage.Literals.PAGE_REFERENCE;
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
	public String getPageId() {
		return (String)eGet(Elwiki_dataPackage.Literals.PAGE_REFERENCE__PAGE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setPageId(String newPageId) {
		eSet(Elwiki_dataPackage.Literals.PAGE_REFERENCE__PAGE_ID, newPageId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WikiPage getWikipage() {
		return (WikiPage)eGet(Elwiki_dataPackage.Literals.PAGE_REFERENCE__WIKIPAGE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setWikipage(WikiPage newWikipage) {
		eSet(Elwiki_dataPackage.Literals.PAGE_REFERENCE__WIKIPAGE, newWikipage);
	}

} //PageReferenceImpl
