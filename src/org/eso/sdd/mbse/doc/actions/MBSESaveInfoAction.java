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
 *    $Id: MBSESaveInfoAction.java 639 2013-06-25 14:16:57Z mzampare $
 *
*/

package org.eso.sdd.mbse.doc.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyManager;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;


@SuppressWarnings("serial")
public class MBSESaveInfoAction extends DefaultBrowserAction {

	private PropertyManager properties = null;
	private static final boolean Debug = true;
	private static StringBuffer theDumpBuffer = null;
	private static String       theModuleElementName = null;
	private static String       theAttributeName     = null;
	
	private JFileChooser fc = null;
    private static FileWriter mbseEngineeringBudgetFW = null;
	
 	public MBSESaveInfoAction() {
		super("", "SE2:Save Info", null, null);
		properties = new PropertyManager();
		properties.addProperty(new BooleanProperty(
				PropertyID.SHOW_DIAGRAM_INFO, true));
			}

    public static void setDumpInfo(StringBuffer sb, String men, String an) { 
		theDumpBuffer = sb;
		theModuleElementName = men;
		theAttributeName     = an;
	
}

	private void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File destFile,pFile,dirFile = null;
		String projectFileName = null;
		String fileName = null;
		String defaultDir = null;
		
		fc = new JFileChooser();
		Project theProject = Application.getInstance().getProject();
		
		
		if ( theProject == null) { 
			   JOptionPane.showMessageDialog(null,"MBSE: YOU HAVE NOT LOADED ANY PROJECT!");
			   return;
		}

		Model theModel   = theProject.getModel();
		if (theModel == null) { 
			   JOptionPane.showMessageDialog(null,"MBSE: YOUR MODEL IS EMPTY");
		}
		
		projectFileName = theProject.getFileName();
		if ( projectFileName == null) { 
			JOptionPane.showMessageDialog(null,"MBSE: YOU SHOULD SAVE YOUR PROJECT FIRST!");
		   return;
		}
		
		if ( theDumpBuffer == null) { 
			JOptionPane.showMessageDialog(null,"MBSE: YOU HAVE NOT DONE ANY COMPUTATION YET!");
			return;
		}

		// this is the default directory, in case
		// we cannot get one from the project file, because there isn't one...
		
		if(System.getenv("HOME") == null) { 
			File tmpFile = fc.getCurrentDirectory();
			defaultDir = tmpFile.toString();
		} else {
			defaultDir = System.getenv("HOME");
		}

		//	the fileName starts with a File Separator
		fileName = File.separator + theProject.getName()+"-"
		+theModuleElementName + "-" 
		+theAttributeName + ".txt";		
		
		//System.out.println("HOME: " + System.getenv("HOME") + " FILENAME: "+ fileName);
		if (theProject.isRemote() ) {
			// we assume it is in TWS
			dirFile = new File(defaultDir);
			destFile = new File(dirFile.getPath() + fileName);			
			
		} else {
			// it is a local project
			pFile    = new File(theProject.getFileName());
			if(pFile.getParent() == null) { 
				// unlikely event
				dirFile = new File(defaultDir);
				destFile = new File(dirFile.getPath() + fileName);	
			} else { 
				dirFile = new File(pFile.getParent());
				destFile = new File(pFile.getParent() + fileName);
			}			
		}

		
		// displaying the file chooser
		fc.setSelectedFile(destFile);
		int returnVal = fc.showSaveDialog(null);

	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	File tmpFile = null;
	    	destFile = fc.getSelectedFile();
	        tmpFile = destFile.getParentFile();
	        
			if(destFile.exists() && destFile.canWrite() == false ) { 
			  	JOptionPane.showMessageDialog(null,"MBSE: FILE ALREADY EXISTS AND I CANNOT WRITE TO IT: " + 
									  destFile.toString());
				return;
			} else if(tmpFile.canWrite() == false ) {
				
				// the condition above doesn't seem to work. I do not understand why,
				// but I have to give up now.
				// @todo: fix it.
			  	//JOptionPane.showMessageDialog(null,"MBSE:CANNOT WRITE TO DIR: " + dirFile.toString());
				//return;
			}
	        
	        // do the real writing here. 
			dumpEngineeringBudgetInfoToFile(destFile);
			
			
			JOptionPane.showMessageDialog(null,"MBSE: WROTE TO: " + destFile.getAbsolutePath());
	    } else { // user pressed CANCEL
	    	//System.out.println("Open command cancelled by user." );
	    	return;
	    }
	}
	
	public static void dumpEngineeringBudgetInfoToFile(File destFile) { 
	    	try { 	
	    		mbseEngineeringBudgetFW = new FileWriter(destFile);
	    		mbseEngineeringBudgetFW.write("#\r\n");
	    		mbseEngineeringBudgetFW.write("# CREATED BY MAGICDRAW MBSE PLUGIN\r\n");
	    		mbseEngineeringBudgetFW.write("# FROM " + Application.getInstance().getProject().getFileName() + "\r\n");
	    		mbseEngineeringBudgetFW.write("# "+ Calendar.getInstance().getTime().toString()+"\r\n" );
	    		mbseEngineeringBudgetFW.write("#\r\n");
	    		mbseEngineeringBudgetFW.write("#\r\n");
	    		//mbsePriceFW.newLine();
	    		mbseEngineeringBudgetFW.write(theDumpBuffer.toString());
	    		mbseEngineeringBudgetFW.close();
	    	} catch(IOException e) { 
	    		System.out.println("MBSE Could not open file: "+ destFile.toString() + " for writing");
	    		System.out.flush();
	    		return;
	    	}
	   }

	
  }
    
  