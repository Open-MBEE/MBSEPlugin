package org.eso.sdd.mbse.reasoner;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Generalization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Relationship;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

import org.eso.sdd.mbse.doc.algo.genUtility;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.magicdraw.uml.BaseElement;


public class UnapplyPatternReasoner {
	public static ArrayList<Element> patternProperties;
	public static ArrayList<Element> partProperties;
	
	public UnapplyPatternReasoner(){
		patternProperties = new ArrayList<Element>();
		partProperties = new ArrayList<Element>();
	}
	
	public void setPatternProp(ArrayList<Element> pP){
		patternProperties = pP;
	}
	
	public void setPartProp(ArrayList<Element> pP){
		partProperties = pP;
	}
	
	public void storeAllPatternProperties(BaseElement be) {
		NamedElement ee = (NamedElement) be;
		for (Element ownedElement : ee.getOwnedElement()) {

			if (ownedElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
				String name = ((NamedElement) ownedElement).getName();
				String humanType = ownedElement.getHumanType();
				if (humanType.equals("Value Property")) {
					System.out.println("Registering specification for \"" + name
							+ "\", type:" + humanType);
					patternProperties.add(ownedElement);
					// displayWarning(ownedElement.getHumanName());
				}
				else if ((humanType.equals("Part Property") || humanType.equals("Reference Property")  ||  humanType.equals("Shared Property"))  &&
						// only register properties with the same type as the owner to ensure recursion
						((TypedElement)ownedElement).getType().getName().equals(ee.getName())) {
					System.out.println("Registering specification for \"" + name
							+ "\", type:" + humanType);
					partProperties.add(ownedElement);
					// displayWarning(ownedElement.getHumanName());
				}
			}
		}
	}

	public void removeClassifier(Element userObject, Classifier gen,
			Classifier spec, ElementsFactory factory, boolean recurOption,
			boolean roleNameOp, boolean subsettedOp,boolean redefineOp, boolean createValueOp) {

		Generalization general = null;
		Generalization genToRemove = null;
		Iterator<Element> it2 = null;

		for (Relationship rel : userObject.get_relationshipOfRelatedElement()) {

			if (rel.getHumanName().trim().equals("Generalization")) {

				general = (Generalization) rel;
				if (general.getGeneral() == gen) {
					genToRemove = general;
				}
			}

		}// end for remove specialization

		// remove the matching gen
		if (genToRemove != null) {
			try {
				ModelElementsManager.getInstance().removeElement(genToRemove);
			} catch (ReadOnlyElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	    		StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);

				genUtility.displayWarning(sw.toString());
			}
		}

		if (true) {
			if (userObject.hasOwnedElement()) {

				for (it2 = userObject.getOwnedElement().iterator(); it2
						.hasNext();) {
					Element ownedElement = it2.next();

					if (ownedElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
						String name = ((NamedElement) ownedElement).getName();
						String humanType = ownedElement.getHumanType();
						if (humanType.equals("Part Property") || humanType.equals("Reference Property") ||  humanType.equals("Shared Property")) {
							System.out.println("removing specification for \"" + name
									+ "\", type :" + humanType);
							Property prop = ((Property) ownedElement);

							if (roleNameOp) {// remove rolename=true
								// empty rolename
								prop.setName("");
							}
							
							if(subsettedOp){
								for(Element kk:partProperties){
									Property jj = (Property)kk;
									prop.getSubsettedProperty().remove(jj);
								}
							}

							if (recurOption) {
							removeClassifier((Element) prop.getType(), gen,
									(Classifier) prop.getType(),
									factory, recurOption, roleNameOp, subsettedOp,
									redefineOp, createValueOp);
							}

						}
					}
				}// end for

			}
		}// recurring

		// redefine prop
		if (redefineOp) {
			removeRedefineProp(userObject);
		}

		// create value prop
		if (createValueOp) {
			removeValueProp(userObject, factory);
		}
	}

	private void removeValueProp(Element userObject, ElementsFactory factory) {
		Property propToRemove = null;
		// TODO Auto-generated method stub
		for (Element gg : patternProperties) {

			Collection<Element> partProps = userObject.getOwnedElement();
			for (Element prop : partProps) {
				if (prop instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
					String humanType = prop.getHumanType();
					if (humanType.equals("Value Property")) {
						if (gg.getHumanName().equals(prop.getHumanName())) {
							propToRemove = (Property) prop;

						}
					}// value prop
				}
			}
			// remove the matching property
			if (propToRemove != null) {
				try {
					ModelElementsManager.getInstance().removeElement(
							propToRemove);
				} catch (ReadOnlyElementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		    		StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);

					genUtility.displayWarning(sw.toString());
				}
			}

		}// end search for match
	}

	private void removeRedefineProp(Element ownedElement) {

		for (Element prop : ownedElement.getOwnedElement()) {
			if (prop instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
				String humanType = prop.getHumanType();
				if (humanType.equals("Value Property")) {
					for (Element gg : patternProperties) {
						if (gg.getHumanName().equals(prop.getHumanName())) {

							// redefine
							Property jj = (Property) prop;
							Collection<Property> redefProperties = jj
									.getRedefinedProperty();
							redefProperties.remove(gg);
						}
					}// end search for match
				}// value prop
			}
		}

	}

}
