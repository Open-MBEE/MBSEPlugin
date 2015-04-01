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
import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.doc.ui.CustomDialog;
import org.eso.sdd.mbse.doc.ui.MultilineDialogPanel;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreateBookAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;
	org.eso.sdd.mbse.templates.Utilities utT = null;

	public CreateBookAction() {
		super("", "SE2: create Book", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));
		ut = new Utilities();
		utT = new org.eso.sdd.mbse.templates.Utilities();

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Tree tree = null; 

		Object userObject = null; 
		NamedElement ne = null;
		Node node = null;
		//
		tree = getTree();	
		if (tree.getSelectedNodes().length > 1) {
			return;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		if (userObject instanceof NamedElement) {
			ne = (NamedElement) userObject;
			if (userObject instanceof Package) {

				// this is a comment
			} else {
				Utilities.displayWarning("This is not a package");
				return;
			}
		} else {
			Utilities.displayWarning("This is not a named element");
		}
		addBook(ne);
	}


	private void addBook(NamedElement ne) {
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;

			String theBookName = null;
			String theIssueDate   = null;
			String theIssueNumber = null;
			String theDocumentNumber = null;

			SessionManager.getInstance().createSession("MBSE-doc");
			factory = Application.getInstance().getProject().getElementsFactory();

			Package theBook = factory.createPackageInstance();
			StereotypesHelper.addStereotype(theBook, ut.getTheBookStereotype());

			CustomDialog dlg = new CustomDialog("Multiline dialog","book");
			MultilineDialogPanel panel = CustomDialog.getResults();
			theBookName = panel.getResults()[0];
			theDocumentNumber = panel.getResults()[1];
			theIssueDate = panel.getResults()[2];
			theIssueNumber = panel.getResults()[3];
			
			theBook.setName(theBookName);
			
		/*	theBook.setName(JOptionPane.showInputDialog(MDDialogParentProvider.getProvider().getDialogParent(),
					"" , "Enter Book Name", JOptionPane.QUESTION_MESSAGE));
			
			theDocumentNumber     = JOptionPane.showInputDialog(MDDialogParentProvider.getProvider().getDialogParent(),
					"" , "Enter Document Number ", JOptionPane.QUESTION_MESSAGE);

			theIssueDate       = JOptionPane.showInputDialog(MDDialogParentProvider.getProvider().getDialogParent(),
					"" , "Enter Issue ", JOptionPane.QUESTION_MESSAGE);
			theIssueNumber = JOptionPane.showInputDialog(MDDialogParentProvider.getProvider().getDialogParent(),
					"" , "Enter Issue Number", JOptionPane.QUESTION_MESSAGE);	*/	


			// Issue
			// Issue Number
			// Document Number
			// Authors
			// all the values for the above items should be obtained from a SWing Widget.

			StereotypesHelper.setStereotypePropertyValue(theBook,ut.getTheBookStereotype(), "authors", "",true);
			StereotypesHelper.setStereotypePropertyValue(theBook,ut.getTheBookStereotype(), "issueDate", theIssueDate,true);
			StereotypesHelper.setStereotypePropertyValue(theBook,ut.getTheBookStereotype(), "issue", theIssueNumber,true);
			StereotypesHelper.setStereotypePropertyValue(theBook,ut.getTheBookStereotype(), "documentNumber", theDocumentNumber,true);

			try {
				ModelElementsManager.getInstance().addElement(theBook, ne);
				utT.createDiagram(theBook, theBook.getName() + "_Content",utT.DT_PKD );
				
			} catch (ReadOnlyElementException roee) {
				Utilities.displayWarning("Read only element");
				// displayWarning("Here I would create a "+strName+"
				// package\n");
			}

			SessionManager.getInstance().closeSession();

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}
}
