package org.eso.sdd.mbse.doc.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
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

import org.eso.sdd.mbse.doc.algo.Utilities;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class CreateTableParagraphAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	Utilities ut = null;

	public CreateTableParagraphAction() {
		super("", "SE2: create Table Paragraph", null, null);
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
			addTableParagraph(tree, ne, null);
		} else {
			Utilities.displayWarning("This is not a named element");
		}
	}

	public void addTableParagraph(Tree tree, NamedElement father, Element after) {
		ElementsFactory factory = null;
		String stub = "<html><head></head><body><table cellpadding='0' width='100%' cellspacing='0'><tr> <td> </td><td></td></tr></table></body></html>";

		if (!SessionManager.getInstance().isSessionCreated()) {

			SessionManager.getInstance().createSession("MBSE-doc"); 
			factory = Application.getInstance().getProject().getElementsFactory();

			Comment theTableParagraph = factory.createCommentInstance();
			StereotypesHelper.addStereotype(theTableParagraph, ut.getTheTableParagraphStereotype());
			
			String tableCaption = JOptionPane.showInputDialog(
					MDDialogParentProvider.getProvider().getDialogParent(), "",
					"Enter Table Caption", JOptionPane.QUESTION_MESSAGE);
			
			
			try {
				ModelElementsManager.getInstance().addElement(theTableParagraph, father);

			} catch (ReadOnlyElementException roee) {
				Utilities.displayWarning("Read only element");
				// displayWarning("Here I would create a "+strName+"
				// package\n");
			}

			if( Utilities.isSection(father)) { 
				if(after != null) {
					Utilities.insertElementInTaggedValueList(father, theTableParagraph, after, ut.getTheSectionStereotype(), "blockelements");
				} else {
					StereotypesHelper.setStereotypePropertyValue(father ,ut.getTheSectionStereotype(), "blockelements", theTableParagraph,true);			
				}

			}

			if ( Utilities.isChapter(father)) { 
				if(after != null) {
					Utilities.insertElementInTaggedValueList(father, theTableParagraph, after, ut.getTheChapterStereotype(), "blockelements");
				} else {
					StereotypesHelper.setStereotypePropertyValue(father ,ut.getTheChapterStereotype(), "blockelements", theTableParagraph,true);			
				}
			}
			
			if ( Utilities.isPreface(father)) { 
				if(after != null) {
					Utilities.insertElementInTaggedValueList(father, theTableParagraph, after, ut.getThePrefaceStereotype(), "prefacePara");
				} else {
					StereotypesHelper.setStereotypePropertyValue(father ,ut.getThePrefaceStereotype(), "prefacePara", theTableParagraph,true);			
				}
			}
			
			//set empty tablestub
			theTableParagraph.setBody(stub);
			
			// set tag with caption text
			StereotypesHelper.setStereotypePropertyValue(theTableParagraph,
					ut.getTheTableParagraphStereotype(), "captionText",
					tableCaption, true);

			SessionManager.getInstance().closeSession();
			tree.openNode(theTableParagraph, true,true);
			
			MBSEShowEditPanelAction.updateEditorView(father,theTableParagraph,"tableParagraph");

		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

}
