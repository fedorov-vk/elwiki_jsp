/**
 */
package org.elwiki_data;

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IModify Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.IModifyInfo#getVersion <em>Version</em>}</li>
 *   <li>{@link org.elwiki_data.IModifyInfo#getLastModify <em>Last Modify</em>}</li>
 *   <li>{@link org.elwiki_data.IModifyInfo#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.elwiki_data.IModifyInfo#getChangeNote <em>Change Note</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getIModifyInfo()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface IModifyInfo extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(int)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIModifyInfo_Version()
	 * @model
	 * @generated
	 */
	int getVersion();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IModifyInfo#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(int value);

	/**
	 * Returns the value of the '<em><b>Last Modify</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Modify</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Modify</em>' attribute.
	 * @see #setLastModify(Date)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIModifyInfo_LastModify()
	 * @model
	 * @generated
	 */
	Date getLastModify();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IModifyInfo#getLastModify <em>Last Modify</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Modify</em>' attribute.
	 * @see #getLastModify()
	 * @generated
	 */
	void setLastModify(Date value);

	/**
	 * Returns the value of the '<em><b>Author</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Author</em>' attribute.
	 * @see #setAuthor(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIModifyInfo_Author()
	 * @model default=""
	 * @generated
	 */
	String getAuthor();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IModifyInfo#getAuthor <em>Author</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Author</em>' attribute.
	 * @see #getAuthor()
	 * @generated
	 */
	void setAuthor(String value);

	/**
	 * Returns the value of the '<em><b>Change Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Change Note</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Change Note</em>' attribute.
	 * @see #setChangeNote(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIModifyInfo_ChangeNote()
	 * @model
	 * @generated
	 */
	String getChangeNote();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IModifyInfo#getChangeNote <em>Change Note</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Change Note</em>' attribute.
	 * @see #getChangeNote()
	 * @generated
	 */
	void setChangeNote(String value);

} // IModifyInfo
