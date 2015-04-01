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
 *    $Id: MBSEActionGetTemplate.java 577 2012-05-29 08:22:20Z nb-linux $
 *
*/

package org.eso.sdd.mbse.utilities;

import com.nomagic.magicdraw.core.Application; // added specifically for this file
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.openapi.uml.SessionManager;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import com.nomagic.magicdraw.uml.symbols.PresentationElement;

import com.nomagic.magicdraw.uml.symbols.shapes.ClassView;
import com.nomagic.magicdraw.uml.symbols.shapes.ClassifierView;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;


@SuppressWarnings("serial")
public class MBSEActionAddCID extends DefaultDiagramAction {

	private Stereotype cidStereotype = null;
	private String cidStereoName = "Configuration Item";


	public MBSEActionAddCID() {
		super("", "SE2: convert to CID", null, null);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		PresentationElement pe = getSelected().get(0);
		Element userObject = pe.getElement();
		if(userObject == null) {
			// Debug
			System.out.println("MBSE WARN: actionPerformed: no element selected??");
			return;
		}
		cidStereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(), cidStereoName);
		if(cidStereotype == null) { 
			System.out.println("MBSE WARN: actionPerformed: Stereotype for " + cidStereoName + " null??");			
			return;
		}
		Object theAnswer = JOptionPane.showInputDialog(MDDialogParentProvider.getProvider().getDialogParent(),
				"" , "Enter CI Id ", JOptionPane.QUESTION_MESSAGE);
		// TAGGED_VALUES_DISPLAY_MODE IN_COMPARTMENT (undefined state - false)Index1;valueIN_COMPARTMENT

		if(!pe.isEditable()) {
			System.out.println("MBSE INFO: The presentation element of " + userObject.getHumanName() +
					" in diagram " + getDiagram().getName() + " is not readable");
			return;
		}
		
		if(pe instanceof ClassView) { 
			((ClassView) pe).setTaggedValuesDisplayMode(ClassifierView.IN_COMPARTMENT);
		} else { 
			System.out.println("MBSE ERROR: The presentation element of " + userObject.getHumanName() +
					" in diagram " + getDiagram().getName() + " is not a ClassView, aborting.");
			return;
		}
        
        SessionManager manager = SessionManager.getInstance();
        
        if (!manager.isSessionCreated())
        {
            manager.createSession("MBSE-SetCID");
            StereotypesHelper.addStereotype(userObject, cidStereotype);
            StereotypesHelper.setStereotypePropertyValue(userObject, cidStereotype, "id", theAnswer.toString(), true);
            manager.closeSession();
        }
	}

	@Override
	public void updateState() {

	}
}
