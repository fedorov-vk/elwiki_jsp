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
	int COMPARABLE = 5;

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
	 * The feature id for the '<em><b>Pagecontents</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__PAGECONTENTS = COMPARABLE_FEATURE_COUNT + 6;

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
	 * The feature id for the '<em><b>Total Attachment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__TOTAL_ATTACHMENT = COMPARABLE_FEATURE_COUNT + 13;

	/**
	 * The feature id for the '<em><b>Acl</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__ACL = COMPARABLE_FEATURE_COUNT + 14;

	/**
	 * The feature id for the '<em><b>Web Log</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__WEB_LOG = COMPARABLE_FEATURE_COUNT + 15;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE__ATTRIBUTES = COMPARABLE_FEATURE_COUNT + 16;

	/**
	 * The number of structural features of the '<em>Wiki Page</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE_FEATURE_COUNT = COMPARABLE_FEATURE_COUNT + 17;

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
	 * The operation id for the '<em>Get Last Modified</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_LAST_MODIFIED = COMPARABLE_OPERATION_COUNT + 3;

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
	 * The operation id for the '<em>Get Version</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE___GET_VERSION = COMPARABLE_OPERATION_COUNT + 6;

	/**
	 * The number of operations of the '<em>Wiki Page</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WIKI_PAGE_OPERATION_COUNT = COMPARABLE_OPERATION_COUNT + 7;

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
	 * The feature id for the '<em><b>Next Attach Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE__NEXT_ATTACH_ID = 3;

	/**
	 * The number of structural features of the '<em>Pages Store</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Pages Store</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGES_STORE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.elwiki_data.IModifyInfo <em>IModify Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.IModifyInfo
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getIModifyInfo()
	 * @generated
	 */
	int IMODIFY_INFO = 7;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODIFY_INFO__VERSION = 0;

	/**
	 * The feature id for the '<em><b>Last Modify</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODIFY_INFO__LAST_MODIFY = 1;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODIFY_INFO__AUTHOR = 2;

	/**
	 * The feature id for the '<em><b>Change Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODIFY_INFO__CHANGE_NOTE = 3;

	/**
	 * The number of structural features of the '<em>IModify Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODIFY_INFO_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>IModify Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMODIFY_INFO_OPERATION_COUNT = 0;

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
	int PAGE_CONTENT__VERSION = IMODIFY_INFO__VERSION;

	/**
	 * The feature id for the '<em><b>Last Modify</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__LAST_MODIFY = IMODIFY_INFO__LAST_MODIFY;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__AUTHOR = IMODIFY_INFO__AUTHOR;

	/**
	 * The feature id for the '<em><b>Change Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__CHANGE_NOTE = IMODIFY_INFO__CHANGE_NOTE;

	/**
	 * The feature id for the '<em><b>Content</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__CONTENT = IMODIFY_INFO_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Wikipage</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT__WIKIPAGE = IMODIFY_INFO_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Page Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT_FEATURE_COUNT = IMODIFY_INFO_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Page Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_CONTENT_OPERATION_COUNT = IMODIFY_INFO_OPERATION_COUNT + 0;

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
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__VERSION = IMODIFY_INFO__VERSION;

	/**
	 * The feature id for the '<em><b>Last Modify</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__LAST_MODIFY = IMODIFY_INFO__LAST_MODIFY;

	/**
	 * The feature id for the '<em><b>Author</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__AUTHOR = IMODIFY_INFO__AUTHOR;

	/**
	 * The feature id for the '<em><b>Change Note</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__CHANGE_NOTE = IMODIFY_INFO__CHANGE_NOTE;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__ID = IMODIFY_INFO_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__NAME = IMODIFY_INFO_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Cacheable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__CACHEABLE = IMODIFY_INFO_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Wikipage</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__WIKIPAGE = IMODIFY_INFO_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Place</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__PLACE = IMODIFY_INFO_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT__SIZE = IMODIFY_INFO_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Page Attachment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT_FEATURE_COUNT = IMODIFY_INFO_FEATURE_COUNT + 6;

	/**
	 * The number of operations of the '<em>Page Attachment</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PAGE_ATTACHMENT_OPERATION_COUNT = IMODIFY_INFO_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link java.lang.Object <em>Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Object
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getObject()
	 * @generated
	 */
	int OBJECT = 4;

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
	int CLONEABLE = 6;

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
	int PAGE_REFERENCE = 8;

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
	 * The meta object id for the '{@link org.elwiki_data.impl.AclEntryImpl <em>Acl Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.AclEntryImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAclEntry()
	 * @generated
	 */
	int ACL_ENTRY = 9;

	/**
	 * The feature id for the '<em><b>Principal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_ENTRY__PRINCIPAL = 0;

	/**
	 * The feature id for the '<em><b>Permission</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_ENTRY__PERMISSION = 1;

	/**
	 * The number of structural features of the '<em>Acl Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_ENTRY_FEATURE_COUNT = 2;

	/**
	 * The operation id for the '<em>Check Permission</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_ENTRY___CHECK_PERMISSION__PERMISSION = 0;

	/**
	 * The number of operations of the '<em>Acl Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_ENTRY_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.AclImpl <em>Acl</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.AclImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAcl()
	 * @generated
	 */
	int ACL = 11;

	/**
	 * The meta object id for the '{@link java.security.Principal <em>Principal</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.security.Principal
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPrincipal()
	 * @generated
	 */
	int PRINCIPAL = 10;

	/**
	 * The number of structural features of the '<em>Principal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRINCIPAL_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Principal</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PRINCIPAL_OPERATION_COUNT = 0;

	/**
	 * The feature id for the '<em><b>Acl Entries</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL__ACL_ENTRIES = 0;

	/**
	 * The number of structural features of the '<em>Acl</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_FEATURE_COUNT = 1;

	/**
	 * The operation id for the '<em>Get Entry</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL___GET_ENTRY__PRINCIPAL = 0;

	/**
	 * The operation id for the '<em>Find Principals</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL___FIND_PRINCIPALS__PERMISSION = 1;

	/**
	 * The number of operations of the '<em>Acl</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ACL_OPERATION_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.elwiki_data.impl.StringToObjectMapImpl <em>String To Object Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.StringToObjectMapImpl
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getStringToObjectMap()
	 * @generated
	 */
	int STRING_TO_OBJECT_MAP = 12;

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
	 * The meta object id for the '<em>Access List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Object
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAccessList()
	 * @generated
	 */
	int ACCESS_LIST = 14;

	/**
	 * The meta object id for the '<em>Array Principal</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getArrayPrincipal()
	 * @generated
	 */
	int ARRAY_PRINCIPAL = 15;

	/**
	 * The meta object id for the '<em>Permission Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.security.Permission
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPermissionObject()
	 * @generated
	 */
	int PERMISSION_OBJECT = 16;

	/**
	 * The meta object id for the '<em>Principal Object</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.security.Principal
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPrincipalObject()
	 * @generated
	 */
	int PRINCIPAL_OBJECT = 17;

	/**
	 * The meta object id for the '<em>Array String</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getArrayString()
	 * @generated
	 */
	int ARRAY_STRING = 13;


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
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.WikiPage#getPagecontents <em>Pagecontents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Pagecontents</em>'.
	 * @see org.elwiki_data.WikiPage#getPagecontents()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_Pagecontents();

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
	 * Returns the meta object for the attribute '{@link org.elwiki_data.WikiPage#getTotalAttachment <em>Total Attachment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Total Attachment</em>'.
	 * @see org.elwiki_data.WikiPage#getTotalAttachment()
	 * @see #getWikiPage()
	 * @generated
	 */
	EAttribute getWikiPage_TotalAttachment();

	/**
	 * Returns the meta object for the containment reference '{@link org.elwiki_data.WikiPage#getAcl <em>Acl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Acl</em>'.
	 * @see org.elwiki_data.WikiPage#getAcl()
	 * @see #getWikiPage()
	 * @generated
	 */
	EReference getWikiPage_Acl();

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
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getLastModified() <em>Get Last Modified</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Last Modified</em>' operation.
	 * @see org.elwiki_data.WikiPage#getLastModified()
	 * @generated
	 */
	EOperation getWikiPage__GetLastModified();

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
	 * Returns the meta object for the '{@link org.elwiki_data.WikiPage#getVersion() <em>Get Version</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Version</em>' operation.
	 * @see org.elwiki_data.WikiPage#getVersion()
	 * @generated
	 */
	EOperation getWikiPage__GetVersion();

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
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PagesStore#getNextAttachId <em>Next Attach Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Next Attach Id</em>'.
	 * @see org.elwiki_data.PagesStore#getNextAttachId()
	 * @see #getPagesStore()
	 * @generated
	 */
	EAttribute getPagesStore_NextAttachId();

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
	 * Returns the meta object for class '{@link org.elwiki_data.PageAttachment <em>Page Attachment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Page Attachment</em>'.
	 * @see org.elwiki_data.PageAttachment
	 * @generated
	 */
	EClass getPageAttachment();

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
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageAttachment#isCacheable <em>Cacheable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cacheable</em>'.
	 * @see org.elwiki_data.PageAttachment#isCacheable()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EAttribute getPageAttachment_Cacheable();

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
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageAttachment#getPlace <em>Place</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Place</em>'.
	 * @see org.elwiki_data.PageAttachment#getPlace()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EAttribute getPageAttachment_Place();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.PageAttachment#getSize <em>Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Size</em>'.
	 * @see org.elwiki_data.PageAttachment#getSize()
	 * @see #getPageAttachment()
	 * @generated
	 */
	EAttribute getPageAttachment_Size();

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
	 * Returns the meta object for class '{@link org.elwiki_data.IModifyInfo <em>IModify Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>IModify Info</em>'.
	 * @see org.elwiki_data.IModifyInfo
	 * @generated
	 */
	EClass getIModifyInfo();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IModifyInfo#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.elwiki_data.IModifyInfo#getVersion()
	 * @see #getIModifyInfo()
	 * @generated
	 */
	EAttribute getIModifyInfo_Version();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IModifyInfo#getLastModify <em>Last Modify</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Modify</em>'.
	 * @see org.elwiki_data.IModifyInfo#getLastModify()
	 * @see #getIModifyInfo()
	 * @generated
	 */
	EAttribute getIModifyInfo_LastModify();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IModifyInfo#getAuthor <em>Author</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Author</em>'.
	 * @see org.elwiki_data.IModifyInfo#getAuthor()
	 * @see #getIModifyInfo()
	 * @generated
	 */
	EAttribute getIModifyInfo_Author();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.IModifyInfo#getChangeNote <em>Change Note</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Change Note</em>'.
	 * @see org.elwiki_data.IModifyInfo#getChangeNote()
	 * @see #getIModifyInfo()
	 * @generated
	 */
	EAttribute getIModifyInfo_ChangeNote();

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
	 * Returns the meta object for class '{@link org.elwiki_data.AclEntry <em>Acl Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Acl Entry</em>'.
	 * @see org.elwiki_data.AclEntry
	 * @generated
	 */
	EClass getAclEntry();

	/**
	 * Returns the meta object for the attribute list '{@link org.elwiki_data.AclEntry#getPermission <em>Permission</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Permission</em>'.
	 * @see org.elwiki_data.AclEntry#getPermission()
	 * @see #getAclEntry()
	 * @generated
	 */
	EAttribute getAclEntry_Permission();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.AclEntry#checkPermission(java.security.Permission) <em>Check Permission</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Check Permission</em>' operation.
	 * @see org.elwiki_data.AclEntry#checkPermission(java.security.Permission)
	 * @generated
	 */
	EOperation getAclEntry__CheckPermission__Permission();

	/**
	 * Returns the meta object for the attribute '{@link org.elwiki_data.AclEntry#getPrincipal <em>Principal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Principal</em>'.
	 * @see org.elwiki_data.AclEntry#getPrincipal()
	 * @see #getAclEntry()
	 * @generated
	 */
	EAttribute getAclEntry_Principal();

	/**
	 * Returns the meta object for class '{@link org.elwiki_data.Acl <em>Acl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Acl</em>'.
	 * @see org.elwiki_data.Acl
	 * @generated
	 */
	EClass getAcl();

	/**
	 * Returns the meta object for the containment reference list '{@link org.elwiki_data.Acl#getAclEntries <em>Acl Entries</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Acl Entries</em>'.
	 * @see org.elwiki_data.Acl#getAclEntries()
	 * @see #getAcl()
	 * @generated
	 */
	EReference getAcl_AclEntries();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.Acl#getEntry(java.security.Principal) <em>Get Entry</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Get Entry</em>' operation.
	 * @see org.elwiki_data.Acl#getEntry(java.security.Principal)
	 * @generated
	 */
	EOperation getAcl__GetEntry__Principal();

	/**
	 * Returns the meta object for the '{@link org.elwiki_data.Acl#findPrincipals(java.security.Permission) <em>Find Principals</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Find Principals</em>' operation.
	 * @see org.elwiki_data.Acl#findPrincipals(java.security.Permission)
	 * @generated
	 */
	EOperation getAcl__FindPrincipals__Permission();

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
	 * Returns the meta object for class '{@link java.security.Principal <em>Principal</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Principal</em>'.
	 * @see java.security.Principal
	 * @model instanceClass="java.security.Principal"
	 * @generated
	 */
	EClass getPrincipal();

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
	 * Returns the meta object for data type '<em>Array Principal</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Array Principal</em>'.
	 * @model instanceClass="java.security.Principal[]"
	 * @generated
	 */
	EDataType getArrayPrincipal();

	/**
	 * Returns the meta object for data type '{@link java.security.Permission <em>Permission Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Permission Object</em>'.
	 * @see java.security.Permission
	 * @model instanceClass="java.security.Permission"
	 * @generated
	 */
	EDataType getPermissionObject();

	/**
	 * Returns the meta object for data type '{@link java.security.Principal <em>Principal Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Principal Object</em>'.
	 * @see java.security.Principal
	 * @model instanceClass="java.security.Principal"
	 * @generated
	 */
	EDataType getPrincipalObject();

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
		 * The meta object literal for the '<em><b>Pagecontents</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__PAGECONTENTS = eINSTANCE.getWikiPage_Pagecontents();

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
		 * The meta object literal for the '<em><b>Total Attachment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute WIKI_PAGE__TOTAL_ATTACHMENT = eINSTANCE.getWikiPage_TotalAttachment();

		/**
		 * The meta object literal for the '<em><b>Acl</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WIKI_PAGE__ACL = eINSTANCE.getWikiPage_Acl();

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
		 * The meta object literal for the '<em><b>Get Last Modified</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_LAST_MODIFIED = eINSTANCE.getWikiPage__GetLastModified();

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
		 * The meta object literal for the '<em><b>Get Version</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation WIKI_PAGE___GET_VERSION = eINSTANCE.getWikiPage__GetVersion();

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
		 * The meta object literal for the '<em><b>Next Attach Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGES_STORE__NEXT_ATTACH_ID = eINSTANCE.getPagesStore_NextAttachId();

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
		 * The meta object literal for the '{@link org.elwiki_data.impl.PageAttachmentImpl <em>Page Attachment</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.PageAttachmentImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPageAttachment()
		 * @generated
		 */
		EClass PAGE_ATTACHMENT = eINSTANCE.getPageAttachment();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__NAME = eINSTANCE.getPageAttachment_Name();

		/**
		 * The meta object literal for the '<em><b>Cacheable</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__CACHEABLE = eINSTANCE.getPageAttachment_Cacheable();

		/**
		 * The meta object literal for the '<em><b>Wikipage</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PAGE_ATTACHMENT__WIKIPAGE = eINSTANCE.getPageAttachment_Wikipage();

		/**
		 * The meta object literal for the '<em><b>Place</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__PLACE = eINSTANCE.getPageAttachment_Place();

		/**
		 * The meta object literal for the '<em><b>Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__SIZE = eINSTANCE.getPageAttachment_Size();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PAGE_ATTACHMENT__ID = eINSTANCE.getPageAttachment_Id();

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
		 * The meta object literal for the '{@link org.elwiki_data.IModifyInfo <em>IModify Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.IModifyInfo
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getIModifyInfo()
		 * @generated
		 */
		EClass IMODIFY_INFO = eINSTANCE.getIModifyInfo();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMODIFY_INFO__VERSION = eINSTANCE.getIModifyInfo_Version();

		/**
		 * The meta object literal for the '<em><b>Last Modify</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMODIFY_INFO__LAST_MODIFY = eINSTANCE.getIModifyInfo_LastModify();

		/**
		 * The meta object literal for the '<em><b>Author</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMODIFY_INFO__AUTHOR = eINSTANCE.getIModifyInfo_Author();

		/**
		 * The meta object literal for the '<em><b>Change Note</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMODIFY_INFO__CHANGE_NOTE = eINSTANCE.getIModifyInfo_ChangeNote();

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
		 * The meta object literal for the '{@link org.elwiki_data.impl.AclEntryImpl <em>Acl Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.AclEntryImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAclEntry()
		 * @generated
		 */
		EClass ACL_ENTRY = eINSTANCE.getAclEntry();

		/**
		 * The meta object literal for the '<em><b>Permission</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACL_ENTRY__PERMISSION = eINSTANCE.getAclEntry_Permission();

		/**
		 * The meta object literal for the '<em><b>Check Permission</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ACL_ENTRY___CHECK_PERMISSION__PERMISSION = eINSTANCE.getAclEntry__CheckPermission__Permission();

		/**
		 * The meta object literal for the '<em><b>Principal</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ACL_ENTRY__PRINCIPAL = eINSTANCE.getAclEntry_Principal();

		/**
		 * The meta object literal for the '{@link org.elwiki_data.impl.AclImpl <em>Acl</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.AclImpl
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getAcl()
		 * @generated
		 */
		EClass ACL = eINSTANCE.getAcl();

		/**
		 * The meta object literal for the '<em><b>Acl Entries</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ACL__ACL_ENTRIES = eINSTANCE.getAcl_AclEntries();

		/**
		 * The meta object literal for the '<em><b>Get Entry</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ACL___GET_ENTRY__PRINCIPAL = eINSTANCE.getAcl__GetEntry__Principal();

		/**
		 * The meta object literal for the '<em><b>Find Principals</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation ACL___FIND_PRINCIPALS__PERMISSION = eINSTANCE.getAcl__FindPrincipals__Permission();

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
		 * The meta object literal for the '{@link java.security.Principal <em>Principal</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.security.Principal
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPrincipal()
		 * @generated
		 */
		EClass PRINCIPAL = eINSTANCE.getPrincipal();

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
		 * The meta object literal for the '<em>Array Principal</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getArrayPrincipal()
		 * @generated
		 */
		EDataType ARRAY_PRINCIPAL = eINSTANCE.getArrayPrincipal();

		/**
		 * The meta object literal for the '<em>Permission Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.security.Permission
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPermissionObject()
		 * @generated
		 */
		EDataType PERMISSION_OBJECT = eINSTANCE.getPermissionObject();

		/**
		 * The meta object literal for the '<em>Principal Object</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.security.Principal
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getPrincipalObject()
		 * @generated
		 */
		EDataType PRINCIPAL_OBJECT = eINSTANCE.getPrincipalObject();

		/**
		 * The meta object literal for the '<em>Array String</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.elwiki_data.impl.Elwiki_dataPackageImpl#getArrayString()
		 * @generated
		 */
		EDataType ARRAY_STRING = eINSTANCE.getArrayString();

	}

} //Elwiki_dataPackage
