/**
 */
package org.elwiki_data;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.elwiki_data.Elwiki_dataPackage
 * @generated
 */
public interface Elwiki_dataFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Elwiki_dataFactory eINSTANCE = org.elwiki_data.impl.Elwiki_dataFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Wiki Page</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Wiki Page</em>'.
	 * @generated
	 */
	WikiPage createWikiPage();

	/**
	 * Returns a new object of class '<em>Pages Store</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Pages Store</em>'.
	 * @generated
	 */
	PagesStore createPagesStore();

	/**
	 * Returns a new object of class '<em>Page Content</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Page Content</em>'.
	 * @generated
	 */
	PageContent createPageContent();

	/**
	 * Returns a new object of class '<em>Page Attachment</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Page Attachment</em>'.
	 * @generated
	 */
	PageAttachment createPageAttachment();

	/**
	 * Returns a new object of class '<em>Attachment Content</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Attachment Content</em>'.
	 * @generated
	 */
	AttachmentContent createAttachmentContent();

	/**
	 * Returns a new object of class '<em>Comparable</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Comparable</em>'.
	 * @generated
	 */
	Comparable createComparable();

	/**
	 * Returns a new object of class '<em>Cloneable</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Cloneable</em>'.
	 * @generated
	 */
	Cloneable createCloneable();

	/**
	 * Returns a new object of class '<em>Page Reference</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Page Reference</em>'.
	 * @generated
	 */
	PageReference createPageReference();

	/**
	 * Returns a new object of class '<em>Unknown Page</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Unknown Page</em>'.
	 * @generated
	 */
	UnknownPage createUnknownPage();

	/**
	 * Returns a new object of class '<em>Tags List</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Tags List</em>'.
	 * @generated
	 */
	TagsList createTagsList();

	/**
	 * Returns a new object of class '<em>Acl Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Acl Info</em>'.
	 * @generated
	 */
	AclInfo createAclInfo();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	Elwiki_dataPackage getElwiki_dataPackage();

} //Elwiki_dataFactory
