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



package org.eso.sdd.mbse.doc.algo;

import org.eso.sdd.mbse.doc.algo.Utilities;

public class Author {
	private String surName = "";
	private String firstName = "";
	private String organization = "";
	
	
	public Author() { 
		
	}
	
	
	public String getSurName() {
		return surName;
	}
	public void setSurName(String surName) {
		this.surName = surName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	// convenience method.
	private String encase(String prefix, String content) { 
		return "<"+prefix+">" + content + "</" + prefix + ">";
	}
	
	public String authorInfo(String role) {
		final String lE = System.getProperty("line.separator");

		String content = 	"<author role=\""+ role + "\">"+ lE + 
				encase("personname",
				encase("firstname", Utilities.convertHTML2DocBook(this.getFirstName(),true)) + lE +  
		        encase("surname",   Utilities.convertHTML2DocBook(this.getSurName(),true)  ) + lE ) + lE +
		        encase("affiliation", encase("orgname",Utilities.convertHTML2DocBook(this.getOrganization(),true))) + lE + 							
		"</author>" + lE;
		return content;
	}
}
