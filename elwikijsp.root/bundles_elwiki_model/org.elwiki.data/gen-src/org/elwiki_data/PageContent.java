/**
 */
package org.elwiki_data;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Page Content</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.PageContent#getContent <em>Content</em>}</li>
 *   <li>{@link org.elwiki_data.PageContent#getWikipage <em>Wikipage</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getPageContent()
 * @model
 * @generated
 */
public interface PageContent extends IHistoryInfo {
	/**
	 * Returns the value of the '<em><b>Content</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Content</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Content</em>' attribute.
	 * @see #setContent(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageContent_Content()
	 * @model default=""
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='VARCHAR' columnLength='10000000'"
	 * @generated
	 */
	String getContent();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageContent#getContent <em>Content</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content</em>' attribute.
	 * @see #getContent()
	 * @generated
	 */
	void setContent(String value);

	/**
	 * Returns the value of the '<em><b>Wikipage</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link org.elwiki_data.WikiPage#getPageContents <em>Page Contents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Wikipage</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Wikipage</em>' container reference.
	 * @see #setWikipage(WikiPage)
	 * @see org.elwiki_data.Elwiki_dataPackage#getPageContent_Wikipage()
	 * @see org.elwiki_data.WikiPage#getPageContents
	 * @model opposite="pageContents" transient="false"
	 * @generated
	 */
	WikiPage getWikipage();

	/**
	 * Sets the value of the '{@link org.elwiki_data.PageContent#getWikipage <em>Wikipage</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Wikipage</em>' container reference.
	 * @see #getWikipage()
	 * @generated
	 */
	void setWikipage(WikiPage value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Returns length of content.
	 * <!-- end-model-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	Integer getLength();

} // PageContent
