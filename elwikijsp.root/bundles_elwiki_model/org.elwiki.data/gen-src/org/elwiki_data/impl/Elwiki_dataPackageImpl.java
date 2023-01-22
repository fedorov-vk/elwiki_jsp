/**
 */
package org.elwiki_data.impl;

import java.lang.Cloneable;
import java.lang.Comparable;
import java.lang.Object;

import java.security.Permission;
import java.security.Principal;

import java.util.List;
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
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.IHistoryInfo;
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
	private EClass iHistoryInfoEClass = null;

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
	private EClass principalEClass = null;

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
	private EClass attachmentContentEClass = null;

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
	private EDataType listPageContentEDataType = null;

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
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Alias() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Redirect() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_ViewCount() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_PageContents() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Attachments() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_Wiki() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Children() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_Parent() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_OldParents() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getWikiPage_PageReferences() {
		return (EReference)wikiPageEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getWikiPage_LastVersion() {
		return (EAttribute)wikiPageEClass.getEStructuralFeatures().get(2);
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
	public EOperation getWikiPage__GetLastModifiedDate() {
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
	public EOperation getWikiPage__GetAttribute__String() {
		return wikiPageEClass.getEOperations().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__SetAttribute__String_Object() {
		return wikiPageEClass.getEOperations().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__IsInternalPage() {
		return wikiPageEClass.getEOperations().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getWikiPage__GetPageContentsReversed() {
		return wikiPageEClass.getEOperations().get(9);
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
	public EAttribute getPagesStore_NextAttachmentId() {
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
	public EOperation getPageContent__GetLength() {
		return pageContentEClass.getEOperations().get(0);
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
	public EAttribute getPageAttachment_Id() {
		return (EAttribute)pageAttachmentEClass.getEStructuralFeatures().get(0);
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
	public EReference getPageAttachment_Wikipage() {
		return (EReference)pageAttachmentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPageAttachment_AttachContents() {
		return (EReference)pageAttachmentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPageAttachment_LastVersion() {
		return (EAttribute)pageAttachmentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPageAttachment_AttachmentContent() {
		return (EReference)pageAttachmentEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getPageAttachment__ForLastContent() {
		return pageAttachmentEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getPageAttachment__ForVersionContent__int() {
		return pageAttachmentEClass.getEOperations().get(1);
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
	public EClass getIHistoryInfo() {
		return iHistoryInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIHistoryInfo_Version() {
		return (EAttribute)iHistoryInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIHistoryInfo_CreationDate() {
		return (EAttribute)iHistoryInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIHistoryInfo_Author() {
		return (EAttribute)iHistoryInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getIHistoryInfo_ChangeNote() {
		return (EAttribute)iHistoryInfoEClass.getEStructuralFeatures().get(3);
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
	public EAttribute getAclEntry_Principal() {
		return (EAttribute)aclEntryEClass.getEStructuralFeatures().get(0);
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
	public EOperation getAclEntry__FindPermission__Permission() {
		return aclEntryEClass.getEOperations().get(1);
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
	public EClass getAttachmentContent() {
		return attachmentContentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAttachmentContent_Place() {
		return (EAttribute)attachmentContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAttachmentContent_Size() {
		return (EAttribute)attachmentContentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAttachmentContent_PageAttachment() {
		return (EReference)attachmentContentEClass.getEStructuralFeatures().get(2);
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
	public EDataType getListPageContent() {
		return listPageContentEDataType;
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
		createEAttribute(wikiPageEClass, WIKI_PAGE__LAST_VERSION);
		createEAttribute(wikiPageEClass, WIKI_PAGE__DESCRIPTION);
		createEAttribute(wikiPageEClass, WIKI_PAGE__ALIAS);
		createEAttribute(wikiPageEClass, WIKI_PAGE__REDIRECT);
		createEAttribute(wikiPageEClass, WIKI_PAGE__VIEW_COUNT);
		createEReference(wikiPageEClass, WIKI_PAGE__PAGE_CONTENTS);
		createEReference(wikiPageEClass, WIKI_PAGE__ATTACHMENTS);
		createEAttribute(wikiPageEClass, WIKI_PAGE__WIKI);
		createEReference(wikiPageEClass, WIKI_PAGE__CHILDREN);
		createEReference(wikiPageEClass, WIKI_PAGE__PARENT);
		createEAttribute(wikiPageEClass, WIKI_PAGE__OLD_PARENTS);
		createEReference(wikiPageEClass, WIKI_PAGE__PAGE_REFERENCES);
		createEReference(wikiPageEClass, WIKI_PAGE__ACL);
		createEAttribute(wikiPageEClass, WIKI_PAGE__WEB_LOG);
		createEReference(wikiPageEClass, WIKI_PAGE__ATTRIBUTES);
		createEOperation(wikiPageEClass, WIKI_PAGE___COMPARE_TO__OBJECT);
		createEOperation(wikiPageEClass, WIKI_PAGE___CLONE);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_LAST_MODIFIED_DATE);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_AUTHOR);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_LAST_CONTENT);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_VERSION);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_ATTRIBUTE__STRING);
		createEOperation(wikiPageEClass, WIKI_PAGE___SET_ATTRIBUTE__STRING_OBJECT);
		createEOperation(wikiPageEClass, WIKI_PAGE___IS_INTERNAL_PAGE);
		createEOperation(wikiPageEClass, WIKI_PAGE___GET_PAGE_CONTENTS_REVERSED);

		pagesStoreEClass = createEClass(PAGES_STORE);
		createEReference(pagesStoreEClass, PAGES_STORE__WIKIPAGES);
		createEAttribute(pagesStoreEClass, PAGES_STORE__MAIN_PAGE_ID);
		createEAttribute(pagesStoreEClass, PAGES_STORE__NEXT_PAGE_ID);
		createEAttribute(pagesStoreEClass, PAGES_STORE__NEXT_ATTACHMENT_ID);

		pageContentEClass = createEClass(PAGE_CONTENT);
		createEAttribute(pageContentEClass, PAGE_CONTENT__CONTENT);
		createEReference(pageContentEClass, PAGE_CONTENT__WIKIPAGE);
		createEOperation(pageContentEClass, PAGE_CONTENT___GET_LENGTH);

		pageAttachmentEClass = createEClass(PAGE_ATTACHMENT);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__ID);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__NAME);
		createEAttribute(pageAttachmentEClass, PAGE_ATTACHMENT__LAST_VERSION);
		createEReference(pageAttachmentEClass, PAGE_ATTACHMENT__WIKIPAGE);
		createEReference(pageAttachmentEClass, PAGE_ATTACHMENT__ATTACH_CONTENTS);
		createEReference(pageAttachmentEClass, PAGE_ATTACHMENT__ATTACHMENT_CONTENT);
		createEOperation(pageAttachmentEClass, PAGE_ATTACHMENT___FOR_LAST_CONTENT);
		createEOperation(pageAttachmentEClass, PAGE_ATTACHMENT___FOR_VERSION_CONTENT__INT);

		objectEClass = createEClass(OBJECT);

		comparableEClass = createEClass(COMPARABLE);
		createEOperation(comparableEClass, COMPARABLE___COMPARE_TO__OBJECT);

		cloneableEClass = createEClass(CLONEABLE);
		createEOperation(cloneableEClass, CLONEABLE___CLONE);

		iHistoryInfoEClass = createEClass(IHISTORY_INFO);
		createEAttribute(iHistoryInfoEClass, IHISTORY_INFO__VERSION);
		createEAttribute(iHistoryInfoEClass, IHISTORY_INFO__CREATION_DATE);
		createEAttribute(iHistoryInfoEClass, IHISTORY_INFO__AUTHOR);
		createEAttribute(iHistoryInfoEClass, IHISTORY_INFO__CHANGE_NOTE);

		pageReferenceEClass = createEClass(PAGE_REFERENCE);
		createEAttribute(pageReferenceEClass, PAGE_REFERENCE__PAGE_ID);
		createEReference(pageReferenceEClass, PAGE_REFERENCE__WIKIPAGE);

		aclEntryEClass = createEClass(ACL_ENTRY);
		createEAttribute(aclEntryEClass, ACL_ENTRY__PRINCIPAL);
		createEAttribute(aclEntryEClass, ACL_ENTRY__PERMISSION);
		createEOperation(aclEntryEClass, ACL_ENTRY___CHECK_PERMISSION__PERMISSION);
		createEOperation(aclEntryEClass, ACL_ENTRY___FIND_PERMISSION__PERMISSION);

		principalEClass = createEClass(PRINCIPAL);

		aclEClass = createEClass(ACL);
		createEReference(aclEClass, ACL__ACL_ENTRIES);
		createEOperation(aclEClass, ACL___GET_ENTRY__PRINCIPAL);
		createEOperation(aclEClass, ACL___FIND_PRINCIPALS__PERMISSION);

		stringToObjectMapEClass = createEClass(STRING_TO_OBJECT_MAP);
		createEAttribute(stringToObjectMapEClass, STRING_TO_OBJECT_MAP__KEY);
		createEAttribute(stringToObjectMapEClass, STRING_TO_OBJECT_MAP__VALUE);

		attachmentContentEClass = createEClass(ATTACHMENT_CONTENT);
		createEAttribute(attachmentContentEClass, ATTACHMENT_CONTENT__PLACE);
		createEAttribute(attachmentContentEClass, ATTACHMENT_CONTENT__SIZE);
		createEReference(attachmentContentEClass, ATTACHMENT_CONTENT__PAGE_ATTACHMENT);

		// Create data types
		arrayStringEDataType = createEDataType(ARRAY_STRING);
		accessListEDataType = createEDataType(ACCESS_LIST);
		principalObjectEDataType = createEDataType(PRINCIPAL_OBJECT);
		arrayPrincipalEDataType = createEDataType(ARRAY_PRINCIPAL);
		permissionObjectEDataType = createEDataType(PERMISSION_OBJECT);
		listPageContentEDataType = createEDataType(LIST_PAGE_CONTENT);
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
		pageContentEClass.getESuperTypes().add(this.getIHistoryInfo());
		attachmentContentEClass.getESuperTypes().add(this.getIHistoryInfo());

		// Initialize classes, features, and operations; add parameters
		initEClass(wikiPageEClass, WikiPage.class, "WikiPage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getWikiPage_Id(), ecorePackage.getEString(), "id", "", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Name(), ecorePackage.getEString(), "name", "", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_LastVersion(), ecorePackage.getEInt(), "lastVersion", "0", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Description(), ecorePackage.getEString(), "description", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Alias(), ecorePackage.getEString(), "alias", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Redirect(), ecorePackage.getEString(), "redirect", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_ViewCount(), ecorePackage.getEInt(), "viewCount", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_PageContents(), this.getPageContent(), this.getPageContent_Wikipage(), "pageContents", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Attachments(), this.getPageAttachment(), this.getPageAttachment_Wikipage(), "attachments", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_Wiki(), ecorePackage.getEString(), "wiki", "", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Children(), this.getWikiPage(), this.getWikiPage_Parent(), "children", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Parent(), this.getWikiPage(), this.getWikiPage_Children(), "parent", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_OldParents(), this.getArrayString(), "oldParents", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_PageReferences(), this.getPageReference(), this.getPageReference_Wikipage(), "pageReferences", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Acl(), this.getAcl(), null, "acl", null, 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getWikiPage_WebLog(), ecorePackage.getEBoolean(), "webLog", "false", 0, 1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWikiPage_Attributes(), this.getStringToObjectMap(), null, "attributes", null, 0, -1, WikiPage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		getWikiPage_Attributes().getEKeys().add(this.getStringToObjectMap_Key());

		EOperation op = initEOperation(getWikiPage__CompareTo__Object(), ecorePackage.getEInt(), "compareTo", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getObject(), "obj", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__Clone(), this.getObject(), "clone", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetLastModifiedDate(), ecorePackage.getEDate(), "getLastModifiedDate", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetAuthor(), ecorePackage.getEString(), "getAuthor", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetLastContent(), this.getPageContent(), "getLastContent", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetVersion(), ecorePackage.getEInt(), "getVersion", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getWikiPage__GetAttribute__String(), ecorePackage.getEJavaObject(), "getAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getWikiPage__SetAttribute__String_Object(), null, "setAttribute", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEJavaObject(), "value", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__IsInternalPage(), ecorePackage.getEBoolean(), "isInternalPage", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEOperation(getWikiPage__GetPageContentsReversed(), this.getListPageContent(), "getPageContentsReversed", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(pagesStoreEClass, PagesStore.class, "PagesStore", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPagesStore_Wikipages(), this.getWikiPage(), null, "wikipages", null, 0, -1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPagesStore_MainPageId(), ecorePackage.getEString(), "mainPageId", null, 0, 1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPagesStore_NextPageId(), ecorePackage.getEString(), "nextPageId", "0", 0, 1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPagesStore_NextAttachmentId(), ecorePackage.getEString(), "nextAttachmentId", "0", 0, 1, PagesStore.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pageContentEClass, PageContent.class, "PageContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPageContent_Content(), ecorePackage.getEString(), "content", "", 0, 1, PageContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageContent_Wikipage(), this.getWikiPage(), this.getWikiPage_PageContents(), "wikipage", null, 0, 1, PageContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getPageContent__GetLength(), ecorePackage.getEIntegerObject(), "getLength", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(pageAttachmentEClass, PageAttachment.class, "PageAttachment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPageAttachment_Id(), ecorePackage.getEString(), "id", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageAttachment_Name(), ecorePackage.getEString(), "name", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPageAttachment_LastVersion(), ecorePackage.getEInt(), "lastVersion", "0", 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageAttachment_Wikipage(), this.getWikiPage(), this.getWikiPage_Attachments(), "wikipage", null, 0, 1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageAttachment_AttachContents(), this.getAttachmentContent(), this.getAttachmentContent_PageAttachment(), "attachContents", null, 0, -1, PageAttachment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageAttachment_AttachmentContent(), this.getAttachmentContent(), null, "attachmentContent", null, 0, 1, PageAttachment.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getPageAttachment__ForLastContent(), this.getAttachmentContent(), "forLastContent", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getPageAttachment__ForVersionContent__int(), this.getAttachmentContent(), "forVersionContent", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEInt(), "desiredVersion", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(objectEClass, Object.class, "Object", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEClass(comparableEClass, Comparable.class, "Comparable", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		op = initEOperation(getComparable__CompareTo__Object(), ecorePackage.getEInt(), "compareTo", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getObject(), "obj", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(cloneableEClass, Cloneable.class, "Cloneable", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEOperation(getCloneable__Clone(), this.getObject(), "clone", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(iHistoryInfoEClass, IHistoryInfo.class, "IHistoryInfo", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getIHistoryInfo_Version(), ecorePackage.getEInt(), "version", null, 0, 1, IHistoryInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIHistoryInfo_CreationDate(), ecorePackage.getEDate(), "creationDate", null, 0, 1, IHistoryInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIHistoryInfo_Author(), ecorePackage.getEString(), "author", "", 0, 1, IHistoryInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getIHistoryInfo_ChangeNote(), ecorePackage.getEString(), "changeNote", null, 0, 1, IHistoryInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(pageReferenceEClass, PageReference.class, "PageReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPageReference_PageId(), ecorePackage.getEString(), "pageId", "", 0, 1, PageReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPageReference_Wikipage(), this.getWikiPage(), this.getWikiPage_PageReferences(), "wikipage", null, 0, 1, PageReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(aclEntryEClass, AclEntry.class, "AclEntry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAclEntry_Principal(), this.getPrincipalObject(), "principal", null, 0, 1, AclEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAclEntry_Permission(), this.getPermissionObject(), "permission", null, 0, -1, AclEntry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = initEOperation(getAclEntry__CheckPermission__Permission(), ecorePackage.getEBooleanObject(), "checkPermission", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getPermissionObject(), "permission", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getAclEntry__FindPermission__Permission(), this.getPermissionObject(), "findPermission", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getPermissionObject(), "p", 0, 1, IS_UNIQUE, IS_ORDERED);

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

		initEClass(attachmentContentEClass, AttachmentContent.class, "AttachmentContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAttachmentContent_Place(), ecorePackage.getEString(), "place", null, 0, 1, AttachmentContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAttachmentContent_Size(), ecorePackage.getELong(), "size", null, 0, 1, AttachmentContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAttachmentContent_PageAttachment(), this.getPageAttachment(), this.getPageAttachment_AttachContents(), "pageAttachment", null, 0, 1, AttachmentContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize data types
		initEDataType(arrayStringEDataType, String[].class, "ArrayString", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(accessListEDataType, Object.class, "AccessList", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(principalObjectEDataType, Principal.class, "PrincipalObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(arrayPrincipalEDataType, Principal[].class, "ArrayPrincipal", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(permissionObjectEDataType, Permission.class, "PermissionObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(listPageContentEDataType, List.class, "ListPageContent", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.List<org.elwiki_data.PageContent>");

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
