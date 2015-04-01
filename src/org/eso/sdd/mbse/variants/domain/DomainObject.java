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


/*
 * @author Bertil Muth
 */
public abstract class DomainObject {
	private String id;
	private String name;
	private String stereotypeName;
	
	public DomainObject(){
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract boolean addChildElement(DomainObject element);

	public String getStereotypeName() {
		return stereotypeName;
	}

	public void setStereotypeName(String stereotypeName) {
		this.stereotypeName = stereotypeName;
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof DomainObject)) return false;
		
		String otherObjId = ((DomainObject)o).getId();
		return (otherObjId == null && id==null) ||
				(otherObjId.equals(id));
	}
}
