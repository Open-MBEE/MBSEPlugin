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
 *    $Id: MBSEPdfAction.java 2961 2011-11-04 18:40:44Z jesdabod $
 *
 */

package org.eso.sdd.mbse.doc.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

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

import org.eso.sdd.mbse.doc.algo.xsltFOP;
import org.eso.sdd.mbse.doc.algo.xsltFOP.GENERATION_MODE;

@SuppressWarnings("serial")
public class MBSERTFDirectAction extends DefaultBrowserAction {

	private PropertyManager properties = null;
	private JFileChooser fc = new JFileChooser();

	public MBSERTFDirectAction() {
		super("", "SE2:Generate RTF", null, null);
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
		File destFile, rtfFile = null;
		String xmlFileName = null;
		Tree tree = getTree();
		Node node = null;
		FileFilter ff = new FileNameExtensionFilter("RTF file", "rtf", "rtf");
		Object userObject = null;
		if (tree.getSelectedNodes().length > 1) {
			displayWarning("MBSE Plugin: cannot do multiple selection");
			return;
		}


		fc.addChoosableFileFilter(ff);


		// generate XML first
		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();
		NamedElement book = (NamedElement) userObject;


		// set default name
		File f = new File(book.getName().toString().trim()	+ ".rtf");
		fc.setSelectedFile(f);

		int ret = fc.showDialog(null, "Save as RTF");

		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		rtfFile = fc.getSelectedFile();
		xmlFileName = rtfFile.getAbsolutePath();
		xmlFileName = xmlFileName.replaceFirst(".rtf", ".xml");
		destFile = new File(xmlFileName);

		// check files already exist and ask user
		if(rtfFile.exists() || destFile.exists()) {
			ret = JOptionPane.showConfirmDialog(
					null,
					"Some files already exist and will be overwritten.\n" +
							"Proceed and overwrite files?",
					"Proceed and overwrite files?",
					JOptionPane.YES_NO_OPTION);
			if(ret != JOptionPane.YES_OPTION) {
				return;
			}
		}

		// Generate RTF
		System.out.println("Generating RTF...");
		xsltFOP k = new xsltFOP(GENERATION_MODE.XML_AND_RTF);
		xsltFOP.setRTFFile(rtfFile);
		xsltFOP.setXMLFile(destFile);
		xsltFOP.setUserObject(userObject);
		// k.genPDF();
		// displayWarning("Generated PDF to " + pdfFile.toString());

		try {
			ProgressStatusRunner.runWithProgressStatus(k,
					"Docbook RTF Generation", true, 1000);
		} catch (Exception e1) {
			e1.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);

			displayWarning("An error occurred while generating the Editor Panel JTree\n" + sw.toString());
		}
		if (k.getStatus() == 0) {
			displayWarning("Generated RTF to " + rtfFile.toString());
		}
		if (k.getStatus() == 1) {
			displayWarning("Aborted by user ");
		}
		if (k.getStatus() == 2) {
			//displayWarning("Exception aborted. ");
		}
	}
}
