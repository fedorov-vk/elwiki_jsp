/**
 */
package org.elwiki_data;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.elwiki_data.Elwiki_dataFactory
 * @model kind="package"
 * @generated
 */
public interface Elwiki_dataPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "elwiki_data";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http:///org/elwiki/data.ecore";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "elwiki.data";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Elwiki_dataPackage eINSTANCE = org.elwiki_data.impl.Elwiki_dataPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.ComparableImpl <em>Comparable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.ComparableImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getComparable()
	 * @generated
	 */
	int COMPARABLE = 6;

	/**
	 * The number of structural features of the '<em>Comparable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARABLE_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Compare To</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARABLE___COMPARE_TO__OBJECT = 0;

	/**
	 * The number of operations of the '<em>Comparable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPARABLE_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.WikiPageImpl <em>Wiki Page</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.WikiPageImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getWikiPage()
	 * @generated
	 */
	int WIKI_PAGE = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__ID = COMPARABLE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__NAME = COMPARABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__DESCRIPTION = COMPARABLE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Alias</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__ALIAS = COMPARABLE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Redirect</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__REDIRECT = COMPARABLE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>View Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__VIEW_COUNT = COMPARABLE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Page Contents</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__PAGE_CONTENTS = COMPARABLE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Attachments</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__ATTACHMENTS = COMPARABLE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Wiki</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__WIKI = COMPARABLE_FEATURE_COUNT + 8;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__CHILDREN = COMPARABLE_FEATURE_COUNT + 9;

	/**
	 * The feature id for the '<em><b>Parent</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__PARENT = COMPARABLE_FEATURE_COUNT + 10;

	/**
	 * The feature id for the '<em><b>Old Parents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__OLD_PARENTS = COMPARABLE_FEATURE_COUNT + 11;

	/**
	 * The feature id for the '<em><b>Page References</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__PAGE_REFERENCES = COMPARABLE_FEATURE_COUNT + 12;

	/**
	 * The feature id for the '<em><b>Web Log</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__WEB_LOG = COMPARABLE_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__ATTRIBUTES = COMPARABLE_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Unknown Pages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__UNKNOWN_PAGES = COMPARABLE_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__TAGS = COMPARABLE_FEATURE_COUNT + 16;

	/**
	 * The feature id for the '<em><b>Acl Infos</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__ACL_INFOS = COMPARABLE_FEATURE_COUNT + 17;

	/**
	 * The number of structural features of the '<em>Wiki Page</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE_FEATURE_COUNT = COMPARABLE_FEATURE_COUNT + 18;

	/**
	 * The operation id for the '<em>Compare To</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___COMPARE_TO__OBJECT = COMPARABLE_OPERATION_COUNT + 1;

	/**
	 * The operation id for the '<em>Clone</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___CLONE = COMPARABLE_OPERATION_COUNT + 2;

	/**
	 * The operation id for the '<em>Get Last Modified Date</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_LAST_MODIFIED_DATE = COMPARABLE_OPERATION_COUNT + 3;

	/**
	 * The operation id for the '<em>Get Author</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_AUTHOR = COMPARABLE_OPERATION_COUNT + 4;

	/**
	 * The operation id for the '<em>Get Last Content</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_LAST_CONTENT = COMPARABLE_OPERATION_COUNT + 5;

	/**
	 * The operation id for the '<em>Get Last Version</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_LAST_VERSION = COMPARABLE_OPERATION_COUNT + 6;

	/**
	 * The operation id for the '<em>Get Attribute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_ATTRIBUTE__STRING = COMPARABLE_OPERATION_COUNT + 7;

	/**
	 * The operation id for the '<em>Set Attribute</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___SET_ATTRIBUTE__STRING_OBJECT = COMPARABLE_OPERATION_COUNT + 8;

	/**
	 * The operation id for the '<em>Is Internal Page</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___IS_INTERNAL_PAGE = COMPARABLE_OPERATION_COUNT + 9;

	/**
	 * The operation id for the '<em>Get Page Contents Reversed</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_PAGE_CONTENTS_REVERSED = COMPARABLE_OPERATION_COUNT + 10;

	/**
	 * The operation id for the '<em>To String</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___TO_STRING = COMPARABLE_OPERATION_COUNT + 11;

	/**
	 * The operation id for the '<em>Get Content Text</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_CONTENT_TEXT__INT = COMPARABLE_OPERATION_COUNT + 12;

	/**
	 * The operation id for the '<em>Get Content Text</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_CONTENT_TEXT = COMPARABLE_OPERATION_COUNT + 13;

	/**
	 * The number of operations of the '<em>Wiki Page</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE_OPERATION_COUNT = COMPARABLE_OPERATION_COUNT + 14;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.PagesStoreImpl <em>Pages Store</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.PagesStoreImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPagesStore()
	 * @generated
	 */
	int PAGES_STORE = 1;

	/**
	 * The feature id for the '<em><b>Wikipages</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE__WIKIPAGES = 0;

	/**
	 * The feature id for the '<em><b>Main Page Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE__MAIN_PAGE_ID = 1;

	/**
	 * The feature id for the '<em><b>Next Page Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE__NEXT_PAGE_ID = 2;

	/**
	 * The feature id for the '<em><b>Next Attachment Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE__NEXT_ATTACHMENT_ID = 3;

	/**
	 * The feature id for the '<em><b>Tagslist</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE__TAGSLIST = 4;

	/**
	 * The number of structural features of the '<em>Pages Store</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Pages Store</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.IHistoryInfo <em>IHistory Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.IHistoryInfo
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getIHistoryInfo()
	 * @generated
	 */
	int IHISTORY_INFO = 8;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IHISTORY_INFO__VERSION = 0;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IHISTORY_INFO__CREATION_DATE = 1;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IHISTORY_INFO__AUTHOR = 2;

	/**
	 * The feature id for the '<em><b>Change Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IHISTORY_INFO__CHANGE_NOTE = 3;

	/**
	 * The number of structural features of the '<em>IHistory Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IHISTORY_INFO_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>IHistory Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IHISTORY_INFO_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.PageContentImpl <em>Page Content</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.PageContentImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPageContent()
	 * @generated
	 */
	int PAGE_CONTENT = 2;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__VERSION = IHISTORY_INFO__VERSION;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__CREATION_DATE = IHISTORY_INFO__CREATION_DATE;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__AUTHOR = IHISTORY_INFO__AUTHOR;

	/**
	 * The feature id for the '<em><b>Change Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__CHANGE_NOTE = IHISTORY_INFO__CHANGE_NOTE;

	/**
	 * The feature id for the '<em><b>Content</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__CONTENT = IHISTORY_INFO_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Wikipage</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__WIKIPAGE = IHISTORY_INFO_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Page Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT_FEATURE_COUNT = IHISTORY_INFO_FEATURE_COUNT + 2;

	/**
	 * The operation id for the '<em>Get Length</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT___GET_LENGTH = IHISTORY_INFO_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Page Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT_OPERATION_COUNT = IHISTORY_INFO_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.PageAttachmentImpl <em>Page Attachment</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.PageAttachmentImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPageAttachment()
	 * @generated
	 */
	int PAGE_ATTACHMENT = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__NAME = 1;

	/**
	 * The feature id for the '<em><b>Last Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__LAST_VERSION = 2;

	/**
	 * The feature id for the '<em><b>Wikipage</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__WIKIPAGE = 3;

	/**
	 * The feature id for the '<em><b>Attach Contents</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__ATTACH_CONTENTS = 4;

	/**
	 * The number of structural features of the '<em>Page Attachment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT_FEATURE_COUNT = 5;

	/**
	 * The operation id for the '<em>For Last Content</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT___FOR_LAST_CONTENT = 0;

	/**
	 * The operation id for the '<em>For Version Content</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT___FOR_VERSION_CONTENT__INT = 1;

	/**
	 * The number of operations of the '<em>Page Attachment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT_OPERATION_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.AttachmentContentImpl <em>Attachment Content</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.AttachmentContentImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAttachmentContent()
	 * @generated
	 */
	int ATTACHMENT_CONTENT = 4;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT__VERSION = IHISTORY_INFO__VERSION;

	/**
	 * The feature id for the '<em><b>Creation Date</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT__CREATION_DATE = IHISTORY_INFO__CREATION_DATE;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT__AUTHOR = IHISTORY_INFO__AUTHOR;

	/**
	 * The feature id for the '<em><b>Change Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT__CHANGE_NOTE = IHISTORY_INFO__CHANGE_NOTE;

	/**
	 * The feature id for the '<em><b>Place</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT__PLACE = IHISTORY_INFO_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT__SIZE = IHISTORY_INFO_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Page Attachment</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT__PAGE_ATTACHMENT = IHISTORY_INFO_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Attachment Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT_FEATURE_COUNT = IHISTORY_INFO_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Attachment Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTACHMENT_CONTENT_OPERATION_COUNT = IHISTORY_INFO_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link java.lang.Object <em>Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Object
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getObject()
	 * @generated
	 */
	int OBJECT = 5;

	/**
	 * The number of structural features of the '<em>Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJECT_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OBJECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.CloneableImpl <em>Cloneable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.CloneableImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getCloneable()
	 * @generated
	 */
	int CLONEABLE = 7;

	/**
	 * The number of structural features of the '<em>Cloneable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLONEABLE_FEATURE_COUNT = 0;

	/**
	 * The operation id for the '<em>Clone</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLONEABLE___CLONE = 0;

	/**
	 * The number of operations of the '<em>Cloneable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLONEABLE_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.PageReferenceImpl <em>Page Reference</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.PageReferenceImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPageReference()
	 * @generated
	 */
	int PAGE_REFERENCE = 9;

	/**
	 * The feature id for the '<em><b>Page Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_REFERENCE__PAGE_ID = 0;

	/**
	 * The feature id for the '<em><b>Wikipage</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_REFERENCE__WIKIPAGE = 1;

	/**
	 * The number of structural features of the '<em>Page Reference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_REFERENCE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Page Reference</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_REFERENCE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.StringToObjectMapImpl <em>String To Object Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.StringToObjectMapImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getStringToObjectMap()
	 * @generated
	 */
	int STRING_TO_OBJECT_MAP = 10;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_OBJECT_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_OBJECT_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String To Object Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_OBJECT_MAP_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>String To Object Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_OBJECT_MAP_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.UnknownPageImpl <em>Unknown Page</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.UnknownPageImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getUnknownPage()
	 * @generated
	 */
	int UNKNOWN_PAGE = 11;

	/**
	 * The feature id for the '<em><b>Page Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNKNOWN_PAGE__PAGE_NAME = 0;

	/**
	 * The feature id for the '<em><b>Wikipage</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNKNOWN_PAGE__WIKIPAGE = 1;

	/**
	 * The number of structural features of the '<em>Unknown Page</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNKNOWN_PAGE_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Unknown Page</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNKNOWN_PAGE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.TagsListImpl <em>Tags List</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.TagsListImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getTagsList()
	 * @generated
	 */
	int TAGS_LIST = 12;

	/**
	 * The feature id for the '<em><b>Tag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAGS_LIST__TAG = 0;

	/**
	 * The number of structural features of the '<em>Tags List</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAGS_LIST_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Tags List</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAGS_LIST_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.AclInfoImpl <em>Acl Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.AclInfoImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAclInfo()
	 * @generated
	 */
	int ACL_INFO = 13;

	/**
	 * The feature id for the '<em><b>Allow</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_INFO__ALLOW = 0;

	/**
	 * The feature id for the '<em><b>Permission</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_INFO__PERMISSION = 1;

	/**
	 * The feature id for the '<em><b>Roles</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_INFO__ROLES = 2;

	/**
	 * The number of structural features of the '<em>Acl Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_INFO_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Acl Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_INFO_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '<em>Array String</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getArrayString()
	 * @generated
	 */
	int ARRAY_STRING = 14;

	/**
	 * The meta object id for the '<em>Access List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Object
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAccessList()
	 * @generated
	 */
	int ACCESS_LIST = 15;

	/**
	 * The meta object id for the '<em>List Page Content</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getListPageContent()
	 * @generated
	 */
	int LIST_PAGE_CONTENT = 16;


	/**
	 * Returns the meta object for class '{@link org.elwiki_data.WikiPage <em>Wiki Page</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Wiki Page</em>'.
	 * @see org.elwiki_data.WikiPage
	 * @generated
	 */
	EClass getWikiPage();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.elwiki_data.WikiPage#getId()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.elwiki_data.WikiPage#getName()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.elwiki_data.WikiPage#getDescription()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getAlias <em>Alias</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alias</em>'.
	 * @see org.elwiki_data.WikiPage#getAlias()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_Alias();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getRedirect <em>Redirect</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Redirect</em>'.
	 * @see org.elwiki_data.WikiPage#getRedirect()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_Redirect();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getViewCount <em>View Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>View Count</em>'.
	 * @see org.elwiki_data.WikiPage#getViewCount()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_ViewCount();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.WikiPage#getPageContents <em>Page Contents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Page Contents</em>'.
	 * @see org.elwiki_data.WikiPage#getPageContents()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_PageContents();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.WikiPage#getAttachments <em>Attachments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attachments</em>'.
	 * @see org.elwiki_data.WikiPage#getAttachments()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_Attachments();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getWiki <em>Wiki</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Wiki</em>'.
	 * @see org.elwiki_data.WikiPage#getWiki()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_Wiki();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.WikiPage#getChildren <em>Children</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Children</em>'.
	 * @see org.elwiki_data.WikiPage#getChildren()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_Children();

	/**
	 * Returns the meta object for the container reference '{@link org.elwiki_data.WikiPage#getParent <em>Parent</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent</em>'.
	 * @see org.elwiki_data.WikiPage#getParent()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_Parent();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getOldParents <em>Old Parents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Old Parents</em>'.
	 * @see org.elwiki_data.WikiPage#getOldParents()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_OldParents();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.WikiPage#getPageReferences <em>Page References</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Page References</em>'.
	 * @see org.elwiki_data.WikiPage#getPageReferences()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_PageReferences();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#isWebLog <em>Web Log</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Web Log</em>'.
	 * @see org.elwiki_data.WikiPage#isWebLog()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_WebLog();

	/**
	 * Returns the meta object for the map '{@link org.elwiki_data.WikiPage#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Attributes</em>'.
	 * @see org.elwiki_data.WikiPage#getAttributes()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_Attributes();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.WikiPage#getUnknownPages <em>Unknown Pages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Unknown Pages</em>'.
	 * @see org.elwiki_data.WikiPage#getUnknownPages()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_UnknownPages();

	/**
	 * Returns the meta object for the attribute list '{@link org.elwiki_data.WikiPage#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Tags</em>'.
	 * @see org.elwiki_data.WikiPage#getTags()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_Tags();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.WikiPage#getAclInfos <em>Acl Infos</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Acl Infos</em>'.
	 * @see org.elwiki_data.WikiPage#getAclInfos()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_AclInfos();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#compareTo(java.lang.Object) <em>Compare To</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Compare To</em>' operation.
	 * @see org.elwiki_data.WikiPage#compareTo(java.lang.Object)
	 * @generated
	 */
	EOperation getWikiPage__CompareTo__Object();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#clone() <em>Clone</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Clone</em>' operation.
	 * @see org.elwiki_data.WikiPage#clone()
	 * @generated
	 */
	EOperation getWikiPage__Clone();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getLastModifiedDate() <em>Get Last Modified Date</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Last Modified Date</em>' operation.
	 * @see org.elwiki_data.WikiPage#getLastModifiedDate()
	 * @generated
	 */
	EOperation getWikiPage__GetLastModifiedDate();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getAuthor() <em>Get Author</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Author</em>' operation.
	 * @see org.elwiki_data.WikiPage#getAuthor()
	 * @generated
	 */
	EOperation getWikiPage__GetAuthor();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getLastContent() <em>Get Last Content</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Last Content</em>' operation.
	 * @see org.elwiki_data.WikiPage#getLastContent()
	 * @generated
	 */
	EOperation getWikiPage__GetLastContent();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getLastVersion() <em>Get Last Version</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Last Version</em>' operation.
	 * @see org.elwiki_data.WikiPage#getLastVersion()
	 * @generated
	 */
	EOperation getWikiPage__GetLastVersion();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getAttribute(java.lang.String) <em>Get Attribute</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Attribute</em>' operation.
	 * @see org.elwiki_data.WikiPage#getAttribute(java.lang.String)
	 * @generated
	 */
	EOperation getWikiPage__GetAttribute__String();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#setAttribute(java.lang.String, java.lang.Object) <em>Set Attribute</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Set Attribute</em>' operation.
	 * @see org.elwiki_data.WikiPage#setAttribute(java.lang.String, java.lang.Object)
	 * @generated
	 */
	EOperation getWikiPage__SetAttribute__String_Object();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#isInternalPage() <em>Is Internal Page</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Is Internal Page</em>' operation.
	 * @see org.elwiki_data.WikiPage#isInternalPage()
	 * @generated
	 */
	EOperation getWikiPage__IsInternalPage();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getPageContentsReversed() <em>Get Page Contents Reversed</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Page Contents Reversed</em>' operation.
	 * @see org.elwiki_data.WikiPage#getPageContentsReversed()
	 * @generated
	 */
	EOperation getWikiPage__GetPageContentsReversed();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#toString() <em>To String</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>To String</em>' operation.
	 * @see org.elwiki_data.WikiPage#toString()
	 * @generated
	 */
	EOperation getWikiPage__ToString();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getContentText(int) <em>Get Content Text</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Content Text</em>' operation.
	 * @see org.elwiki_data.WikiPage#getContentText(int)
	 * @generated
	 */
	EOperation getWikiPage__GetContentText__int();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getContentText() <em>Get Content Text</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Content Text</em>' operation.
	 * @see org.elwiki_data.WikiPage#getContentText()
	 * @generated
	 */
	EOperation getWikiPage__GetContentText();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.PagesStore <em>Pages Store</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Pages Store</em>'.
	 * @see org.elwiki_data.PagesStore
	 * @generated
	 */
	EClass getPagesStore();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.PagesStore#getWikipages <em>Wikipages</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Wikipages</em>'.
	 * @see org.elwiki_data.PagesStore#getWikipages()
	 * @see #getPagesStore()
	 * @generated
	 */
	EReference getPagesStore_Wikipages();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PagesStore#getMainPageId <em>Main Page Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Main Page Id</em>'.
	 * @see org.elwiki_data.PagesStore#getMainPageId()
	 * @see #getPagesStore()
	 * @generated
	 */
	EAttribute getPagesStore_MainPageId();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PagesStore#getNextPageId <em>Next Page Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Next Page Id</em>'.
	 * @see org.elwiki_data.PagesStore#getNextPageId()
	 * @see #getPagesStore()
	 * @generated
	 */
	EAttribute getPagesStore_NextPageId();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PagesStore#getNextAttachmentId <em>Next Attachment Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Next Attachment Id</em>'.
	 * @see org.elwiki_data.PagesStore#getNextAttachmentId()
	 * @see #getPagesStore()
	 * @generated
	 */
	EAttribute getPagesStore_NextAttachmentId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.PagesStore#getTagslist <em>Tagslist</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Tagslist</em>'.
	 * @see org.elwiki_data.PagesStore#getTagslist()
	 * @see #getPagesStore()
	 * @generated
	 */
	EReference getPagesStore_Tagslist();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.PageContent <em>Page Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page Content</em>'.
	 * @see org.elwiki_data.PageContent
	 * @generated
	 */
	EClass getPageContent();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageContent#getContent <em>Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content</em>'.
	 * @see org.elwiki_data.PageContent#getContent()
	 * @see #getPageContent()
	 * @generated
	 */
	EAttribute getPageContent_Content();

	/**
	 * Returns the meta object for the container reference '{@link org.elwiki_data.PageContent#getWikipage <em>Wikipage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Wikipage</em>'.
	 * @see org.elwiki_data.PageContent#getWikipage()
	 * @see #getPageContent()
	 * @generated
	 */
	EReference getPageContent_Wikipage();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.PageContent#getLength() <em>Get Length</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Length</em>' operation.
	 * @see org.elwiki_data.PageContent#getLength()
	 * @generated
	 */
	EOperation getPageContent__GetLength();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.PageAttachment <em>Page Attachment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page Attachment</em>'.
	 * @see org.elwiki_data.PageAttachment
	 * @generated
	 */
	EClass getPageAttachment();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageAttachment#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.elwiki_data.PageAttachment#getId()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EAttribute getPageAttachment_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageAttachment#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.elwiki_data.PageAttachment#getName()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EAttribute getPageAttachment_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageAttachment#getLastVersion <em>Last Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Version</em>'.
	 * @see org.elwiki_data.PageAttachment#getLastVersion()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EAttribute getPageAttachment_LastVersion();

	/**
	 * Returns the meta object for the container reference '{@link org.elwiki_data.PageAttachment#getWikipage <em>Wikipage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Wikipage</em>'.
	 * @see org.elwiki_data.PageAttachment#getWikipage()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EReference getPageAttachment_Wikipage();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.PageAttachment#getAttachContents <em>Attach Contents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attach Contents</em>'.
	 * @see org.elwiki_data.PageAttachment#getAttachContents()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EReference getPageAttachment_AttachContents();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.PageAttachment#forLastContent() <em>For Last Content</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>For Last Content</em>' operation.
	 * @see org.elwiki_data.PageAttachment#forLastContent()
	 * @generated
	 */
	EOperation getPageAttachment__ForLastContent();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.PageAttachment#forVersionContent(int) <em>For Version Content</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>For Version Content</em>' operation.
	 * @see org.elwiki_data.PageAttachment#forVersionContent(int)
	 * @generated
	 */
	EOperation getPageAttachment__ForVersionContent__int();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.AttachmentContent <em>Attachment Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attachment Content</em>'.
	 * @see org.elwiki_data.AttachmentContent
	 * @generated
	 */
	EClass getAttachmentContent();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.AttachmentContent#getPlace <em>Place</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Place</em>'.
	 * @see org.elwiki_data.AttachmentContent#getPlace()
	 * @see #getAttachmentContent()
	 * @generated
	 */
	EAttribute getAttachmentContent_Place();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.AttachmentContent#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see org.elwiki_data.AttachmentContent#getSize()
	 * @see #getAttachmentContent()
	 * @generated
	 */
	EAttribute getAttachmentContent_Size();

	/**
	 * Returns the meta object for the container reference '{@link org.elwiki_data.AttachmentContent#getPageAttachment <em>Page Attachment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Page Attachment</em>'.
	 * @see org.elwiki_data.AttachmentContent#getPageAttachment()
	 * @see #getAttachmentContent()
	 * @generated
	 */
	EReference getAttachmentContent_PageAttachment();

	/**
	 * Returns the meta object for class '{@link java.lang.Object <em>Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Object</em>'.
	 * @see java.lang.Object
	 * @model instanceClass="java.lang.Object"
	 * @generated
	 */
	EClass getObject();

	/**
	 * Returns the meta object for class '{@link java.lang.Comparable <em>Comparable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Comparable</em>'.
	 * @see java.lang.Comparable
	 * @model instanceClass="java.lang.Comparable"
	 * @generated
	 */
	EClass getComparable();

	/**
	 * Returns the meta object for the '{@link java.lang.Comparable#compareTo(java.lang.Object) <em>Compare To</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Compare To</em>' operation.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * @generated
	 */
	EOperation getComparable__CompareTo__Object();

	/**
	 * Returns the meta object for class '{@link java.lang.Cloneable <em>Cloneable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Cloneable</em>'.
	 * @see java.lang.Cloneable
	 * @model instanceClass="java.lang.Cloneable"
	 * @generated
	 */
	EClass getCloneable();

	/**
	 * Returns the meta object for the '{@link java.lang.Cloneable#clone() <em>Clone</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Clone</em>' operation.
	 * @see java.lang.Cloneable#clone()
	 * @generated
	 */
	EOperation getCloneable__Clone();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.IHistoryInfo <em>IHistory Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IHistory Info</em>'.
	 * @see org.elwiki_data.IHistoryInfo
	 * @generated
	 */
	EClass getIHistoryInfo();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IHistoryInfo#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.elwiki_data.IHistoryInfo#getVersion()
	 * @see #getIHistoryInfo()
	 * @generated
	 */
	EAttribute getIHistoryInfo_Version();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IHistoryInfo#getCreationDate <em>Creation Date</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Creation Date</em>'.
	 * @see org.elwiki_data.IHistoryInfo#getCreationDate()
	 * @see #getIHistoryInfo()
	 * @generated
	 */
	EAttribute getIHistoryInfo_CreationDate();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IHistoryInfo#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see org.elwiki_data.IHistoryInfo#getAuthor()
	 * @see #getIHistoryInfo()
	 * @generated
	 */
	EAttribute getIHistoryInfo_Author();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IHistoryInfo#getChangeNote <em>Change Note</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Change Note</em>'.
	 * @see org.elwiki_data.IHistoryInfo#getChangeNote()
	 * @see #getIHistoryInfo()
	 * @generated
	 */
	EAttribute getIHistoryInfo_ChangeNote();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.PageReference <em>Page Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page Reference</em>'.
	 * @see org.elwiki_data.PageReference
	 * @generated
	 */
	EClass getPageReference();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageReference#getPageId <em>Page Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Page Id</em>'.
	 * @see org.elwiki_data.PageReference#getPageId()
	 * @see #getPageReference()
	 * @generated
	 */
	EAttribute getPageReference_PageId();

	/**
	 * Returns the meta object for the container reference '{@link org.elwiki_data.PageReference#getWikipage <em>Wikipage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Wikipage</em>'.
	 * @see org.elwiki_data.PageReference#getWikipage()
	 * @see #getPageReference()
	 * @generated
	 */
	EReference getPageReference_Wikipage();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To Object Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String To Object Map</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EJavaObject"
	 * @generated
	 */
	EClass getStringToObjectMap();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToObjectMap()
	 * @generated
	 */
	EAttribute getStringToObjectMap_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToObjectMap()
	 * @generated
	 */
	EAttribute getStringToObjectMap_Value();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.UnknownPage <em>Unknown Page</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Unknown Page</em>'.
	 * @see org.elwiki_data.UnknownPage
	 * @generated
	 */
	EClass getUnknownPage();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.UnknownPage#getPageName <em>Page Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Page Name</em>'.
	 * @see org.elwiki_data.UnknownPage#getPageName()
	 * @see #getUnknownPage()
	 * @generated
	 */
	EAttribute getUnknownPage_PageName();

	/**
	 * Returns the meta object for the container reference '{@link org.elwiki_data.UnknownPage#getWikipage <em>Wikipage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Wikipage</em>'.
	 * @see org.elwiki_data.UnknownPage#getWikipage()
	 * @see #getUnknownPage()
	 * @generated
	 */
	EReference getUnknownPage_Wikipage();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.TagsList <em>Tags List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tags List</em>'.
	 * @see org.elwiki_data.TagsList
	 * @generated
	 */
	EClass getTagsList();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.TagsList#getTag <em>Tag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tag</em>'.
	 * @see org.elwiki_data.TagsList#getTag()
	 * @see #getTagsList()
	 * @generated
	 */
	EAttribute getTagsList_Tag();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.AclInfo <em>Acl Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Acl Info</em>'.
	 * @see org.elwiki_data.AclInfo
	 * @generated
	 */
	EClass getAclInfo();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.AclInfo#isAllow <em>Allow</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Allow</em>'.
	 * @see org.elwiki_data.AclInfo#isAllow()
	 * @see #getAclInfo()
	 * @generated
	 */
	EAttribute getAclInfo_Allow();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.AclInfo#getPermission <em>Permission</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Permission</em>'.
	 * @see org.elwiki_data.AclInfo#getPermission()
	 * @see #getAclInfo()
	 * @generated
	 */
	EAttribute getAclInfo_Permission();

	/**
	 * Returns the meta object for the attribute list '{@link org.elwiki_data.AclInfo#getRoles <em>Roles</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Roles</em>'.
	 * @see org.elwiki_data.AclInfo#getRoles()
	 * @see #getAclInfo()
	 * @generated
	 */
	EAttribute getAclInfo_Roles();

	/**
	 * Returns the meta object for data type '<em>Array String</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Array String</em>'.
	 * @model instanceClass="java.lang.String[]"
	 * @generated
	 */
	EDataType getArrayString();

	/**
	 * Returns the meta object for data type '{@link java.lang.Object <em>Access List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Access List</em>'.
	 * @see java.lang.Object
	 * @model instanceClass="java.lang.Object"
	 * @generated
	 */
	EDataType getAccessList();

	/**
	 * Returns the meta object for data type '{@link java.util.List <em>List Page Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>List Page Content</em>'.
	 * @see java.util.List
	 * @model instanceClass="java.util.List&lt;org.elwiki_data.PageContent&gt;"
	 * @generated
	 */
	EDataType getListPageContent();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	Elwiki_dataFactory getElwiki_dataFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.WikiPageImpl <em>Wiki Page</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.WikiPageImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getWikiPage()
		 * @generated
		 */
		EClass WIKI_PAGE = eINSTANCE.getWikiPage();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__ID = eINSTANCE.getWikiPage_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__NAME = eINSTANCE.getWikiPage_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__DESCRIPTION = eINSTANCE.getWikiPage_Description();

		/**
		 * The meta object literal for the '<em><b>Alias</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__ALIAS = eINSTANCE.getWikiPage_Alias();

		/**
		 * The meta object literal for the '<em><b>Redirect</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__REDIRECT = eINSTANCE.getWikiPage_Redirect();

		/**
		 * The meta object literal for the '<em><b>View Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__VIEW_COUNT = eINSTANCE.getWikiPage_ViewCount();

		/**
		 * The meta object literal for the '<em><b>Page Contents</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__PAGE_CONTENTS = eINSTANCE.getWikiPage_PageContents();

		/**
		 * The meta object literal for the '<em><b>Attachments</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__ATTACHMENTS = eINSTANCE.getWikiPage_Attachments();

		/**
		 * The meta object literal for the '<em><b>Wiki</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__WIKI = eINSTANCE.getWikiPage_Wiki();

		/**
		 * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__CHILDREN = eINSTANCE.getWikiPage_Children();

		/**
		 * The meta object literal for the '<em><b>Parent</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__PARENT = eINSTANCE.getWikiPage_Parent();

		/**
		 * The meta object literal for the '<em><b>Old Parents</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__OLD_PARENTS = eINSTANCE.getWikiPage_OldParents();

		/**
		 * The meta object literal for the '<em><b>Page References</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__PAGE_REFERENCES = eINSTANCE.getWikiPage_PageReferences();

		/**
		 * The meta object literal for the '<em><b>Web Log</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__WEB_LOG = eINSTANCE.getWikiPage_WebLog();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__ATTRIBUTES = eINSTANCE.getWikiPage_Attributes();

		/**
		 * The meta object literal for the '<em><b>Unknown Pages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__UNKNOWN_PAGES = eINSTANCE.getWikiPage_UnknownPages();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__TAGS = eINSTANCE.getWikiPage_Tags();

		/**
		 * The meta object literal for the '<em><b>Acl Infos</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__ACL_INFOS = eINSTANCE.getWikiPage_AclInfos();

		/**
		 * The meta object literal for the '<em><b>Compare To</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___COMPARE_TO__OBJECT = eINSTANCE.getWikiPage__CompareTo__Object();

		/**
		 * The meta object literal for the '<em><b>Clone</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___CLONE = eINSTANCE.getWikiPage__Clone();

		/**
		 * The meta object literal for the '<em><b>Get Last Modified Date</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_LAST_MODIFIED_DATE = eINSTANCE.getWikiPage__GetLastModifiedDate();

		/**
		 * The meta object literal for the '<em><b>Get Author</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_AUTHOR = eINSTANCE.getWikiPage__GetAuthor();

		/**
		 * The meta object literal for the '<em><b>Get Last Content</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_LAST_CONTENT = eINSTANCE.getWikiPage__GetLastContent();

		/**
		 * The meta object literal for the '<em><b>Get Last Version</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_LAST_VERSION = eINSTANCE.getWikiPage__GetLastVersion();

		/**
		 * The meta object literal for the '<em><b>Get Attribute</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_ATTRIBUTE__STRING = eINSTANCE.getWikiPage__GetAttribute__String();

		/**
		 * The meta object literal for the '<em><b>Set Attribute</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___SET_ATTRIBUTE__STRING_OBJECT = eINSTANCE.getWikiPage__SetAttribute__String_Object();

		/**
		 * The meta object literal for the '<em><b>Is Internal Page</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___IS_INTERNAL_PAGE = eINSTANCE.getWikiPage__IsInternalPage();

		/**
		 * The meta object literal for the '<em><b>Get Page Contents Reversed</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_PAGE_CONTENTS_REVERSED = eINSTANCE.getWikiPage__GetPageContentsReversed();

		/**
		 * The meta object literal for the '<em><b>To String</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___TO_STRING = eINSTANCE.getWikiPage__ToString();

		/**
		 * The meta object literal for the '<em><b>Get Content Text</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_CONTENT_TEXT__INT = eINSTANCE.getWikiPage__GetContentText__int();

		/**
		 * The meta object literal for the '<em><b>Get Content Text</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_CONTENT_TEXT = eINSTANCE.getWikiPage__GetContentText();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.PagesStoreImpl <em>Pages Store</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.PagesStoreImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPagesStore()
		 * @generated
		 */
		EClass PAGES_STORE = eINSTANCE.getPagesStore();

		/**
		 * The meta object literal for the '<em><b>Wikipages</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGES_STORE__WIKIPAGES = eINSTANCE.getPagesStore_Wikipages();

		/**
		 * The meta object literal for the '<em><b>Main Page Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_STORE__MAIN_PAGE_ID = eINSTANCE.getPagesStore_MainPageId();

		/**
		 * The meta object literal for the '<em><b>Next Page Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_STORE__NEXT_PAGE_ID = eINSTANCE.getPagesStore_NextPageId();

		/**
		 * The meta object literal for the '<em><b>Next Attachment Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_STORE__NEXT_ATTACHMENT_ID = eINSTANCE.getPagesStore_NextAttachmentId();

		/**
		 * The meta object literal for the '<em><b>Tagslist</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGES_STORE__TAGSLIST = eINSTANCE.getPagesStore_Tagslist();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.PageContentImpl <em>Page Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.PageContentImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPageContent()
		 * @generated
		 */
		EClass PAGE_CONTENT = eINSTANCE.getPageContent();

		/**
		 * The meta object literal for the '<em><b>Content</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_CONTENT__CONTENT = eINSTANCE.getPageContent_Content();

		/**
		 * The meta object literal for the '<em><b>Wikipage</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_CONTENT__WIKIPAGE = eINSTANCE.getPageContent_Wikipage();

		/**
		 * The meta object literal for the '<em><b>Get Length</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PAGE_CONTENT___GET_LENGTH = eINSTANCE.getPageContent__GetLength();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.PageAttachmentImpl <em>Page Attachment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.PageAttachmentImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPageAttachment()
		 * @generated
		 */
		EClass PAGE_ATTACHMENT = eINSTANCE.getPageAttachment();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__ID = eINSTANCE.getPageAttachment_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__NAME = eINSTANCE.getPageAttachment_Name();

		/**
		 * The meta object literal for the '<em><b>Last Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__LAST_VERSION = eINSTANCE.getPageAttachment_LastVersion();

		/**
		 * The meta object literal for the '<em><b>Wikipage</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_ATTACHMENT__WIKIPAGE = eINSTANCE.getPageAttachment_Wikipage();

		/**
		 * The meta object literal for the '<em><b>Attach Contents</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_ATTACHMENT__ATTACH_CONTENTS = eINSTANCE.getPageAttachment_AttachContents();

		/**
		 * The meta object literal for the '<em><b>For Last Content</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PAGE_ATTACHMENT___FOR_LAST_CONTENT = eINSTANCE.getPageAttachment__ForLastContent();

		/**
		 * The meta object literal for the '<em><b>For Version Content</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation PAGE_ATTACHMENT___FOR_VERSION_CONTENT__INT = eINSTANCE.getPageAttachment__ForVersionContent__int();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.AttachmentContentImpl <em>Attachment Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.AttachmentContentImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAttachmentContent()
		 * @generated
		 */
		EClass ATTACHMENT_CONTENT = eINSTANCE.getAttachmentContent();

		/**
		 * The meta object literal for the '<em><b>Place</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACHMENT_CONTENT__PLACE = eINSTANCE.getAttachmentContent_Place();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ATTACHMENT_CONTENT__SIZE = eINSTANCE.getAttachmentContent_Size();

		/**
		 * The meta object literal for the '<em><b>Page Attachment</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ATTACHMENT_CONTENT__PAGE_ATTACHMENT = eINSTANCE.getAttachmentContent_PageAttachment();

		/**
		 * The meta object literal for the '{@link java.lang.Object <em>Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.Object
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getObject()
		 * @generated
		 */
		EClass OBJECT = eINSTANCE.getObject();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.ComparableImpl <em>Comparable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.ComparableImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getComparable()
		 * @generated
		 */
		EClass COMPARABLE = eINSTANCE.getComparable();

		/**
		 * The meta object literal for the '<em><b>Compare To</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation COMPARABLE___COMPARE_TO__OBJECT = eINSTANCE.getComparable__CompareTo__Object();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.CloneableImpl <em>Cloneable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.CloneableImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getCloneable()
		 * @generated
		 */
		EClass CLONEABLE = eINSTANCE.getCloneable();

		/**
		 * The meta object literal for the '<em><b>Clone</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation CLONEABLE___CLONE = eINSTANCE.getCloneable__Clone();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.IHistoryInfo <em>IHistory Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.IHistoryInfo
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getIHistoryInfo()
		 * @generated
		 */
		EClass IHISTORY_INFO = eINSTANCE.getIHistoryInfo();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IHISTORY_INFO__VERSION = eINSTANCE.getIHistoryInfo_Version();

		/**
		 * The meta object literal for the '<em><b>Creation Date</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IHISTORY_INFO__CREATION_DATE = eINSTANCE.getIHistoryInfo_CreationDate();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IHISTORY_INFO__AUTHOR = eINSTANCE.getIHistoryInfo_Author();

		/**
		 * The meta object literal for the '<em><b>Change Note</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IHISTORY_INFO__CHANGE_NOTE = eINSTANCE.getIHistoryInfo_ChangeNote();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.PageReferenceImpl <em>Page Reference</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.PageReferenceImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPageReference()
		 * @generated
		 */
		EClass PAGE_REFERENCE = eINSTANCE.getPageReference();

		/**
		 * The meta object literal for the '<em><b>Page Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_REFERENCE__PAGE_ID = eINSTANCE.getPageReference_PageId();

		/**
		 * The meta object literal for the '<em><b>Wikipage</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_REFERENCE__WIKIPAGE = eINSTANCE.getPageReference_Wikipage();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.StringToObjectMapImpl <em>String To Object Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.StringToObjectMapImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getStringToObjectMap()
		 * @generated
		 */
		EClass STRING_TO_OBJECT_MAP = eINSTANCE.getStringToObjectMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_OBJECT_MAP__KEY = eINSTANCE.getStringToObjectMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_OBJECT_MAP__VALUE = eINSTANCE.getStringToObjectMap_Value();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.UnknownPageImpl <em>Unknown Page</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.UnknownPageImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getUnknownPage()
		 * @generated
		 */
		EClass UNKNOWN_PAGE = eINSTANCE.getUnknownPage();

		/**
		 * The meta object literal for the '<em><b>Page Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNKNOWN_PAGE__PAGE_NAME = eINSTANCE.getUnknownPage_PageName();

		/**
		 * The meta object literal for the '<em><b>Wikipage</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNKNOWN_PAGE__WIKIPAGE = eINSTANCE.getUnknownPage_Wikipage();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.TagsListImpl <em>Tags List</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.TagsListImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getTagsList()
		 * @generated
		 */
		EClass TAGS_LIST = eINSTANCE.getTagsList();

		/**
		 * The meta object literal for the '<em><b>Tag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAGS_LIST__TAG = eINSTANCE.getTagsList_Tag();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.AclInfoImpl <em>Acl Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.AclInfoImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAclInfo()
		 * @generated
		 */
		EClass ACL_INFO = eINSTANCE.getAclInfo();

		/**
		 * The meta object literal for the '<em><b>Allow</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACL_INFO__ALLOW = eINSTANCE.getAclInfo_Allow();

		/**
		 * The meta object literal for the '<em><b>Permission</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACL_INFO__PERMISSION = eINSTANCE.getAclInfo_Permission();

		/**
		 * The meta object literal for the '<em><b>Roles</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACL_INFO__ROLES = eINSTANCE.getAclInfo_Roles();

		/**
		 * The meta object literal for the '<em>Array String</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getArrayString()
		 * @generated
		 */
		EDataType ARRAY_STRING = eINSTANCE.getArrayString();

		/**
		 * The meta object literal for the '<em>Access List</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.Object
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAccessList()
		 * @generated
		 */
		EDataType ACCESS_LIST = eINSTANCE.getAccessList();

		/**
		 * The meta object literal for the '<em>List Page Content</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.List
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getListPageContent()
		 * @generated
		 */
		EDataType LIST_PAGE_CONTENT = eINSTANCE.getListPageContent();

	}

} //Elwiki_dataPackage
