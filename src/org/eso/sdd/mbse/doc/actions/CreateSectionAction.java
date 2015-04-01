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
 *    $Id: CreateSectionAction.java 646 2013-06-26 11:03:23Z mzampare $
 *
 */

package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
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
import javax.swing.JOptionPane;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreateSectionAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;

	public CreateSectionAction() {
		super("", "SE2: createSection", null, null);
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
		//
		tree = getTree();
		if (tree.getSelectedNodes().length > 1) {
			return;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		if (userObject instanceof NamedElement) {
			ne = (NamedElement) userObject;
			addSection(ne);
		} else {
			Utilities.displayWarning("This is not a named element");
		}
	}

	public void addSection(NamedElement ne) {
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;

			SessionManager.getInstance().createSession("MBSE-doc");
			factory = Application.getInstance().getProject()
					.getElementsFactory();

			Package theSection = factory.createPackageInstance();
			StereotypesHelper.addStereotype(theSection,
					ut.getTheSectionStereotype());

			String sectionName = null;
			sectionName = JOptionPane.showInputDialog(MDDialogParentProvider
					.getProvider().getDialogParent(), "", "Enter Section Name",
					JOptionPane.QUESTION_MESSAGE);

			if (sectionName != null) {
				theSection.setName(sectionName);
				try {
					ModelElementsManager.getInstance().addElement(theSection,
							ne);
				} catch (ReadOnlyElementException roee) {
					Utilities.displayWarning("Read only element");
					// displayWarning("Here I would create a "+strName+"
					// package\n");
				}

				if (StereotypesHelper.hasStereotypeOrDerived(ne, "chapter")) {
					StereotypesHelper.setStereotypePropertyValue(ne,ut.getTheChapterStereotype(), "sections",theSection, true);
					MBSEShowEditPanelAction.updateEditorView(ne, theSection, "section");
				}

				if (StereotypesHelper.hasStereotypeOrDerived(ne, "section")) {
					StereotypesHelper.setStereotypePropertyValue(ne,ut.getTheSectionStereotype(), "subsection",theSection, true);
					MBSEShowEditPanelAction.updateEditorView(ne, theSection,"subsection");
				}

				if (Utilities.isPreface(ne)) {
					StereotypesHelper.setStereotypePropertyValue(ne,ut.getThePrefaceStereotype(), "prefaceSection",theSection, true);
					MBSEShowEditPanelAction.updateEditorView(ne, theSection,"prefaceSection");
				}

			}

			SessionManager.getInstance().closeSession();

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}
}
