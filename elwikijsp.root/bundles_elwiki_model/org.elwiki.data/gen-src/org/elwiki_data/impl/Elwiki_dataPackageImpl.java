/**
 */
package org.elwiki_data.impl;

import java.lang.Cloneable;
import java.lang.Comparable;
import java.lang.Object;

import java.security.Permission;
import java.security.Principal;
import java.util.Map;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.IModifyInfo;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Elwiki_dataPackageImpl extends EPackageImpl implements Elwiki_dataPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass wikiPageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pagesStoreEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pageContentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pageAttachmentEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass objectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass comparableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cloneableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass iModifyInfoEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass pageReferenceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass aclEntryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass aclEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringToObjectMapEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass principalEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType arrayStringEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType accessListEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType arrayPrincipalEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType permissionObjectEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType principalObjectEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.elwiki_data.Elwiki_dataPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Elwiki_dataPackageImpl() {
		super(eNS_URI, Elwiki_dataFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link Elwiki_dataPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static Elwiki_dataPackage init() {
		if (isInited) return (Elwiki_dataPackage)EPackage.Registry.INSTANCE.getEPackage(Elwiki_dataPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredElwiki_dataPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		Elwiki_dataPackageImpl theElwiki_dataPackage = registeredElwiki_dataPackage instanceof Elwiki_dataPackageImpl ? (Elwiki_dataPackageImpl)registeredElwiki_dataPackage : new Elwiki_dataPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theElwiki_dataPackage.createPackageContents();

		// Initialize created meta-data
		theElwiki_dataPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theElwiki_dataPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(Elwiki_dataPackage.eNS_URI, theElwiki_dataPackage);
		return theElwiki_dataPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getWikiPage() {
		return wikiPageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Id() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Name() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Description() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Alias() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Redirect() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_ViewCount() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Pagecontents() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Attachments() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Wiki() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Children() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Parent() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_OldParents() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_PageReferences() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_TotalAttachment() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Acl() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_WebLog() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Attributes() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(16);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__CompareTo__Object() {
		return wikiPageEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__Clone() {
		return wikiPageEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__GetLastModified() {
		return wikiPageEClass.getEOperations().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__GetAuthor() {
		return wikiPageEClass.getEOperations().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__GetLastContent() {
		return wikiPageEClass.getEOperations().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__GetVersion() {
		return wikiPageEClass.getEOperations().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPagesStore() {
		return pagesStoreEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPagesStore_Wikipages() {
		return (EReference)pagesStoreEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPagesStore_MainPageId() {
		return (EAttribute)pagesStoreEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPagesStore_NextPageId() {
		return (EAttribute)pagesStoreEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPagesStore_NextAttachId() {
		return (EAttribute)pagesStoreEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPageContent() {
		return pageContentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageContent_Content() {
		return (EAttribute)pageContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPageContent_Wikipage() {
		return (EReference)pageContentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPageAttachment() {
		return pageAttachmentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageAttachment_Name() {
		return (EAttribute)pageAttachmentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageAttachment_Cacheable() {
		return (EAttribute)pageAttachmentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPageAttachment_Wikipage() {
		return (EReference)pageAttachmentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageAttachment_Place() {
		return (EAttribute)pageAttachmentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageAttachment_Size() {
		return (EAttribute)pageAttachmentEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageAttachment_Id() {
		return (EAttribute)pageAttachmentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getObject() {
		return objectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getComparable() {
		return comparableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getComparable__CompareTo__Object() {
		return comparableEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCloneable() {
		return cloneableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getCloneable__Clone() {
		return cloneableEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getIModifyInfo() {
		return iModifyInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIModifyInfo_Version() {
		return (EAttribute)iModifyInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIModifyInfo_LastModify() {
		return (EAttribute)iModifyInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIModifyInfo_Author() {
		return (EAttribute)iModifyInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIModifyInfo_ChangeNote() {
		return (EAttribute)iModifyInfoEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPageReference() {
		return pageReferenceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageReference_PageId() {
		return (EAttribute)pageReferenceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPageReference_Wikipage() {
		return (EReference)pageReferenceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAclEntry() {
		return aclEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAclEntry_Permission() {
		return (EAttribute)aclEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAclEntry__CheckPermission__Permission() {
		return aclEntryEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAclEntry_Principal() {
		return (EAttribute)aclEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAcl() {
		return aclEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAcl_AclEntries() {
		return (EReference)aclEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAcl__GetEntry__Principal() {
		return aclEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getAcl__FindPrincipals__Permission() {
		return aclEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStringToObjectMap() {
		return stringToObjectMapEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringToObjectMap_Key() {
		return (EAttribute)stringToObjectMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringToObjectMap_Value() {
		return (EAttribute)stringToObjectMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPrincipal() {
		return principalEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getAccessList() {
		return accessListEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getArrayPrincipal() {
		return arrayPrincipalEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPermissionObject() {
		return permissionObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getPrincipalObject() {
		return principalObjectEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getArrayString() {
		return arrayStringEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Elwiki_dataFactory getElwiki_dataFactory() {
		return (Elwiki_dataFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		wikiPageEClass = createEClass(WIKI_PAGE);
		createEAttribute(wikiPageEClass, WIKI_PAGE__ID);
		createEAttribute(wikiPageEClass, WIKI_PAGE__NAME);
		createEAttribute(wikiPageEClass, WIKI_PAGE__DESCRIPTION);
		createEAttribute(wikiPageEClass, WIKI_PAGE__ALIAS);
		createEAttribute(wikiPageEClass, WIKI_PAGE__REDIRECT);
		createEAttribute(wikiPageEClass, WIKI_PAGE__VIEW_COUNT);
		createEReference(wikiPageEClass, WIKI_PAGE__PAGECONTENTS);
		createEReference(wikiPageEClass, WIKI_PAGE__ATTACHMENTS);
		createEAttribute(wikiPageEClass, WIKI_PAGE__WIKI);
		createEReference(wikiPageEClass, WIKI_PAGE__CHILDREN);
		createEReference(wikiPageEClass, WIKI_PAGE__PARENT);
		createEAttribute(wikiPageEClass, WIKI_PAGE__OLD_PARENTS);
		createEReference(wikiPageEClass, WIKI_PAGE__PAGE_REFERENCES);
		createEAttribute(wikiPageEClass, WIKI_PAGE__TOTAL_ATTACHMENT);
		createEReference(wikiPageEClass, WIKI_PAGE__ACL);
		createEAttribute(wikiPageEClass, WIKI_PAGE__WEB_LOG);
		createEReference(wikiPageEClass, WIKI_PAGE__ATTRIBUTES);
		createEOperation(wikiPageEClass, WIKI_PAGE___COMPARE_TO__OBJECT);
		createEOperation(wikiPageEClass, WIKI_PAGE___CLONE);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_LAST_MODIFIED);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_AUTHOR);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_LAST_CONTENT);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_VERSION);

		pagesStoreEClass = createEClass(PAGES_STORE);
		createEReference(pagesStoreEClass, PAGES_STORE__WIKIPAGES);
		createEAttribute(pagesStoreEClass, PAGES_STORE__MAIN_PAGE_ID);
		createEAttribute(pagesStoreEClass, PAGES_STORE__NEXT_PAGE_ID);
		createEAttribute(pagesStoreEClass, PAGES_STORE__NEXT_ATTACH_ID);

		pageContentEClass = createEClass(PAGE_CONTENT);
		createEAttribute(pageContentEClass, PAGE_CONTENT__CONTENT);
		createEReference(pageContentEClass, PAGE_CONTENT__WIKIPAGE);

		pageAttachmentEClass = createEClass(PAGE_ATTACHMENT);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__ID);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__NAME);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__CACHEABLE);
		createEReference(pageAttachmentEClass, PAGE_ATTACHMENT__WIKIPAGE);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__PLACE);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__SIZE);

		objectEClass = createEClass(OBJECT);

		comparableEClass = createEClass(COMPARABLE);
		createEOperation(comparableEClass, COMPARABLE___COMPARE_TO__OBJECT);

		cloneableEClass = createEClass(CLONEABLE);
		createEOperation(cloneableEClass, CLONEABLE___CLONE);

		iModifyInfoEClass = createEClass(IMODIFY_INFO);
		createEAttribute(iModifyInfoEClass, IMODIFY_INFO__VERSION);
		createEAttribute(iModifyInfoEClass, IMODIFY_INFO__LAST_MODIFY);
		createEAttribute(iModifyInfoEClass, IMODIFY_INFO__AUTHOR);
		createEAttribute(iModifyInfoEClass, IMODIFY_INFO__CHANGE_NOTE);

		pageReferenceEClass = createEClass(PAGE_REFERENCE);
		createEAttribute(pageReferenceEClass, PAGE_REFERENCE__PAGE_ID);
		createEReference(pageReferenceEClass, PAGE_REFERENCE__WIKIPAGE);

		aclEntryEClass = createEClass(ACL_ENTRY);
		createEAttribute(aclEntryEClass, ACL_ENTRY__PRINCIPAL);
		createEAttribute(aclEntryEClass, ACL_ENTRY__PERMISSION);
		createEOperation(aclEntryEClass, ACL_ENTRY___CHECK_PERMISSION__PERMISSION);

		principalEClass = createEClass(PRINCIPAL);

		aclEClass = createEClass(ACL);
		createEReference(aclEClass, ACL__ACL_ENTRIES);
		createEOperation(aclEClass, ACL___GET_ENTRY__PRINCIPAL);
		createEOperation(aclEClass, ACL___FIND_PRINCIPALS__PERMISSION);

		stringToObjectMapEClass = createEClass(STRING_TO_OBJECT_MAP);
		createEAttribute(stringToObjectMapEClass, STRING_TO_OBJECT_MAP__KEY);
		createEAttribute(stringToObjectMapEClass, STRING_TO_OBJECT_MAP__VALUE);

		// Create data types
		arrayStringEDataType = createEDataType(ARRAY_STRING);
		accessListEDataType = createEDataType(ACCESS_LIST);
		arrayPrincipalEDataType = createEDataType(ARRAY_PRINCIPAL);
		permissionObjectEDataType = createEDataType(PERMISSION_OBJECT);
		principalObjectEDataType = createEDataType(PRINCIPAL_OBJECT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		wikiPageEClass.getESuperTypes().add(this.getComparable());
		wikiPageEClass.getESuperTypes().add(this.getCloneable());
		pageContentEClass.getESuperTypes().add(this.getIModifyInfo());
		pageAttachmentEClass.getESuperTypes().add(this.getIModifyInfo());

		// Initialize classes, features, and operations; add parameters
		initEClass(wikiPageEClass, WikiPage.class, "WikiPage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWikiPage_Id(), ecorePackage.getEString(), "id", "", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Name(), ecorePackage.getEString(), "name", "", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Description(), ecorePackage.getEString(), "description", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Alias(), ecorePackage.getEString(), "alias", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Redirect(), ecorePackage.getEString(), "redirect", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_ViewCount(), ecorePackage.getEInt(), "viewCount", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Pagecontents(), this.getPageContent(), this.getPageContent_Wikipage(), "pagecontents", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Attachments(), this.getPageAttachment(), this.getPageAttachment_Wikipage(), "attachments", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Wiki(), ecorePackage.getEString(), "wiki", "", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Children(), this.getWikiPage(), this.getWikiPage_Parent(), "children", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Parent(), this.getWikiPage(), this.getWikiPage_Children(), "parent", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_OldParents(), this.getArrayString(), "oldParents", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_PageReferences(), this.getPageReference(), this.getPageReference_Wikipage(), "pageReferences", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_TotalAttachment(), ecorePackage.getEInt(), "totalAttachment", "0", 0, 1, WikiPage.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Acl(), this.getAcl(), null, "acl", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_WebLog(), ecorePackage.getEBoolean(), "webLog", "false", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Attributes(), this.getStringToObjectMap(), null, "attributes", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		getWikiPage_Attributes().getEKeys().add(this.getStringToObjectMap_Key());

		EOperation op = initEOperation(getWikiPage__CompareTo__Object(), ecorePackage.getEInt(), "compareTo", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getObject(), "obj", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__Clone(), this.getObject(), "clone", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetLastModified(), ecorePackage.getEDate(), "getLastModified", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetAuthor(), ecorePackage.getEString(), "getAuthor", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetLastContent(), this.getPageContent(), "getLastContent", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetVersion(), ecorePackage.getEInt(), "getVersion", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(pagesStoreEClass, PagesStore.class, "PagesStore", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPagesStore_Wikipages(), this.getWikiPage(), null, "wikipages", null, 0, -1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPagesStore_MainPageId(), ecorePackage.getEString(), "mainPageId", null, 0, 1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPagesStore_NextPageId(), ecorePackage.getEString(), "nextPageId", "", 0, 1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPagesStore_NextAttachId(), ecorePackage.getEString(), "nextAttachId", null, 0, 1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pageContentEClass, PageContent.class, "PageContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPageContent_Content(), ecorePackage.getEString(), "content", null, 0, 1, PageContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageContent_Wikipage(), this.getWikiPage(), this.getWikiPage_Pagecontents(), "wikipage", null, 0, 1, PageContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pageAttachmentEClass, PageAttachment.class, "PageAttachment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPageAttachment_Id(), ecorePackage.getEString(), "id", "", 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageAttachment_Name(), ecorePackage.getEString(), "name", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageAttachment_Cacheable(), ecorePackage.getEBoolean(), "cacheable", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageAttachment_Wikipage(), this.getWikiPage(), this.getWikiPage_Attachments(), "wikipage", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageAttachment_Place(), ecorePackage.getEString(), "place", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageAttachment_Size(), ecorePackage.getELong(), "size", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(objectEClass, Object.class, "Object", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEClass(comparableEClass, Comparable.class, "Comparable", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getComparable__CompareTo__Object(), ecorePackage.getEInt(), "compareTo", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getObject(), "obj", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(cloneableEClass, Cloneable.class, "Cloneable", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getCloneable__Clone(), this.getObject(), "clone", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(iModifyInfoEClass, IModifyInfo.class, "IModifyInfo", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIModifyInfo_Version(), ecorePackage.getEInt(), "version", null, 0, 1, IModifyInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIModifyInfo_LastModify(), ecorePackage.getEDate(), "lastModify", null, 0, 1, IModifyInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIModifyInfo_Author(), ecorePackage.getEString(), "author", "", 0, 1, IModifyInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIModifyInfo_ChangeNote(), ecorePackage.getEString(), "changeNote", null, 0, 1, IModifyInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pageReferenceEClass, PageReference.class, "PageReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPageReference_PageId(), ecorePackage.getEString(), "pageId", "", 0, 1, PageReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageReference_Wikipage(), this.getWikiPage(), this.getWikiPage_PageReferences(), "wikipage", null, 0, 1, PageReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(aclEntryEClass, AclEntry.class, "AclEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAclEntry_Principal(), this.getPrincipalObject(), "principal", null, 0, 1, AclEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAclEntry_Permission(), this.getPermissionObject(), "permission", null, 0, -1, AclEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = initEOperation(getAclEntry__CheckPermission__Permission(), ecorePackage.getEBooleanObject(), "checkPermission", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getPermissionObject(), "permission", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(principalEClass, Principal.class, "Principal", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEClass(aclEClass, Acl.class, "Acl", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getAcl_AclEntries(), this.getAclEntry(), null, "aclEntries", null, 0, -1, Acl.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = initEOperation(getAcl__GetEntry__Principal(), this.getAclEntry(), "getEntry", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getPrincipalObject(), "principal", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAcl__FindPrincipals__Permission(), this.getArrayPrincipal(), "findPrincipals", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getPermissionObject(), "permission", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(stringToObjectMapEClass, Map.Entry.class, "StringToObjectMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringToObjectMap_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStringToObjectMap_Value(), ecorePackage.getEJavaObject(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize data types
		initEDataType(arrayStringEDataType, String[].class, "ArrayString", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(accessListEDataType, Object.class, "AccessList", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(arrayPrincipalEDataType, Principal[].class, "ArrayPrincipal", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(permissionObjectEDataType, Permission.class, "PermissionObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(principalObjectEDataType, Principal.class, "PrincipalObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/CDO/DBStore
		createDBStoreAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/CDO/DBStore</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createDBStoreAnnotations() {
		String source = "http://www.eclipse.org/CDO/DBStore";
		addAnnotation
		  (getPageContent_Content(),
		   source,
		   new String[] {
			   "columnType", "VARCHAR",
			   "columnLength", "10000000"
		   });
	}

} //Elwiki_dataPackageImpl
