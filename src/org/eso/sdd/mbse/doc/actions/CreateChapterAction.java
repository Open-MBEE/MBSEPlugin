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
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import javax.swing.JOptionPane;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Random;

@SuppressWarnings("serial")
public class CreateChapterAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;
	org.eso.sdd.mbse.templates.Utilities utT = null;
	
	public CreateChapterAction() {
		super("", "SE2: createChapter", null, null);
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

		tree = getTree();
		if (tree.getSelectedNodes().length > 1) {
			return;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		if (userObject instanceof NamedElement) {
			ne = (NamedElement) userObject;
			if (userObject instanceof Package) {
				if (!StereotypesHelper.hasStereotypeOrDerived(ne, "book")
						&& !StereotypesHelper
								.hasStereotypeOrDerived(ne, "part")) {
					Utilities.displayWarning("This is not a book nor a part");
					return;
				}
			} else {
				Utilities.displayWarning("This is not a package");
				return;
			}
		} else {
			Utilities.displayWarning("This is not a named element");
		}
		addChapter(ne);
	}

	public void addChapter(NamedElement ne) {
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;

			SessionManager.getInstance().createSession("MBSE-doc");
			factory = Application.getInstance().getProject()
					.getElementsFactory();

			Package theChapter = factory.createPackageInstance();
			StereotypesHelper.addStereotype(theChapter,
					ut.getTheChapterStereotype());

			String chapName = null;
			chapName = JOptionPane.showInputDialog(MDDialogParentProvider
					.getProvider().getDialogParent(), "", "Enter Chapter Name",
					JOptionPane.QUESTION_MESSAGE);

			if (chapName != null) {
				theChapter.setName(chapName);
				try {
					Diagram theBookContentDiagram = null;
					ModelElementsManager.getInstance().addElement(theChapter,ne);
					// add the chapter to the content diagram of the owning element
					theBookContentDiagram = utT.getDiagram(ne, ne.getName()+"_Content");
					if (theBookContentDiagram != null && theBookContentDiagram.isEditable()) {
						Random generator = new Random();
						int randomIndex = generator.nextInt( 30 );						
						// add the newly added chapter to the content diagram
						// notice: the random placement is merely an ugly workaround for a proper layouting, which would be
					    // a more complex undertaking if done properly.

						utT.addElementToDiagram(theChapter, theBookContentDiagram, new Rectangle(100 + randomIndex,100 + randomIndex,100,60));
					}
					
					// add a Content diagram for this chapter as well
					utT.createDiagram(theChapter, chapName+"_Content", utT.DT_PKD);
					
				} catch (ReadOnlyElementException roee) {
					Utilities.displayWarning("Read only element");
					// displayWarning("Here I would create a "+strName+"
					// package\n");
				}

				if (StereotypesHelper.hasStereotypeOrDerived(ne, "book")) {
					StereotypesHelper.setStereotypePropertyValue(ne,
							ut.getTheBookStereotype(), "bookComponent",
							theChapter, true);
				}

				if (StereotypesHelper.hasStereotypeOrDerived(ne, "part")) {
					StereotypesHelper.setStereotypePropertyValue(ne,
							ut.getThePartStereotype(), "components",
							theChapter, true);
				}

				MBSEShowEditPanelAction.updateEditorView(ne, theChapter, "chapter");

			}

			SessionManager.getInstance().closeSession();

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
