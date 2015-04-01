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

import java.util.Collection;
import java.util.Vector;

@SuppressWarnings("serial")
public class TreeFolder extends Vector<Object> {
	Object folderObject;

	public TreeFolder(){
		folderObject = new String("ROOT");
	}
	
	public TreeFolder(Object folderObject) {
		this.folderObject = folderObject;
	}

	public void addCheckboxNodes(Collection<? extends Object> elements) {
		for (Object object : elements) {
			// Create a check box for the element and add it to the vector.
			CheckBoxNode cb = new CheckBoxNode(object, false);
			add(cb);
		}
	}
	
	public CheckBoxNode addCheckboxNode(Object object, boolean isChecked)  {
		// Create a check box for the element and add it to the vector.
		CheckBoxNode cb = new CheckBoxNode(object, isChecked);
		add(cb);
		return cb;
	}
	
	@Override
	public String toString() {
		return folderObject.toString();
	}
}