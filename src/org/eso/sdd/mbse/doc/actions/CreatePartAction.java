package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import java.util.Random;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreatePartAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;
	org.eso.sdd.mbse.templates.Utilities utT = null;

	public CreatePartAction() {
		super("", "SE2: create Part", null, null);
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
				if( !StereotypesHelper.hasStereotypeOrDerived(ne, "book") ) {
					Utilities.displayWarning("This is not a book");
					return;
				}
			} else {
				Utilities.displayWarning("This is not a package");
				return;
			}
		} else {
			Utilities.displayWarning("This is not a named element");
		}

		addPart(ne);
	}


	public void addPart(NamedElement ne) {
		if (!SessionManager.getInstance().isSessionCreated()) {
			ElementsFactory factory = null;

			SessionManager.getInstance().createSession("MBSE-doc");
			factory = Application.getInstance().getProject().getElementsFactory();

			Package thePart = factory.createPackageInstance();
			StereotypesHelper.addStereotype(thePart, ut.getThePartStereotype());

			thePart.setName("");
			try {
				Diagram theBookContentDiagram = null;
				ModelElementsManager.getInstance().addElement(thePart, ne);
				// add the chapter to the content diagram of the owning element
				theBookContentDiagram = utT.getDiagram(ne, ne.getName()+"_Content");
				if (theBookContentDiagram != null && theBookContentDiagram.isEditable()) {
					Random generator = new Random();
					int randomIndex = generator.nextInt( 30 );						
					
					// add the newly added chapter to the content diagram
					// notice: the random placement is merely an ugly workaround for a proper layouting, which would be
				    // a more complex undertaking if done properly.
					utT.addElementToDiagram(thePart, theBookContentDiagram, new Rectangle(100 + randomIndex,100 + randomIndex,100,60));

				}
				
				// add a Content diagram for this chapter as well
				utT.createDiagram(thePart, thePart.getName() + "_Content", utT.DT_PKD);

				
				
			} catch (ReadOnlyElementException roee) {
				Utilities.displayWarning("Read only element");
				// displayWarning("Here I would create a "+strName+"
				// package\n");
			}
			StereotypesHelper.setStereotypePropertyValue(ne ,ut.getTheBookStereotype(), "divisons", thePart,true);
			
			MBSEShowEditPanelAction.updateEditorView(ne,thePart,"part");
			SessionManager.getInstance().closeSession();

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}
}
