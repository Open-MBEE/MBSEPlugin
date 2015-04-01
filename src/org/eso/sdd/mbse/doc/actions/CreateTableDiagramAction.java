package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlg;
import com.nomagic.magicdraw.ui.dialogs.selection.ElementSelectionDlgFactory;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import java.util.ArrayList;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreateTableDiagramAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;

	public CreateTableDiagramAction() {
		super("", "SE2: create Table Diagram", null, null);
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
		if (tree.getSelectedNodes() != null
				&& tree.getSelectedNodes().length > 1) {
			return;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		if (userObject instanceof NamedElement) {
			ne = (NamedElement) userObject;
			addDiagramTable(tree, ne, null);
		} else {
			Utilities.displayWarning("This is not a named element");
		}
	}

	public void addDiagramTable(Tree tree, NamedElement father, Element after) {
		ElementsFactory factory = null;

		if (!SessionManager.getInstance().isSessionCreated()) {

			SessionManager.getInstance().createSession("MBSE-doc");
			factory = Application.getInstance().getProject()
					.getElementsFactory();
			
			ArrayList<Class<?>> select = new ArrayList<Class<?>>();
			select.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram.class);

			ArrayList<Class<?>> display = new ArrayList<Class<?>>();
			display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class);
			display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);
			display.add(com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile.class);

			ArrayList<Class<?>> create = new ArrayList<Class<?>>();

			ArrayList<Class<?>> restricted = new ArrayList<Class<?>>();

			SelectElementTypes seTypes = new SelectElementTypes(display,
					select, create, restricted);

			// Martynas says: ElementSelectionDlg is preferred

			SelectElementInfo sei = new SelectElementInfo(true, true,
					Application.getInstance().getProject().getModel(), true);

			ElementSelectionDlg dlg = ElementSelectionDlgFactory
					.create(MDDialogParentProvider.getProvider()
							.getDialogParent());
			ElementSelectionDlgFactory.initSingle(dlg, seTypes, sei,
					Application.getInstance().getProject().getModel());
			dlg.show();

			if (dlg.getResult() == com.nomagic.ui.DialogConstants.OK
					&& dlg.getSelectedElement() != null) {
				BaseElement be = dlg.getSelectedElement();
				
				com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class theTable = factory
						.createClassInstance();
				StereotypesHelper.addStereotype(theTable,
						ut.getTheTableDiagramStereotype());
				
				try {
					ModelElementsManager.getInstance().addElement(theTable, father);
					
				} catch (ReadOnlyElementException roee) {
					Utilities.displayWarning("Read only element");
				}

				// add hyperlink to selected model element
				final Stereotype hyperlinkOwnerStereotype =
						StereotypesHelper.getStereotype(
								Application.getInstance().getProject(),
								"HyperlinkOwner");
				StereotypesHelper.addStereotype(theTable,
						hyperlinkOwnerStereotype);
				StereotypesHelper.setStereotypePropertyValue(theTable,
						hyperlinkOwnerStereotype, "hyperlinkModel",
						be, true);
				StereotypesHelper.setStereotypePropertyValue(theTable,
						hyperlinkOwnerStereotype, "hyperlinkModelActive",
						be, false);

				if (StereotypesHelper.hasStereotypeOrDerived(father, "section")) {
					StereotypesHelper.setStereotypePropertyValue(father,
							ut.getTheSectionStereotype(), "blockelements",
							theTable, true);
				} else if (StereotypesHelper.hasStereotypeOrDerived(father,
						"chapter")) {
					StereotypesHelper.setStereotypePropertyValue(father,
							ut.getTheChapterStereotype(), "blockelements",
							theTable, true);
				}

				// set tag to selected diagram
				StereotypesHelper
						.setStereotypePropertyValue(theTable,
								ut.getTheTableDiagramStereotype(), "diagramTable",
								be, true);
				// set tag with caption text
				StereotypesHelper.setStereotypePropertyValue(theTable,
						ut.getTheTableDiagramStereotype(), "captionText",
						be.getHumanName(), true);
				theTable.setName(be.getHumanName());
				tree.openNode(theTable, true, true);
				MBSEShowEditPanelAction.updateEditorView(father, theTable, "tableDiagram");

			} else {
				System.out.println("User cancelled Diagram selection");
			}

			SessionManager.getInstance().closeSession();
		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
