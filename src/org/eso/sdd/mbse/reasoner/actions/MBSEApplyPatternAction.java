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
 *    $Id: CreateFigureDiagramAction.java 3118 2011-12-20 18:03:39Z jesdabod $
 *
 */

package org.eso.sdd.mbse.reasoner.actions;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree; //import com.nomagic.magicdraw.uml.NamedElement;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

import java.util.ArrayList;
import java.util.List;

import com.nomagic.uml2.impl.ElementsFactory;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;

import javax.swing.JOptionPane;
import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.doc.ui.CustomDialog;
import org.eso.sdd.mbse.doc.ui.MultilineDialogPanel2;
import org.eso.sdd.mbse.reasoner.ApplyPatternReasoner;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class MBSEApplyPatternAction extends DefaultBrowserAction {
	PropertyManager properties = null;
	ArrayList<Property> allOwnedElement = null;
	ArrayList<Element> patternProperties = null;
	ArrayList<Element> partProperties = null;

	public MBSEApplyPatternAction() {
		super("", "SE2: apply Reasoning pattern", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));
	}

	private void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Tree tree = null;
		Object userObject = null;
		NamedElement ne = null;
		Node node = null;
		boolean multi = false;

		tree = getTree();
		if (tree.getSelectedNodes().length > 1) {
			multi = true;
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();

		ne = (NamedElement) userObject;

		createReasoner(ne, multi, tree);
	}

	public void createReasoner(NamedElement ne, boolean multi, Tree tree) {

		allOwnedElement = new ArrayList<Property>();

		if (!SessionManager.getInstance().isSessionCreated()) {
			
			ElementsFactory factory = null;
			SessionManager.getInstance().createSession("MBSE-doc");
			factory = Application.getInstance().getProject()
					.getElementsFactory();

			CustomDialog dlg = new CustomDialog("System Reasoner Pattern Wizard", "apply");
			MultilineDialogPanel2 tmp = CustomDialog.getResults2();
			
			if(dlg.getSelection() == 0){

				List<BaseElement> be = tmp.getSelectedElements();
				if (be != null) {
					for(BaseElement kk:be){
						//displayWarning(kk.getHumanName());
						patternProperties = new ArrayList<Element>();
						partProperties = new ArrayList<Element>();
						
						ApplyPatternReasoner jj = new ApplyPatternReasoner();
						jj.setPatternProp(patternProperties);
						jj.setPartProp(partProperties);
						jj.storeAllPatternProperties(kk);
						
						// set base classifier
							for (Node e1 : tree.getSelectedNodes()) {

								jj.createClassifier((Element) e1.getUserObject(),
										(Classifier) ((Element) kk),
										(Classifier) ((Element) e1.getUserObject()),
										factory
										,tmp.getResults()[0]//apply recursively
										,tmp.getResults()[1]//set role names
										,tmp.getResults()[2]//set subsetted
										,tmp.getResults()[3]//redef
										,tmp.getResults()[4]);//valuprop
   
							}
					}
				}
				else {
					displayWarning("You have not selected any pattern.");
					SessionManager.getInstance().closeSession();
					createReasoner(ne, multi, tree);
				}
			}
			
			if(SessionManager.getInstance().isSessionCreated()){
			SessionManager.getInstance().closeSession();
			}
			
		} else {
			Utilities.displayWarning("could not create session manager");
		}
	}

	

}
