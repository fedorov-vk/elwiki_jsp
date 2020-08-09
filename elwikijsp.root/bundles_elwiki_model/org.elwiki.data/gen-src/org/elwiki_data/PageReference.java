/**
 */
package org.elwiki_data;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Page Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.PageReference#getPageId <em>Page Id</em>}</li>
 *   <li>{@link org.elwiki_data.PageReference#getWikipage <em>Wikipage</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getPageReference()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface PageReference extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Page Id</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Page Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Page Id</em>' attribute.
	 * @see #setPageId(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageReference_PageId()
	 * @model default=""
	 * @generated
	 */
	String getPageId();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageReference#getPageId <em>Page Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Page Id</em>' attribute.
	 * @see #getPageId()
	 * @generated
	 */
	void setPageId(String value);

	/**
	 * Returns the value of the '<em><b>Wikipage</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.WikiPage#getPageReferences <em>Page References</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wikipage</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wikipage</em>' container reference.
	 * @see #setWikipage(WikiPage)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageReference_Wikipage()
	 * @see org.elwiki_data.WikiPage#getPageReferences
	 * @model opposite="pageReferences" transient="false"
	 * @generated
	 */
	WikiPage getWikipage();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageReference#getWikipage <em>Wikipage</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wikipage</em>' container reference.
	 * @see #getWikipage()
	 * @generated
	 */
	void setWikipage(WikiPage value);

} // PageReference
