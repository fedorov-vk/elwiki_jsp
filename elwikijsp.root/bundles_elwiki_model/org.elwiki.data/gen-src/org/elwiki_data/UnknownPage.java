/**
 */
package org.elwiki_data;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Unknown Page</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.UnknownPage#getPageName <em>Page Name</em>}</li>
 *   <li>{@link org.elwiki_data.UnknownPage#getWikipage <em>Wikipage</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getUnknownPage()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface UnknownPage extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Page Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Page Name</em>' attribute.
	 * @see #setPageName(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getUnknownPage_PageName()
	 * @model
	 * @generated
	 */
	String getPageName();

	/**
	 * Sets the value of the '{@link org.elwiki_data.UnknownPage#getPageName <em>Page Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Page Name</em>' attribute.
	 * @see #getPageName()
	 * @generated
	 */
	void setPageName(String value);

	/**
	 * Returns the value of the '<em><b>Wikipage</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.WikiPage#getUnknownPages <em>Unknown Pages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Wikipage</em>' container reference.
	 * @see #setWikipage(WikiPage)
	 * @see org.elwiki_data.Elwiki_dataPackage#getUnknownPage_Wikipage()
	 * @see org.elwiki_data.WikiPage#getUnknownPages
	 * @model opposite="unknownPages" transient="false"
	 * @generated
	 */
	WikiPage getWikipage();

	/**
	 * Sets the value of the '{@link org.elwiki_data.UnknownPage#getWikipage <em>Wikipage</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wikipage</em>' container reference.
	 * @see #getWikipage()
	 * @generated
	 */
	void setWikipage(WikiPage value);

} // UnknownPage
