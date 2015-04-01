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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.eso.sdd.mbse.doc.algo.genUtility;
import org.eso.sdd.mbse.variants.control.IConfigurationManager;
import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.domain.Variant;
import org.eso.sdd.mbse.variants.gui.VariationTree;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelingToolAdapter;

public abstract class AbstractTask extends SwingWorker<Void, Void> implements PropertyChangeListener{

	protected Configuration config;
	protected IConfigurationManager configManager;
	protected IModelingToolAdapter toolAdapter;
	protected VariationTree tree;
	protected ArrayList<String> progressStrings;
	protected int progress;
	
	protected static final String NAME_CONFIGURATION_FILE = "VariationsConfiguration";
	protected static final String NAME_CONFIGURATION_FILEXTENSION = "varxml";
	protected static final FileNameExtensionFilter FILTER_CONFIGURATION_FILEXTENSION = new FileNameExtensionFilter(
				"Configuration File (*.varxml)", NAME_CONFIGURATION_FILEXTENSION);

	public AbstractTask(VariationTree tree, Configuration config, IConfigurationManager configManager, IModelingToolAdapter toolAdapter) {
		this.tree = tree;
		this.config = config;
		this.configManager = configManager;
		this.toolAdapter = toolAdapter;
		progressStrings = new ArrayList<String>();
		addPropertyChangeListener(this);
	}

	public int getMaximumProgress() {
		return this.progressStrings.size()-1;
	}

	public String getProgressString() {
		return progressStrings.get(progress);
	}
	/**
	 * Invoked when task's progress property changes.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			JProgressBar progressBar = tree.getProgressBar();
			progressBar.setValue(progress);
			progressBar.setString(getProgressString());
		}
	}

	public void updateConfigurationWithCheckBoxSelections() {
		// Loop over all checkboxes. If a checkbox is selected,
		// then get its variant. The set of checked variants defines
		// which variants are relevant for the product specific model.
		List<CheckBoxNode> variantCheckboxNodes = tree.getCheckBoxesOfTree();
		List<Variant> variantsOfProduct = new ArrayList<Variant>();
		for (CheckBoxNode checkboxNode : variantCheckboxNodes) {
			// Access the variant that corresponds to the selected checkbox.
			Variant selectedVariant = (Variant) checkboxNode.getObject();
	
			// Depending on whether the checkbox is selected or not,
			// make the variant part of the product or not.
			if (checkboxNode.isSelected()) {
				variantsOfProduct.add(selectedVariant);
			} 
		}
		config.setVariantsOfProduct(variantsOfProduct);
	}

	protected void handleException(Exception ex) {
		ex.printStackTrace();
		String msg = ex.getMessage();
		int maxProgress = getMaximumProgress();
		progressStrings.remove(maxProgress);
		progressStrings.add(msg);
		progress = maxProgress;
		setProgress(progress);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);

		JOptionPane.showMessageDialog(tree.getJframe(), sw.toString(), "Error", JOptionPane.OK_OPTION);
	}
}
