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
 *    $Id: MBSEComputation.java 642 2013-06-25 15:04:17Z mzampare $
 *
*/

package org.eso.sdd.mbse.reasoner;

import javax.swing.JOptionPane;

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralReal;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralInteger;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralUnlimitedNatural;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;

public class MBSEComputation {

	private static boolean isNamedElement(Object element) { 
		if (element instanceof 
				com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) { 
			return true;
		}
		return false;
	}

	private static void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);

	}

	
	private static int getMultiplicity(Object element) { 
		if (element instanceof 
				com.nomagic.uml2.ext.magicdraw.classes.mdkernel.MultiplicityElement) { 
			int retVal = ((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.MultiplicityElement) element).getUpper();
			if (retVal == -1) { 
				return 0;
			} 
			return retVal;
		}
		return 1;

	}

	
	public static double computeValue(NamedElement nel, String attribute, StringBuffer theBuffer) { 
    	double theValue = 0.0f;
      	double childrenContrib = 0.0f;
      	double ownContrib = 0.0f;
    	//TODO check that the nel is a block
    	
    	//System.out.println("Acting on: " + nel.getName());
    	// System.out.println(" Type: " + nel.getHumanType());     	
    	if(nel.hasOwnedElement()) { 
    		for(Element ownedElement : nel.getOwnedElement()) { 
    			if ( isNamedElement(ownedElement) ) { 
    				String name = ((NamedElement)ownedElement).getName();
    				String humanType = ownedElement.getHumanType();
    				if(humanType.equals("Part Property")) { 

    					//displayWarning("5");
    					// here the simple computation
    					
    					if ( ownedElement instanceof Property) { 
    						NamedElement theType = ((Property)ownedElement).getType();
    							if (theType != null) {
    								double tmpVal = computeValue(theType, attribute , theBuffer);
    								int multi    = getMultiplicity(ownedElement) ; 
    								childrenContrib +=   multi *  tmpVal ;
    								//System.out.println(name + " is of  type: " + theType.getHumanName() + ", has multiplicity "+ multi + ", and value " + tmpVal);
    								String tmpString = new String("<tr><td>" + name + "</td><td>" + theType.getHumanName() + "</td><td>" + multi + "</td><td>" + tmpVal + "</td></tr>");

    								//String tmpString = new String("\'" + name + "\'" + " is of  type: " + theType.getHumanName() + ", has multiplicity "+ multi + ", and value " + tmpVal + "<br>\r\n");
    								theBuffer.append(tmpString);
    							} else { 
    								System.out.println("Type of "+name+" is empty!");
    							}
    					} else { 
    						//System.out.println(ownedElement.getHumanName()+ " is not a Property, alas");
    						
    					}
    					System.out.println("");	
    				} else if(humanType.equals("Value Property")) {

    					//displayWarning("4");
       					if(isNamedElement(ownedElement) && 
       							((NamedElement)ownedElement).getName().equals(attribute)) { 
       						if (ownedElement instanceof Property) {
       						// this is the termination condition for recursion
       							Object value = ((Property)ownedElement).getDefaultValue();
       							if(value instanceof LiteralString) {	
       								String valueString = ((LiteralString)value).getValue();
       						      try {
       						         double aValue = Float.valueOf(valueString.trim()).floatValue();
       						         //System.out.println("Found a price: " + price);
       						         ownContrib =  aValue;	
       						      } catch (NumberFormatException nfe) {
       						         System.out.println("NumberFormatException: " + nfe.getMessage());
       						      }       						       
       							} else if (value instanceof LiteralReal) {
       								ownContrib = ((LiteralReal)value).getValue();
       							} else if (value instanceof LiteralInteger) {
       								ownContrib = ((LiteralInteger)value).getValue();
       							} else if (value instanceof LiteralUnlimitedNatural) {
       								ownContrib = ((LiteralUnlimitedNatural)value).getValue();
       							}
       						}
       					}
    				} else if(humanType.equals("Connector"))  {

    					//displayWarning("3");
    					// ignoring Connectors
    				} else if(humanType.equals("Diagram"))  {

    					//displayWarning("2");
    					// ignoring diagrams
    				} else if(humanType.equals("Instance Specification"))  {
    					//displayWarning(ownedElement.getHumanName() + " s  " + humanType);
    						// neither value nor part property.... what is it?
    						System.out.println(name + " is neither Value Property not Part Property !! (" + humanType + ")");
    				}
    			} else { 

					//displayWarning(ownedElement.getHumanName() + " " + ownedElement.getHumanType());
    				// the element does not have a name, bad luck.
    				System.out.println("THIS ELEMENT HAS NO NAME " + ownedElement.toString());
    			} 
    		}
    	} // module has no owned element, not even a value property 

    	theValue = childrenContrib + ownContrib;
    	return theValue;
	}
}
