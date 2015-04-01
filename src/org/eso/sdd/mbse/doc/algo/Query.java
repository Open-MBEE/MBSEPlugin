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
 *    $Id: Query.java 697 2014-10-28 16:31:47Z mzampare $
 *
*/

package org.eso.sdd.mbse.doc.algo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.Interface;
import com.nomagic.uml2.ext.magicdraw.classes.mdinterfaces.InterfaceRealization;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Constraint;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Enumeration;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.OpaqueExpression;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdbasicbehaviors.BehavioredClassifier;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdcommunications.Reception;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import org.eso.sdd.mbse.doc.algo.Utilities;

public class Query {
	String queryType = "";
	boolean tableRep = false;
	Utilities.TEXTUSAGEKIND useText;
	boolean showQueriedElementDocumentation = false;
	boolean showTypesDocumentation = false;
	boolean showPropertiesDocumentation = false;
	boolean showDefaultValue = false;
	boolean showQualifiedName = false;
	private static Utilities  theUtilities = null;
	boolean Debug = false;
	List<?> referenced  = null;
	Element el = null;
	public String lE = Utilities.lE;
	Stereotype theQueryStereotype = null;

	private static Property thePropertiesProperty = null;
	
	
	
	public Query (Element theQueryElement, boolean theDebug){ 
		theUtilities = new Utilities();
		el = theQueryElement;
		referenced = StereotypesHelper.getStereotypePropertyValue(el ,theUtilities.getTheQueryStereotype(), "element");
		queryType         = getQueryType(el);
		tableRep          = isQueryRepTable(el);
		useText           = theUtilities.isTextToBeUsed(el,theUtilities.getTheQueryStereotype(), "useQueryText");
		showQueriedElementDocumentation = isDocToBeShown(el);
		showTypesDocumentation      = isTypesDocToBeShown(el);
		showPropertiesDocumentation = isPropertiesDocToBeShown(el);
		showDefaultValue = isDefaultValueToBeShown(el);
		showQualifiedName = isQualifiedNameToBeShown(el);
		Debug = theDebug;

		if(el == null) { 
			Utilities.displayWarning("Empty query element!");
		}
	}
	

	public String provideDocBookForQuery() { 
		String content = "";
		if (Debug) { System.out.println("  is Query  TYPE: " + 
				getQueryType(el) +" REP " +  ((tableRep)?"table":"Para") + " size: " + referenced.size()  );}

		if (referenced.size() == 0) {
			if (Debug) {
				// this generates an execption if the body is shorter than 20
				// chars - must be handled.
				//shorter than 20 have been handled
				int l = ((Comment) el).getBody().length();

				if (l > 20) {

					l = 20;
					System.out
							.println("  this query does not reference any element ("
									+ ((Comment) el).getBody().substring(1, l)
									+ ")");
				} else {
					System.out
							.println("  this query does not reference any element ("
									+ ((Comment) el).getBody() + ")");
				}
			}
		} else {
			System.out.println("");
		}
		content += "<para annotations=\"query " + queryType +  " format=" + ((tableRep)?"table":"Para") + 
		  " showQueriedElementDocumentation="+ 		showQueriedElementDocumentation + 
		" showTypesDocumentation="+  		showTypesDocumentation + 
		" showPropertiesDocumentation="+ 		showPropertiesDocumentation + 
		" showDefaultValue="+ 		showDefaultValue +"\">" + lE ;
		

		if(useText == Utilities.TEXTUSAGEKIND.before) { 
			content +=  Utilities.convertHTML2DocBook(((Comment)el).getBody(), true) + lE;
		} 
		content += "</para>" + lE; // moved here from the end to avoid having paras within paras
		// note: DocBook seems to allow a para in a para

		boolean tableHeaderDone = false;
		for (Object refElement : referenced) {
			if(refElement != null) {
				// special handling
				if(! (refElement instanceof NamedElement) ) {
					System.out.println("\tWARNING: referenced element is not a NamedElement type but a "+ 
							refElement.getClass().toString());
					continue;
				}
				NamedElement namedRefEl = (NamedElement)refElement;
				if(Debug) { 
					System.out.println("\treferenced element "+Utilities.replaceBracketCharacters (namedRefEl.getName()) + " [" + queryType +"]");				
				}

				// if the named referenced element is a requirement, it is handled and such and the value of queryType is 
				// entirely ignored.

				if( Utilities.isRequirement(namedRefEl) ) {
					if (tableRep && !tableHeaderDone) {
						content += "<table frame=\"all\"><title>"+ Utilities.replaceBracketCharacters (namedRefEl.getName()) + "</title>" +
								"<tgroup cols=\"3\"><thead><row><entry>ReqID</entry>" + 
								"<entry>ReqName</entry><entry>Text</entry></row></thead>" + 
								lE + "<tbody>";
						tableHeaderDone = true;
					}
					content += processRequirement(namedRefEl);
					continue;
				}

				// DOCUMENTATION
				
				if(queryType.equals("documentation")) { 
					if(tableRep) {
						if (!tableHeaderDone) {
							content +=  "<table><tgroup  cols=\"2\"><tbody>" + lE;
							tableHeaderDone = true;
						}
						content += "<row>";
						content += entryReplaceBrackets(namedRefEl.getName()) + lE;
						content += entryReplaceBrackets(Utilities.getDocumentation(namedRefEl)) + lE;
						content += "</row>" + lE;
						
					} else {
						// GENERIC ELEMENT INFO
						// representation attribute ignored in this case
						content +=  "<emphasis role=\"bold\">" + Utilities.replaceBracketCharacters(namedRefEl.getName()) + "</emphasis>";
						if(showQualifiedName) { 
							content += "<emphasis> ( " + Utilities.replaceBracketCharacters(namedRefEl.getQualifiedName()) + " )  </emphasis>" + lE;
						}
						content += "<para annotations=\"finished doc\">" + lE;

						if(Utilities.hasDocumentation(namedRefEl)) { 
							content += Utilities.getDocumentation(namedRefEl) + lE;
						}
						content += "</para><para></para>" + lE;
					}
				} else if(namedRefEl.hasOwnedElement()) {

					if(queryType.equals("operations")) { 	
						content += processOperations(namedRefEl);
					} else if(queryType.equals("properties")) {
						content += processProperties(namedRefEl);						
					} else if(queryType.equals("ports")) { 	
						content += processPorts(namedRefEl);
					} else if(queryType.equals("constraintProperties")) { 	
						content += processConstraintProperties(namedRefEl);					
					} else if(queryType.equals("constraints")) { 	
						content += processConstraints(namedRefEl);					
					} else if(queryType.equals("flowProperties")) { 	
						content += processFlowProperties(namedRefEl);					
					} else if(queryType.equals("partProperties")) { 	
						content += processPartProperties(namedRefEl);					
					} else if(queryType.equals("referenceProperties")) { 	
						content += processReferenceProperties(namedRefEl);					
					} else if(queryType.equals("valueProperties")) { 	
						content += processValueProperties(namedRefEl);					
					} 
					
					// ACTIVITIES

				} else { // no owned elements 
					// content += "this referenced object does not seem to have any owned elements.."+ lE;
				} // end if no owned elements

			} // referenced element not null
		} // end loop over all referenced elements
		if(tableRep && queryType.equals("documentation") && tableHeaderDone) {
			content += "</tbody></tgroup></table>" + lE;
		}
		//content += "</para>" + lE; // moved up to avoid having paras in paras
		return content;
	}
	
	
	
	private String processConstraintProperties(NamedElement namedRefEl) {
		String tablePrefix= "";
		String cpContent = "";
		boolean cpFound = false;
		String content = "";

		//tablePrefix = "<para annotations=\"title for table\"><emphasis role=\"bold\">" + 
		//namedRefEl.getName() + "</emphasis></para>" + lE;
		if(showQueriedElementDocumentation) { 
			//tablePrefix  += "<tgroup annotation=\"element docu\">"+ lE + 
			// getDocumentation(namedRefEl) + lE + "</para>";
			tablePrefix += "<para annotations=\"\">"+ lE + 
			Utilities.getDocumentation(namedRefEl) + lE + "</para>";									
		}

		tablePrefix += tableHeader(namedRefEl, " Constraint Properties", "Constraint");


		if(tableRep) { 
			cpContent += tablePrefix + "<tbody>" + lE;
		}
		for(Element ownedElement : namedRefEl.getOwnedElement()) {
			// System.out.println("owned element: " + ownedElement.getHumanName()  );
			if(ownedElement.getHumanType().equals("Constraint Property")) { 
				NamedElement theType = ((TypedElement)ownedElement).getType();
				cpFound = true;
				if(tableRep) { 
					cpContent += "<row>";
					// part name
					cpContent += entryReplaceBrackets(((NamedElement)ownedElement).getName()) + lE;
					// part type
					if(  theType != null ) { 
						cpContent += entryReplaceBrackets(theType.getName()) +   lE; 						
					} else {
						cpContent += entryReplaceBrackets("UNDEFINED") + lE;
					}
					cpContent += addDocumentationForPropertyAndType(ownedElement, theType);
					if(showDefaultValue){
					cpContent += getDefaultValue() +   lE;
					}
					cpContent += "</row>" + lE;
				} else {
					cpContent += Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "," + 
							Utilities.replaceBracketCharacters(((TypedElement)ownedElement).getType().getName())+ " ";
				}

			}
		} // end loop over owned elements
		if(tableRep) { 
			cpContent += "</tbody></tgroup></table>" + lE;
		}
		if(cpFound) { 
			content += cpContent;
		} else {
			System.out.println("\tWARNING: no Constraint Property found for this referenced element");									
		}
		
		return content;
	}

	private String processConstraints(NamedElement namedRefEl) {
		String tablePrefix= "";
		String csContent = "";
		boolean csFound = false;
		String content = "";

		//tablePrefix = "<para annotations=\"title for table\"><emphasis role=\"bold\">" + 
		//namedRefEl.getName() + "</emphasis></para>" + lE;
		if(showQueriedElementDocumentation) { 
			//tablePrefix  += "<tgroup annotation=\"element docu\">"+ lE + 
			// getDocumentation(namedRefEl) + lE + "</para>";
			tablePrefix += "<para annotations=\"\">"+ lE + 
			Utilities.getDocumentation(namedRefEl) + lE + "</para>";									
		}

		tablePrefix += 
			"<table frame=\"all\"><title>"+ Utilities.replaceBracketCharacters(namedRefEl.getName()) + " Constraints</title>";
		// showDocumentation


		tablePrefix += "<tgroup cols=\"2\">" + 
		"<thead><row><entry>Name</entry>" + 
		"<entry>Constraint</entry></row></thead>";

		if(tableRep) { 
			csContent += tablePrefix + "<tbody>" + lE;
		}
		for(Element ownedElement : namedRefEl.getOwnedElement()) {
			// System.out.println("owned element: " + ownedElement.getHumanName()  );
				if(ownedElement instanceof Constraint) { 
					Constraint theConstraint = (Constraint)ownedElement;
					OpaqueExpression theSpec = ((OpaqueExpression)theConstraint.getSpecification());
					String theCsText = theSpec.getBody().iterator().next(); // get first element
					System.out.println("\t"+  theCsText );

					csFound = true;					    				
					if(tableRep) { 
						csContent += "<row>";
						csContent += entryReplaceBrackets(((NamedElement)ownedElement).getName()) + lE;
						csContent += entryReplaceBrackets(theCsText)+ lE;					    										    					
						csContent += "</row>" + lE;

					} else {
						csContent +=  Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "<function>"  + theCsText + "</function>, ";
					}
			}
		} // end loop over owned elements
		if(tableRep) { 
			csContent += "</tbody></tgroup></table>" + lE;
		}
		if(csFound) { 
			content += csContent;
		} else {
			System.out.println("\tWARNING: no Constraint found for this referenced element");									
		}
		
		return content;
	}


	
	private String processOperations(NamedElement namedRefEl) { 
		String tablePrefix= "";
		String opContent = "";
		boolean opFound = false;
		String content = "";
		tablePrefix = "<table frame=\"all\"><title>"+ Utilities.replaceBracketCharacters(namedRefEl.getName()) + 
		"</title>";
		if(showQueriedElementDocumentation) { 
			tablePrefix  += "<para annotation=\"element docu\">"+ lE + 
				Utilities.getDocumentation(namedRefEl) + lE + "</para>";
		}
		tablePrefix += "<tgroup cols=\"2\"><thead><row><entry>Operation</entry>" + 
		"<entry>Description</entry></row></thead>";
		
		if(tableRep) { 
			opContent += tablePrefix + "<tbody>" + lE;
		}
		for(Element ownedElement : namedRefEl.getOwnedElement()) { 
			//System.out.println("owned element: " + ownedElement.getHumanName()  );

			if(ownedElement.getHumanType().equals("Operation") ) {
				opFound = true;
				if(tableRep) { 
					opContent += "<row>";
					opContent += "<entry>" + Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "</entry>" + lE;
					if(Utilities.hasDocumentation(ownedElement)) { 
						opContent += "<entry>" + Utilities.getDocumentation(ownedElement);
						opContent += "</entry>" + lE;
					}
					opContent += "</row>" + lE;
				} else {
					opContent += "<function>" +  Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "</function>" ;
						// (hasDocumentation(ownedElement)?getDocumentation(ownedElement):"");
				}
			} else {
				//content += "<entry>" + "unknown type "+  ownedElement.getHumanName() + "</entry>" + lineEnd;					    				
			}
		}
		if(tableRep) { 
			opContent += "</tbody></tgroup></table>" + lE;
		}
		if(opFound) {
			content += opContent;
		} else {
			System.out.println("\tWARNING: no Operations found for this referenced element");									
		}
		return content;
		
	}
	
	
	private String processPorts(NamedElement namedRefEl) throws NullPointerException { 
		String tablePrefix= "";
		String cpContent = "",poContent = "";
		boolean cpFound = false ,poFound = false;
		String content = "";
		// PORTS
		//tablePrefix = "<para annotations=\"title for table\"><emphasis role=\"bold\">" + 
		//namedRefEl.getName() + "</emphasis></para>" + lE;
		if(showQueriedElementDocumentation && Utilities.hasDocumentation(namedRefEl)) { 
			tablePrefix += "<para annotations=\"element docu\">"+ lE + 
			Utilities.getDocumentation(namedRefEl) + lE + "</para>";									
		}

		tablePrefix += 
			"<table frame=\"all\"><title>"+ Utilities.replaceBracketCharacters(namedRefEl.getName()) ;


		if(Utilities.isConstraintBlock(namedRefEl)) {
			//ownedElement.getHumanType().equals("Constraint Property") ||
			tablePrefix += " Parameters</title>";
			
			tablePrefix += "<tgroup cols=\"3\"><thead><row><entry>Parameter</entry>" + 
			"<entry>Type</entry><entry>Description</entry></row></thead>";
			if(tableRep) { 
				cpContent += tablePrefix + "<tbody>" + lE;
			}
			
			for(Element ownedElement : namedRefEl.getOwnedElement()) {
				//System.out.println("owned element: " + ownedElement.getHumanName()  );
				if(	ownedElement.getHumanType().equals("Constraint Parameter") 	) {
					cpFound = true;
					//Port myPort = (Port)ownedElement;
					if(tableRep) { 
						cpContent += "<row>";
						cpContent += entryReplaceBrackets(((NamedElement)ownedElement).getName()) + lE;
						cpContent += entryReplaceBrackets(((TypedElement)ownedElement).getType().getName()) +   lE; 
						
						if(Utilities.hasDocumentation(ownedElement)) { 
							cpContent += entryReplaceBrackets( Utilities.getDocumentation(ownedElement)) + lE;
						}
						
						if(showDefaultValue){
							cpContent += getDefaultValue() +   lE;
							}
						cpContent += "</row>" + lE;
					} else {
						cpContent += Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "," + 
								Utilities.replaceBracketCharacters(((TypedElement)ownedElement).getType().getName())+ " ";
					}
				} else {
					// we're only interested in CP at this stage, so we do nothing here
				}

			}
			if(tableRep) { 
				cpContent += "</tbody></tgroup></table>" + lE;
			}
			if(cpFound) { 
				content += cpContent;
			} else {
				System.out.println("\tWARNING: no Constraint Properties found for this referenced element");
			}

		} else { // it is a 'normal' block.
			//tablePrefix += "<tgroup cols=\"3\"><thead><row><entry>Port</entry>" + 
			//"<entry>Type</entry><entry>Description</entry></row></thead>";
			//tablePrefix += " Ports</title>";
			tablePrefix = tableHeader(namedRefEl,"Ports","Port");
			
			if(tableRep) { 
				poContent += tablePrefix + "<tbody>" + lE;
			}
			for(Element ownedElement : namedRefEl.getOwnedElement()) {
				// System.out.println("owned element: " + ownedElement.getHumanName()  );
				if(ownedElement.getHumanType().equals("Port") ||
						ownedElement.getHumanType().equals("Flow Port") ||
						ownedElement.getHumanType().equals("Proxy Port") ||
						ownedElement.getHumanType().equals("Full Port")) {
					poFound = true;					    				
					Port myPort = (Port)ownedElement;
					NamedElement theType = myPort.getType();
					if(tableRep) { 
						poContent += "<row>";
						// port name
						poContent += entryReplaceBrackets(((NamedElement)ownedElement).getName()) + lE;
						// port type
						if(theType != null) { 
							poContent += entryReplaceBrackets(theType.getName()) + lE;					    					
						} else { 
							poContent += entryReplaceBrackets("UNDEFINED") + lE;					    										    					
						}
						
						poContent += addDocumentationForPropertyAndType(ownedElement,theType);						
						
						if(showDefaultValue){
							poContent += getDefaultValue() +   lE;
							}
						
						poContent += "</row>" + lE;
					} else {
						poContent += "<function>" + Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "</function>, ";
					}
				} else {
					// we do not care about anything else but ports here
				}
			} // end loop over owned elements
			if(tableRep) { 
				poContent += "</tbody></tgroup></table>" + lE;
			}
			if(poFound) { 
				content += poContent;
			} else {
				System.out.println("\tWARNING: no Ports found for this referenced element");									
			}
		} // end case 'normal' Block
		return content;
	}
	
	
	private String processProperties(NamedElement namedRefEl) { 
		String tablePrefix= "";
		String opContent = "";
		boolean opFound = false;
		String content = "";
		// PROPERTIES
		List<?> requestedPropList = getQueryPropertiesToBeShown(el);
		Enumeration tmpEnum = null;
		List<EnumerationLiteral> profileProps = null;

		if(requestedPropList.size() == 0) { 
			System.out.println(">>> Query type is properties but seemingly no desired properties have been listed"  );									
		}

		theQueryStereotype     = theUtilities.getTheQueryStereotype();
		thePropertiesProperty  = StereotypesHelper.getDefinedPropertyByName(theUtilities.getTheQueryStereotype(), "property",false);
		if(thePropertiesProperty != null) { 
			tmpEnum = (Enumeration)(thePropertiesProperty.getType());
			profileProps = tmpEnum.getOwnedLiteral();
		} else {
			System.out.println(">>> Nothing I can do with this query, the Property from queryStereotype is null");
			return "";
		}


		tablePrefix = "<table frame=\"all\"><title>"+ Utilities.replaceBracketCharacters(namedRefEl.getName()) + "</title><tgroup cols=\"2\">";
		if(tableRep) { 
			content += tablePrefix + lE + "<tbody>";
		}

		// loop over requested properties
		
		if(requestedPropList.size() == 0 ) { 
			System.out.println("WARNING: empty list of requested properties for query pointing to  "+ Utilities.replaceBracketCharacters (namedRefEl.getName()));
		}
		for (int kk = 0; kk < requestedPropList.size(); kk++) {
			String requestedPropString = null;
			Object propObj = requestedPropList.get(kk);
			requestedPropString = Utilities.replaceBracketCharacters(((EnumerationLiteral)propObj).getName());

			if(propObj instanceof EnumerationLiteral) {

				// loop over possible known properties, as obtained from the enumeration in the DocBook 
				// Profile
				for(int jj = 0; jj < profileProps.size(); jj++) {
					String profilePropName = Utilities.replaceBracketCharacters (profileProps.get(jj).getName());
					System.out.println(">>> Checking for property " + profilePropName + " (" + requestedPropString + ")");
					if(requestedPropString.equals( profilePropName )) { 
						if(tableRep) { 
							content += "<row><entry><function>"+ profilePropName+ "</function></entry>" ;
						} else { 
							content += "<emphasis role=\"bold\">" + profilePropName + " " ;
						}
						// unfortunately still some hard-coded names are needed in here
						if(profilePropName.equals("owner")) { 
							String result = namedRefEl.getOwner().getHumanName() ;
							if(tableRep) { 
								content += "<entry>" +  result + "</entry>" + lE;
							} else { 
								content += result; 
							}
						}
						if(profilePropName.equals("baseClassifier")) { 
							if(namedRefEl instanceof Class) {
								String result = new String("");
								Collection<Class> tmpColl =  ((Class)namedRefEl).getSuperClass();
								for(Class c : tmpColl)  {
									result += Utilities.replaceBracketCharacters(c.getName())+ " ";
								}
								if(tableRep) { 
									content += "<entry>" + result +  "</entry>" + lE;
								} else { 
									content += result;
								}
							}
						}
						if(profilePropName.equals("realizedInterface")) {
							opContent = "";
							String srContent = "";
							boolean srFound   = false;
							opFound = false;
							// THIS IS NOT THE RIGHT METHOD @TODO;
							String result = "";
							if(namedRefEl instanceof BehavioredClassifier) { 
								Collection<InterfaceRealization> bcColl = ((BehavioredClassifier)namedRefEl).getInterfaceRealization();
								for(Iterator<InterfaceRealization> itxx = bcColl.iterator(); itxx.hasNext(); ) { 
									Interface iFace = itxx.next().getContract() ;   
									System.out.println("^^^ Interface found: " + Utilities.replaceBracketCharacters(iFace.getName()));

									tablePrefix = "<table frame=\"all\">" + lE + "<tgroup cols=\"2\">"+lE+"<thead><row>"+lE+"<entry>Operation</entry>" + 
									"<entry>Description</entry>"+lE+"</row></thead>";

									// operations within an interface
									if(tableRep) { 
										opContent += tablePrefix + "<tbody>" + lE;

									}
									for(Element ownedElement :iFace.getOwnedOperation()) { 
										//System.out.println("owned element: " + ownedElement.getHumanName()  );

										if(ownedElement.getHumanType().equals("Operation") ) {
											opFound = true;
											String resultOP = Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()); 
											System.out.println("^^^ operation found: " +   resultOP  );																		
											if(tableRep) { 
												opContent += "<row>";
												opContent +=  entryReplaceBrackets(resultOP) + lE;

												if(Utilities.hasDocumentation(ownedElement)) { 
													opContent += entryReplaceBrackets( Utilities.getDocumentation(ownedElement)) + lE;
												}
												opContent += "</row>" + lE;
											} else {
												opContent += "<function>" +  resultOP + "</function>" + lE;
												// (hasDocumentation(ownedElement)?getDocumentation(ownedElement):"");
											}
										} else {
											//content += "<entry>" + "unknown type "+  ownedElement.getHumanName() + "</entry>" + lineEnd;					    				
										}
									}

									tablePrefix = "<table frame=\"all\">" + lE + "<tgroup cols=\"2\">"+lE+"<thead><row>"+lE+"<entry>Reception</entry>" + 
									"<entry>Description</entry>"+lE+"</row></thead>";
									if(tableRep) { 
										opContent += "</tbody></tgroup></table>" + lE;
										srContent += tablePrefix + "<tbody>" + lE;
									}
									if(opFound) { 
										content += opContent;
									}
									// reception within an interface

									for(Reception ownedElement : iFace.getOwnedReception()) { 
										//System.out.println("owned element: " + ownedElement.getHumanName()  );

										if(ownedElement.getHumanType().equals("Signal Reception") ) {
											srFound = true;
											String resultSR = Utilities.replaceBracketCharacters(ownedElement.getSignal().getName());
											System.out.println("^^^ Reception found: " +   resultSR   );
											if(tableRep) { 
												srContent += "<row>" + lE;
												srContent += entryReplaceBrackets( resultSR ) + lE;

												if(Utilities.hasDocumentation(ownedElement)) { 
													srContent += entryReplaceBrackets(Utilities.getDocumentation(ownedElement)) + lE;
												}
												srContent += "</row>" + lE;
											} else {
												srContent += "<function>" +  resultSR + "</function>" ;
												// (hasDocumentation(ownedElement)?getDocumentation(ownedElement):"");
											}
										} else {
											//content += "<entry>" + "unknown type "+  ownedElement.getHumanName() + "</entry>" + lineEnd;					    				
										}
									}
									if(tableRep) { 
										srContent += "</tbody>"+lE+"</tgroup>"+ lE + "</table>" + lE;
									}
									if(srFound) { 
										content += srContent;
									}
								} //end loop over interface realization
							} else {
								System.out.println("Idiot! it's not well mannered!");
							}
							if(tableRep) { 
								content += entryReplaceBrackets(result) + lE;
							} else { 
								content += result;
							}
						} // end case realized interface
						if(profilePropName.equals("classifierbehavior")) { // notice the case error
							// THIS IS NOT THE RIGHT METHOD @TODO;
							String result = "NOT IMPLEMENTED YET";

							if(tableRep) { 
								content += entryReplaceBrackets(result)+ lE;
							} else { 
								content += result;
							}
						}
				
						if(tableRep) { 
							content += "</row>" + lE;
						} else { 
							content += "</emphasis>"  + lE;
						}
					}
				} // end loop over all properties available in the profile.
			} else {  // propObj is not an instance of EnumerationLiteral
				System.out.println(">>> getQueryPropertiesToBeShown returned something which is not a EnumerationLiteral ("+propObj.getClass().getName()+ ")");										
			}
		} // for (int kk = 0; kk < requestedPropList.size(); kk++)
		if(tableRep) { 
			content += "</tbody></tgroup></table>" + lE;
		}
		return content;
	}
	
	private String processRequirement(NamedElement namedRefEl) { 
		String reqName, reqId,  reqText = null;
		String content = "";
		reqText = ((String)StereotypesHelper.getStereotypePropertyFirst(namedRefEl, theUtilities.getTheRequirementStereotype(), "Text"));
		reqId   = ((String)StereotypesHelper.getStereotypePropertyFirst(namedRefEl, theUtilities.getTheRequirementStereotype(), "Id"));
		reqName = Utilities.replaceBracketCharacters(namedRefEl.getName());

		if(tableRep) { 
			String tablePrefix = "";
			//tablePrefix += "<table frame=\"all\"><title>"+ namedRefEl.getName() + "</title>" +
			//"<tgroup cols=\"3\"><thead><row><entry>ReqID</entry>" + 
			//"<entry>ReqName</entry><entry>Text</entry></row></thead>" + 
			//lE + "<tbody>";
			content += tablePrefix + "<row><entry>" + reqId + "</entry><entry>" + reqName + 
			"</entry><entry>" + reqText  + "</entry></row>" + lE;
			//content += "</tbody></tgroup></table>" + lE;
		} else {
			// we're showing many less attributes of a requirement than in table mode
			content +=   reqId + "," + "\"" + reqText + "\"" + "<para></para>";
		}
		// here we assume that if it is a requirement other features like ports, constraints 
		// and that like do not need to be documented.
		return content;
		
	}
	

	
	private String processFlowProperties(NamedElement namedRefEl) { 
		String tablePrefix= "";
		String content = "";
		boolean fpFound = false;
		String fpContent = "";
		
		
		//tablePrefix = "<para annotations=\"title for table\"><emphasis role=\"bold\">" + 
		//namedRefEl.getName() + "</emphasis></para>" + lE;
		if(showQueriedElementDocumentation && Utilities.hasDocumentation(namedRefEl)) { 
			tablePrefix += "<para annotations=\"element docu\">"+ lE + 
			Utilities.getDocumentation(namedRefEl) + lE + "</para>";									
		}

		tablePrefix += tableHeader(namedRefEl,"Flow Properties","Property");
		
		if(tableRep) {
			fpContent += tablePrefix + "<tbody>" + lE;
		}
		
		for(Element ownedElement : namedRefEl.getOwnedElement()) {
			if(ownedElement.getHumanType().equals("Flow Property")	) {
				fpFound = true;
				String direction = "";
				direction = Utilities.getFirstElementString(ownedElement, theUtilities.getTheFlowPropertyStereotype(), "direction");
				//Port myPort = (Port)ownedElement;
				NamedElement theType = ((TypedElement)ownedElement).getType();
				if(tableRep) { 
					fpContent += "<row>";
					fpContent += entryReplaceBrackets(((NamedElement)ownedElement).getName()) + lE;
					if(  theType!= null ) { 
						fpContent += entryReplaceBrackets(theType.getName()) +   lE; 						
					} else {
						fpContent += entryReplaceBrackets("UNDEFINED") + lE;
					}
					fpContent += entryReplaceBrackets(direction) + lE;
					fpContent += addDocumentationForPropertyAndType(ownedElement,theType);
					if(showDefaultValue){
						fpContent += getDefaultValue() +   lE;
						}
					fpContent += "</row>" + lE;
				} else {
					fpContent += Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "," +	Utilities.replaceBracketCharacters(theType.getName())+ " ";
				}
			} else {
				// we're only interested in FP at this stage, so we do nothing here
			}
		}
		if(tableRep) { 
			fpContent += "</tbody></tgroup></table>" + lE;
		}

		if(fpFound) { 
			content += fpContent;
		} 
		
		return content;
	}
	
	private String processPartProperties(NamedElement namedRefEl) { 
		String tablePrefix= "";
		String content = "";
		boolean ppFound = false;
		String ppContent = "";
		
		//tablePrefix = "<para annotations=\"title for table\"><emphasis role=\"bold\">" + 
		//namedRefEl.getName() + "</emphasis></para>" + lE;
		if(showQueriedElementDocumentation && Utilities.hasDocumentation(namedRefEl)) { 
			tablePrefix += "<para annotations=\"element docu\">"+ lE + 
			Utilities.getDocumentation(namedRefEl) + lE + "</para>";									
		}

	
		tablePrefix += tableHeader(namedRefEl, " Part Properties", "Part");
		
		if(tableRep) { 
			ppContent += tablePrefix + "<tbody>" + lE;
		} else {
			ppContent += "<para annotations=\"query content\">" + lE;
		}
		
		for(Element ownedElement : namedRefEl.getOwnedElement()) { 
			if(ownedElement.getHumanType().equals("Part Property")	) {
				NamedElement theType = ((TypedElement)ownedElement).getType();
				String theName = ((NamedElement)ownedElement).getName();
				ppFound = true;
				//Port myPort = (Port)ownedElement;
				if(tableRep) { 
					ppContent += "<row>";
					// part name
					ppContent += entryReplaceBrackets(theName) + lE;
					// part type
					if(  theType != null ) { 
						ppContent += entryReplaceBrackets(theType.getName()) +   lE; 						
					} else {
						ppContent += entryReplaceBrackets("UNDEFINED") + lE;
					}

					ppContent += addDocumentationForPropertyAndType(ownedElement, theType);
					if(showDefaultValue){
						ppContent += getDefaultValue() +   lE;
						}
					

					ppContent += "</row>" + lE;
				} else {
					ppContent += Utilities.replaceBracketCharacters(theName) + "," ;
					if(theType != null) { 
						ppContent += Utilities.replaceBracketCharacters(theType.getName())+ " ";
					} else {
						ppContent += "NULL Type";
					}
				}
			} else {
				// we're only interested in PP at this stage, so we do nothing here
			}
		}
		if(tableRep) { 
			ppContent += "</tbody></tgroup></table>" + lE;
		} else {
			ppContent += "</para>" +lE;
		}

		if(ppFound) { 
			content += ppContent;
		} 
		
		return content;
	}

	private String processReferenceProperties(NamedElement namedRefEl) { 
		String tablePrefix= "";
		String content = "";
		boolean ppFound = false;
		String ppContent = "";
		
		//tablePrefix = "<para annotations=\"title for table\"><emphasis role=\"bold\">" + 
		//namedRefEl.getName() + "</emphasis></para>" + lE;
		if(showQueriedElementDocumentation && Utilities.hasDocumentation(namedRefEl)) { 
			tablePrefix += "<para annotations=\"element docu\">"+ lE + 
			Utilities.getDocumentation(namedRefEl) + lE + "</para>";									
		}

	
		tablePrefix += tableHeader(namedRefEl, " Ref Properties", "Part");
		
		if(tableRep) { 
			ppContent += tablePrefix + "<tbody>" + lE;
		}
		
		for(Element ownedElement : namedRefEl.getOwnedElement()) { 
			if(ownedElement.getHumanType().equals("Shared Property")	) {
				NamedElement theType = ((TypedElement)ownedElement).getType();
				ppFound = true;
				//Port myPort = (Port)ownedElement;
				if(tableRep) { 
					ppContent += "<row>";
					// part name
					ppContent += entryReplaceBrackets(((NamedElement)ownedElement).getName()) + lE;
					// part type
					if(  theType != null ) { 
						ppContent += entryReplaceBrackets(theType.getName()) +   lE; 						
					} else {
						ppContent += entryReplaceBrackets("UNDEFINED") + lE;
					}

					ppContent += addDocumentationForPropertyAndType(ownedElement, theType);
					if(showDefaultValue){
						ppContent += getDefaultValue() +   lE;
					}


					ppContent += "</row>" + lE;
				} else {
					ppContent += Utilities.replaceBracketCharacters (((NamedElement)ownedElement).getName()) + "," + 
							Utilities.replaceBracketCharacters(((TypedElement)ownedElement).getType().getName())+ " ";
				}
			} else {
				// we're only interested in PP at this stage, so we do nothing here
			}
		}
		if(tableRep) { 
			ppContent += "</tbody></tgroup></table>" + lE;
		}

		if(ppFound) { 
			content += ppContent;
		} 
		
		return content;
	}

	
	private String processValueProperties(NamedElement namedRefEl) { 
		String tablePrefix= "";
		String content = "";
		boolean vpFound = false;
		String vpContent = "";
		
		//tablePrefix = "<para annotations=\"title for table\"><emphasis role=\"bold\">" + 
		//namedRefEl.getName() + "</emphasis></para>" + lE;
		if(showQueriedElementDocumentation && Utilities.hasDocumentation(namedRefEl)) { 
			tablePrefix += "<para annotations=\"element docu\">"+ lE + 
			Utilities.getDocumentation(namedRefEl) + lE + "</para>";									
		}

	
		tablePrefix += tableHeader(namedRefEl, " Value Properties", "Property");
		
		if(tableRep) { 
			vpContent += tablePrefix + "<tbody>" + lE;
		}
		
		for(Element ownedElement : namedRefEl.getOwnedElement()) { 
			if(ownedElement.getHumanType().equals("Value Property")	) {
				NamedElement theType = ((TypedElement)ownedElement).getType();
				vpFound = true;
				//Port myPort = (Port)ownedElement;
				if(tableRep) { 
					vpContent += "<row>";
					// part name
					vpContent += entryReplaceBrackets(((NamedElement)ownedElement).getName()) + lE;
					// part type
					if(  theType != null ) { 
						vpContent += entryReplaceBrackets(theType.getName()) +   lE; 						
					} else {
						vpContent += entryReplaceBrackets("UNDEFINED") + lE;
					}

					vpContent += addDocumentationForPropertyAndType(ownedElement, theType);
					if(showDefaultValue){
						vpContent += getDefaultValue() +   lE;
					}


					vpContent += "</row>" + lE;
				} else {
					vpContent += Utilities.replaceBracketCharacters(((NamedElement)ownedElement).getName()) + "," + 
							Utilities.replaceBracketCharacters(((TypedElement)ownedElement).getType().getName())+ " ";
				}
			} else {
				// we're only interested in VP at this stage, so we do nothing here
			}
		}
		if(tableRep) { 
			vpContent += "</tbody></tgroup></table>" + lE;
		}

		if(vpFound) { 
			content += vpContent;
		} 
		
		return content;
	}
	
	
	
	private String addDocumentationForPropertyAndType(Element ownedElement, NamedElement theType) {
		String retVal = "";
		if(showPropertiesDocumentation) { 
			if(Utilities.hasDocumentation(ownedElement)) { 
				retVal += entryNoReplaceBrackets( Utilities.getDocumentation(ownedElement)) + lE;
			} else { 
				retVal += entryReplaceBrackets("");
			}
		} 

		if(showTypesDocumentation) { 
			if(theType != null && Utilities.hasDocumentation(theType)) {
				retVal += entryNoReplaceBrackets( Utilities.getDocumentation(theType)) + lE;				
			} else {
				retVal += entryReplaceBrackets("");
			}
		}

		return retVal;
	}

	private String tableHeader(NamedElement namedRefEl, String title, String firstCol) {
		String tableHeader = "",tablePrefix = "",colspec = "";
		int columns = 2;

		tablePrefix =			"<table frame=\"all\"><title>"+ Utilities.replaceBracketCharacters (namedRefEl.getName()) + " " + title + "</title>";
		tableHeader += 
				"<thead><row>" + entryReplaceBrackets(firstCol) + entryReplaceBrackets("Type");
		if(queryType.equals("flowProperties")) { 
			columns++;
			tableHeader += entryReplaceBrackets("Dir");
		}

		if( showPropertiesDocumentation) {
			columns++;
			tableHeader += entryReplaceBrackets(firstCol + " Doc.");
		}
		if( showTypesDocumentation) {
			columns++;
			tableHeader += "<entry>Type Doc.</entry>";
		}
		if( showDefaultValue) {
			columns++;
			tableHeader += "<entry>Default Value</entry>";
		}

		if (queryType.equals("flowProperties")) {
			if (columns == 4) {
				colspec +=  "<colspec colnum=\"1\" colname=\"col1\" colwidth=\"3*\"/>" + 
						"<colspec colnum=\"2\" colname=\"col2\" colwidth=\"3*\"/>" +
						"<colspec colnum=\"3\" colname=\"col3\" colwidth=\"1*\"/>" +
						"<colspec colnum=\"4\" colname=\"col4\" colwidth=\"12*\"/>";
			} else if (columns == 5) {
				colspec +=  "<colspec colnum=\"1\" colname=\"col1\" colwidth=\"3*\"/>" + 
						"<colspec colnum=\"2\" colname=\"col2\" colwidth=\"3*\"/>" +
						"<colspec colnum=\"3\" colname=\"col3\" colwidth=\"1*\"/>" +
						"<colspec colnum=\"4\" colname=\"col4\" colwidth=\"6*\"/>" +
						"<colspec colnum=\"5\" colname=\"col5\" colwidth=\"6*\"/>";
			}
		} else {
			if (columns == 3) {
				colspec +=  "<colspec colnum=\"1\" colname=\"col1\" colwidth=\"3*\"/>" + 
						"<colspec colnum=\"2\" colname=\"col2\" colwidth=\"3*\"/>" +
						"<colspec colnum=\"3\" colname=\"col3\" colwidth=\"12*\"/>";
			} else if (columns == 4) {
				colspec += 	"<colspec colnum=\"1\" colname=\"col1\" colwidth=\"3*\"/>" + 
						"<colspec colnum=\"2\" colname=\"col2\" colwidth=\"3*\"/>" +
						"<colspec colnum=\"3\" colname=\"col3\" colwidth=\"6*\"/>" +
						"<colspec colnum=\"4\" colname=\"col4\" colwidth=\"6*\"/>";
			}
		}

		tableHeader += "</row></thead>" + lE;
		tablePrefix += "<tgroup cols=\"" + columns + "\">" + colspec + tableHeader;		

		return tablePrefix;
	}

	
	private static String getQueryType(Element el) {
		//int rval = 0;
		String rval = "";
		List<?> list = StereotypesHelper.getStereotypePropertyValue(el ,theUtilities.getTheQueryStereotype(), "type");
		if(list.size() > 0 ) {
			Object eLit = null;
			eLit =  list.get(0);
			rval = ((EnumerationLiteral)eLit).getName();
		}
		
		return rval;
	}
/*
	private static String getQueryPresentationType(Element el) {
		//int rval = 0;
		String rval = "";
		List<?> list = StereotypesHelper.getStereotypePropertyValue(el ,theUtilities.getTheQueryStereotype(), "representation");
		if(list.size() > 0 ) {
			Object eLit = null;
			eLit =  list.get(0);
			rval = ((EnumerationLiteral)eLit).getName();
		}
		
		return rval;
	}
*/
	private static boolean isQueryRepTable(Element el) {
		//int rval = 0;
		String repString = "";
		boolean rval = false;
		// a missing attribute means paragraph 
		List<?> list = StereotypesHelper.getStereotypePropertyValue(el ,theUtilities.getTheQueryStereotype(), "representation");
		if(list.size() > 0 ) {
			Object eLit = null;
			eLit =  list.get(0);
			repString = ((EnumerationLiteral)eLit).getName();

			if(repString.equals("table")) { 
				return true;
			}
		}
		
		return rval;
	}

	
	private static boolean isDocToBeShown( Element el) {
		return isQueryPropertySet(el,"showQueriedElementDocumentation");		
	}
	
	private static boolean isQualifiedNameToBeShown( Element el) {
		return isQueryPropertySet(el,"showQualifiedName");		
	}
	
	
	private static boolean isTypesDocToBeShown( Element el) {
		// @todo: to be changed with right one once implemented.
		return isQueryPropertySet(el,"showTypesDocumentation");
	}

	private static boolean isPropertiesDocToBeShown( Element el) {
		// @todo: to be changed with right one once implemented.
		return isQueryPropertySet(el,"showPropertiesDocumentation");

	}
	

	private boolean isDefaultValueToBeShown(Element el2) {
		// @todo: to be changed with right one once implemented.
	   return isQueryPropertySet(el,"showDefaultValue");
	}


	private static boolean isQueryPropertySet(Element el, String theProperty) { 
		Boolean rval = false;
		List<?> list = StereotypesHelper.getStereotypePropertyValue(el ,theUtilities.getTheQueryStereotype(), theProperty);
		if(list.size() > 0 ) {
			Object eLit = null;
			eLit =  list.get(0);
			if(eLit instanceof Boolean) { 
				rval = (Boolean)eLit;
				return rval.booleanValue();
			} else {
				System.out.println("MBSE: isDocToBeShown: there is not a Boolean in the " + theProperty +
						" parameter of the query" + el.getHumanName());
			}
		} else { 
			System.out.println("\tWARNING: " + theProperty + " does not exist in Element " + el.getHumanName());
		}
		return false;
	}

	private static List<?> getQueryPropertiesToBeShown(Element el) {
		return StereotypesHelper.getStereotypePropertyValue(el, theUtilities.getTheQueryStereotype(), "property");
	}

	private static String entryReplaceBrackets(String theContent) { 
		return "<entry align=\"left\">"+ Utilities.replaceBracketCharacters (theContent) + "</entry>";
	}

	private static String entryNoReplaceBrackets(String theContent) { 
		return "<entry align=\"left\">"+ theContent + "</entry>";
	}

	private static String getDefaultValue(){
		return "<entry align=\"left\">"+"test" + "</entry>";
	}

	
}
