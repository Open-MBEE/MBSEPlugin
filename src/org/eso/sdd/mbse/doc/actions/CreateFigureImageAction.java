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

import javax.swing.JOptionPane;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;
import com.nomagic.magicdraw.ui.dialogs.selection.*;
import com.nomagic.magicdraw.ui.dialogs.*;

@SuppressWarnings("serial")
public class CreateFigureImageAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	private Utilities ut = null;

	public CreateFigureImageAction() {
		super("", "SE2: create Figure Image", null, null);
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

			SessionManager.getInstance().createSession("MBSE-doc-addFigureImage");
			factory = Application.getInstance().getProject()
					.getElementsFactory();

			Comment theFigure = factory.createCommentInstance();
			StereotypesHelper.addStereotype(theFigure,
					ut.getTheFigureImageStereotype());
			theFigure.setBody("a");
			String figureCaption = JOptionPane.showInputDialog(
					MDDialogParentProvider.getProvider().getDialogParent(), "",
					"Enter Figure Caption", JOptionPane.QUESTION_MESSAGE);

			if(figureCaption != null){

				ArrayList<Class<?>> select = new ArrayList<Class<?>>();
				select.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);

				ArrayList<Class<?>> display = new ArrayList<Class<?>>();
				display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class);
				display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);
				display.add(com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile.class);

				ArrayList<Class<?>> create = new ArrayList<Class<?>>();

				ArrayList<Class<?>> restricted = new ArrayList<Class<?>>();

				SelectElementTypes seTypes = new SelectElementTypes(display,
						select, create, restricted);

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
					BaseElement be = dlg.getSelectedElement();

					try {
						theFigure.setBody(theFigure.getBody() + " "
								+ be.getHumanName());
						ModelElementsManager.getInstance()
						.addElement(theFigure, ne);
					} catch (ReadOnlyElementException roee) {
						Utilities.displayWarning("Read only element");
						// displayWarning("Here I would create a "+strName+"
						// package\n");
					}
					if (StereotypesHelper.hasStereotypeOrDerived(ne, "section")) {
						StereotypesHelper.setStereotypePropertyValue(ne,
								ut.getTheSectionStereotype(), "blockelements",
								theFigure, true);
					} else if (StereotypesHelper.hasStereotypeOrDerived(ne,
							"chapter")) {
						StereotypesHelper.setStereotypePropertyValue(ne,
								ut.getTheChapterStereotype(), "blockelements",
								theFigure, true);
					}
					StereotypesHelper.setStereotypePropertyValue(theFigure,
							ut.getTheFigureImageStereotype(), "imageContainer", be,
							true);
					StereotypesHelper.setStereotypePropertyValue(theFigure,
							ut.getTheFigureImageStereotype(), "captionText",
							figureCaption, true);

					MBSEShowEditPanelAction.updateEditorView(ne, theFigure, "figureImage");
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
