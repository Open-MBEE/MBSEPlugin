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
 *    $Id: CreateFigureDiagramAction.java 673 2013-11-20 08:20:46Z mzampare $
 *
 */

package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import java.util.ArrayList;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import javax.swing.JOptionPane;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;
import com.nomagic.magicdraw.ui.dialogs.selection.*;
import com.nomagic.magicdraw.ui.dialogs.*;

@SuppressWarnings("serial")
public class CreateFigureDiagramAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	private Utilities ut = null;

	public CreateFigureDiagramAction() {
		super("", "SE2: create Figure Diagram", null, null);
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
			if (userObject instanceof Package) {
				if (!StereotypesHelper.hasStereotypeOrDerived(ne, "section")
						&& !StereotypesHelper.hasStereotypeOrDerived(ne,
								"chapter")) {
					Utilities.displayWarning("This is not a section");
					return;
				}
			} else {
				Utilities.displayWarning("This is not a package");
				return;
			}
		} else {
			Utilities.displayWarning("This is not a named element");

		}

		addFigure(ne);
	}

	public void addFigure(NamedElement ne) {
		// below line does not seem to be necessary
		// List tmpVe = StereotypesHelper.getStereotypePropertyValue(ne
		// ,theBookStereotype, "bookComponent");
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;

			SessionManager.getInstance().createSession("MBSE-doc-addFigureDiagram");
			factory = Application.getInstance().getProject().getElementsFactory();

			Comment theFigDiag = factory.createCommentInstance();
			StereotypesHelper.addStereotype(theFigDiag,
					ut.getTheFigureDiagramStereotype());
			theFigDiag.setBody("a");
			String figureCaption = JOptionPane.showInputDialog(
					MDDialogParentProvider.getProvider().getDialogParent(), "",
					"Enter Figure Caption", JOptionPane.QUESTION_MESSAGE);

			if (figureCaption != null) {
				ArrayList<Class<?>> select = new ArrayList<Class<?>>();
				select.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram.class);

				ArrayList<Class<?>> display = new ArrayList<Class<?>>();
				display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class);
				display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram.class);
				display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);
				display.add(com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile.class);

				ArrayList<Class<?>> createEmpty = new ArrayList<Class<?>>();

				ArrayList<Class<?>> restrictedEmpty = new ArrayList<Class<?>>();

				SelectElementTypes seTypes = new SelectElementTypes(display,
						select, createEmpty, restrictedEmpty);

				// Martynas says: ElementSelectionDlg is preferred

				SelectElementInfo sei = new SelectElementInfo(true, false,
						Application.getInstance().getProject().getModel(), true);

				ElementSelectionDlg dlg = ElementSelectionDlgFactory
						.create(MDDialogParentProvider.getProvider()
								.getDialogParent());
				ElementSelectionDlgFactory.initSingle(dlg, seTypes, sei,
						Application.getInstance().getProject().getModel());
				dlg.show();

				if (dlg.isOkClicked() && dlg.getSelectedElement() != null) {
					BaseElement selectedElement = dlg.getSelectedElement();

					try {
						theFigDiag.setBody(theFigDiag.getBody() + " "
								+ selectedElement.getHumanName());
						ModelElementsManager.getInstance().addElement(
								theFigDiag, ne);
					} catch (ReadOnlyElementException roee) {
						Utilities.displayWarning("Read only element");
						// displayWarning("Here I would create a "+strName+"
						// package\n");
					}

					// add hyperlink to selected model element
					final Stereotype hyperlinkOwnerStereotype =
							StereotypesHelper.getStereotype(
									Application.getInstance().getProject(),
									"HyperlinkOwner");
					StereotypesHelper.addStereotype(theFigDiag,
							hyperlinkOwnerStereotype);
					StereotypesHelper.setStereotypePropertyValue(theFigDiag,
							hyperlinkOwnerStereotype, "hyperlinkModel",
							selectedElement, true);
					StereotypesHelper.setStereotypePropertyValue(theFigDiag,
							hyperlinkOwnerStereotype, "hyperlinkModelActive",
							selectedElement, false);

					if (StereotypesHelper.hasStereotypeOrDerived(ne, "section")) {
						StereotypesHelper.setStereotypePropertyValue(ne,
								ut.getTheSectionStereotype(), "blockelements",
								theFigDiag, true);
					} else if (StereotypesHelper.hasStereotypeOrDerived(ne,
							"chapter")) {
						StereotypesHelper.setStereotypePropertyValue(ne,
								ut.getTheChapterStereotype(), "blockelements",
								theFigDiag, true);
					}
					StereotypesHelper.setStereotypePropertyValue(theFigDiag,
							ut.getTheFigureDiagramStereotype(), "diagram", selectedElement,
							true);
					StereotypesHelper.setStereotypePropertyValue(theFigDiag,
							ut.getTheFigureDiagramStereotype(), "captionText",
							figureCaption, true);
					System.out.println("***  CHECK:"+ selectedElement.getHumanName());
					MBSEShowEditPanelAction.updateEditorView(ne, theFigDiag,"figureDiagram");
				}

			} else {
				System.out.println("User cancelled Diagram selection");
			}

			SessionManager.getInstance().closeSession();

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
