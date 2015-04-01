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
 *    $Id: InsertQueryAfterAction.java 646 2013-06-26 11:03:23Z mzampare $
 *
*/

package org.eso.sdd.mbse.doc.actions;

import java.awt.event.ActionEvent;

import org.eso.sdd.mbse.doc.algo.Utilities;

import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

@SuppressWarnings("serial")
public class InsertQueryAfterAction extends CreateQueryAction {
	
	public InsertQueryAfterAction() {
		super();
		setName("Insert Query After");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Tree tree = null; 
		Object userObject = null; 
		NamedElement father = null;
		Element el = null;
		Node node = null;
	
		tree = getTree();	
		if (tree.getSelectedNodes().length > 1) {
			return;
		}
		
		node = tree.getSelectedNodes()[0];
		userObject = node.getUserObject();
		
		if (userObject instanceof Element) {
			el = (Element) userObject;
			father = (NamedElement)el.getOwner();
			if (userObject instanceof Comment) {
				if( ! Utilities.isParagraph(el) && !Utilities.isQuery(el) 	) {
					Utilities.displayWarning("This is neither a paragraph nor a query");
					return;
				}
			} else {
				Utilities.displayWarning("This is not a Comment");
				return;
			}
			super.addQuery(father,el);
		
		} else {
			Utilities.displayWarning("This is not a named element");
		}
		

	}


}
