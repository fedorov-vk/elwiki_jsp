/**
 */
package org.elwiki_data;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tags List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.TagsList#getTag <em>Tag</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getTagsList()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface TagsList extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Tag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tag</em>' attribute.
	 * @see #setTag(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getTagsList_Tag()
	 * @model ordered="false"
	 * @generated
	 */
	String getTag();

	/**
	 * Sets the value of the '{@link org.elwiki_data.TagsList#getTag <em>Tag</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tag</em>' attribute.
	 * @see #getTag()
	 * @generated
	 */
	void setTag(String value);

} // TagsList
