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

import org.eso.sdd.mbse.variants.control.ProductModelCreator;
import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.gui.VariationTree;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelingToolAdapter;

public class CreateProductModelTask extends AbstractTask {
	
	// The class for transforming the cross-product model
	// to the product specific model.
	private ProductModelCreator productModelCreator;
	
	// Tells whether saving the product model was successful.
	private boolean isProductModelCorrectlySaved = false;
	
	private final String NAME_DIALOG = "Save Product Model As";
	private final String NAME_PRODUCTMODEL_FILE = "ProductModel";

	public CreateProductModelTask(VariationTree tree, Configuration config, ProductModelCreator productModelCreator, IModelingToolAdapter toolAdapter) {
		super(tree, config, null, toolAdapter);
		this.productModelCreator = productModelCreator;
		progressStrings.add("");
		progressStrings.add("Choose file...");
		progressStrings.add("Loading source project..");
		progressStrings.add("Updating configuration..");
		progressStrings.add("Creating target product model..");
		progressStrings.add("Product model saved successfully.");
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
			String fileExtension = toolAdapter.getProjectFileNameExtension();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("Project File (*." + fileExtension  + ")", fileExtension);
		    String initialFileName = NAME_PRODUCTMODEL_FILE + "." + fileExtension;
		    FileChooser fileChooser = new FileChooser(jframe, NAME_DIALOG, filter, true);
			File chosenFile = fileChooser.chooseFile(initialFileName);

			if (chosenFile != null) {
				// 2. Loading project
				setProgress(++progress);
				String sourceProjectFileName = config.getSourceProjectFileName();
				if (sourceProjectFileName != null) {
					toolAdapter.loadProject(sourceProjectFileName);
				}
				
				// 3. Updating configuration :
				// Make sure that the configuration reflects 
				// the currently selected check boxes.
				setProgress(++progress);
				updateConfigurationWithCheckBoxSelections();
								
				// 4. Create product model
				setProgress(++progress);
				tree.setProductModelCorrectlySaved(false);
				String outputPath = chosenFile.getAbsolutePath();
				productModelCreator.createProductModel(outputPath, config);
				
				if (chosenFile.exists()) {
					// 5. Done.
					setProgress(++progress);
					isProductModelCorrectlySaved = true;
				}else{
					isProductModelCorrectlySaved = false;	
				}
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
	
	@Override
	public void done() {
		tree.setProductModelCorrectlySaved(isProductModelCorrectlySaved);
	}
}
