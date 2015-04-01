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
 *    $Id: MBSEXMLAction.java 652 2013-08-27 20:25:59Z nb-linux $
 *
 */

package org.eso.sdd.mbse.doc.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.ui.ProgressStatusRunner;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

import org.eso.sdd.mbse.doc.algo.CommonGenerator;

@SuppressWarnings("serial")
public class MBSEXMLAction extends DefaultBrowserAction {

	private PropertyManager properties = null;
	private JFileChooser fc = new JFileChooser();


	public MBSEXMLAction() {
		super("", "SE2:Generate XML", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));

	}

	private void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.nomagic.magicdraw.actions.MDAction#actionPerformed(java.awt.event.ActionEvent)
	 * @todo: get the XmlFileFilter to work.
	 */

	@Override
	public void actionPerformed(ActionEvent e) {
		File destFile  = null;
		Tree tree = getTree();
		Node node = null;
		FileFilter ff = new FileNameExtensionFilter("XML file", "xml", "xml");
		Object userObject = null;
		if (tree.getSelectedNodes().length > 1) {
			displayWarning("MBSE Plugin: cannot do multiple selection");
			return;
		}

		// Application.getInstance().getMainFrame().getDialogParent(),

		fc.addChoosableFileFilter(ff);

		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();
		NamedElement book = (NamedElement) userObject;

		File f = new File(book.getName().toString().trim()	+ ".xml");
		fc.setSelectedFile(f);

		int returnVal = fc.showSaveDialog(null);
		// set default name

		// TODO looks a bit messy to me. cleanup, use early exit,
		//      and check if file already exists (as in MBSEPdf[Direct]Action)
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File tmpFile = null;
			destFile = fc.getSelectedFile();
			tmpFile = destFile.getParentFile();

			if (destFile.exists() && destFile.canWrite() == false) {
				JOptionPane.showMessageDialog(null,
						"MBSE: FILE ALREADY EXISTS AND I CANNOT WRITE TO IT: "
								+ destFile.toString());
				return;
			} else if (tmpFile.canWrite() == false) {

				// the condition above doesn't seem to work. I do not understand
				// why,
				// but I have to give up now.
				// @todo: fix it.
				JOptionPane.showMessageDialog(null,
						"MBSE:CANNOT WRITE TO DIR: " + tmpFile.toString());
				return;
			}
		} else {
			return;
		}

		// FIXME DEAD CODE: the above code exists all paths with return
		// I don't know what this is for?! (NBE-2013-08-27)

		// here we go.
		if (userObject instanceof NamedElement) {
			// ProgressStatus sps = new SimpleProgressStatus();
			CommonGenerator theGenerator = new CommonGenerator();
			CommonGenerator.setDestFile(destFile);
			CommonGenerator.setStartElement((NamedElement) userObject);
			// theGenerator.generate();
			ProgressStatusRunner.runWithProgressStatus(theGenerator,
					"Model Based Document Generation", true, 1000);
			if (theGenerator.getStatus() == 0) {
				displayWarning("Wrote DocBook to " + destFile.toString());
			}
			if (theGenerator.getStatus() == 1) {
				displayWarning("Aborted by user ");
			}

		} else {
			displayWarning("Cannot apply to a nameless element");
		}

	}
}
