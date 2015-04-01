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

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;

import javax.swing.JOptionPane;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreateBibliographyAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;

	public CreateBibliographyAction() {
		super("", "SE2: createBibliography", null, null);
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
		addBibliography(ne);
	}

	public void addBibliography(NamedElement ne) {
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;

			SessionManager.getInstance().createSession("MBSE-doc-bibliography");
			factory = Application.getInstance().getProject()
					.getElementsFactory();

			Package theBibliography = factory.createPackageInstance();
			StereotypesHelper.addStereotype(theBibliography,
					ut.getTheBibliographyStereotype());

			String bibliographyName = null;
			bibliographyName = JOptionPane.showInputDialog(
					MDDialogParentProvider.getProvider().getDialogParent(), "",
					"Enter Bibliography Name", JOptionPane.QUESTION_MESSAGE);

			if (bibliographyName != null) {
				theBibliography.setName(bibliographyName);
				// TODO: abbrevPrefix should also be set.
				try {
					ModelElementsManager.getInstance().addElement(
							theBibliography, ne);
				} catch (ReadOnlyElementException roee) {
					Utilities.displayWarning("Read only element");
				}

				if (StereotypesHelper.hasStereotypeOrDerived(ne, "section")) {
					StereotypesHelper.setStereotypePropertyValue(ne,
							ut.getTheSectionStereotype(), "bibliography",
							theBibliography, true);
				}

				MBSEShowEditPanelAction.updateEditorView(ne, theBibliography,
						"bibliography");
			}

			SessionManager.getInstance().closeSession();

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
