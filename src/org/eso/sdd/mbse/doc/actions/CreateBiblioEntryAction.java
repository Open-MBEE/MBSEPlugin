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
*/



package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;

import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.doc.ui.CustomDialog;
import org.eso.sdd.mbse.doc.ui.MultilineDialogPanel;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreateBiblioEntryAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;

	public CreateBiblioEntryAction() {
		super("", "SE2: create Biblio Entry", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));
		ut = new Utilities();

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Tree tree = null; 
		Object userObject = null; 
		NamedElement ne = null;
		Node node = null;

		tree = getTree();	
		if (tree.getSelectedNodes().length > 1) {
			return;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		if (userObject instanceof NamedElement) {
			ne = (NamedElement) userObject;
			if (userObject instanceof Package) {
				;
			} else {
				Utilities.displayWarning("This is not a package");
				return;
			}
		} else {
			Utilities.displayWarning("This is not a named element");
		}
		addBiblioEntry(ne);
	}


	public void addBiblioEntry(NamedElement ne) {
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;

			SessionManager.getInstance().createSession("MBSE-doc-biblio Entry");
			factory = Application.getInstance().getProject().getElementsFactory();

			Class theBiblioEntry = factory.createClassInstance();
			StereotypesHelper.addStereotype(theBiblioEntry, ut.getTheBiblioEntryStereotype());

			String theTitle = null;
			String theIssueNumber = null;
			String theProductNumber = null;
			String thePublicationDate = null;
			String theName = null;
			
			CustomDialog dlg = new CustomDialog("Multiline dialog","bibliography");
			MultilineDialogPanel panel = CustomDialog.getResults();
			theName = panel.getResults()[0];
			theTitle = panel.getResults()[1];
			theIssueNumber = panel.getResults()[2];
			theProductNumber = panel.getResults()[3];
			thePublicationDate = panel.getResults()[4];
			
			if(dlg.getSelection() == 0){
			
			theBiblioEntry.setName(theName);
			

			StereotypesHelper.setStereotypePropertyValue(theBiblioEntry,ut.getTheBiblioEntryStereotype(), "issueNumber", theIssueNumber,true);
			StereotypesHelper.setStereotypePropertyValue(theBiblioEntry,ut.getTheBiblioEntryStereotype(), "productNumber", theProductNumber,true);
			StereotypesHelper.setStereotypePropertyValue(theBiblioEntry,ut.getTheBiblioEntryStereotype(), "pubDate", thePublicationDate,true);
			StereotypesHelper.setStereotypePropertyValue(theBiblioEntry,ut.getTheBiblioEntryStereotype(), "title", theTitle,true);
			
			try {
				ModelElementsManager.getInstance().addElement(theBiblioEntry, ne);
			} catch (ReadOnlyElementException roee) {
				Utilities.displayWarning("Read only element");
			}

			if(	StereotypesHelper.hasStereotypeOrDerived(ne, "bibliography")  ) {
				StereotypesHelper.setStereotypePropertyValue(ne ,ut.getTheBibliographyStereotype(), "biblioEntry", theBiblioEntry,true);
			}


			
			MBSEShowEditPanelAction.updateEditorView(ne,theBiblioEntry,"biblioEntry");
			
			}
			
			SessionManager.getInstance().closeSession();

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
