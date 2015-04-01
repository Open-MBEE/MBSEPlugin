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
 *    $Id: MBSEActionGetTemplate.java 502 2012-03-23 15:33:03Z mzampare $
 *
*/

package org.eso.sdd.mbse.templates;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import java.util.Vector;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.magicdraw.sysml.util.SysMLConstants ;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;



import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import org.eso.sdd.mbse.templates.Utilities;

@SuppressWarnings("serial")
public class MBSEActionGetPartCatalogueTemplate extends DefaultBrowserAction {

	private Package theRoot = null;
	private String theName = null;
	private Diagram theContentDiagram;
	private Vector<PresentationElement> toBeLayouted = new Vector<PresentationElement>();


	private final String DT_BDD = SysMLConstants.SYSML_BLOCK_DEFINITION_DIAGRAM;
	private final String DT_IBD = SysMLConstants.SYSML_INTERNAL_BLOCK_DIAGRAM;
	private final String DT_PMD = SysMLConstants.SYSML_PARAMETERIC_DIAGRAM;
	private final String DT_RD  = SysMLConstants.SYSML_REQUIREMENTS_DIAGRAM;
	private final String DT_PKD = SysMLConstants.SYSML_PACKAGE_DIAGRAM;


	private Profile SysMLProfile = StereotypesHelper.getProfile(Application
			.getInstance().getProject(), "SysML");
	
	private Profile UMLProfile = StereotypesHelper.getProfile(Application
			.getInstance().getProject(), "UML Standard Profile");
	
	private Utilities ut = new Utilities();

	public MBSEActionGetPartCatalogueTemplate() {
		super("", "SE2:get Part Catalogue Template", null, null);

		/*
		An action on package for getPartCatalogueTemplate creates packages:
			   applies the stereotypes "se2.partscatalogue" and "modelLibrary" to
			   the current package and adds the following subpackages:
			   COTS/Electronics
			       /Software
			   Custom/Electronics
			         /Software
			   Furthermore a package Diagram named like the package + _Content is
			   created and stereotyped by "se2.contentDiagram".
		 */
			   
		// package Stereotype, diagram SysML type, diagram Stereotype, diagram Name
		
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
				theRoot = (Package)userObject;
				if (!SessionManager.getInstance().isSessionCreated()) {
					SessionManager.getInstance().createSession("MBSE");

					createCatalogue();
					
					SessionManager.getInstance().closeSession();
					Application.getInstance().getProject().getDiagram(theContentDiagram).setSelected(toBeLayouted);
					Application.getInstance().getProject().getDiagram(theContentDiagram).layout(true);
				}
			} else { 
					ut.displayWarning("MBSE Plugin: what do you want me to do with this?!\n" + ne.getHumanName()+ " is not a package");
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
	
	private void createCatalogue() { 
		Package COTS, Custom = null;
		Rectangle theRectangle = new Rectangle(300, 300, 80, 50);
		String pName = 	theRoot.getName();
		Stereotype modelLibrary = null;
		// applying SE2Profile stereotype, here we can reuse
		ut.applyStereotypeToPackage(theRoot, "se2.partscatalogue");
		// applying MD stereotype, here we cannot reuse.
		
		modelLibrary = StereotypesHelper.getStereotype(
				Application.getInstance().getProject(), "modelLibrary",
				UMLProfile);
		if(modelLibrary != null) { 
			StereotypesHelper.addStereotype(theRoot, modelLibrary);
		} else { 
			ut.logDebug("WARN: stereotype  modelLibrary seems not to exist.");
		}
		
		
		
		theRoot.setName(pName + " Part Catalogue");
		COTS =   ut.addPackage(theRoot, "COTS");
		Custom = ut.addPackage(theRoot,"Custom");
		
		ut.addPackage(COTS,"Electronics");
		ut.addPackage(COTS,"Software");

		ut.addPackage(Custom,"Electronics");
		ut.addPackage(Custom,"Software");

		theContentDiagram = ut.createDiagram(theRoot, theRoot.getName() + "_Content", DT_PKD);
		ut.applyStereotypeToDiagram(theContentDiagram, "se2.ContentDiagram");
		ut.addElementToDiagram(COTS, theContentDiagram, theRectangle);
		ut.addElementToDiagram(Custom, theContentDiagram, theRectangle);
		
		
	}

}
