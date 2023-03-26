/**
 */
package org.elwiki_data;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tags List</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.TagsList#getTags <em>Tags</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getTagsList()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface TagsList extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Tags</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' attribute list.
	 * @see org.elwiki_data.Elwiki_dataPackage#getTagsList_Tags()
	 * @model ordered="false"
	 * @generated
	 */
	EList<String> getTags();

} // TagsList
