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
*/
package org.eso.sdd.mbse.variants.gui.internal;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class FileChooser{
	
	private File lastUsedDirectory;
	private Component parentComponent;
	private String title;
	private FileNameExtensionFilter filter;
	private boolean isSaveDialog;
	
	/**
	 * Creates a component for choosing files.
	 * 
	 * @param parentComponent the parent component
	 * @param title the title of the window that is shown
	 * @param filter only files that are accepted by the filter can be chosen.
	 * @param isSaveDialog if true, a save dialog is shown, otherwise, a load dialog is shown.
	 */
	public FileChooser(Component parentComponent, String title, FileNameExtensionFilter filter, boolean isSaveDialog)  {
		this.parentComponent = parentComponent;
		this.title = title;
		this.filter = filter;
		this.isSaveDialog = isSaveDialog;
	}
	
	public File chooseFile(String initialFileName) {
		boolean validFileSelectedOrCancel = false;
		File chosenFile = null;
		
			do {
				chosenFile = makeChoice(initialFileName);

				if (chosenFile != null) {
					// Check if the file already exists.
					if (chosenFile.exists()) {
						// Ok, the file exists. 
						if(isSaveDialog){
							// In a save dialog, check if the file is read-only.
							if (!chosenFile.canWrite()) {
								// Read-only files can't be overwritten (of course).
								JOptionPane.showMessageDialog(parentComponent, "File "
										+ chosenFile.getName()
										+ " is read only. Pick another file.",
										title, JOptionPane.OK_OPTION);
							} else {
								// The file is not read only, it can be overwritten.
								// But ask the user first.
								Object[] options = { "Yes", "No" };
								int userInput = JOptionPane
										.showOptionDialog(
												parentComponent,
												"File "
														+ chosenFile.getName()
														+ " already exists, do you really want to overwrite it ?",
												title, JOptionPane.YES_NO_OPTION,
												JOptionPane.QUESTION_MESSAGE, null,
												options, options[1]);
								if (userInput == JOptionPane.YES_OPTION) {
									validFileSelectedOrCancel = true;
								}
							}
						}else{
							// In an open dialog, check if the file can be read.
							if (!chosenFile.canRead()) {
								JOptionPane.showMessageDialog(parentComponent, "File "
										+ chosenFile.getName()
										+ " can't be read. Pick another file.",
										title, JOptionPane.OK_OPTION);
							}else{
								validFileSelectedOrCancel = true;
							}
						}
					} else {
						// the file does not exist. For save dialogs,
						// that is good, for open dialogs, this is bad.
						validFileSelectedOrCancel = isSaveDialog;
					}
				} else {
					validFileSelectedOrCancel = true;
				}
			} while (!validFileSelectedOrCancel);
		
		return chosenFile;
	}
		
	private File makeChoice(String initialFileName) {
		
		//create a FileChooser with a xml-file Filter
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		
		if(initialFileName != null){
			chooser.setSelectedFile(new File(initialFileName));
		}
	    
	    //if the lastUsedDirectory is not empty it is used as starting directory for the file choosing
	    if(lastUsedDirectory != null) {
	    	chooser.setCurrentDirectory(lastUsedDirectory);
	    }
	    
	    //set explanation text as title
	    chooser.setDialogTitle(title);
	   
	    //open the file dialog and return the chosen file
	    int returnVal;
	    if(isSaveDialog){
	    	returnVal = chooser.showSaveDialog(parentComponent);	
	    }else{
	    	returnVal = chooser.showOpenDialog(parentComponent);
	    }
	    
	    // Let the user pick a file.
	    parentComponent.setVisible(true);
	    File file = chooser.getSelectedFile();
	    if(file != null){
			// save the directory of the chosen file for an eventually next
			// browse of the file system.
			lastUsedDirectory = file.getParentFile();
		}
	    
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String filePath = file.getAbsolutePath();
			if(filter.getExtensions().length == 1){
				// if there is exactly one extension in the file filter,
				// make sure that the selected file name ends with it.
				String fileExtension = filter.getExtensions()[0];
				if(!filePath.endsWith("." + fileExtension)){
					String newFilePath = filePath + "." + fileExtension;
					file = new File(newFilePath);
				}
			}
			
			return file;
		}
		// no file to return
		return null;	
	}
}
