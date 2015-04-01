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
package org.eso.sdd.mbse.variants.tooladapter.magicdraw;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.eso.sdd.mbse.variants.control.IConfigurationManager;
import org.eso.sdd.mbse.variants.control.ProductModelCreator;
import org.eso.sdd.mbse.variants.control.XMLConfigurationManager;
import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.gui.VariationTree;
import org.eso.sdd.mbse.variants.gui.IGUILabels;

import com.nomagic.magicdraw.actions.MDAction;

/*
 * @author Bertil Muth
 */
@SuppressWarnings("serial")
public class MenuAction extends MDAction {
	private MagicDrawToolAdapter toolAdapter;

	public MenuAction(String id, String name) {
		super(id, name, null, null);
		toolAdapter = new MagicDrawToolAdapter();
	}

	@Override
	public void actionPerformed(ActionEvent e) {	
		// Create the tree and initialise it with a transformer
		// to transform the cross-product model to a product model
		// and a config manager that can load / save configurations.
		ProductModelCreator transformer = new ProductModelCreator(toolAdapter);
		IConfigurationManager configManager = new XMLConfigurationManager();
		VariationTree tree = new VariationTree(transformer, configManager);
		
		try{
			// Access the tool and create the configuration (if there is an active project).
			Object project = toolAdapter.getActiveProject();
			if(project != null){
				Configuration configuration = configManager.createConfiguration(toolAdapter);
				// Update the tree with the configuration and show it.
				tree.updateTree(configuration);
			}
			tree.display();
		}catch(Exception ex){
			String title = IGUILabels.TITLE;
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, ex.getMessage(), title, JOptionPane.OK_OPTION);
			return;
		}
	}

	@Override
	public void updateState() {
		setEnabled(true);
	}
}
