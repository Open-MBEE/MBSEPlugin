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
 *    $Id: MBSEVersionAction.java 639 2013-06-25 14:16:57Z mzampare $
 *
*/

package org.eso.sdd.mbse.doc.actions;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;

@SuppressWarnings("serial")
public class MBSEVersionAction extends DefaultBrowserAction {
	private String theVersion = null;
	
	static private String rcsVersion = "$Id: MBSEVersionAction.java 639 2013-06-25 14:16:57Z mzampare $";
	public MBSEVersionAction() {
		super("", "Version", null, null);
		
	}

	private void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
			displayWarning("MBSE Plugin: SDD/SED Michele Zamparelli\nVersion " + "MBSE-0_8");
		}
}