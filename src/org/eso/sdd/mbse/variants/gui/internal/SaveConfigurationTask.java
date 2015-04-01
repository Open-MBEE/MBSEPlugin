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

import java.io.File;

import javax.swing.JFrame;

import org.eso.sdd.mbse.variants.control.IConfigurationManager;
import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.gui.VariationTree;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelingToolAdapter;

public class SaveConfigurationTask extends AbstractTask {
	
	private final String NAME_DIALOG = "Save Configuration As";

	public SaveConfigurationTask(VariationTree tree, Configuration config, IConfigurationManager configManager, IModelingToolAdapter toolAdapter) {
		super(tree, config, configManager, toolAdapter);
		progressStrings.add("");
		progressStrings.add("Choose file...");
		progressStrings.add("Updating configuration..");
		progressStrings.add("Saving configuration..");
		progressStrings.add("Configuration saved successfully.");
	}

	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Void doInBackground() {
		progress = 0;
		setProgress(progress);

		JFrame jframe = tree.getJframe();
		try {
			
			// 1. Choose file (let the user pick the file)
			setProgress(++progress);
			String initialFileName = NAME_CONFIGURATION_FILE + "." + NAME_CONFIGURATION_FILEXTENSION;
			FileChooser fileChooser = new FileChooser(jframe, NAME_DIALOG,FILTER_CONFIGURATION_FILEXTENSION, true);
			File chosenFile = fileChooser.chooseFile(initialFileName);

			if (chosenFile != null) {
				// 2. Updating configuration :
				// Make sure that the configuration reflects 
				// the currently selected check boxes.
				setProgress(++progress);
				updateConfigurationWithCheckBoxSelections();
				
				// 3. Updating configuration
				setProgress(++progress);
				String outputPath = chosenFile.getAbsolutePath();
				configManager.saveConfiguration(outputPath, config);

				// 4. Done.
				setProgress(++progress);
			}
			else{
				// If no file is picked, no progress is made.
				progress = 0;
				setProgress(progress);
			}
		} catch (Exception ex) {
			handleException(ex);
		}

		return null;
	}
}
