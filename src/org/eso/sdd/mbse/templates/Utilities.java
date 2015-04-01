package org.eso.sdd.mbse.templates;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.sysml.util.SysMLConstants;
import com.nomagic.requirements.util.SysMLRequirementConstants;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.DiagramPropertiesShape;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Namespace;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;


import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;

import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

public class Utilities {
	private PropertyManager properties = null;
	private ElementsFactory factory = null;
	private boolean se2Available = true;  
	private Profile SE2Profile = StereotypesHelper.getProfile(Application
			.getInstance().getProject(), "SE2Profile");

	public final String DT_BDD = SysMLConstants.SYSML_BLOCK_DEFINITION_DIAGRAM;
	public final String DT_IBD = SysMLConstants.SYSML_INTERNAL_BLOCK_DIAGRAM;
	public final String DT_PMD = SysMLConstants.SYSML_PARAMETERIC_DIAGRAM;
	//public final String DT_RD  = SysMLRequirementConstants.SYSML_REQUIREMENTS_DIAGRAM;
	public final String DT_RD  = SysMLConstants.SYSML_REQUIREMENTS_DIAGRAM;
	public final String DT_PKD = SysMLConstants.SYSML_PACKAGE_DIAGRAM;
	

	
	public Utilities() {
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));
		factory = Application.getInstance().getProject().getElementsFactory();

		if (SE2Profile == null) { 
			//displayWarning("MBSE Internal Error: SE2Profile is null");
			logDebug("MBSE WARN: SE2Profile seems not to exist: reduced functionality");
			se2Available = false;
		}
	}
	
	
	public boolean haveSE2() {
		return se2Available;
	}

	public void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);

	}

	public void logDebug(String string) {
		System.out.println(string);
	}

	
	public void setupDiagramInfo(Diagram diagram) { 
		try { 
			DiagramPresentationElement diagramPE = Application.getInstance().getProject().getDiagram(
				diagram);
			PresentationElementsManager.getInstance().setPresentationElementProperties(diagramPE, properties);
		// adding diagram info
			StereotypesHelper.addStereotypeByString(diagram, "DiagramInfo");				
			PresentationElementsManager.getInstance().setPresentationElementProperties(diagramPE, properties); 
		
			List<PresentationElement> presentationElements = diagramPE.getPresentationElements();
			for (int i = presentationElements.size() - 1; i >= 0; --i) {
				PresentationElement presentationElement = presentationElements.get(i);
				if (presentationElement instanceof DiagramPropertiesShape) {
		     // change bounds
					DiagramPropertiesShape diagramPropertiesShape = (DiagramPropertiesShape) presentationElement;
					Rectangle bounds = new Rectangle(diagramPropertiesShape.getBounds());
					bounds.x = 650;
					bounds.y = 5;
					PresentationElementsManager.getInstance().reshapeShapeElement(diagramPropertiesShape, bounds);
					break;
		    }
		   }
		} catch (ReadOnlyElementException e) { 
			displayWarning(e.getStackTrace()+"");
		}
	}

	public Diagram createDiagram(Namespace father, String diagramName, String diagramType) {
		Diagram diagram = null;
		try {
			diagram = ModelElementsManager.getInstance().createDiagram(diagramType, father);
			diagram.setName(diagramName);
			// add diagram info
			setupDiagramInfo(diagram);
		} catch (ReadOnlyElementException e) {
			displayWarning("MBSE Plugin " + e.getStackTrace());
		} catch (IllegalArgumentException iae) { 
			logDebug("Attention: empty IAE for diagram "+ diagramType);
		}
		return diagram;
	}


	public Package addPackage(Element ne, String strName) { 
		Package nPack = factory.createPackageInstance();
		nPack.setName(strName);
		try {
			ModelElementsManager.getInstance().addElement(nPack, ne);
		} catch (ReadOnlyElementException e) {
			displayWarning("Element "+ne.getHumanName()+ "(" + ne.getHumanType() + ") is read only");
			// package\n");
		}
	 	return nPack;
	}
	
	public void applyStereotypeToPackage(Package p,String stereo) { 
		Stereotype packStereotype = null;
		if(se2Available) {
			packStereotype = StereotypesHelper.getStereotype(
					Application.getInstance().getProject(), stereo,
					SE2Profile);
			if(packStereotype != null) { 
				StereotypesHelper.addStereotype(p, packStereotype);
			} else { 
				logDebug("WARN: stereotype  "+stereo+ " seems not to exist.");
			}
		}
	}

	public void applyStereotypeToDiagram(Diagram d,String stereo) { 
		Stereotype packStereotype = null;
		if(se2Available) {
			packStereotype = StereotypesHelper.getStereotype(
					Application.getInstance().getProject(), stereo,
					SE2Profile);
			if(packStereotype != null) { 
				StereotypesHelper.addStereotype(d, packStereotype);
			} else { 
				logDebug("WARN: stereotype  "+stereo+ " seems not to exist.");
			}
		}
		
	}
	
	public void addElementToDiagram(Element theElement, Diagram diagram, Rectangle newBounds) { 
		DiagramPresentationElement diagramPE = null;
		diagramPE = Application.getInstance().getProject().getDiagram(diagram);
		// adding the requirement to the requirement diagram
		try {
			ShapeElement shape = PresentationElementsManager.getInstance()
					.createShapeElement(theElement, diagramPE);
			PresentationElementsManager.getInstance().reshapeShapeElement(
					shape, newBounds);

		} catch (ReadOnlyElementException e) {
			displayWarning("MBSE Plugin: readonly element "
					+ diagram.getHumanName() + "\n" + e.getStackTrace());
		}
		
	}
	
	public Diagram getDiagram(NamedElement theElement, String diagramName) { 
		Diagram theDiagram = null;
		Iterator<Element> it = null;
		for( it = theElement.getOwnedElement().iterator(); it.hasNext(); ) { 
			Element ownedElement = it.next();
			if(ownedElement instanceof Diagram) { 
				if(ownedElement instanceof NamedElement) { 
					if (((NamedElement)ownedElement).getName().equals(diagramName)) { 
						theDiagram = (Diagram)ownedElement;
					}
				}
			}
		}
		
		return theDiagram;
	}


}
