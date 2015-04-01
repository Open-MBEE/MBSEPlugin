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
 *    $Id: MBSEActionGetTemplate.java 577 2012-05-29 08:22:20Z nb-linux $
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


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import com.nomagic.uml2.impl.ElementsFactory;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;


import com.nomagic.magicdraw.uml.symbols.PresentationElement;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import org.eso.sdd.mbse.templates.Utilities;
import org.apache.log4j.Logger;



@SuppressWarnings("serial")
public class MBSEActionRepairTemplate extends DefaultBrowserAction {

	private Profile SysMLProfile = StereotypesHelper.getProfile(Application
			.getInstance().getProject(), "SysML");
	private Profile SE2Profile = StereotypesHelper.getProfile(Application
			.getInstance().getProject(), "SE2Profile");
	
	private Utilities ut = new Utilities();
	private TemplateStructure ts = null;
	private Logger  logger = null;

	public MBSEActionRepairTemplate(Logger l) {
		super("", "SE2:rename System Element Template", null, null);
		logger = l;	
		ts = new TemplateStructure();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Tree tree = getTree();
		NamedElement ne = null;
		Package father = null;
		Package grandFather = null;
				
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

		logger.info("MBSE: calling RepairTemplate on " + ((NamedElement)userObject).getName());
		ne = (NamedElement) userObject;
		Stereotype checkBlock = StereotypesHelper.getAppliedStereotypeByString(ne,"Block");
		if (checkBlock == null ) {
			ut.displayWarning("MBSE Plugin: what do you want me to do with this?!\n" + ne.getHumanName()+ " is not a block");
			return;
		}


		// it should be a block	
		if (! (ne.getOwner() instanceof Package)) { 
			ut.displayWarning("You cannot create a template from a part property!");
			return;
		}
		father = (Package)ne.getOwner();
		// check if the block is contained in a package which ends with structure

		if(!father.getName().endsWith("Structure")) { 
			ut.displayWarning("The location of this block in the template structure cannot be safely determined. Abort.");
			logger.error("The owner of " + ne.getName() + " is not called Structure");
			return;
		}
		
		grandFather = (Package)father.getOwner();
		
		if (!SessionManager.getInstance().isSessionCreated()) {
			SessionManager.getInstance().createSession("MBSE");
			checkStructure(ne,grandFather);
			checkDiagrams(ne);
			grandFather.setName(ne.getName());
			SessionManager.getInstance().closeSession();
		} // end of the game.

				return;
		// displayWarning(text);
	}

	@Override
	public void updateState() {
		setEnabled(getTree().getSelectedNode() != null);
	}

	private void checkStructure(NamedElement ne,Package gf) {
		Iterator<Element> it = null;
		String sEN = ne.getName();
		String str = null;
		Enumeration<String> names = null;

		Stereotype dSt = StereotypesHelper.getStereotype(Application.getInstance().getProject(),"se2.ContentDiagram",SE2Profile);

		if (! ne.hasOwnedElement()) {
			logger.error("This element " + ne.getName() + " does not own any element.");
			return;
		}

		
		// ProductTree diagram, starting from father of given block.
		for(it = ne.getOwner().getOwnedElement().iterator(); it.hasNext(); ) { 
			Element ownedElement = it.next();
			if(ownedElement instanceof Diagram) { 
				Diagram d = (Diagram)ownedElement;
				// check the diagram type
				if(Application.getInstance().getProject().getDiagram(d).getDiagramType().getType().equals(ts.DT_BDD)) { 
					if(d.getName().endsWith(ts.struct.get("Structure").dName)) {
						String strName  = ne.getName()+"_"+ ts.struct.get("Structure").dName;
						logger.debug("Found local " + d.getName()); 
						if(!d.getName().equals(strName)) {
							logger.debug("Would rename " + d.getName() + " ==> " + strName);
							d.setName(strName);
						}
					}
				}
			}
		}
		
		// all regular packages
		for (it = gf.getOwnedElement().iterator(); it.hasNext();) {
			Element ownedElement = it.next();
			if (ownedElement instanceof Package) {
				names = ts.struct.keys();
				while(names.hasMoreElements()) {
					str = names.nextElement(); 
					String strName = sEN + "_" + str;

					Package p = (Package)ownedElement;
					if(str.equals("Requirements")) { 
						handleRequirementBlock(p,sEN);
					}
					if (p.getName().endsWith(str)) {
						if(! p.getName().equals(strName)) {
							// would rename this one.
							logger.info("Would rename: " + p.getName() + " ==> " + strName);
							p.setName(strName);
						}
						renameContainedDiagrams(str, p,ne.getName());
					}
				} // end loop over structures (Packages) to be checked
			} // end Package case
			if (ownedElement instanceof Diagram) {
				Diagram d = (Diagram)ownedElement;
				if(d.getName().endsWith("Content")) {
					String strName = ne.getName()+"_Content";
					if(StereotypesHelper.hasStereotype(d,dSt)) {
						if(!d.getName().equals(strName)) { 
							logger.info("Would rename: " + d.getName() + " ==> " + strName);
							d.setName(strName);
						}
					}
				}
			}
			
		} // end iteration over children

		// then we check the meta structure
		for (int i = 0; i < ts.metaStructure.length; i++) {
			str = ts.metaStructure[i];
			String strName = "_" + sEN + "_" + str;
			for (it = gf.getOwnedElement().iterator(); it.hasNext();) {
				Element ownedElement = it.next();
				if (ownedElement instanceof Package) {
					Package p = (Package)ownedElement;
					if (p.getName().endsWith(str)) {
						if(!p.getName().equals(strName)) { 
							logger.info("Would rename: " + p.getName() + " ==> " + strName);
							p.setName(strName);
						}
					}
				}
			} // end iteration over children

		} // end loop over ts.metaStructures (Packages) to be created
		
		// 
		
	}

	
	private void handleRequirementBlock(Package p, String newName) {
		Iterator<Element> it = null;
		if(! p.getName().contains("_")) {
			logger.error("The supplied package " + p.getName() + " does not contain an underscore in its name.");
			return;
		}
		String origName = p.getName().substring(0, p.getName().indexOf('_'));
		Stereotype reqSt = StereotypesHelper.getStereotype(Application.getInstance().getProject(),"Requirement",SysMLProfile);
		for (it = p.getOwnedElement().iterator(); it.hasNext();) {
			Element ownedElement = it.next();			
			if(!StereotypesHelper.hasStereotype(ownedElement, reqSt)) {
				continue;
			}
			NamedElement ne = (NamedElement)ownedElement;
			if(ne.getName().equals(origName) ) { 
				logger.info("Would rename: " + ne.getName() + " ==> " + newName );
				ne.setName(newName);

			} else {
				logger.error("Found a requirement, named " + ne.getName() + ", not the right one.");
			}
		}
		
		
	}

	/*
	 * @para: index: the string key of the struct structure.
	 * @para: p the package where to search for diagrams
	 * @para: name: the name which shall be used for renaming.
	 */
	private void renameContainedDiagrams(String index, Package p, String name) {
		Iterator<Element> it = null;
		String diagramSuffix = ts.struct.get(index).dName;
		String diagramStereo = "se2."+ts.struct.get(index).dStereo;
		
		Stereotype dSt = StereotypesHelper.getStereotype(Application.getInstance().getProject(), diagramStereo, SE2Profile);
		
		for (it = p.getOwnedElement().iterator(); it.hasNext();) {
			Element ownedElement = it.next();
			if (ownedElement instanceof Diagram) {
				Diagram d = (Diagram)ownedElement;
				if(Application.getInstance().getProject().getDiagram(d).getDiagramType().getType().equals(ts.DT_BDD) || 
						Application.getInstance().getProject().getDiagram(d).getDiagramType().getType().equals(ts.DT_PKD) ||
						Application.getInstance().getProject().getDiagram(d).getDiagramType().getType().equals(ts.DT_RD)  	 ) { 

					if(d.getName().endsWith(diagramSuffix)) {
						if(StereotypesHelper.hasStereotype(d,dSt)) { 
							String strName = name+"_"+ diagramSuffix;
							if(!d.getName().equals( strName)) { 
								logger.info("Would rename: " + d.getName() + " ==> " + strName);
								d.setName(strName);
							}
						} else {
							logger.debug("Would check "+d.getName() + " but it does not have the correct stereotype ("+
									diagramStereo+")");
						}
					} else {
						logger.debug("Diagram " + d.getName() + " does not end with " + diagramSuffix);
					}
				}
			} // end iteration over children

		}	
	}

	private void checkDiagrams(NamedElement ne) { 
		String sEN = ne.getName();
		Iterator<Element> it = null;

		for (it = ne.getOwnedElement().iterator(); it.hasNext();) {
			Element ownedElement = it.next();
			if (ownedElement instanceof Diagram) {
				Diagram d = (Diagram)ownedElement;
				if(Application.getInstance().getProject().getDiagram(d).getDiagramType().getType().equals(ts.DT_IBD)) { 
					// found an IBD
					logger.debug("Found IBD " + d.getName());
					for (int i = 0; i < ts.IBDs.length; i++) {
						String str = ts.IBDs[i];
						String strName = sEN + "_" + str;

						if (d.getName().endsWith(str)) {
							if(!d.getName().equals(strName)) { 
								logger.info("Would rename: " + d.getName() + " ==> " + strName);
								d.setName(strName);
							}
						}
					}
				}
			} // end iteration over children

		}	
	}

}
