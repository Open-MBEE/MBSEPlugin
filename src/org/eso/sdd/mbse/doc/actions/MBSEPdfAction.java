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


import org.eso.sdd.mbse.doc.algo.genUtility;
import org.eso.sdd.mbse.doc.algo.xsltFOP;
import org.eso.sdd.mbse.doc.algo.xsltFOP.GENERATION_MODE;

@SuppressWarnings("serial")
public class MBSEPdfAction extends DefaultBrowserAction {

	private PropertyManager properties = null;
	private JFileChooser fc = new JFileChooser();


	public MBSEPdfAction() {
		super("", "SE2:Generate XML to PDF", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String pdfFileName =null; 
		File destFile, pdfFile = null;
		Tree tree = getTree();
		Object userObject = null;		
		Node node = null;
		
		FileFilter ff = new FileNameExtensionFilter("PDF file", "pdf", "pdf");
		if (tree.getSelectedNodes().length > 1) {
			genUtility.displayWarning("MBSE Plugin: cannot do multiple selection");
			return;
		}

		// Application.getInstance().getMainFrame().getDialogParent(),
		fc.addChoosableFileFilter(ff);



		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();
		NamedElement book = (NamedElement) userObject;
		

		File f = new File(book.getName().toString().trim()	+ ".xml");
		fc.setSelectedFile(f);

		int ret = fc.showDialog(null, "Save as PDF");

		if (ret != JFileChooser.APPROVE_OPTION) {
			return;
		}
		destFile = fc.getSelectedFile();
		pdfFileName = destFile.getAbsolutePath();
		pdfFileName = pdfFileName.replaceFirst(".xml", ".pdf");
		pdfFile = new File(pdfFileName);

		// check file already exists and ask user
		if(pdfFile.exists()) {
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

		// Generate PDF
		System.out.println("Generating PDF...");
		xsltFOP k = new xsltFOP(GENERATION_MODE.PDF_ONLY);
		xsltFOP.setPDFFile(pdfFile);
		xsltFOP.setXMLFile(destFile);
		// k.genPDF();
		// displayWarning("Generated PDF to " + pdfFile.toString());

		try{
			ProgressStatusRunner.runWithProgressStatus(k,
					"Docbook PDF Generation", true, 1000);
		}catch(Exception e1){
			e1.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);

			genUtility.displayWarning("An error occurred while generating the Editor Panel JTree\n" + sw.toString());

		}

		if (k.getStatus() == 0) {
			genUtility.displayWarning("Generated PDF to " + pdfFile.toString());
		}
		if (k.getStatus() == 1) {
			genUtility.displayWarning("Aborted by user ");
		}
		if (k.getStatus() == 2) {
			//displayWarning("Exception aborted. ");
			// above additional display warning deemed superfluous and unclear.
		}
	}
}
