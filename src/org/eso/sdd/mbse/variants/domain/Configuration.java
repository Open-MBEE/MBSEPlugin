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
package org.eso.sdd.mbse.variants.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/*
 * @author Bertil Muth
 * @author Carlos Ortega-Miguez: Updated to consider multiple VariationsAspect model elements instead of one root VariationsAspect.
 */
public class Configuration{
	 
	private HashMap<Variant, VariationsAspect> variantsOfProduct = new HashMap<Variant, VariationsAspect>();
	private String sourceProjectFileName;
	private List<VariationsAspect> variationsAspects = new ArrayList<VariationsAspect>();
	
	/**
	 * Constructs a configuration. 
	 */
	public Configuration() {
	}

	/**
	 * Set the file name of the source (=cross-product) modeling project.
	 * 
	 * @param modelProjectFileName the file name of the model project (including full path)
	 */
	public void setSourceProjectFileName(String modelProjectFileName) {
		this.sourceProjectFileName  = modelProjectFileName;
	}
	
	/**
	 * Returns the file name (including the full path) of the source modeling project.
	 * 
	 * @return path to modeling project.
	 */
	public String getSourceProjectFileName(){
		return sourceProjectFileName;
	}
	
	/**
	 * Get the variants that are selected to be part of the product belonging to the specified VariationsAspect.
	 * This is a subset of all variants.
	 * 
	 * @return the variants that are contained in the product, belonging to the specified VariationsAspect
	 */
	public List<Variant> getVariantsOfProductByVariationsAspect(VariationsAspect variationsAspect){
		List<Variant> variantsOfProduct = new ArrayList<Variant>();
		for (Entry<Variant, VariationsAspect> variantToVariationsAspect : (this.variantsOfProduct).entrySet()) {
			if (variationsAspect.equals(variantToVariationsAspect.getValue())) {
				variantsOfProduct.add(variantToVariationsAspect.getKey());
			}
		}
		return variantsOfProduct;
	}

	/**
	 * Set the variants that are selected to be part of the product.
	 * Every Variant is then mapped to its corresponding Variations Aspect.
	 * This is a subset of all variants.
	 * 
	 * @param variants the variants that will be part of the product.
	 * @return the variants that are contained in the product, mapped to their corresponding Variations Aspect.
	 */
	
	public void setVariantsOfProduct(List<Variant> variants){
		this.variantsOfProduct = new HashMap<Variant, VariationsAspect>();
		for(Variant variant:variants){
			List<VariationsAspect> variationsAspectsList = this.variationsAspects;
			for (VariationsAspect variationsAspect:variationsAspectsList){
				for (Variation variationsIt:variationsAspect.getVariations()){
					List<Variant> allVariantsFromVariation = variationsIt.getVariants();
					if (allVariantsFromVariation.contains(variant)){
						// There is already available information about the children of the VariationsAspects
						// It is possible, therefore, to derive this information and associate a VariationsAspect to each Variant
						(this.variantsOfProduct).put(variant, variationsAspect);
					}
				}
			}
		}
	}
	
	/**
	 * Get the top-level variation aspects that contains
	 * the variations that contain the variants.
	 * 
	 * @return the variations aspects list.
	 */
	public List<VariationsAspect> getVariationsAspects() {
		return variationsAspects;
	}

	/** 
	 * Set the top-level variation aspects that contains
	 * the variations that contain the variants.
	 * 
	 * @param variationsAspects the top-level variation aspects list.
	 */
	public void setVariationsAspects (List<VariationsAspect> variationsAspects){
		this.variationsAspects = variationsAspects;
	}
	
	@Override
	public String toString(){
		return "Product Configuration";
	}
}
