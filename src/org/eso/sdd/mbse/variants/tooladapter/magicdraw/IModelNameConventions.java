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

/**
 * This interface contains the names of profiles and stereotypes that must
 * be used in the modeling tool in order to make the plugin work.
 * 
 * @author Bertil Muth
 *
 */
public interface IModelNameConventions {
	public final static String STEREOTYPE_VARIATIONSASPECT = "Variations Aspect";
	public final static String STEREOTYPE_VARIATION = "Variation";
	public final static String STEREOTYPE_VARIANT = "Variant";
	public final static String STEREOTYPE_SE2VARIATIONSASPECT = "se2.Variations Aspect";
	public final static String STEREOTYPE_SE2VARIATION = "se2.Variation";
	public final static String STEREOTYPE_SE2VARIANT = "se2.Variant";
}
