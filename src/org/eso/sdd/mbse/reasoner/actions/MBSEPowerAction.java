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
 *    $Id: MBSEPowerAction.java 642 2013-06-25 15:04:17Z mzampare $
 *
*/

package org.eso.sdd.mbse.reasoner.actions;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import org.eso.sdd.mbse.MBSEScrollableTextDialog;
import org.eso.sdd.mbse.doc.actions.MBSESaveInfoAction;
import org.eso.sdd.mbse.reasoner.MBSEComputation;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;


@SuppressWarnings("serial")
public class MBSEPowerAction extends DefaultBrowserAction {

	private PropertyManager properties = null;
	private static final boolean Debug = true;
	private static StringBuffer powerBreakDown = new StringBuffer();
    	
	public MBSEPowerAction() {
		super("", "SE2:getPower", null, null);
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
		Tree tree = getTree();
		Node node = null;
		Object userObject = null;
		if (tree.getSelectedNodes().length > 1) {
			displayWarning("MBSE Plugin: cannot do multiple selection");
			return;
		}
		
		//Application.getInstance().getMainFrame().getDialogParent(),
		Project theProject = Application.getInstance().getProject();

		if ( theProject == null) { 
			   JOptionPane.showMessageDialog(null,"MBSE: YOU HAVE NOT LOADED ANY PROJECT!");
			   return;
		}
		Model theModel   = theProject.getModel();
		if (theModel == null) { 
			   JOptionPane.showMessageDialog(null,"MBSE: YOUR MODEL IS EMPTY");
		}

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();
		// firstly, empty the string buffer so that new computations do not get appended.	
		powerBreakDown = new StringBuffer();
		powerBreakDown.append("<table>");
		powerBreakDown.append("<tr><td><b>Name</b></td><td><b>Type</b></td><td><b>Multiplicity</b></td><td><b>Value</b></td></tr>");
		if (userObject instanceof NamedElement) {
			Double power = 0.0;
			if(((NamedElement)userObject).hasOwnedElement()) { 
		    		power = MBSEComputation.computeValue((NamedElement)userObject,"power",powerBreakDown);
		    		powerBreakDown.append("</table>");
		    		powerBreakDown.append("<b>Total Power for " + ((NamedElement)userObject).getName() + " is " + ((Double)power).toString() + "<b><br>\r\n");
		    		MBSESaveInfoAction.setDumpInfo(powerBreakDown,((NamedElement)userObject).getName(),"power");
			} else {
				powerBreakDown.append(((NamedElement)userObject).getName() + " has no elements!"); 
			}
			
			MBSEScrollableTextDialog.showDialog ("Power Rollup", powerBreakDown.toString());
			//displayWarning(powerBreakDown.toString());
			
		} else { 
			return;
		}
		//
	}
  }
    
  