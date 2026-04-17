package adapters.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.omg.sysml.lang.sysml.Annotation;
import org.omg.sysml.lang.sysml.Documentation;
import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.lang.sysml.OwningMembership;
import org.omg.sysml.lang.sysml.Relationship;
import org.omg.sysml.lang.sysml.TextualRepresentation;

public class InitialNode implements Element {
	
    private String elementId = UUID.randomUUID().toString();
	private String declaredName;
	private Element owner;

	@Override
	public EClass eClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource eResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EObject eContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EReference eContainmentFeature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<EObject> eContents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean eIsProxy() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EList<EObject> eCrossReferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Adapter> eAdapters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean eDeliver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void eSetDeliver(boolean deliver) {
		// TODO Auto-generated method stub
	}

	@Override
	public void eNotify(Notification notification) {
		// TODO Auto-generated method stub
	}

	@Override
	public String effectiveName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String effectiveShortName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String escapedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<String> getAliasIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeclaredName() {
		return declaredName;
	}

	@Override
	public String getDeclaredShortName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Documentation> getDocumentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public String getElementId() {
        return elementId;
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Annotation> getOwnedAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Element> getOwnedElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<Relationship> getOwnedRelationship() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element getOwner() {
		return owner;
	}

	@Override
	public OwningMembership getOwningMembership() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Namespace getOwningNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Relationship getOwningRelationship() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EList<TextualRepresentation> getTextualRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isImpliedIncluded() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLibraryElement() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Namespace libraryNamespace() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String path() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDeclaredName(String arg0) {
		this.declaredName = arg0;
	}

	@Override
	public void setDeclaredShortName(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setElementId(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setIsImpliedIncluded(boolean arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setIsLibraryElement(boolean arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setName(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setOwner(Element arg0) {
		this.owner = arg0;
	}

	@Override
	public void setOwningMembership(OwningMembership arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setOwningNamespace(Namespace arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setOwningRelationship(Relationship arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setQualifiedName(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setShortName(String arg0) {
		// TODO Auto-generated method stub
	}
}
