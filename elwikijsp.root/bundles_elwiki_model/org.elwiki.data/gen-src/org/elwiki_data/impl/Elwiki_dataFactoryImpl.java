/**
 */
package org.elwiki_data.impl;

import java.security.Permission;
import java.security.Principal;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.AttachmentContent;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
import org.elwiki_data.TagsList;
import org.elwiki_data.UnknownPage;
import org.elwiki_data.WikiPage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Elwiki_dataFactoryImpl extends EFactoryImpl implements Elwiki_dataFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Elwiki_dataFactory init() {
		try {
			Elwiki_dataFactory theElwiki_dataFactory = (Elwiki_dataFactory)EPackage.Registry.INSTANCE.getEFactory(Elwiki_dataPackage.eNS_URI);
			if (theElwiki_dataFactory != null) {
				return theElwiki_dataFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new Elwiki_dataFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Elwiki_dataFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case Elwiki_dataPackage.WIKI_PAGE: return (EObject)createWikiPage();
			case Elwiki_dataPackage.PAGES_STORE: return (EObject)createPagesStore();
			case Elwiki_dataPackage.PAGE_CONTENT: return (EObject)createPageContent();
			case Elwiki_dataPackage.PAGE_ATTACHMENT: return (EObject)createPageAttachment();
			case Elwiki_dataPackage.ATTACHMENT_CONTENT: return (EObject)createAttachmentContent();
			case Elwiki_dataPackage.COMPARABLE: return (EObject)createComparable();
			case Elwiki_dataPackage.CLONEABLE: return (EObject)createCloneable();
			case Elwiki_dataPackage.PAGE_REFERENCE: return (EObject)createPageReference();
			case Elwiki_dataPackage.ACL_ENTRY: return (EObject)createAclEntry();
			case Elwiki_dataPackage.ACL: return (EObject)createAcl();
			case Elwiki_dataPackage.STRING_TO_OBJECT_MAP: return (EObject)createStringToObjectMap();
			case Elwiki_dataPackage.UNKNOWN_PAGE: return (EObject)createUnknownPage();
			case Elwiki_dataPackage.TAGS_LIST: return (EObject)createTagsList();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case Elwiki_dataPackage.ARRAY_STRING:
				return createArrayStringFromString(eDataType, initialValue);
			case Elwiki_dataPackage.ACCESS_LIST:
				return createAccessListFromString(eDataType, initialValue);
			case Elwiki_dataPackage.PRINCIPAL_OBJECT:
				return createPrincipalObjectFromString(eDataType, initialValue);
			case Elwiki_dataPackage.ARRAY_PRINCIPAL:
				return createArrayPrincipalFromString(eDataType, initialValue);
			case Elwiki_dataPackage.PERMISSION_OBJECT:
				return createPermissionObjectFromString(eDataType, initialValue);
			case Elwiki_dataPackage.LIST_PAGE_CONTENT:
				return createListPageContentFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case Elwiki_dataPackage.ARRAY_STRING:
				return convertArrayStringToString(eDataType, instanceValue);
			case Elwiki_dataPackage.ACCESS_LIST:
				return convertAccessListToString(eDataType, instanceValue);
			case Elwiki_dataPackage.PRINCIPAL_OBJECT:
				return convertPrincipalObjectToString(eDataType, instanceValue);
			case Elwiki_dataPackage.ARRAY_PRINCIPAL:
				return convertArrayPrincipalToString(eDataType, instanceValue);
			case Elwiki_dataPackage.PERMISSION_OBJECT:
				return convertPermissionObjectToString(eDataType, instanceValue);
			case Elwiki_dataPackage.LIST_PAGE_CONTENT:
				return convertListPageContentToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WikiPage createWikiPage() {
		WikiPageImpl wikiPage = new WikiPageImpl();
		return wikiPage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PagesStore createPagesStore() {
		PagesStoreImpl pagesStore = new PagesStoreImpl();
		return pagesStore;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PageContent createPageContent() {
		PageContentImpl pageContent = new PageContentImpl();
		return pageContent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PageAttachment createPageAttachment() {
		PageAttachmentImpl pageAttachment = new PageAttachmentImpl();
		return pageAttachment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AttachmentContent createAttachmentContent() {
		AttachmentContentImpl attachmentContent = new AttachmentContentImpl();
		return attachmentContent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Comparable createComparable() {
		ComparableImpl comparable = new ComparableImpl();
		return comparable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Cloneable createCloneable() {
		CloneableImpl cloneable = new CloneableImpl();
		return cloneable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PageReference createPageReference() {
		PageReferenceImpl pageReference = new PageReferenceImpl();
		return pageReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AclEntry createAclEntry() {
		AclEntryImpl aclEntry = new AclEntryImpl();
		return aclEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Acl createAcl() {
		AclImpl acl = new AclImpl();
		return acl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, Object> createStringToObjectMap() {
		StringToObjectMapImpl stringToObjectMap = new StringToObjectMapImpl();
		return stringToObjectMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public UnknownPage createUnknownPage() {
		UnknownPageImpl unknownPage = new UnknownPageImpl();
		return unknownPage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TagsList createTagsList() {
		TagsListImpl tagsList = new TagsListImpl();
		return tagsList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String[] createArrayStringFromString(EDataType eDataType, String initialValue) {
		return (String[])super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertArrayStringToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object createAccessListFromString(EDataType eDataType, String initialValue) {
		return super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertAccessListToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Principal createPrincipalObjectFromString(EDataType eDataType, String initialValue) {
		return (Principal)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPrincipalObjectToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Principal[] createArrayPrincipalFromString(EDataType eDataType, String initialValue) {
		return (Principal[])super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertArrayPrincipalToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Permission createPermissionObjectFromString(EDataType eDataType, String initialValue) {
		return (Permission)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPermissionObjectToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public List<PageContent> createListPageContentFromString(EDataType eDataType, String initialValue) {
		return (List<PageContent>)super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertListPageContentToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Elwiki_dataPackage getElwiki_dataPackage() {
		return (Elwiki_dataPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static Elwiki_dataPackage getPackage() {
		return Elwiki_dataPackage.eINSTANCE;
	}

} //Elwiki_dataFactoryImpl
