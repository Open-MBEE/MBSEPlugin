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
 *    $Id: MBSEActionGetTemplate.java 705 2015-01-20 06:41:02Z mzampare $
 *
*/

package org.eso.sdd.mbse.templates;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.magicdraw.sysml.util.SysMLConstants ;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;

import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.uml.symbols.layout.OrthogonalDiagramLayouter;

import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import org.eso.sdd.mbse.templates.Utilities;

@SuppressWarnings("serial")
public class MBSEActionGetTemplate extends DefaultBrowserAction {

	// TODO @TODO@: replace this with a hashtable
	

	private Class mainBlock = null;
	private Class theElementReq = null;

	
	private Package requirementsPackage = null;
	private Package structurePackage = null;
	private Package interfacesPackage = null;
	private Package performancePackage = null;
	private Package variantsPackage    = null;


	private Rectangle newBounds = null;

	private Diagram theContentDiagram;
	private Diagram theInterfacesDiagram;
	private Diagram theProductTreeDiagram;

	private Vector<PresentationElement> toBeLayouted = new Vector<PresentationElement>();

	private ElementsFactory factory = null;
	private String theName = null;
	


	private Stereotype hyperlinkStereotype;
	private Profile SysMLProfile = StereotypesHelper.getProfile(Application
			.getInstance().getProject(), "SysML");
	private Profile SE2Profile = StereotypesHelper.getProfile(Application
			.getInstance().getProject(), "SE2Profile");

	
	private Stereotype blockStereotype = StereotypesHelper.getStereotype(
			Application.getInstance().getProject(), "Block",
			SysMLProfile);
	

	
	private Utilities ut = new Utilities();
	private TemplateStructure ts = null;

	public MBSEActionGetTemplate() {
		super("", "SE2:get System Element Template", null, null);
		ts = new TemplateStructure();
		factory = Application.getInstance().getProject().getElementsFactory();

		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Tree tree = getTree();
		NamedElement ne = null;
		if (tree.getSelectedNodes().length > 1) {
		  ut.displayWarning("MBSE Plugin: cannot do multiple selection");
			return;
		}

		// checks to be done:
		// 1) one selection only
		// 2) is of type package
		Node node = tree.getSelectedNodes()[0];
		Object userObject = node.getUserObject();
		
		if (SysMLProfile == null) {
			ut.displayWarning("MBSE Internal Error: SysML Profile is null");
			return;
		}

		
		if (userObject instanceof NamedElement) {
			System.out.println("MBSE: calling getTemplate on " + ((NamedElement)userObject).getName());
			ne = (NamedElement) userObject;
			theName = ne.getName();
			if (userObject instanceof Package) {

				if (!SessionManager.getInstance().isSessionCreated()) {
					SessionManager.getInstance().createSession("MBSE");
					createTheBlock();
					createStructure(ne);
					createBlockInStructure(ne);
					
					createAllDiagrams(ne);
					SessionManager.getInstance().closeSession();
					Application.getInstance().getProject().getDiagram(theContentDiagram).setSelected(toBeLayouted);
					Application.getInstance().getProject().getDiagram(theContentDiagram).layout(true);
				}
			} else {
				Stereotype checkBlock = StereotypesHelper.getAppliedStereotypeByString(ne,"Block");
				if (checkBlock == null ) {
					ut.displayWarning("MBSE Plugin: what do you want me to do with this?!\n" + ne.getHumanName()+ " is not a block");
					return;
				}
					

				// it should be a block	

				Element father = ne.getOwner();
				if (! (father instanceof Package)) { 
					ut.displayWarning("You cannot create a template from a part property!");
					return;
				}

				// check if a package with the same name exists in the common father
				// if not create one.
				if(elementHasPackageWithName(father,theName) ) {
					ut.displayWarning("MBSE Plugin: cannot work on this block,\nthere's already a package by that name!!");
					return;
				}
				// all checks done, go ahead, the block already exists.
				mainBlock = (Class)ne;

				if (!SessionManager.getInstance().isSessionCreated()) {
					SessionManager.getInstance().createSession("MBSE");
					factory = Application.getInstance().getProject().getElementsFactory();
					// now do the real work, start by adding a package to the father of the block
					// with the same name of the block
					Package theCreatedPackage = ut.addPackage(father,theName);
					//
					ut.applyStereotypeToPackage(theCreatedPackage, "se2.DecomposedSystemElement");
					createStructure(theCreatedPackage);
					createBlockInStructure(theCreatedPackage);

					createAllDiagrams(theCreatedPackage);

					SessionManager.getInstance().closeSession();
					Application.getInstance().getProject().getDiagram(
							theContentDiagram).setSelected(toBeLayouted);
					OrthogonalDiagramLayouter odl = new OrthogonalDiagramLayouter(); 
					Application.getInstance().getProject().getDiagram(theContentDiagram).layout(true,odl);
				} // end of the game.

				return;
					
			}
		} else {
			ut.displayWarning("MBSE Plugin: give your package a name!");
			return;
		}
		// displayWarning(text);
	}

	@Override
	public void updateState() {
		setEnabled(getTree().getSelectedNode() != null);
	}

	
	private boolean elementHasPackageWithName(Element ele, String packageName) {
		Iterator<Element> it = null;
		for (it = ele.getOwnedElement().iterator(); it.hasNext();) {
			Element ownedElement = it.next();
			if ((ownedElement instanceof Package )
					&& ((NamedElement) ownedElement).getName().equals(packageName)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	private void createTheBlock() { 
		// find Block stereotype
		if (blockStereotype != null) {
			Class block = factory.createClassInstance();
			block.setName(theName);
			// apply stereotype
			StereotypesHelper.addStereotype(block, blockStereotype);
			mainBlock = block;
			if(ut.haveSE2()) {
				Stereotype physicalStereotype = StereotypesHelper.getStereotype(
						Application.getInstance().getProject(), "se2.physical",
						SE2Profile);
				if(physicalStereotype != null) { 
					StereotypesHelper.addStereotype(block, physicalStereotype);
				} else { 
					ut.logDebug("WARN: stereotype  physical seems not to exist.");
				}

			}
		}
	}
	

	private void createStructure(NamedElement ne) {
		Boolean flag = false;
		Iterator<Element> it = null;
		String sEN = ne.getName();
		String str = null;
		Enumeration<String> names = ts.struct.keys();
		// here we add the structure first
		while(names.hasMoreElements()) {
			str = names.nextElement(); 
			flag = false;
			String strName = sEN + "_" + str;
			if (ne.hasOwnedElement()) {
				for (it = ne.getOwnedElement().iterator(); it.hasNext();) {
					Element ownedElement = it.next();
					if (ownedElement instanceof NamedElement) {
						if (((NamedElement) ownedElement).getName().equals(strName)) {
							// an element with such a name already exists, skip;
							flag = true;
							break;
						}
					}
				} // end iteration over children
			} // end has children
			if (!flag) {
				Diagram itsDiagram = null;
				String diagramName = "";
				Package nPack = ut.addPackage(ne,strName);
				ut.applyStereotypeToPackage(nPack,"se2."+ ts.struct.get(str).pStereo);
				ts.struct.get(str).pkg = nPack;
				
				diagramName = ts.struct.get(str).dName;
				if(! diagramName.equals("")) { 
					ut.logDebug("creating diagram "+ diagramName + " of type "+ ts.struct.get(str).dType);
					itsDiagram = ut.createDiagram(nPack,sEN+"_"+diagramName,ts.struct.get(str).dType);
					if(itsDiagram == null) { 
						continue;
					}
					ut.applyStereotypeToDiagram(itsDiagram,"se2."+ts.struct.get(str).dStereo);
					ts.struct.get(str).diagram = itsDiagram;
				}
				
			} // end check if structure already exists
		} // end loop over structures (Packages) to be created

		// the assignment of these specific variables is needed by
		// other methods later on
		structurePackage = ts.struct.get("Structure").pkg;		
		requirementsPackage = ts.struct.get("Requirements").pkg;
		performancePackage  = ts.struct.get("Performance").pkg;	
		variantsPackage = ts.struct.get("Variations").pkg;
		
		theProductTreeDiagram  = ts.struct.get("Structure").diagram;
		
		// then we add the meta structure
		for (int i = 0; i < ts.metaStructure.length; i++) {
			flag = false;
			String strName = "_" + sEN + "_" + ts.metaStructure[i];
			if (ne.hasOwnedElement()) {
				for (it = ne.getOwnedElement().iterator(); it.hasNext();) {
					Element ownedElement = it.next();
					if (ownedElement instanceof NamedElement) {
						if (((NamedElement) ownedElement).getName().equals(
								strName)) {
							// an element with such a name already exists, skip;
							flag = true;
							break;
						}
					}
				} // end iteration over children
			} // end has children
			if (!flag) {
				Package nPack = ut.addPackage(ne,strName);
				ut.applyStereotypeToPackage(nPack,"se2."+ ts.metaStructure[i]);

			} // end check if structure already exists
		} // end loop over ts.metaStructures (Packages) to be created
		
		// add a subpackage to the structurePackage
		Package subElementPackage = ut.addPackage(structurePackage, "Subelement Template");
		ut.applyStereotypeToPackage(subElementPackage, "se2.DecomposedSystemElement");		
	}

	/**
	 * @param ne
	 */
	private void createBlockInStructure(NamedElement ne) {
		if (structurePackage != null) {
			try {
				ModelElementsManager.getInstance().addElement(mainBlock,
						structurePackage);
			} catch (ReadOnlyElementException e) {
				ut.displayWarning("Failed to create Block in " + theName
						+ "_Structure");
			}
			
		} else { // end structurePackage != null
			ut.displayWarning("MBSE Action: internal error, the structure package is empty\n");
			return;
		}

		//
		// creating requirement
		if (requirementsPackage != null) {
			Stereotype requirementsStereotype = StereotypesHelper
					.getStereotype(Application.getInstance().getProject(),
							"Requirement");
			if (requirementsStereotype != null) {
				Class req = factory.createClassInstance();
				req.setName(theName);
				try {
					ModelElementsManager.getInstance().addElement(req,
							requirementsPackage);
					// apply stereotype
					StereotypesHelper
							.addStereotype(req, requirementsStereotype);
					theElementReq = req;
				} catch (ReadOnlyElementException e) {
					ut.displayWarning("Failed to create Requirement in " + theName
							+ "_Requirements");
					return;
				}
			} else {
				ut.displayWarning("Requirement stereotype is null");
				return;	
			}

		} else {
			ut.displayWarning("MBSE Internal Error: Requirements Package is null");
			return;
		}

		// prepare mainBlock to host a hyperlink (or several)
		hyperlinkStereotype = StereotypesHelper.getStereotype(
				Application.getInstance().getProject(), "HyperlinkOwner");
		// apply stereotype
		if (hyperlinkStereotype != null) {
			StereotypesHelper.addStereotype(mainBlock, hyperlinkStereotype);
		} else {
			ut.displayWarning("Hyperlink Stereotyping failed");
		}
	}




	private void createContentDiagram(NamedElement ne) {
		Iterator<Element> it = null;
		DiagramPresentationElement diagramPE = null;
		String sEN = ne.getName();

		// content diagram
		theContentDiagram = ut.createDiagram( (Package)ne,
					theName + "_" + "Content", 
					ts.DT_PKD);
		// add all the non META structure packages to this diagram
		diagramPE = Application.getInstance().getProject().getDiagram(theContentDiagram);
			
		for (it = ne.getOwnedElement().iterator(); it.hasNext();) {
			Element ownedElement = it.next();
			if ((ownedElement instanceof Package)
					&& ((NamedElement) ownedElement).getName().startsWith(
							sEN)) {
				try { 
					ShapeElement shape = PresentationElementsManager.getInstance().createShapeElement(
						ownedElement, diagramPE);
					toBeLayouted.add(shape);
					PresentationElementsManager.getInstance().movePresentationElement(shape, new Point(100,100));
				} catch (ReadOnlyElementException e) { 
					ut.displayWarning("MBSE Plugin: " + e.toString());
				}
			}
		}
		ut.applyStereotypeToDiagram(theContentDiagram,"se2.ContentDiagram");
	}


	private void fillPerformancePackage() { 
		//
		Diagram analysisTemplateDefinitionDiagram = null, diagram = null;
		Package aT = ut.addPackage(performancePackage,"Analysis template");
		Class aContext = factory.createClassInstance();
		aContext.setName("Template Analysis Context");

		analysisTemplateDefinitionDiagram = ut.createDiagram(aT, "Analysis Template_DefinitionDiagram", ts.DT_BDD);
		ut.applyStereotypeToDiagram(analysisTemplateDefinitionDiagram, "se2.DefinitionDiagram");		

		try {
			ModelElementsManager.getInstance().addElement(aContext,aT);
			ModelElementsManager.getInstance().addElement(analysisTemplateDefinitionDiagram, aT);
			// apply stereotype
			StereotypesHelper.addStereotype(aContext, blockStereotype);
			if(ut.haveSE2()) {
				Stereotype acStereotype = StereotypesHelper.getStereotype(
						Application.getInstance().getProject(), "se2.analysis context",
						SE2Profile);
				if(acStereotype != null) { 
					StereotypesHelper.addStereotype(aContext, acStereotype);
				} else { 
					ut.logDebug("WARN: stereotype se2.analysis context seems not to exist.");
				}
			}
			diagram = ut.createDiagram(aContext,"Template Analysis Context",ts.DT_PMD );
			ut.applyStereotypeToDiagram(diagram,"se2.AnalysisDiagram");
		} catch (ReadOnlyElementException e) {
			ut.displayWarning("Failed to add element  " + aContext.getHumanName() + " in "
					+ "");
			return;
		}

		ut.addElementToDiagram(aT,ts.struct.get("Performance").diagram,new Rectangle(10, 70, 80, 50));
		ut.addElementToDiagram(aContext,analysisTemplateDefinitionDiagram,new Rectangle(10, 70, 80, 50));
		
		StereotypesHelper.setStereotypePropertyValue(aContext,
				hyperlinkStereotype, "hyperlinkModel", diagram,
				true);
		StereotypesHelper.setStereotypePropertyValue(aContext,
				hyperlinkStereotype, "hyperlinkModelActive",
				diagram, true);

	}
	
	private void fillVariantsPackage() { 
		Class block = null;
		// notice: first diagram "Variations Content" has already been created by createStructure() method.
		
		Package variationTemplate, variantTemplate, variantElementTemplate,variantElementTemplateStructure = null; 
		Diagram variantTemplateContent, variantElementTemplateProductTree = null;		
				
		variationTemplate = ut.addPackage(variantsPackage,"Variation Template");
		ut.applyStereotypeToPackage(variationTemplate, "se2.Variation");
		
		variantTemplate = ut.addPackage(variationTemplate, "Variant Template");
		ut.applyStereotypeToPackage(variantTemplate, "se2.Variant");
		
		variantElementTemplate = ut.addPackage(variantTemplate, "Variant Element Template");		
		ut.applyStereotypeToPackage(variantElementTemplate, "se2.Variant element");
		
		variantElementTemplateStructure = ut.addPackage(variantElementTemplate, "Variant Element Template_Structure");
		ut.applyStereotypeToPackage(variantElementTemplateStructure, "se2.Structure Aspect");		
		
		
		// second diagram, for the Variant. It is a Content Diagram for the Variant
		variantTemplateContent = ut.createDiagram(variantTemplate,"Variant Template_Content",ts.DT_PKD);
		ut.applyStereotypeToDiagram(variantTemplateContent, "se2.ContentDiagram");
		ut.addElementToDiagram(variantElementTemplate,variantTemplateContent ,new Rectangle(100, 200, 80, 50) );		
		
		if (blockStereotype != null) {
			 block = factory.createClassInstance();
			block.setName("Variant Element Template");
			// apply stereotype
			StereotypesHelper.addStereotype(block, blockStereotype);
			if(ut.haveSE2()) {
				Stereotype physicalStereotype = StereotypesHelper.getStereotype(
						Application.getInstance().getProject(), "se2.physical",
						SE2Profile);
				if(physicalStereotype != null) { 
					StereotypesHelper.addStereotype(block, physicalStereotype);
				} else { 
					ut.logDebug("WARN: stereotype  physical seems not to exist.");
				}
				Stereotype variantStereotype = StereotypesHelper.getStereotype(
						Application.getInstance().getProject(), "se2.Variant element",
						SE2Profile);
				if(variantStereotype != null) { 
					StereotypesHelper.addStereotype(block, variantStereotype);
				} else { 
					ut.logDebug("WARN: stereotype  Variant element seems not to exist.");
				}
			}
		}
		
		try { 
			ModelElementsManager.getInstance().addElement(block, variantElementTemplateStructure);
		} catch (ReadOnlyElementException e) {
			ut.displayWarning("Failed to add Block to variantElementTemplateStructure");
		}
		
		// third diagram, a "real" ProducTree
		variantElementTemplateProductTree = ut.createDiagram(variantElementTemplateStructure, "Variant Element Template_ProductTree", ts.DT_BDD);
		ut.applyStereotypeToDiagram(variantElementTemplateProductTree, "se2.ProductTreeDiagram");		

		// add the physical block corresponding to the Variant Element to the ProductTree Diagram
		ut.addElementToDiagram(block, variantElementTemplateProductTree,new Rectangle(100, 200, 80, 50) );
		
		// finally, adding the package to  Variation Template to the outer Content Diagram 
		ut.addElementToDiagram(variationTemplate,ts.struct.get("Variations").diagram ,new Rectangle(100, 200, 80, 50) );		
	}
	
	private void createHTMLCrossReferences() {
		// now we add the respective HTML links in content and ProductTree
		// diagram

		if (theContentDiagram != null && theProductTreeDiagram != null) {
			ShapeElement shape = null;
			DiagramPresentationElement diagramPE1 = Application.getInstance()
					.getProject().getDiagram(theContentDiagram);
			DiagramPresentationElement diagramPE2 = Application.getInstance()
					.getProject().getDiagram(theProductTreeDiagram);
			try {
				newBounds = new Rectangle(10, 700, 80, 50);

				shape = PresentationElementsManager.getInstance()
						.createShapeElement(theProductTreeDiagram, diagramPE1);
				PresentationElementsManager.getInstance().reshapeShapeElement(
						shape, newBounds);

				shape = PresentationElementsManager.getInstance()
						.createShapeElement(theContentDiagram, diagramPE2);
				PresentationElementsManager.getInstance().reshapeShapeElement(
						shape, newBounds);
			} catch (ReadOnlyElementException e) {
				ut.displayWarning("" + e.getStackTrace());
			}
		}
		
		// the main Block tags are set to show this IBD as
		// hyperlink.
		StereotypesHelper.setStereotypePropertyValue(structurePackage,
				hyperlinkStereotype, "hyperlinkModel", theProductTreeDiagram,
				true);
		StereotypesHelper.setStereotypePropertyValue(structurePackage,
				hyperlinkStereotype, "hyperlinkModelActive",
				theProductTreeDiagram, true);

	}
	
	



	private void createIBDDiagrams(NamedElement ne) {
		DiagramPresentationElement diagramPE = null;
		String sEN = ne.getName();
		// specific IBDs

		if (mainBlock != null) {
			for (int i = 0; i < ts.IBDs.length; i++) {
				try {
					ShapeElement shape = null;
					Diagram diagram = ut.createDiagram(mainBlock,sEN + "_" + ts.IBDs[i],ts.DT_IBD );

					// here we add the HTML navigation
					diagramPE = Application.getInstance().getProject()
							.getDiagram(diagram);

					// content diagram is added as hyperlink to this IBD
					shape = PresentationElementsManager.getInstance()
							.createShapeElement(theContentDiagram, diagramPE);

					newBounds = new Rectangle(10, 700, 80, 50);
					PresentationElementsManager.getInstance()
					.reshapeShapeElement(shape, newBounds);

					// product tree diagram is added as hyperlink to this IBD
					shape = PresentationElementsManager.getInstance()
							.createShapeElement(theProductTreeDiagram,
									diagramPE);

					newBounds = new Rectangle(140, 700, 80, 50);
					PresentationElementsManager.getInstance()
					.reshapeShapeElement(shape, newBounds);

					// the main Block tags are set to show this IBD as
					// hyperlink.
					StereotypesHelper.setStereotypePropertyValue(mainBlock,
							hyperlinkStereotype, "hyperlinkModel", diagram,
							true);
					//  lowercase conversion due to Profile inconsistency
					ut.applyStereotypeToDiagram(diagram,"se2."+ ts.IBDs[i].toLowerCase());


				} catch (ReadOnlyElementException e) {
					ut.displayWarning(e.getStackTrace() + "\nELEMENT IS READONLY\n");
				}
			} // end loop over IBDs
		} else {
			ut.displayWarning("cannot create IBDs since no block was created\n");
		}
		// the main Block tags are set to show this IBD as
		// hyperlink.
		StereotypesHelper.setStereotypePropertyValue(mainBlock,
				hyperlinkStereotype, "hyperlinkModel", theContentDiagram,
				true);
		StereotypesHelper.setStereotypePropertyValue(mainBlock,
				hyperlinkStereotype, "hyperlinkModelActive",
				theContentDiagram, true);
	}

	

	
	private void createAllDiagrams(NamedElement ne) {
		createContentDiagram(ne);
		createHTMLCrossReferences();
		if(mainBlock != null ) {
			ut.addElementToDiagram(mainBlock,theProductTreeDiagram,new Rectangle(300, 300, 80, 50) );
		}
		createIBDDiagrams(ne);
		if(ts.struct.get("Requirements").diagram != null) { 
			ut.addElementToDiagram(theElementReq,ts.struct.get("Requirements").diagram,new Rectangle(100, 100, 80, 50));
		}
		fillPerformancePackage();
		fillVariantsPackage();
	}
}
