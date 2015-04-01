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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.domain.DomainObject;
import org.eso.sdd.mbse.variants.domain.Variant;
import org.eso.sdd.mbse.variants.domain.Variation;
import org.eso.sdd.mbse.variants.domain.VariationsAspect;
import org.eso.sdd.mbse.variants.gui.IGUILabels;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelNameConventions;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelingToolAdapter;

/*
 * Class that loads / saves a XML configuration file
 * and creates a configuration from it.
 * 
 * @author Bertil Muth
 * @author Carlos Ortega-Miguez: Updated to consider multiple VariationsAspect model elements instead of one root VariationsAspect.
 */
public class XMLConfigurationManager implements IConfigurationManager {

	private IModelingToolAdapter toolAdapter;

	public XMLConfigurationManager() {
	}
	
	/**
	 * Create a configuration by accessing a modeling tool and processing
	 * the variations of the currently opened model. 
	 * 
	 * @param toolAdapter the adapter to the modeling tool (e.g. MagicDraw)
	 */
	@Override
	public Configuration createConfiguration(IModelingToolAdapter toolAdapter)
			throws FileNotFoundException {
		this.toolAdapter = toolAdapter;
		List<VariationsAspect> variationsAspectList = createVariationsAspects();
		Configuration config = new Configuration();
		config.setVariationsAspects(variationsAspectList);
		String activeProjectFileName = toolAdapter.getActiveProjectFileName();
		config.setSourceProjectFileName(activeProjectFileName);
		return config;
	}
	
	/**
	 * Create a configuration by accessing a modeling tool, opening a
	 * model project and processing its contained variations. 
	 * 
	 * @param toolAdapter the adapter to the modeling tool (e.g. MagicDraw)
	 * @param projectFileName the file name of the model project.
	 */
	@Override
	public Configuration createConfiguration(IModelingToolAdapter toolAdapter,
			String projectFileName) throws Exception { 
		toolAdapter.loadProject(projectFileName);
		Configuration config = createConfiguration(toolAdapter);
		return config;
	}
	
	/** Crate a configuration by opening a XML configuration file that
	 * has been previously saved via the saveConfiguration method.
	 * 
	 * @param fileName the file name of the XML configuration file.
	 */
	@Override
	public Configuration openConfiguration(String fileName)
			throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(fileName);
		ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				Configuration.class.getClassLoader());
		Configuration conf = null;
		
		try{
			XMLDecoder decoder = new XMLDecoder(inputStream);
			conf = (Configuration) decoder.readObject();
			decoder.close();
		}finally{
			Thread.currentThread().setContextClassLoader(threadCL);
		}
		
		return conf;
	}

	/**
	 * Saves a configuration as a XML file.
	 * 
	 * @param fileName the full name (including path) of the XML file
	 * @param the configuration to be saved.
	 */
	@Override
	public void saveConfiguration(String fileName, Configuration config)
			throws FileNotFoundException {
		ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(
				Configuration.class.getClassLoader());

		FileOutputStream outputStream = new FileOutputStream(fileName);
		XMLEncoder encoder = new XMLEncoder(outputStream);
		encoder.writeObject(config);
		encoder.close();

		Thread.currentThread().setContextClassLoader(threadCL);
	}

	private List<VariationsAspect> createVariationsAspects() {
		List<VariationsAspect> variationsAspectList = new ArrayList<VariationsAspect>();
		// The set sortedModelElements will contain all model elements sorted by
		// the hierarchical level in the package hierarchy.
		PackageHierarchyLevelComparator comparator = new PackageHierarchyLevelComparator();
		TreeSet<Object> sortedModelElements = getSortedModelElements(comparator);

		// Now, set up a hash map that will keep track of all domain objects that will be created.
		// (For example: VariationsAspect, Variation, Variant)
		// Keeping track is managed by mapping the qualified name of the model element to the domain object itself. 
		// This way, parent -> child relationships between domain objects can be easily established.
		HashMap<String, DomainObject> domainObjects = new HashMap<String, DomainObject>();

		// Loop over all sorted model elements.
		for (Object modelElement : sortedModelElements) {
			// Get the qualified name of the parent model element.
			String qname = toolAdapter.getModelElementQName(modelElement);
			
			// Create a domain object for the model element.
			DomainObject domainObject = null;
			if(qname != null){
				domainObject = createDomainObject(modelElement);
			}
		
			// Access the parent domain object by its qname.
			DomainObject parentDomainObject = getParentDomainObject(domainObjects, modelElement);
			if (parentDomainObject != null) {
				// Add the child to the parent domain object
				parentDomainObject.addChildElement(domainObject);
			} else if (domainObject instanceof VariationsAspect) {
				// If there is no parent domain object, and the domain object is
				// a variations aspect, add it to the Variations Aspect list. 
				variationsAspectList.add((VariationsAspect) domainObject);
			} else { 
				System.out.println(
						IGUILabels.TITLE + ": ERROR: Model object " + qname + " does not have a correctly stereotyped parent element !");
			}

			// Save the domain object in the list of domain objects
			domainObjects.put(qname, domainObject);
		}

		if (variationsAspectList.isEmpty()){
			throw new RuntimeException(IGUILabels.TITLE
					+ ": ERROR: Can't find any variations aspect !");
		}
		return variationsAspectList;
	}
	
	private DomainObject getParentDomainObject(HashMap<String, DomainObject> domainObjects, Object modelElement){
		// Get the qualified name of the model element.
		String qname = toolAdapter.getModelElementQName(modelElement);
		// Get the qualified name of the parent model element.
		String parentQName = getQualifiedNameOfParent(qname);
		
		while(parentQName != null){
			// Access the parent domain object.
			DomainObject parentDomainObject = domainObjects.get(parentQName);
			if(parentDomainObject != null){
				return parentDomainObject;
			}
			// Move up further and further through the ancestors
			// and look for a parent.
			parentQName = getQualifiedNameOfParent(parentQName);
		}
		
		return null;
	}
	
	private String getQualifiedNameOfParent(String modelElementQName) {
		// Strip the name from the qualified name of the model element
		// to go one level up in the hierarchy of domain objects.
		int lastSeparatorIndex = 
				modelElementQName.lastIndexOf(IModelingToolAdapter.QUALIFIED_NAME_SEPARATOR);
		if(lastSeparatorIndex > 0){
			String parentQName = modelElementQName.substring(0, lastSeparatorIndex);
			return parentQName;
		}
		
		return null;
	}
	
	private DomainObject createDomainObject(Object modelElement){
		// Create a map that maps stereotypes of model elements
		// to their corresponding domain object.
		HashMap<String, DomainObject> stereotypeToDomainObjectMap = new HashMap<String, DomainObject>();
		DomainObject variant = new Variant();
		DomainObject variation = new Variation();
		DomainObject variationsAspect = new VariationsAspect();
		stereotypeToDomainObjectMap.put(IModelNameConventions.STEREOTYPE_VARIANT, variant);
		stereotypeToDomainObjectMap.put(IModelNameConventions.STEREOTYPE_VARIATION, variation);
		stereotypeToDomainObjectMap.put(IModelNameConventions.STEREOTYPE_VARIATIONSASPECT, variationsAspect);
		stereotypeToDomainObjectMap.put(IModelNameConventions.STEREOTYPE_SE2VARIANT, variant);
		stereotypeToDomainObjectMap.put(IModelNameConventions.STEREOTYPE_SE2VARIATION, variation);
		stereotypeToDomainObjectMap.put(IModelNameConventions.STEREOTYPE_SE2VARIATIONSASPECT, variationsAspect);
		
		// Get the id and the name of the model element
		String id = toolAdapter.getModelElementID(modelElement);
		String name = toolAdapter.getModelElementName(modelElement);
		
		// Get the domain object based on the model element's stereotype.
		DomainObject domainObj = null;
		for (String stereotypeName : stereotypeToDomainObjectMap.keySet()) {
			boolean modelElementHasStereotype = 
					toolAdapter.modelElementHasStereotype(modelElement, stereotypeName);
			if(modelElementHasStereotype){
				domainObj = stereotypeToDomainObjectMap.get(stereotypeName);
				domainObj.setId(id);
				domainObj.setName(name);
				domainObj.setStereotypeName(stereotypeName);
				return domainObj;
			}
		}
		
		return null;
	}
	
	TreeSet<Object> getSortedModelElements(Comparator<Object> comparator) {
		TreeSet<Object> modelElements = new TreeSet<Object>(comparator);
		// Access all variation aspect, variation and variants in the model.
		Collection<? extends Object> variationAspectModelElements = toolAdapter
				.getModelElementsByStereotype(IModelNameConventions.STEREOTYPE_VARIATIONSASPECT);
		Collection<? extends Object> variationModelElements = toolAdapter
				.getModelElementsByStereotype(IModelNameConventions.STEREOTYPE_VARIATION);
		Collection<? extends Object> variantModelElements = toolAdapter
				.getModelElementsByStereotype(IModelNameConventions.STEREOTYPE_VARIANT);
		Collection<? extends Object> variationAspectSE2ModelElements = toolAdapter
				.getModelElementsByStereotype(IModelNameConventions.STEREOTYPE_SE2VARIATIONSASPECT);
		Collection<? extends Object> variationModelSE2Elements = toolAdapter
				.getModelElementsByStereotype(IModelNameConventions.STEREOTYPE_SE2VARIATION);
		Collection<? extends Object> variantModelSE2Elements = toolAdapter
				.getModelElementsByStereotype(IModelNameConventions.STEREOTYPE_SE2VARIANT);
		modelElements.addAll(variationAspectModelElements);
		modelElements.addAll(variationModelElements);
		modelElements.addAll(variantModelElements);
		modelElements.addAll(variationAspectSE2ModelElements);
		modelElements.addAll(variationModelSE2Elements);
		modelElements.addAll(variantModelSE2Elements);
		return modelElements;
	}

	public final class PackageHierarchyLevelComparator implements Comparator<Object> {
		@Override
		public int compare(Object arg0, Object arg1) {
			// To compare two model elements concerning their position in
			// the package hierarchy, get their qualified names and then
			// count the path separators.
			String firstModelElementQName = toolAdapter
					.getModelElementQName(arg0);
			String secondModelElementQName = toolAdapter
					.getModelElementQName(arg1);
			String sep = IModelingToolAdapter.QUALIFIED_NAME_SEPARATOR;
			int firstNumberOfPathSeparators = countOccurrences(
					firstModelElementQName, sep);
			int secondNumberOfPathSeparators = countOccurrences(
					secondModelElementQName, sep);
			int compared = firstNumberOfPathSeparators
					- secondNumberOfPathSeparators;
			if (compared == 0) {
				compared = firstModelElementQName
						.compareTo(secondModelElementQName);
			}

			return compared;
		}

		/**
		 * Count number of occurrences of arg2 in arg1.
		 */
		private int countOccurrences(String arg1, String arg2) {
			int count = 0;
			int index = 0;
			while ((index = arg1.indexOf(arg2, index)) != -1) {
				++index;
				++count;
			}
			return count;
		}
	}
}
