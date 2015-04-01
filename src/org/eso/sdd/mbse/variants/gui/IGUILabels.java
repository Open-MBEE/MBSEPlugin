package org.eso.sdd.mbse.variants.gui;
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


/*
 * @author Bertil Muth
 */
public interface IGUILabels  {

	String TITLE = "SE2: manageVariations";
	String VERSION = "V0.1.4";
	String AUTHORS = "Carlos Ortega, Bertil Muth";
	
	// Constants identifying the names of the menus.
	String MENU_FILE = "File";
	String MENU_HELP = "Help";
	
	// Constants identifying the names of the menu items.
	String MENUITEM_CREATE_PRODUCT_MODEL = "Create Product Model..";
	String MENUITEM_OPEN_CONFIGURATION = "Open Configuration..";
	String MENUITEM_SAVE_CONFIGURATION = "Save Configuration..";
	String MENUITEM_ABOUT = "About " + TITLE;
}
