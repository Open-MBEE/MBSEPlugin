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
package org.eso.sdd.mbse.variants.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.domain.Variant;
import org.eso.sdd.mbse.variants.domain.Variation;
import org.eso.sdd.mbse.variants.domain.VariationsAspect;
import org.eso.sdd.mbse.variants.gui.IGUILabels;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelNameConventions;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelingToolAdapter;

/**
 * Class that transforms the contents of a configuration (=user selected variants of a source model)
 * to a product specific target model.
 * 
 * @author Bertil Muth
 * @author Carlos Ortega-Miguez: Updated to consider multiple VariationsAspect model elements instead of one root VariationsAspect. 
 *
 */
public class ProductModelCreator {
	private IModelingToolAdapter toolAdapter;

	public ProductModelCreator(IModelingToolAdapter adapter) {
		this.toolAdapter = adapter;
	}
	
	/**
	 * Creates a product model by transforming the configuration to a product specific model.
	 * 
	 * @param productProjectFileName the file name (including the path) of the product specific project that will be created.
	 * @param configuration the configuration that contains the variants that will be contained in the product.
	 *
	 * @throws Exception
	 */
	public void createProductModel(String productProjectFileName, Configuration configuration) throws Exception{
		if(toolAdapter == null){
			throw new RuntimeException("Can't access tool !");
		}
		Object activeProjectFileName = toolAdapter.getActiveProject();
		if(activeProjectFileName == null){
			throw new RuntimeException("You need to open a source model project before you can create a product !");
		}
		
		// Save the project first, to make sure that the
		// original model is not overwritten.
		toolAdapter.saveProject(productProjectFileName);
		
		List<VariationsAspect> variationsAspectList = configuration.getVariationsAspects();
		
		// Loop through all the Variations Aspects in the model.
		for (VariationsAspect variationsAspect : variationsAspectList){
			// Get the product specific variants of the specified Variations Aspect, from the configuration.
			List<Variant> productVariantsFromConfig = configuration.getVariantsOfProductByVariationsAspect(variationsAspect);
			
			
			// Get the variant model elements under the specified Variations Aspect
			List<Variant> productVariantsFromModel = new ArrayList<Variant>();
			List<Variation> variationList = variationsAspect.getVariations();
			for (Variation variation : variationList){
				List<Variant> variantList = variation.getVariants();
				for (Variant variant : variantList){
					productVariantsFromModel.add(variant);
				}
			}
			
			// Variant elements in the model must have one
			// of the following stereotypes.
			String[] variantStereotypeNames = {
					IModelNameConventions.STEREOTYPE_VARIANT,
					IModelNameConventions.STEREOTYPE_SE2VARIANT
			};
			
			for (String variantStereotypeName : variantStereotypeNames) {
				// Get the variant model elements in the model.
				Collection<? extends Object> variantModelElements = toolAdapter
						.getModelElementsByStereotype(variantStereotypeName);

				// Loop over all variant model elements and remove
				// all variants that are not part of the product relevant
				// variants
				// and are contained in the specified Variations Aspect.
				for (Object variantModelElement : variantModelElements) {
					// Access the variant in the model.
					Variant modelVariant = new Variant();
					modelVariant.setId(toolAdapter
							.getModelElementID(variantModelElement));
					modelVariant.setName(toolAdapter
							.getModelElementName(variantModelElement));
					modelVariant.setStereotypeName(variantStereotypeName);

					if (!productVariantsFromConfig.contains(modelVariant)
							&& productVariantsFromModel.contains(modelVariant)) {
						// The variant is contained in the model, but not in the
						// product variants. So remove it.
						String qName = toolAdapter
								.getModelElementQName(variantModelElement);
						System.out.println(IGUILabels.TITLE
								+ ": removing model element " + qName);
						toolAdapter.removeModelElement(variantModelElement);
						System.out.println(IGUILabels.TITLE + ": removed "
								+ qName);
					}
				}
			}
		}
		
		toolAdapter.saveProject(productProjectFileName);
	}

	public IModelingToolAdapter getToolAdapter() {
		return toolAdapter;
	}
}
