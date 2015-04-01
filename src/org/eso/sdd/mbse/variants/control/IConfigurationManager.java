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

import java.io.FileNotFoundException;

import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.IModelingToolAdapter;

/*
 * Interface for creating variation configurations based on the
 * data in a (modeling) tool or a configuration file.
 * 
 * @author Bertil Muth
 */
public interface IConfigurationManager {
	/**
	 * Constructs a configuration. 
	 * The currently active project is used as source of information.
	 * It is accessed via the specified tool adapter.
	 * 
	 * @param toolAdapter the adapter to the modeling tool.
	 */
	public Configuration createConfiguration(IModelingToolAdapter toolAdapter) throws FileNotFoundException ;
	
	/**
	 * Constructs a configuration. The project used as source of information
	 * is accessed via the specified tool adapter.
	 * 
	 * @param toolAdapter the adapter to the modeling tool.
	 * @param fileName the full path of the model project file.
	 * @throws Exception 
	 */
	public Configuration createConfiguration(IModelingToolAdapter toolAdapter, String fileName) throws FileNotFoundException, Exception ;
	
	/**
	 * Opens a configuration file and creates a configuration from it.
	 * 
	 * @param fileName the full path to the configuration file.
	 * 
	 * @return the configuration
	 * 
	 * @throws FileNotFoundException
	 */
	public Configuration openConfiguration(String fileName) throws FileNotFoundException ;
	
	/**
	 * Saves a configuration to a file.
	 * 
	 * @param fileName the full path to the configuration file.
	 * @param config the configuration to be saved.
	 * 
	 * @throws FileNotFoundException
	 */
	public void saveConfiguration(String fileName, Configuration config) throws FileNotFoundException ;
	
}
 