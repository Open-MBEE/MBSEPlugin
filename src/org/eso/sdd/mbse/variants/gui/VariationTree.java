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
package org.eso.sdd.mbse.variants.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.eso.sdd.mbse.variants.control.IConfigurationManager;
import org.eso.sdd.mbse.variants.control.ProductModelCreator;
import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.domain.Variant;
import org.eso.sdd.mbse.variants.domain.Variation;
import org.eso.sdd.mbse.variants.domain.VariationsAspect;
import org.eso.sdd.mbse.variants.gui.internal.AbstractTask;
import org.eso.sdd.mbse.variants.gui.internal.CheckBoxNode;
import org.eso.sdd.mbse.variants.gui.internal.CheckBoxNodeEditor;
import org.eso.sdd.mbse.variants.gui.internal.CheckBoxNodeRenderer;
import org.eso.sdd.mbse.variants.gui.internal.CreateProductModelTask;
import org.eso.sdd.mbse.variants.gui.internal.OpenConfigurationTask;
import org.eso.sdd.mbse.variants.gui.internal.SaveConfigurationTask;
import org.eso.sdd.mbse.variants.gui.internal.TreeFolder;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelingToolAdapter;

/**
 * GUI class that shows the contents of a product configuration as a tree. 
 * This includes the variations aspect, the variations and variants.
 * The variants are displayed as selectable checkboxes. All variants that are selected
 * by the user will become part of the product once it is created.
 * 
 * @see #VariationTree(ProductModelCreator)
 * @see #updateTree(Configuration)
 * 
 * @author Bertil Muth
 * @author Carlos Ortega-Miguez: Updated to consider multiple VariationsAspect model elements instead of one root VariationsAspect.
 *
 */
public class VariationTree  implements ActionListener{
	JFrame jframe;
	
	// Constants identifying the actions that may be initiated by the user.
	private static final String ACTION_CREATE_PRODUCT_MODEL = "variations__createproductmodel";
	private static final String ACTION_OPEN_CONFIGURATION = "variations__openconfigurationaction";
	private static final String ACTION_SAVE_CONFIGURATION = "variations__saveconfigurationaction";
	private static final String ACTION_ABOUT = "variations__about";
	
	// The configuration used as source of information for 
	// building the tree and creating the product specific model.
	Configuration configuration;
	
	// The class for transforming the cross-product model
	// to the product specific model.
	private ProductModelCreator productModelCreator;
	
	// The tree with the folders and check boxes.
	private JTree jtree;
	
	// The progress bar.
    private JProgressBar progressBar;

    // The manager of configurations (opens / saves configurations).
	private IConfigurationManager configManager;

	// The adapter to the modelling tool.
	private IModelingToolAdapter toolAdapter;

	// Menu items.
	private JMenuItem saveProductModelMenuItem;
	private JMenuItem openConfigurationMenuItem;
	private JMenuItem saveConfigurationMenuItem;
	
	// Indicates whether the model has been saved
	// without errors previously.
	private boolean isProductModelCorrectlySaved = false;

	private AbstractTask task;
	
	public VariationTree(ProductModelCreator productModelCreator, IConfigurationManager configManager) {
		this.productModelCreator = productModelCreator;
		this.toolAdapter = productModelCreator.getToolAdapter();
		this.configManager = configManager;
		jframe = new JFrame(IGUILabels.TITLE);
		jframe.setSize(600, 300);
		jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		createMenus();
		updateTree(null);
	}
	
	/**
	 * Set the configuration that defines what will be shown as a tree.
	 * 
	 * @param configuration the configuration, or null if no configuration exists yet.
	 */
	public void updateTree(Configuration configuration) {
		this.configuration = configuration;
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
            	JPanel mainPanel = createMainPanel();	
        		jframe.setContentPane(mainPanel);
        		jframe.validate();
        		// expand all tree nodes.
        		expandAllNodes();            
        	}
        });
	}

	public void display() {
		jframe.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == ACTION_CREATE_PRODUCT_MODEL) {
			task = new CreateProductModelTask(this, configuration, productModelCreator, toolAdapter);
		}else if(e.getActionCommand() == ACTION_OPEN_CONFIGURATION) {
			task = new OpenConfigurationTask(this, configuration, configManager, toolAdapter);
		}else if(e.getActionCommand() == ACTION_SAVE_CONFIGURATION) {
			task = new SaveConfigurationTask(this, configuration, configManager, toolAdapter);
		}else if(e.getActionCommand() == ACTION_ABOUT) {
			String displayStr = IGUILabels.TITLE + " " + IGUILabels.VERSION
					+ "\n\n" + "Code contributed by: " + IGUILabels.AUTHORS + "\n\n"
					+ "Licensed under GNU LGPL" + "\n\n";
			JOptionPane.showMessageDialog(jframe, displayStr);
			return;
		} else {
			return;
		}
		progressBar.setMaximum(task.getMaximumProgress());
		task.execute();
	} 
	
	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel();
		BoxLayout mainLayout = new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS);
		mainPanel.setLayout(mainLayout);
		
		jtree = createJTree();
		if(jtree != null){
			JScrollPane scrollPane = new JScrollPane(jtree);
			mainPanel.add(scrollPane);
		}
		
		progressBar = new JProgressBar(0, 1);
		progressBar.setValue(0);
        progressBar.setStringPainted(true);
        
        mainPanel.add(progressBar);
				
		return mainPanel;
	}
	
	private void createMenus() {
		
		// Create a menu bar
		JMenuBar menuBar = new JMenuBar();
		
		// Create the file menu
		JMenu fileMenu = new JMenu(IGUILabels.MENU_FILE);
		fileMenu.addMenuListener(updateGUIStateMenuListener);
		
		// Create the menu items
		saveProductModelMenuItem = new JMenuItem(IGUILabels.MENUITEM_CREATE_PRODUCT_MODEL);
		saveProductModelMenuItem.setActionCommand(ACTION_CREATE_PRODUCT_MODEL);
		saveProductModelMenuItem.addActionListener(this);
		fileMenu.add(saveProductModelMenuItem);
		openConfigurationMenuItem = new JMenuItem(IGUILabels.MENUITEM_OPEN_CONFIGURATION);
		openConfigurationMenuItem.setActionCommand(ACTION_OPEN_CONFIGURATION);
		openConfigurationMenuItem.addActionListener(this);
		fileMenu.add(openConfigurationMenuItem);
		saveConfigurationMenuItem = new JMenuItem(IGUILabels.MENUITEM_SAVE_CONFIGURATION);
		saveConfigurationMenuItem.setActionCommand(ACTION_SAVE_CONFIGURATION);
		saveConfigurationMenuItem.addActionListener(this);
		fileMenu.add(saveConfigurationMenuItem);
		menuBar.add(fileMenu);
		
		// Create the help menu
		JMenu helpMenu = new JMenu(IGUILabels.MENU_HELP);
		JMenuItem aboutMenuItem = new JMenuItem(IGUILabels.MENUITEM_ABOUT);
		aboutMenuItem.setActionCommand(ACTION_ABOUT);
		aboutMenuItem.addActionListener(this);
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
		
		// Install the menu bar
		jframe.setJMenuBar(menuBar);
	}

 
	private JTree createJTree() {
		// Create the folder that represents the root variations aspect.
		// This includes all contained variations and variants.
		if (configuration != null) {
			List<VariationsAspect> variationsAspectList =
					configuration.getVariationsAspects();
			if (variationsAspectList != null) {
				TreeFolder root = new TreeFolder();
				// Loop through all the variations aspects 
				for (VariationsAspect variationsAspect:variationsAspectList){
				// Create the folder for the variations aspect.
					TreeFolder variationAspectFolder = createVariationsAspectFolder(variationsAspect);
					root.add(variationAspectFolder);
				}

				// Create the tree.
				JTree tree = new JTree(root);

				// Set tree renderers.
				CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
				tree.setCellRenderer(renderer);

				// Set cell editors.
				tree.setCellEditor(new CheckBoxNodeEditor(tree));
				tree.setEditable(true);
				return tree;
			}
		}

		return new JTree(new DefaultMutableTreeNode("Select menu item: File > " + IGUILabels.MENUITEM_OPEN_CONFIGURATION));
	}

	
	private TreeFolder createVariationsAspectFolder(TreeFolder parentFolder, VariationsAspect variationsAspect){
		TreeFolder variationsAspectFolder = createVariationsAspectFolder(variationsAspect);
		parentFolder.add(variationsAspectFolder);
		return variationsAspectFolder;
	}
	
	private TreeFolder createVariationsAspectFolder(VariationsAspect variationsAspect) {
		TreeFolder variationAspectFolder = new TreeFolder(variationsAspect);
		// Get product variants contained in the specified variations aspect.
		List<Variant> productVariants = configuration.getVariantsOfProductByVariationsAspect(variationsAspect);
		
		// Loop over all variations.
		List<Variation> variations = variationsAspect.getVariations();
		for (Variation variation : variations) {
			// Get all variants of the variation.
			List<Variant> variants = variation.getVariants();
			
			// Now, create the variation folder that will contain all variants of the variation.
			TreeFolder variationFolder = new TreeFolder(variation);
			for (Variant variant : variants) {
				// Each variant gets its own checkbox. It is checked if and only if
				// the variant has been marked to be "part of the product".
				boolean isChecked = productVariants.contains(variant);
				variationFolder.addCheckboxNode(variant, isChecked);
				 
				// if the variant has a nested variation aspect, create
				// a folder for the variation and call this method recursively.
				VariationsAspect nestedVariationsAspect = variant.getVariationAspect(); 
				if(nestedVariationsAspect != null){
					TreeFolder variantFolder = new TreeFolder(variant);
					createVariationsAspectFolder(variantFolder, nestedVariationsAspect);	
					variationFolder.add(variantFolder);
				}
			}
			
			// Add the variation folder to its variation aspect
			variationAspectFolder.add(variationFolder);
		}
		return variationAspectFolder;
	}

	private void updateGUIState(){
		boolean configurationAvailable = configuration != null;
		boolean toolAdapterAvailable = toolAdapter != null;
		
		boolean createProductModelAvailable = configurationAvailable && !isProductModelCorrectlySaved;
		boolean openConfigurationAvailable = toolAdapterAvailable;
		boolean saveConfigurationAvailable = configurationAvailable;
		
		if(saveProductModelMenuItem != null) saveProductModelMenuItem.setEnabled(createProductModelAvailable);
		if(openConfigurationMenuItem != null) openConfigurationMenuItem.setEnabled(openConfigurationAvailable);
		if(saveConfigurationMenuItem != null) saveConfigurationMenuItem.setEnabled(saveConfigurationAvailable);
	}
	
	public List<CheckBoxNode> getCheckBoxesOfTree(){
		// Access the tree model
		TreeModel treeModel = jtree.getModel();
		// Create an empty result list that will
		// be populated by the check boxes.
		List<CheckBoxNode> checkBoxList = new ArrayList<CheckBoxNode>();
		// Get the checkboxes (below the root, recursively) and insert them in the result list.
		addCheckBoxesOfTree(checkBoxList, treeModel, treeModel.getRoot());
		return checkBoxList;
	}
	
	private void addCheckBoxesOfTree(List<CheckBoxNode> checkboxNodeList, TreeModel treeModel, Object parentObject){
		// Get the children in the tree model.
		int childCount = treeModel.getChildCount(parentObject);
		for(int i=0; i<childCount; i++){
			Object childNode = treeModel.getChild(parentObject, i);
			if(childNode instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)childNode;
				Object userObject = treeNode.getUserObject();
				if(userObject instanceof CheckBoxNode){
					checkboxNodeList.add((CheckBoxNode)userObject);
				}else{ 
					addCheckBoxesOfTree(checkboxNodeList, treeModel, childNode);
				}
			}
		}
	}
	
	private void expandAllNodes() {
		int row = 0;
		while (row < jtree.getRowCount()) {
			jtree.expandRow(row);
			row++;
		}
	}
	
	private final MenuListener updateGUIStateMenuListener = new MenuListener() {
	      @Override
		public void menuCanceled(MenuEvent e) {
	      }

	      @Override
		public void menuDeselected(MenuEvent e) {
	      }

	      @Override
		public void menuSelected(MenuEvent e) {
	        updateGUIState();
	      }
	    };

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JFrame getJframe() {
		return jframe;
	}

	public void setProductModelCorrectlySaved(boolean isProductModelCorrectlySaved) {
		this.isProductModelCorrectlySaved = isProductModelCorrectlySaved;
	}
}
