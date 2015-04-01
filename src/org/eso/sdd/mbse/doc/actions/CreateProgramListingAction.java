package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.impl.ElementsFactory;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class CreateProgramListingAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null; 


	public CreateProgramListingAction() {
		super("", "SE2: create Program Listing", null, null);
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
		if (tree.getSelectedNodes() != null && tree.getSelectedNodes().length > 1) {
			return;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		if (userObject instanceof NamedElement) {
			ne = (NamedElement) userObject;
			addProgramListing(tree, ne,null);
		} else {
			Utilities.displayWarning("This is not a named element");
		}
	}


	public void addProgramListing(Tree tree, NamedElement father, Element after) {
		ElementsFactory factory = null;

		if (!SessionManager.getInstance().isSessionCreated()) {

			SessionManager.getInstance().createSession("MBSE-doc"); 
			factory = Application.getInstance().getProject().getElementsFactory();

			Comment theProgramListing = factory.createCommentInstance();
			StereotypesHelper.addStereotype(theProgramListing, ut.getTheProgramListingStereotype());
			try {
				ModelElementsManager.getInstance().addElement(theProgramListing, father);
				//JTree theRealTree = tree.getTree();
				//theRealTree.setSelectionPath(tree.openNode(theParagraph,true,true));

			} catch (ReadOnlyElementException roee) {
				Utilities.displayWarning("Read only element");
				// displayWarning("Here I would create a "+strName+"
				// package\n");
			}

			if( Utilities.isSection(father)) { 
				if(after != null) {
					Utilities.insertElementInTaggedValueList(father, theProgramListing, after, ut.getTheSectionStereotype(), "blockelements");
				} else {
					StereotypesHelper.setStereotypePropertyValue(father ,ut.getTheSectionStereotype(), "blockelements", theProgramListing,true);			
				}

			}

			if ( Utilities.isChapter(father)) { 
				if(after != null) {
					Utilities.insertElementInTaggedValueList(father, theProgramListing, after, ut.getTheChapterStereotype(), "blockelements");
				} else {
					StereotypesHelper.setStereotypePropertyValue(father ,ut.getTheChapterStereotype(), "blockelements", theProgramListing,true);			
				}
			}
			
			if ( Utilities.isPreface(father)) { 
				if(after != null) {
					Utilities.insertElementInTaggedValueList(father, theProgramListing, after, ut.getThePrefaceStereotype(), "prefacePara");
				} else {
					StereotypesHelper.setStereotypePropertyValue(father ,ut.getThePrefaceStereotype(), "prefacePara", theProgramListing,true);			
				}
			}
			
			

			SessionManager.getInstance().closeSession();
			tree.openNode(theProgramListing, true,true);
			
			MBSEShowEditPanelAction.updateEditorView(father,theProgramListing,"programListing");

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}
		
		
}
