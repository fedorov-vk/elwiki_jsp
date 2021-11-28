/**
 */
package org.elwiki_data.impl;

import java.security.Permission;
import java.security.Principal;

import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.elwiki.data.authorize.GroupPrincipal;
import org.elwiki.data.authorize.Role;
import org.elwiki.data.authorize.UnresolvedPrincipal;
import org.elwiki.data.authorize.WikiPrincipal;
import org.elwiki.permissions.AllPermission;
import org.elwiki.permissions.GroupPermission;
import org.elwiki.permissions.PagePermission;
import org.elwiki.permissions.WikiPermission;
import org.elwiki_data.Acl;
import org.elwiki_data.AclEntry;
import org.elwiki_data.Elwiki_dataFactory;
import org.elwiki_data.Elwiki_dataPackage;
import org.elwiki_data.PageAttachment;
import org.elwiki_data.PageContent;
import org.elwiki_data.PageReference;
import org.elwiki_data.PagesStore;
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
			case Elwiki_dataPackage.COMPARABLE: return (EObject)createComparable();
			case Elwiki_dataPackage.CLONEABLE: return (EObject)createCloneable();
			case Elwiki_dataPackage.PAGE_REFERENCE: return (EObject)createPageReference();
			case Elwiki_dataPackage.ACL_ENTRY: return (EObject)createAclEntry();
			case Elwiki_dataPackage.ACL: return (EObject)createAcl();
			case Elwiki_dataPackage.STRING_TO_OBJECT_MAP: return (EObject)createStringToObjectMap();
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
			case Elwiki_dataPackage.ARRAY_PRINCIPAL:
				return createArrayPrincipalFromString(eDataType, initialValue);
			case Elwiki_dataPackage.PERMISSION_OBJECT:
				return createPermissionObjectFromString(eDataType, initialValue);
			case Elwiki_dataPackage.PRINCIPAL_OBJECT:
				return createPrincipalObjectFromString(eDataType, initialValue);
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
			case Elwiki_dataPackage.ARRAY_PRINCIPAL:
				return convertArrayPrincipalToString(eDataType, instanceValue);
			case Elwiki_dataPackage.PERMISSION_OBJECT:
				return convertPermissionObjectToString(eDataType, instanceValue);
			case Elwiki_dataPackage.PRINCIPAL_OBJECT:
				return convertPrincipalObjectToString(eDataType, instanceValue);
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
	 * @generated NOT
	 */
	public Permission createPermissionObjectFromString(EDataType eDataType, String initialValue) {
		// :FVK: workaround.
		String[] param = initialValue.split("|");
		String className = param[0];
		String name = param[1];
		String actions = (param.length > 2) ? param[2] : null;

		if (className.equals(PagePermission.class.getName())) {
			return new PagePermission(name, actions);
		} else if (className.equals(WikiPermission.class.getName())) {
			return new WikiPermission(name, actions);
		} else if (className.equals(GroupPermission.class.getName())) {
			return new GroupPermission(name, actions);
		} else if (className.equals(AllPermission.class.getName())) {
			return new AllPermission(name);
		}

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
	 * @generated NOT
	 */
	public Principal createPrincipalObjectFromString(EDataType eDataType, String initialValue) {
		// :FVK: workaround.
		String[] param = initialValue.split("|");
		String className = param[0];
		String name = param[1];
		String type = (param.length > 2) ? param[2] : null;

		if (className.equals(WikiPrincipal.class.getName())) {
			return new WikiPrincipal(name, type);
		} else if (className.equals(Role.class.getName())) {
			return new Role(name);
		} else if (className.equals(GroupPrincipal.class.getName())) {
			return new GroupPrincipal(name);
		} else if (className.equals(UnresolvedPrincipal.class.getName())) {
			return new UnresolvedPrincipal(name);
		}

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
