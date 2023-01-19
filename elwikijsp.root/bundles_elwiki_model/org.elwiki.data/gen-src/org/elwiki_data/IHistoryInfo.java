/**
 */
package org.elwiki_data;

import java.util.Date;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>IHistory Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.elwiki_data.IHistoryInfo#getVersion <em>Version</em>}</li>
 *   <li>{@link org.elwiki_data.IHistoryInfo#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.elwiki_data.IHistoryInfo#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.elwiki_data.IHistoryInfo#getChangeNote <em>Change Note</em>}</li>
 * </ul>
 *
 * @see org.elwiki_data.Elwiki_dataPackage#getIHistoryInfo()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface IHistoryInfo extends CDOObject {
	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(short)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIHistoryInfo_Version()
	 * @model
	 * @generated
	 */
	short getVersion();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IHistoryInfo#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(short value);

	/**
	 * Returns the value of the '<em><b>Creation Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Modify</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Creation Date</em>' attribute.
	 * @see #setCreationDate(Date)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIHistoryInfo_CreationDate()
	 * @model
	 * @generated
	 */
	Date getCreationDate();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IHistoryInfo#getCreationDate <em>Creation Date</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Creation Date</em>' attribute.
	 * @see #getCreationDate()
	 * @generated
	 */
	void setCreationDate(Date value);

	/**
	 * Returns the value of the '<em><b>Author</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Author</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Author</em>' attribute.
	 * @see #setAuthor(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIHistoryInfo_Author()
	 * @model default=""
	 * @generated
	 */
	String getAuthor();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IHistoryInfo#getAuthor <em>Author</em>}' attribute.
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
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * <p>
	 * If the meaning of the '<em>Change Note</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Change Note</em>' attribute.
	 * @see #setChangeNote(String)
	 * @see org.elwiki_data.Elwiki_dataPackage#getIHistoryInfo_ChangeNote()
	 * @model
	 * @generated
	 */
	String getChangeNote();

	/**
	 * Sets the value of the '{@link org.elwiki_data.IHistoryInfo#getChangeNote <em>Change Note</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Change Note</em>' attribute.
	 * @see #getChangeNote()
	 * @generated
	 */
	void setChangeNote(String value);

} // IHistoryInfo
