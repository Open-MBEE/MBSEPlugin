/*
 * 
 *    (c) European Southern Observatory, 2011
 *    Copyright by ESO 
 *    All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *
 *    $Id: Utilities.java 697 2014-10-28 16:31:47Z mzampare $
 */

package org.eso.sdd.mbse.safety.algo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.impl.EnumerationLiteralImpl;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;

public class Utilities {
	// from SysML
	private Stereotype theRequirementStereotype  = null;
	private Stereotype theFlowPropertyStereotype  = null;
	private Stereotype theBlockStereotype = null;
	
	// from Safety
	private Stereotype theHazardStereotype  = null;
	private Stereotype theFaultStereotype  = null;
	private Stereotype theBasicFaultStereotype = null;
	private Stereotype theUndevelopedFaultStereotype = null;
	private Stereotype theResultingConditionStereotype = null;
	private Stereotype theFaultSourceStereotype = null;
	private Stereotype theFTANodeStereotype = null;

	private Stereotype theBinaryOperatorStereotype = null;
	private Stereotype theANDOperatorStereotype = null;	
	private Stereotype theOROperatorStereotype = null;	
	private Stereotype theXOROperatorStereotype = null;	
	private Stereotype theNOTOperatorStereotype = null;	
	
	// from MD
	private Stereotype theMDDiagramTableStereotype = null;
	
	private Profile SysMLProfile = null;
	private Profile UMLStandardProfile = null;
	private Profile SafetyProfile = null;
	
	private List<Stereotype> theStereoCollection = null;

	public static String lE = System.getProperty("line.separator");
	private Project PROJECT = null;


	public Utilities() {

		setPROJECT(Application.getInstance().getProject());

		SysMLProfile = StereotypesHelper.getProfile(Application.getInstance()
				.getProject(), "SysML");
		UMLStandardProfile = StereotypesHelper.getProfile(Application.getInstance()
				.getProject(), "MagicDraw Profile");

		SafetyProfile = StereotypesHelper.getProfile(Application.getInstance()
				.getProject(), "Safety Profile");

		theStereoCollection = new ArrayList<Stereotype>();


		// SYSML 
		if (SysMLProfile == null) {
			// LOG ERROR
			System.err.println("MBSE: SysML Profile is null.");
		} else {
			theRequirementStereotype = StereotypesHelper.getStereotype (Application
					.getInstance().getProject(), "Requirement", SysMLProfile);
			theFlowPropertyStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "FlowProperty", SysMLProfile);

			theBlockStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "Block",SysMLProfile );

		}

		// SAFETY
		if (SafetyProfile == null) {
			// LOG ERROR
			System.err.println("MBSE: SafetyProfile is null.");
		} else {
			// safety
			theFTANodeStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "FTANode", SafetyProfile);
			theHazardStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "Hazard", SafetyProfile);
			theBasicFaultStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "BasicFault", SafetyProfile);
			theFaultSourceStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "FaultSource", SafetyProfile);

			theFaultStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "Fault", SafetyProfile);

			theUndevelopedFaultStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "UndevelopedFault", SafetyProfile);
			theResultingConditionStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "ResultingCondition", SafetyProfile);
			
			theBinaryOperatorStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "BinaryOperator", SafetyProfile);

			theANDOperatorStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "ANDOperator", SafetyProfile);

			theOROperatorStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "OROperator", SafetyProfile);
}

		theMDDiagramTableStereotype = StereotypesHelper.getStereotype(Application
				.getInstance().getProject(), "DiagramTable",UMLStandardProfile );
		
	}

	public List<Stereotype> getStereotypesList() {
		return theStereoCollection;

	}

	public static void insertElementInTaggedValueList(Element father,
			Element addendum, Element after, Stereotype theStereotype,
			String tagName) {
		List<Element> nList = StereotypesHelper.getStereotypePropertyValue(
				father, theStereotype, tagName, true);
		for (int i = 0; i < nList.size(); i++) {
			if (nList.get(i).equals(after)) {
				nList.add(i + 1, addendum);
				break;
			}
			// System.out.println("Position " + i + ":" +
			// ((Element)nList.get(i)).getHumanName());
		}
		StereotypesHelper.setStereotypePropertyValue(father, theStereotype, tagName, nList, false);
	}

	public static void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);
	}


	
	// SAFETY
	public Stereotype getTheHazardStereotype() {
		return theHazardStereotype;
	}

	public Stereotype getTheFTANodeStereotype() {
		return theFTANodeStereotype;
	}

	public Stereotype getTheUndevelopedFaultSereotype() {
		return theUndevelopedFaultStereotype;
	}

	public Stereotype getTheFaultStereotype() {
		return theFaultStereotype;
	}

	public Stereotype getTheUndevelopedFaultStereotype() {
		return theUndevelopedFaultStereotype;
	}

	public Stereotype getTheBasicFaultStereotype() {
		return theBasicFaultStereotype;
	}

	public Stereotype getTheBinaryOperatorStereotype() {
		return theBinaryOperatorStereotype;
	}
	
	public Stereotype getTheResultingConditionStereotype() {
		return theResultingConditionStereotype;
	}

	public Stereotype getTheANDOperatorStereotype() {
		return theANDOperatorStereotype;
	}
	
	public Stereotype getTheOROperatorStereotype() {
		return theOROperatorStereotype;
	}
	
	
	public static boolean isPackage(Element ne) {
		return ne.getHumanType().equals("Package");
	}


	public static boolean isRequirement(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "Requirement");
	}

	public static boolean isConstraintBlock(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "ConstraintBlock");
	}


	public static boolean hasDocumentation(Element el) {
		return el.has_commentOfAnnotatedElement();
	}

	public static boolean isAuthor(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "author");
	}

	public static String getFirstElementString(Element theElement,
			Stereotype theStereotype, String key) {
		Object tmpObject = null;
		String retVal = "";
		if (!StereotypesHelper.getStereotypePropertyValue(theElement,
				theStereotype, key).isEmpty()) {

			tmpObject = StereotypesHelper.getStereotypePropertyValue(
					theElement, theStereotype, key).get(0);
			if (tmpObject instanceof EnumerationLiteralImpl) {
				retVal = ((EnumerationLiteralImpl) tmpObject).getName();
			} else {
				retVal = (String) tmpObject;
			}
		}
		return retVal;
	}




	public void setPROJECT(Project pROJECT) {
		PROJECT = pROJECT;
	}

	public Project getPROJECT() {
		return PROJECT;
	}

	



}
