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

import java.text.SimpleDateFormat;
import java.util.Date;


import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;

import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.doc.ui.RevisionEntryDialog;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreateRevisionEntryAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;

	public CreateRevisionEntryAction() {
		super("", "SE2: create Revision Entry", null, null);
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
		addRevisionEntry(ne);
	}


	public void addRevisionEntry(NamedElement ne) {
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;
			String defaultIssue = "";

			Date now = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        	
			
			Stereotype theRevisionEntryStereotype = ut.getTheRevisionEntryStereotype();
			factory = Application.getInstance().getProject().getElementsFactory();

			Class theRevisionEntry = factory.createClassInstance();
			StereotypesHelper.addStereotype(theRevisionEntry, theRevisionEntryStereotype);

			//author, authorinitials, date, revdescription, revnumber, revremark.			
			String theAuthor = null;
			String theDate = null;
			String theRevNumber = null;
			String theRevRemark = null;
			String theRevDescription = null;

			
			// ne now holds a  revhistory element, its father should be a book
			if(StereotypesHelper.hasStereotypeOrDerived(ne.getOwner(),"book")) { 
				defaultIssue = Utilities.getFirstElementString(ne.getOwner(),ut.getTheBookStereotype() , "issue");
			} else {
				System.out.println("addRevisionEntry: this revisionhistory does not seem to be fathered by a book");
			}

			//System.out.println("DATE: " +calendar.get(Calendar.DATE) );
			RevisionEntryDialog dlg = new RevisionEntryDialog(sdf.format( now ).toString(),defaultIssue,"","");
			
			if(dlg.getSelection() == 0) {
				SessionManager.getInstance().createSession("MBSE-doc-revision Entry");

				// user pressed ok
			
				theDate = RevisionEntryDialog.getResults()[0];
				theRevNumber = RevisionEntryDialog.getResults()[1];
				theRevRemark = RevisionEntryDialog.getResults()[2];
				theRevDescription = RevisionEntryDialog.getResults()[3];

				BaseElement theSelectedAuthor = dlg.getSelectedAuthor();
				//System.out.println("YYY: " + theRevNumber);
				
				theRevisionEntry.setName(theRevNumber);

				
				
				StereotypesHelper.setStereotypePropertyValue(theRevisionEntry,theRevisionEntryStereotype, "author", theSelectedAuthor,true);
				
				StereotypesHelper.setStereotypePropertyValue(theRevisionEntry,theRevisionEntryStereotype, "date", theDate,true);
				StereotypesHelper.setStereotypePropertyValue(theRevisionEntry,theRevisionEntryStereotype, "revnumber", theRevNumber,true);
				StereotypesHelper.setStereotypePropertyValue(theRevisionEntry,theRevisionEntryStereotype, "revremark", theRevRemark,true);
				StereotypesHelper.setStereotypePropertyValue(theRevisionEntry,theRevisionEntryStereotype, "revdescription", theRevDescription,true);

				try {
					ModelElementsManager.getInstance().addElement(theRevisionEntry, ne);
				} catch (ReadOnlyElementException roee) {
					Utilities.displayWarning("Read only element");
				}

				if(	StereotypesHelper.hasStereotypeOrDerived(ne, "revhistory")  ) {
					StereotypesHelper.setStereotypePropertyValue(ne ,ut.getTheRevisionHistoryStereotype(), "revisionEntry", theRevisionEntry,true);
				}

				MBSEShowEditPanelAction.updateEditorView(ne,theRevisionEntry,"revisionEntry");
				SessionManager.getInstance().closeSession();
			} else {
				// user pressed cancel
			}



		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
