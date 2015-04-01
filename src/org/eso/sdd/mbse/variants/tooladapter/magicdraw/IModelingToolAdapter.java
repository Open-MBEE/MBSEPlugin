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

import java.io.FileNotFoundException;
import java.util.Collection;

/**
 * This interface provides an adapter for the plugin to any UML/SysML modeling tool. 
 * 
 * The concrete types of objects used as input or output parameters depend on the modeling tool API, 
 * the only constraint this interface imposes is that the implementation uses the objects consistently.
 * 
 *  For example: each object returned by the method <code>getModelElementsByStereotype</code> is a potential
 *  input argument for the method <code>getModelElementID</code>, <code>getModelElementName</code> or <code>getModelElementQName</code>.
 *  
 * @author Bertil Muth
 *
 */
public interface IModelingToolAdapter {
	
	/**
	 * The separator for names in a qualified name.
	 */
	public static String QUALIFIED_NAME_SEPARATOR = "::";
	
	/**
	 * Gets all model elements in the currently active project that have the specified stereotype assigned.
	 * 
	 * @param stereotypeName the name of the stereotype.
	 * @return model elements that have the specified stereotype assigned.
	 */
	public Collection<? extends Object> getModelElementsByStereotype(String stereotypeName);
	
	/**
	 * Returns whether the model element has the specified stereotyped assigned.
	 * 
	 * @param modelElement the model element checked for its stereotypes.
	 * @param stereotypeName the name of the stereotype.
	 * 
	 * @return true if modelElement has stereotype <<stereotypeName>> assigned, false otherwise.
	 * @throws Exception 
	 */
	public boolean modelElementHasStereotype(Object modelElement, String stereotypeName);
	
	/**
	 * Gets the unique identifier of a model element.
	 * 
	 * @param modelElement the model element asked for its id.
	 * @return the id.
	 */
	public String getModelElementID(Object modelElement);
	
	/**
	 * Gets the (simple, non-qualified) name of a model element.
	 * 
	 * @param modelElement the model element asked for its name.
	 * @return the name.
	 */
	public String getModelElementName(Object modelElement);
	
	/**
	 * Gets the qualified name of a model element (= including the package containment hierarchy).
	 * 
	 * @param modelElement the model element asked for its qualified name.
	 * @return the qualified name.
	 */
	public String getModelElementQName(Object modelElement);
	
	/**
	 * Removes the specified model element from the model
	 * 
	 * @param modelElement the model element that needs to be removed.
	 */
	public void removeModelElement(Object modelElement) throws Exception;
	
	/**
	 * Loads the project specified by the file name. After that, the loaded project is considered "the active project".
	 *  
	 * @param fileName location of the project (full path, including folders).
	 * 
	 * @throws FileNotFoundException if the project file can't be opened.
	 * @throws Exception 
	 */
	public void loadProject(String fileName) throws Exception;
	
	/**
	 * Returns the currently active project.
	 * 
	 * @return the active project.
	 */
	public Object getActiveProject();
	
	/**
	 * Returns the file name of the currently active project.
	 * 
	 * @return the active project's file name, including the full path.
	 */
	public String getActiveProjectFileName();
	
	/**
	 * Returns the file name extension of projects.
	 * 
	 * @return the file name extension without any dots.
	 */
	public String getProjectFileNameExtension();
	
	/**
	 * Saves the currently active project to the file name's location. 
	 * If a file already exists at the specified location, it is silently overwritten.
	 *  
	 * @param fileName location where project is saved to (full path, including folders).
	 * 
	 */
	public void saveProject(String fileName) throws Exception;
}
