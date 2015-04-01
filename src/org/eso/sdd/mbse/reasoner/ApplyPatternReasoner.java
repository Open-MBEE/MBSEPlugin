package org.eso.sdd.mbse.reasoner;


import com.nomagic.magicdraw.core.Application; // added specifically for this file

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
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

import javax.swing.JOptionPane;

import org.eso.sdd.mbse.doc.algo.genUtility;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.BaseElement;


public class ApplyPatternReasoner {

	public static ArrayList<Element> patternProperties;
	public static ArrayList<Element> partProperties;
	
	public ApplyPatternReasoner(){
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
					System.out.println("Registering specification for value property\"" + name
							+ "\", type:" + humanType);
                    patternProperties.add(ownedElement);
					//displayWarning(ownedElement.getHumanName()); 
				}
				else if ((humanType.equals("Part Property") || humanType.equals("Reference Property") ||  humanType.equals("Shared Property")) &&
						// only register properties with the same type as the owner to ensure recursion
						((TypedElement)ownedElement).getType().getName().equals(ee.getName())) {
					System.out.println("Registering specification for part/ref property\"" + name
							+ "\", type: " + humanType + 
							" typeOwned: " + ((TypedElement)ownedElement).getType().getName() +
							" typeOwner: " + ee.getName());
                    partProperties.add(ownedElement);
					//displayWarning(ownedElement.getHumanName()); 
				}
			}
		}
	}

	public void createClassifier(Element userObject, Classifier gen,
			Classifier spec, ElementsFactory factory, boolean recurOption, boolean roleNameOp, boolean subsettedOp, boolean redefineOp, boolean createValueOp) {

		Generalization general = null;
		Iterator<Element> it = null;
		Iterator<Relationship> it2 = null;
		boolean hasGen = false;
		
		for (it2 = userObject.get_relationshipOfRelatedElement().iterator(); it2
				.hasNext();) {
			Relationship rel = it2.next();

			if (rel.getHumanName().trim().equals("Generalization")) {

				general = (Generalization) rel;
				if (general.getGeneral() == gen) {
					hasGen = true;
				}
			}

		}//end check for existing generlization
		
		if(!hasGen){
		general = factory.createGeneralizationInstance();

		general.setGeneral(gen);
		general.setSpecific(spec);
		general.setOwner(userObject);

		try {
			ModelElementsManager.getInstance().addElement(general, userObject);
		} catch (ReadOnlyElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		}
		}


		if (true /*recurOption*/) {
			if (userObject.hasOwnedElement()) {

				for (it = userObject.getOwnedElement().iterator(); it.hasNext();) {
					Element ownedElement = it.next();

					if (ownedElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
						String name = ((NamedElement) ownedElement).getName();
						String humanType = ownedElement.getHumanType();
						if (humanType.equals("Part Property")  || humanType.equals("Reference Property")  ||  humanType.equals("Shared Property")) {
							System.out.println("Applying specification for part/ref property \"" + name
									+ "\", type:" + humanType);

							Property prop = ((Property) ownedElement);

							if (prop.getName().equals("") && roleNameOp) {//set rolename=true
			
									// default rolename
									if (countChild(prop.getOwner()) > 1) {
                                        //displayWarning(prop.getType().getHumanName());
										int current = getCurrentPropCount(prop);
										current++;
										prop.setName(prop.getType().getName()
												.toString().toLowerCase()
												+ "_" + current);
									} else {
										//displayWarning(prop.getType().getHumanName());
										
										prop.setName(prop.getType().getName()
												.toString().toLowerCase());
									}
								

							}
							if(subsettedOp){//set subsetted property
								for(Element gg:partProperties){
									Property qq = (Property) gg;
									if(checkContain(prop.getSubsettedProperty(),qq)){
									prop.getSubsettedProperty().add(qq);
									}
								}
							}

							if (recurOption) {
							createClassifier((Element) prop.getType(), gen,
									(Classifier) prop.getType(),
									factory, recurOption, roleNameOp, subsettedOp,redefineOp, createValueOp);
							}

						}
					}
				}// end for

			}
		}// recurring
		
		//create value prop
		if(createValueOp){
		  createValueProp(userObject,factory);
		}
		//redefine prop
		if(redefineOp){
		  redefineProp(userObject);
		}
		
	}

	private int countChild(Element owner) {
		
	Iterator<Element> it = null;
	int count = 0;
	
		if (owner.hasOwnedElement()) {

			for (it = owner.getOwnedElement().iterator(); it.hasNext();) {
				Element ownedElement = it.next();

				if (ownedElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
//					String name = ((NamedElement) ownedElement).getName();
					String humanType = ownedElement.getHumanType();
					// System.out.println("Applying specification for " + name
					//	+ " ,type :" + humanType);
					if (humanType.equals("Part Property") || humanType.equals("Reference Property")  ||  humanType.equals("Shared Property")) {
						count++;
					}
				}
			}
		}

		return count;
	}

	private void createValueProp(Element userObject, ElementsFactory factory) {
		// TODO Auto-generated method stub
		for(Element gg:patternProperties){

			if(!checkExistingValueProp(userObject,gg)){

				//create new value property qq
				Property qq = factory.createPropertyInstance();
				
				qq.setName(((NamedElement)gg).getName());
				
				StereotypesHelper.addStereotype(qq, StereotypesHelper.getStereotype(Application
				.getInstance().getProject(), "ValueProperty"));
				
				qq.setType(((Property)gg).getType());
				
				try {
					ModelElementsManager.getInstance().addElement(qq, userObject);
				} catch (ReadOnlyElementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
		    		StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);

					genUtility.displayWarning(sw.toString());
				}

			}
		}//end search for match
	}

	private boolean checkExistingValueProp(Element userObject, Element gg) {
		for (Element prop : userObject.getOwnedElement()) {
			if (prop instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
				String humanType = prop.getHumanType();
				if (humanType.equals("Value Property")) {
						if(gg.getHumanName().equals(prop.getHumanName()) ){
							//existing property
							return true;
			
						}
				}//value prop
			 }
			}
		return false;
	}

	private void redefineProp(Element ownedElement) {

		for (Element prop : ownedElement.getOwnedElement()) {
		if (prop instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
			String humanType = prop.getHumanType();
			if (humanType.equals("Value Property")) {
				for(Element gg:patternProperties){
					if(gg.getHumanName().equals(prop.getHumanName()) ){
						//displayWarning("collision");
						
							//redefine
							Property jj = (Property)prop;
							Collection<Property> redefProperties = jj.getRedefinedProperty();
							//check if the prop is already redefined
							if(checkContain(redefProperties,(Property)gg)){
								redefProperties.add((Property)gg);
							}
		
					}
				}//end search for match
			}//value prop
		 }
		}
		
	}

	private boolean checkContain(Collection<Property> collection, Property gg) {
		for(Property m:collection){
			if(m.getHumanName().equals(gg.getHumanName())){
				return false;
			}
		}
		return true;
	}

	private int getCurrentPropCount(Property prop) {

		int count = 0;

		for (Association e : prop.getType().get_associationOfEndType()) {

			for (Property k : e.getMemberEnd()) {

				Property pp = k;
				if (pp.getType().getName().equals(prop.getType().getName())) {

					if (pp.getName().contains(
							prop.getType().getName().toString().toLowerCase()
									+ ".")) {
						count++;
					}
				}
			}
		}

		return count;
	}
	
	private void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);

	}
	
}
