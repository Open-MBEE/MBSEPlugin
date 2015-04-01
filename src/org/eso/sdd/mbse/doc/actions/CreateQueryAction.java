package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.ui.dialogs.SelectElementInfo;
import com.nomagic.magicdraw.ui.dialogs.SelectElementTypes;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
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

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;
import com.nomagic.magicdraw.ui.dialogs.selection.*;

@SuppressWarnings("serial")
public class CreateQueryAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;

	public CreateQueryAction() {
		super("", "SE2: create Query", null, null);
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
					Utilities.displayWarning("This is not a section or chapter");
					return;
				}
			} else {
				Utilities.displayWarning("This is not a package");
				return;
			}
		} else {
			Utilities.displayWarning("This is not a named element");

		}
		addQuery(ne, null);
	}

	public void addQuery(NamedElement father, Element after) {
		ElementsFactory factory = null;

		if (!SessionManager.getInstance().isSessionCreated()) {

			SessionManager.getInstance().createSession("MBSE-doc-addQuery");
			factory = Application.getInstance().getProject()
					.getElementsFactory();

			Comment theQuery = factory.createCommentInstance();
			StereotypesHelper.addStereotype(theQuery,
					ut.getTheQueryStereotype());
			

			ArrayList<Class<?>> select = new ArrayList<Class<?>>();
			select.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);
			select.add(com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype.class);
			// select.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class);

			ArrayList<Class<?>> display = new ArrayList<Class<?>>();
			display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class);
			display.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class.class);
			display.add(com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model.class);

			ArrayList<Class<?>> create = new ArrayList<Class<?>>();

			ArrayList<Class<?>> restricted = new ArrayList<Class<?>>();
			// restricted.add(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram.class);

			SelectElementTypes seTypes = new SelectElementTypes(display,
					select, create, restricted);

			SelectElementInfo sei = new SelectElementInfo(true, false,
					Application.getInstance().getProject().getModel(), true);

			ElementSelectionDlg dlg = ElementSelectionDlgFactory
					.create(MDDialogParentProvider.getProvider()
							.getDialogParent());
			ElementSelectionDlgFactory.initMultiple(dlg, seTypes, sei,
					Application.getInstance().getProject().getModel().getMdExtensions());
			dlg.show();

			if (dlg.isOkClicked()
					&& dlg.getSelectedElement() != null) {
				BaseElement be = dlg.getSelectedElement();			
				theQuery.setBody(be.getHumanName());
				//
				try {
					ModelElementsManager.getInstance().addElement(theQuery,
							father);
				} catch (ReadOnlyElementException roee) {
					Utilities.displayWarning("Read only element");
				}
				StereotypesHelper.setStereotypePropertyValue(theQuery,
						ut.getTheQueryStereotype(), "element", be, true);
				if (after != null) {
					Utilities.insertElementInTaggedValueList(father, theQuery,
							after, ut.getTheSectionStereotype(),
							"blockelements");
				} else {
					StereotypesHelper.setStereotypePropertyValue(father,
							ut.getTheSectionStereotype(), "blockelements",
							theQuery, true);
				}
			} else {
				System.out.println("User cancelled reference selection");
			}

			StereotypesHelper.setStereotypePropertyValue(theQuery,
					ut.getTheQueryStereotype(), "type", "documentation", true);
			SessionManager.getInstance().closeSession();
			
			MBSEShowEditPanelAction.updateEditorView(father,theQuery,"query");

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
