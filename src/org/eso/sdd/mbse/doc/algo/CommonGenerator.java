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
 *    $Id: Generate.java 2995 2011-11-14 13:28:57Z jesdabod $
 */

package org.eso.sdd.mbse.doc.algo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;

import com.nomagic.magicdraw.core.Application;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.dependencymatrix.configuration.MatrixDataHelper;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.magicdraw.export.image.ImageExporter;
import javax.swing.Icon;

import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup;
import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup.DiagramGraphicsFormat;

import com.nomagic.uml2.ext.jmi.helpers.ElementImageHelper;

import com.nomagic.generictable.GenericTableManager;
import com.nomagic.reportwizard.tools.DiagramTableTool;
import com.nomagic.reportwizard.*;
import com.nomagic.task.RunnableWithProgress;
import com.nomagic.task.ProgressStatus;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceValue;

public class CommonGenerator implements RunnableWithProgress {

	private static StringBuffer DocDown = null;
	private static String lE = System.getProperty("line.separator");
	// private static String preamble =
	// "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" + lineEnd +
	// "<!DOCTYPE book PUBLIC \"-//OASIS//DTD DocBook V4.2//EN\" \"http://www.oasis-open.org/docbook/xml/4.2/docbookx.dtd\" ["
	// +
	// lineEnd + "<!ENTITY nwalsh \"Michele Zamparelli\">" + lineEnd +
	// "<!ENTITY chap1 SYSTEM \"chap1.xml\">" + lineEnd +
	// "<!ENTITY chap2 SYSTEM \"chap2.xml\">" +
	// lineEnd + "]>" + lineEnd;
	// alternative setting to preamble
	private static String preamble = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
			+ lE
			+ "<book xmlns=\"http://docbook.org/ns/docbook\" version=\"5.0\">"
			+ lE;
	private static String epilogue = "";
	private static int sectionDepth = 1;
	private static int biblioCounter = 1;
	private static int recursionDepth = 0;
	private static String biblioAbbrev = "";
	private static File baseDir = null;
	private static String tokenNameHTML = "&lt;token&gt;";
	private static String tokenName = "<token>";
	private static boolean Debug = true;
	
	private static File theDestFile = null;
	private static NamedElement theStartElement = null;

	private static List<File> imageFilesForDeletion = null;
	private ProgressStatus theProgressStatus = null;
	
	private boolean ABORTED;
	private int status = 2; // poor man's enumeration: 0 success 1 aborted 2 not
							// started 3 failure

	/*
	 * private static Profile docBookProfile =
	 * StereotypesHelper.getProfile(PROJECT, "DocBookProfile"); private static
	 * Profile SysMLProfile = StereotypesHelper.getProfile(PROJECT, "SysML");
	 */
	// maybe not necessary
	// private static Stereotype theBookStereotype =
	// StereotypesHelper.getStereotype(PROJECT,"book", docBookProfile);

	private static Stereotype theBibliographyStereotype = null;
	private static Stereotype theBiblioEntryStereotype = null;
	private static Stereotype theRevisionEntryStereotype = null;
	private static Stereotype theAuthorStereotype = null;

	ArrayList<String> tester = new ArrayList<String>();

	private static Utilities theUtilities = null;

	
	/**
	 * replace characters not allowed / not wanted in a file name 
	 * 
	 */
	private static String cleanUpString4FileName(String str) {
		String fn = str;
		fn = fn.replaceAll(" ", "_")
		  .replaceAll(":", "_")
		  .replaceAll("/", "_")
		  .replaceAll("%", "_")
		  .replaceAll("\\?", "_")
		  .replaceAll("\\.", "_")
		  .replaceAll("\"", "")
		  .replaceAll("\'", "");		
		return fn;		
	}
	
	
	public static void setDestFile(File theFile) {
		theDestFile = theFile;
	}

	public static void setStartElement(NamedElement theElement) {
		theStartElement = theElement;

	}

	public void generate() {
		FileWriter fwDestfile = null;
		ABORTED = false;
		theUtilities = new Utilities();
		theBibliographyStereotype = theUtilities.getTheBibliographyStereotype();
		theBiblioEntryStereotype = theUtilities.getTheBiblioEntryStereotype();
		theRevisionEntryStereotype = theUtilities.getTheRevisionEntryStereotype();
		theAuthorStereotype = theUtilities.getTheAuthorStereotype();
		if(imageFilesForDeletion == null) { 
			imageFilesForDeletion = new ArrayList<File>();
			
		}
		
		if (theDestFile == null) {
			System.out.println("empty file");
			return;

		}
		baseDir = new File(theDestFile.getParent());

		if (theUtilities.getDocBookProfile() == null) {
			displayWarning("No DocBook profile is applied to this project, no can do\n");
			return;
		}

		if (!Utilities.isBook(theStartElement)) {
			displayWarning("This element " + theStartElement.getName()
					+ " is not a book, forget it!");
			return;
		}
		DocDown = new StringBuffer();

		// DocDown.append(preamble);
		sectionDepth = 1;
		recursionDepth = -1;
		recurseDocument(theStartElement);
		if (ABORTED) {
			System.out.println("** DocBook generation cancelled by user");
			status = 1;
		} else {
			System.out.println("DocBook generation COMPLETED");
			/*
			 * for(String s:tester){ System.out.println(s+"\n"); }
			 */
			DocDown.append(epilogue);
			try {

				fwDestfile = new FileWriter(theDestFile);
				fwDestfile.write(DocDown.toString());
				// wsfConfFP.newLine();
				fwDestfile.close();
				status = 0;

			} catch (IOException e) {
				System.out.println("MBSE Could not open file: "
						+ theDestFile.toString() + " for writing");
				System.out.flush();
				status = 3;
				return;
			}
		}
	}

	/*
	 * @todo: is there a better way to create IDs?
	 */
	private void logDebugIndent(Element el,String ld) { 
		if(Debug) {
			String indent = ""; 
			for(int i =0; i < recursionDepth; i++) { 
				indent += "   ";
			}
			System.out.println(indent + " \"" +  el.getHumanName() + "\""+ ld);
		}
		
	}
	
	private void logDebug(boolean lDebug,String ld) { 
		if(lDebug) { 
			System.out.println(ld);
		}
	}
	
	private void recurseDocument(Element el) {
		List<Element> navigateDown = new Vector<Element>();
		String recIdent = el.getHumanName() + " ";
		if (el instanceof Comment) {
			String body = ((Comment) el).getBody().replaceAll("\n", "")
					.replaceAll(">\\s+<", "><");
			recIdent += "("
					+ body.substring(0, ((body.length() <= 40) ? body.length()
							: 40)) + ")";
		}
		if (Debug) {
			//System.out.print("*** YYY recursing on: " + recIdent);
		} else {
			System.out.print("");
		}

		//StringBuffer prefix  = new StringBuffer("<para annotations=\"generic\">" + lE);
		StringBuffer prefix  = new StringBuffer("");
		StringBuffer postfix = new StringBuffer ("");
		StringBuffer content = new StringBuffer("");

		recursionDepth++; 
		

		if (ABORTED) {
			return;
		} else if (theProgressStatus != null) {
			if(theProgressStatus.isCancel()){
			// displaying the warning is care of the Action
			ABORTED = true;
			return;
			}
		}

		if (Utilities.isBook(el)) {
			processBook(el,prefix,postfix,content,navigateDown);	
			
		} else if (Utilities.isPart(el)) {
			processPart(el,prefix,postfix,content,navigateDown);
			
		} else if (Utilities.isChapter(el)) {
			processChapter(el,prefix,postfix,content,navigateDown);			

		} else if (Utilities.isSection(el)) {
			processSection(el,prefix,postfix,content,navigateDown);									
		
		} else if (Utilities.isPreface(el)) {
			processPreface(el,prefix,postfix,content,navigateDown);						
		
		} else if (Utilities.isBibliography(el)) {
			processBibliography(el,prefix,postfix,content,navigateDown);			
			
		} else if (Utilities.isRevisionHistory(el)) {
			processRevisionHistory(el,prefix,postfix,content,navigateDown);		

		} else if (Utilities.isParagraph(el)
				&& ((Comment) el).getBody() != null) {
			processParagraph(el,prefix,postfix,content,navigateDown);					
		} else if (Utilities.isFigure(el)) {
			// find stereotype

			if (Utilities.isFigureDiagram(el)) {
				processFigureDiagram(el,prefix,postfix,content,navigateDown);				
			} // end case is FigureDiagram

			if (Utilities.isFigureImage(el)) {
				processFigureImage(el,prefix,postfix,content,navigateDown);
				
			} // end case is FigureImage

		} else if (Utilities.isBiblioEntry(el)) {
			content.append(provideBiblioText((NamedElement) el, biblioAbbrev
					+ Integer.valueOf(biblioCounter)));
			logDebugIndent(el," is BiblioEntry ()");
			biblioCounter++;

		} else if (Utilities.isRevisionEntry(el)) {
			content.append(provideRevisionText((NamedElement) el));
			logDebugIndent(el," is RevisionEntry ");

		} else if (Utilities.isQuery(el)) {
			// insert refactored code here
			Query theQuery = new Query(el, Debug);
			content.append(theQuery.provideDocBookForQuery());
			logDebugIndent(el," is Query ");
			
		} else if (Utilities.isDiagramTable(el)) {
			processDiagramTable(el,prefix,postfix,content,navigateDown);
			
		} else if (Utilities.isProgramListing(el)) {
			processProgramListing(el,prefix,postfix,content,navigateDown);

		} else if (Utilities.isTableParagraph(el)) {
			content.append(provideTableParagraphContent((Comment) el));
			logDebugIndent(el," is tableParagraph ");
		} else {

			// UNHANDLED CASE
			System.out
			.println("This element is not handled with a known DocBook stereotype:"
					+ el.getHumanName());
			if (el instanceof Comment) {
				System.out.println("Unhandled case Comment: "
						+ el.getOwner().getHumanName());
			}
			content.insert(0,el.getHumanName());
		}

		DocDown.append(prefix.toString());
		DocDown.append(content.toString());

		if (navigateDown != null) {
			Iterator<Element> it3 = null;
			
			for (it3 = navigateDown.iterator(); it3.hasNext();) {
				Element ownedElement =  it3.next();
				//System.out.println(ownedElement.getHumanType() + "Here");

				// restricting recursion to elements of type package or comment.
				if (genUtility.checkType(ownedElement)) {
					tester.add(ownedElement.getHumanType());
					recurseDocument(ownedElement);
				}
			}
		} else {
			// termination condition
			;
		}
		DocDown.append(postfix.toString());
		if (Utilities.isSection(el)) {
			sectionDepth--;
			//System.out.println("Section depth decreased to " + sectionDepth);
		}
		if (Utilities.isBibliography(el)) {
			biblioCounter = 1;
		}

		//System.out.println("*** completed recursion on " + recIdent);
		recursionDepth--; 

	}

	private String provideTableParagraphContent(Comment el) {
		int z = 0;
		int numRow = 0;
		int numColumn = 0;
		
		String firstRow = null;
		String tableDeclaration = null;

		String pattern = "width=\"(\\d+)\"";
		Matcher ma = null;
		Pattern pa = Pattern.compile(pattern, Pattern.DOTALL);

		String captionText = "";
		String token = tokenNameHTML;
		Object captionTextObj = StereotypesHelper
				.getStereotypePropertyFirst(
						el,
						theUtilities.getTheTableParagraphStereotype(),
						"captionText");

		if (captionTextObj != null) {
			captionText += (String) captionTextObj;
		} 
        
		String tablePrefix = "<table annotations=\"tableParagraph\" " + "xml:id=\""
				+ Utilities.uniqueID(el) + "\" "
				+ "frame=\"all\">"+lE+"<title> "+captionText+" </title>"+lE;
		
		
		String content = el.getBody();
		// attention: the <tr here below is error prone
		
		// tableDeclaraton should contain the <table construct to determine its width
		if(content.indexOf("<table") == -1 || content.indexOf("<tr") == -1) { 
			// wrong assumptions, bail out.
			logDebug(true,"tableParagraph is not HTML, bailing out");
			content = "<para annotation=\" failed Table Paragraph\" >" + el.getBody() + " </para>";
			return content;
		}
		tableDeclaration = content.substring(content.indexOf("<table"),content.indexOf("</tr>"));
		firstRow = content.substring(content.indexOf("<tr"),content.indexOf("</tr>"));
		ma = pa.matcher(firstRow);
		
		if(firstRow.length() == 0) { 
			System.out.println("Could not determine first row with headers");
		}
		
		numRow = content.split("\\Q"+"<tr"+"\\E", -1).length - 1;
		numColumn = firstRow.split("\\Q"+"<td"+"\\E", -1).length - 1;

		System.out.println("Rows: "+numRow + " columns: " + numColumn);
		String[] widths = new String[numColumn];
		while(ma.find()) {
			widths[z] = ma.group(1);
			System.out.println("\t****Width: " + z +"/"+numColumn+ "=" + widths[z]);
			z++;
		};
		
		
        String cc = content.substring(content.indexOf("</tr>")+5,content.length());
        //System.out.println(cc);
		String[] eachRowContent = cc.split("</tr>");
		//System.out.println(eachRowContent.length + " " + numRow);
		
		
		String tableTGroup = "<tgroup cols=\""+numColumn+"\">";
		
		String tableColSpec = "";
		
		for(int i =0;i<numColumn;i++){
			tableColSpec += "<colspec colname='c"+i+1+"' />" +lE;
		}
		
		String tableHeaderStart = "<thead><row>"+lE;
		
		String tableHeaderBody = "";
		
		String[] header = firstRow.split("</td>");
		
		for(int i=0;i<header.length-1;i++){
			tableHeaderBody += "<entry align=\"center\">" + header[i].replaceAll("\\<.*?>", "").trim() + "</entry>" + lE;
		}
				
		tableHeaderBody += "</row></thead>" + lE;
		
		String tableBody = "<tbody>"+lE;
		
		//for single row table,weired?
		if(numRow == 1){
			//numRow = 2;
			//eachRowContent = content.split("</tr>");
			return "not supported for single row table";
		}
		
		//excluding first row numRow-1
		  for(int i = 0;i<numRow-1;i++) {
			  tableBody += "<row>";
			  String pS = eachRowContent[i];
			  String[] rower = pS.split("</td>");
			  
			  for(int j=0;j<rower.length-1;j++){
				  tableBody += "<entry align=\"center\">" + Utilities.convertHTML2DocBook(rower[j].replaceAll("\\<.*?>", ""),true).trim() + "</entry>"+lE;
				  //
			  }
			  tableBody += "</row>"+lE;
		  }
		  
		  tableBody += "</tbody>" + lE;  
		
		String tableEnd = "</tgroup>"+lE+"</table>" +lE;

		//solve xref

		List value = StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheTableParagraphStereotype(), "xref");
		tableBody = processXref(value,tableBody,token);
		// precaution against no xref in the element
		if (value.size() == 0) {
			tableBody = tableBody.replaceAll(token, "UNDEFINED TOKEN");
		}

		
		return tablePrefix + tableTGroup + tableColSpec + tableHeaderStart + tableHeaderBody + tableBody  + tableEnd;
	}

	private String processXref(List value, String tableBody, String token) {
		for (int k = 0; k < value.size(); ++k) {
			// tag value
			Object tagValue = value.get(k);
			//displayWarning(tagValue.toString());
			boolean lDebug = false;
			if (tagValue instanceof Element	&& Utilities.isXReference((Element) tagValue)) {
				NamedElement refX = null;
				String tmpString = null;
				int ts = tableBody.indexOf(token);
				int tl = token.length();
				if (ts == -1) {
					logDebug(true,"\tWARNING:MBSE: DocBook: mismatch in number of references, seemingly no "
							+ token + " found");
					// we have a mismatch in the number of xref and the
					// number
					// of occurrences of the string TOKEN
					break;
				}

				if (tagValue instanceof Comment) {
					Comment theComment = (Comment) tagValue;
					if (Utilities.isFigure(theComment)) {
						//
						if (Utilities.isFigureDiagram(theComment)) {

							Object diagramObject = StereotypesHelper.getStereotypePropertyFirst(
											theComment,
											theUtilities
											.getTheFigureDiagramStereotype(),
											"diagram");
							refX = (Diagram) diagramObject;
							if (refX != null) {
								String linkEnd = Utilities.uniqueID((Element) tagValue);
								if (lDebug) {
									logDebug(lDebug,"\t==> Found a Referenced Diagram Figure "	+ refX.getName());
								}
								if (linkEnd.equals("")) {
										logDebug(lDebug,"\t*** WARNING: empty link end for FigureDiagram:"
												+ refX.getName());
								}
								tmpString = tableBody.substring(0, ts)
										+ "<xref linkend=\""
										+ linkEnd
										+ "\">"
										+ "</xref> "
										+ tableBody.substring(ts + tl,
												tableBody.length());
							} else {
								logDebug(lDebug,">> FigureDiagram has empty diagram"
										+ theComment.getHumanName()
										+ " "
										+ theComment.getHumanType()
										+ " in:  "
										+ theComment.getOwner()
										.getHumanName());
								tmpString = "";
							}

						} else if (Utilities.isFigureImage((Element) tagValue)) {

							Object classObject = StereotypesHelper.getStereotypePropertyFirst(
											(Element) tagValue,
											theUtilities.getTheFigureImageStereotype(),
											"imageContainer");

							if (classObject == null) {
								Utilities.displayWarning("We have a problem: "
										+ ((Comment) tagValue)
										.getBody());
									logDebug(lDebug,"***BANZAI!!!*** Found a dead parrot!  ");
								tmpString = "";

								if (StereotypesHelper.hasStereotype(
												(Element) tagValue,
												theUtilities.getTheFigureImageStereotype())) {
									// noop
								} else {
									logDebug(lDebug,"\t***BANZAI!!!*** the dead parrot is not a FigureImage!  ");
								}
							} else {
								refX = (Class) classObject;
								logDebug(lDebug,"\t==> Found a Referenced Diagram Image "+ refX.getName());
								tmpString = tableBody.substring(0, ts)
										+ "<xref linkend=\""
										+ Utilities
										.uniqueID((Element) tagValue)
										+ "\">"
										+ "</xref> "
										+ tableBody.substring(ts + tl,
												tableBody.length());
							}
						} else {
							logDebug(lDebug,"Found a figure which is neither Image nor Diagram:"
									+ ((Comment) tagValue).getBody());
							System.out.println("Found a figure which is neither Image nor Diagram:");
							tmpString = tableBody;
						}
					}
					
					else if (Utilities.isTableParagraph((Element) tagValue)) {
						logDebug(lDebug,"\tWARNING: table Paragraph!");
						String linkEnd = Utilities.uniqueID((Element) tagValue);
						
						tmpString = tableBody.substring(0, ts)
								+ "<xref linkend=\""
								+ linkEnd
								+ "\">"
								+ "</xref> "
								+ tableBody.substring(ts + tl,tableBody.length());
					}
					
					else { // it is not a figure, but still a comment
						logDebug(true,"\tWARNING: processParagraph() UNDHANDLED CASE!");
					}
				} else if (tagValue instanceof NamedElement) {
					refX = (NamedElement) tagValue;
					tmpString = tableBody.substring(0, ts)
							+ "<xref linkend=\"" + Utilities.uniqueID(refX)
							+ "\">" + "</xref> "
							+ tableBody.substring(ts + tl, tableBody.length());
					logDebug(lDebug,"\tWARNING! tagValue not a Comment"
								+ tmpString);
				}

				tableBody = tmpString;

				// System.out.println( String.valueOf(k) + ": " + content +
				// lineEnd);
			} else { // if(tagValue instanceof Element &&
				// Utilities.isXReference((Element)tagValue))
				logDebug(lDebug,"\tWARNING xRef does not point to a xreferenc element) ");
			}

		} // end loop over tagged values
		
		return tableBody;
	}

	private static Property getThePropertyFromTheStereotype(Stereotype st,
			String pName) {
		List<Property> attributes = st.getOwnedAttribute();
		for (int j = 0; j < attributes.size(); ++j) {
			if (attributes.get(j).getName().equals(pName)) {
				return attributes.get(j);
			}
		}
		return null;
	}

	private static void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);
	}

	public int getStatus() {
		return status;
	}

	private static String provideBiblioText(NamedElement ne, String abbrev) {
		String theResult = "<biblioentry xml:id=\"" + Utilities.uniqueID(ne)
				+ "\">" + lE + "<abbrev>" + abbrev + "</abbrev>";
		theResult += "<title>"
				+ Utilities.getFirstElementString(ne,
						theBiblioEntryStereotype, "title") + "</title>" + lE;
		theResult += "<productnumber>"
				+ Utilities.getFirstElementString(ne,
						theBiblioEntryStereotype, "productNumber")
				+ " </productnumber>" + lE;
		theResult += "<issuenum>"
				+ Utilities.getFirstElementString(ne,
						theBiblioEntryStereotype, "issueNumber")
				+ "</issuenum>" + lE;
		theResult += "<pubdate>"
				+ Utilities.getFirstElementString(ne,
						theBiblioEntryStereotype, "pubDate") + "</pubdate>"
				+ lE;
		theResult += "</biblioentry>";

		return theResult;
	}

	/*
	<revision>
	  <revnumber>0.91</revnumber>
	  <date>11 Dec 1996</date>
	  <authorinitials>ndw</authorinitials>
	  <revremark>Bug fixes</revremark>
	</revision>
*/
	
	private static String provideRevisionText(NamedElement ne) {
		Element theAuthor = null;
		String theResult = "<revision xml:id=\"" + Utilities.uniqueID(ne)+"_" + ne.getID()
				+ "\">" + lE ;
		theResult += encase("revnumber",Utilities.getFirstElementString(ne,theRevisionEntryStereotype,
				"revnumber")) + lE;
		theResult += encase("date",Utilities.getFirstElementString(ne,theRevisionEntryStereotype,
				"date")) + lE;
		theResult += encase("revdescription",Utilities.getFirstElementString(ne,theRevisionEntryStereotype,
				"revdescription")) + lE;
		
		List bcVect = StereotypesHelper.getStereotypePropertyValue(ne,theRevisionEntryStereotype, "author");
		if (!bcVect.isEmpty()) {
			 theAuthor = (Element)(bcVect.get(0));
		} else {

		}
		
		theResult += encase("author",encase("personname",
				Utilities.getFirstElementString(theAuthor,theAuthorStereotype, "firstname") + " "+
						Utilities.getFirstElementString(theAuthor,theAuthorStereotype, "surname")			
				)) + lE;
		theResult += encase("revremark",encase("para",Utilities.getFirstElementString(ne,theRevisionEntryStereotype,
				"revremark")))	+ lE;
		theResult += "</revision>" + lE;

		return theResult;
	}
	
	// a utility method for encasing XML tags

	private static String encase(String tag,String content) {
		String retVal = "<"+tag+">" + content + "</" + tag + ">";
		return retVal;
	}
	
	/*
	 * *******************************************************
	 *  PROCEDURES FOR HANDLING OF VARIOUS ELEMENT TYPES IN 
	 *   'RECURSEDOCUMENT'
	 * *******************************************************
	 * 
	 */

	private void processBook(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) {
		List bcVect = null;
		prefix.insert(0,preamble + lE);
		postfix.insert(0,"</book>" + lE);

		String sContent = null;

		if (theUtilities.getTheBookStereotype() == null) {
			System.out
					.println("MBSE: Generate: Warning, book stereotype is empty!");
		}

		sContent = "<info>"
				+ lE
				+ "<subtitle>"
				+ Utilities.replaceBracketCharacters(((NamedElement) el)
						.getName()) + "</subtitle>" + lE;

		// PUBLISHER
		bcVect = StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "publisher");
		if (!bcVect.isEmpty()) {
			sContent += encase("publishername", (String)bcVect.get(0)) + lE;
		}

		// PRODUCT NUMBER
		bcVect = StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "documentNumber");
		if (!bcVect.isEmpty()) {
			sContent += encase("productnumber",(String) bcVect.get(0)) + lE;
		}

		// ISSUE DATE
		if (StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "issueDate").size() > 0) {
			sContent += encase("pubdate",((String) StereotypesHelper
					.getStereotypePropertyValue(el,
							theUtilities.getTheBookStereotype(),
							"issueDate").get(0))) + lE;

		}

		// ISSUE NUMBER
		if (StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "issue").size() > 0) {
			sContent += encase("issuenum",((String) StereotypesHelper
					.getStereotypePropertyValue(el,
							theUtilities.getTheBookStereotype(), "issue")
					.get(0)));
		}

		// PROGRAMME
		if (StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "programme").size() > 0) {
			sContent += encase("title", ((String) StereotypesHelper
					.getStereotypePropertyValue(el,
							theUtilities.getTheBookStereotype(),
							"programme").get(0)));
		}

		Vector tmpVe = new Vector();

		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "revhistory"));
		// something fishy here generated a ProjectClosedExceltion, I do not
		// understand why and cannot find
		// any trace of this exception in the openAPI.
		
		bcVect = StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "bookComponent");
		if (bcVect.size() > 0) {
			tmpVe.addAll(bcVect);
		} else {
			System.out.println("Warning, book " + el.getHumanName()
					+ " has no components");
		}

		// initial attempt at composing different types of elements for
		// recursion.
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "divisions"));

		navigateDown.addAll(tmpVe);

		bcVect = StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheBookStereotype(), "owner");
		// now determining the authors
		if (!bcVect.isEmpty() && bcVect.get(0) instanceof Element) {
			Element author = (Element) bcVect.get(0);
			if (Utilities.isAuthor(author)) {
				Author theAuthor = theUtilities.getAuthorFromBook(el,
						"owner").get(0);
				sContent += theAuthor.authorInfo("owner");
			}
		}

		for (Author theAuthor : theUtilities.getAuthorFromBook(el, "wpManager")) {
			sContent += theAuthor.authorInfo("wpManager");
		}

		for (Author theAuthor : theUtilities.getAuthorFromBook(el, "releaser")) {
			sContent += theAuthor.authorInfo("releaser");
		}

		for (Author theAuthor : theUtilities.getAuthorFromBook(el, "authors")) {
			sContent += theAuthor.authorInfo("author");
		}

		sContent += "</info>" + lE;
		content.insert(0, sContent);
		logDebugIndent(el, " is Book (" + navigateDown.size() + ")");
	}
	
	
	private void processPart(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		prefix.insert(0, "<part xml:id=\""
				+ Utilities.uniqueID(el)
				+ "\" >"
				+ lE
				+ "<info>"
				+ lE
				+ "<title>"
				+ Utilities.replaceBracketCharacters(((NamedElement) el)
						.getName()) + "</title>" + lE + "</info>" + lE
						+ "<partintro>" + "<para> </para></partintro>" + lE);
		postfix.insert(0, "</part>" + lE);
		navigateDown.addAll(StereotypesHelper.getStereotypePropertyValue(
				el,theUtilities.getThePartStereotype(), "components"));
		logDebugIndent(el," is Part");

	}

	private void processChapter(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		prefix.insert(0,"<chapter xml:id=\""
				+ Utilities.uniqueID(el)
				+ "\" >"
				+ lE
				+ "   <title>"
				+ Utilities.replaceBracketCharacters(((NamedElement) el)
						.getName()) + "</title>" + lE);
		postfix.insert(0, "</chapter>" + lE);
		Vector tmpVe = new Vector();
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(
				el,theUtilities.getTheChapterStereotype(), "blockelements"));
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(
				el,theUtilities.getTheChapterStereotype(), "sections"));
		// initial attempt at composing different types of elements for
		// recursion.
		navigateDown.addAll(tmpVe);
		logDebugIndent(el," is Chapter (" + tmpVe.size() + ")");

	}

	private void processBibliography(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		prefix.insert(0, "<bibliography" + " xml:id=\""
				+ Utilities.uniqueID(el) + "\">" + lE);
		postfix.insert(0,"</bibliography>" + lE);
		Vector tmpVe = new Vector();
		biblioCounter = 1;
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(
				el,theUtilities.getTheBibliographyStereotype(), "biblioEntry"));
		navigateDown.addAll(tmpVe);
		biblioAbbrev = Utilities
				.getFirstElementString(el,
						theUtilities.getTheBibliographyStereotype(),
						"abbrevPrefix");
		logDebugIndent(el,"is Bibliography (" + tmpVe.size() + ") "	+ biblioAbbrev);
	}

	private void processRevisionHistory(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		prefix.insert(0,"<bookinfo>" + lE + "<revhistory" + " xml:id=\""
				+ Utilities.uniqueID(el) + "\">" + lE);
		content.insert(0,"");
		postfix.insert(0,"</revhistory>" + lE + "</bookinfo>" + lE);
		Vector tmpVe = new Vector();
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(
				el,theUtilities.getTheRevisionHistoryStereotype(), "revisionEntry"));
		navigateDown.addAll(tmpVe);
		logDebugIndent(el," is Revision History (" + tmpVe.size() + ") ");
	}
	
	private void processPreface(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		prefix.insert(0,"<preface"
				+ " xml:id=\""
				+ Utilities.uniqueID(el)
				+ "\">"
				+ lE
				+ "<title>"
				+ Utilities.replaceBracketCharacters(((NamedElement) el)
						.getName()) + "</title>" + lE);
		content.insert(0,"");
		postfix.insert(0,"</preface>" + lE);
		Vector tmpVe = new Vector();
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(
				el,theUtilities.getThePrefaceStereotype(), "prefacePara"));
		// initial attempt at composing different types of elements for
		// recursion.
		// @todo: specify which order of precedence!!
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(
				el,theUtilities.getThePrefaceStereotype(), "prefaceSection"));
		navigateDown.addAll(tmpVe);

		logDebugIndent(el," is Preface (" + tmpVe.size() + ")");

	}

	private void processSection(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		prefix.insert(0,"<sect"
				+ new Integer(sectionDepth).toString()
				+ " xml:id=\""
				+ Utilities.uniqueID(el)
				+ "\">"
				+ lE
				+ "<title>"
				+ Utilities.replaceBracketCharacters(((NamedElement) el)
						.getName()) + "</title>" + lE);
		Vector tmpVe = new Vector();
		postfix.insert(0,"</sect" + new Integer(sectionDepth).toString() + ">"
				+ lE);
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheSectionStereotype(), "blockelements"));
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheSectionStereotype(), "subsection"));
		tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
				theUtilities.getTheSectionStereotype(), "bibliography"));
		navigateDown.addAll(tmpVe);
		logDebugIndent(el," is Section (" + tmpVe.size() + ")");
		sectionDepth++;
		//System.out.println("Section depth increased to " + sectionDepth);

	}


	
	
	
	
	@SuppressWarnings("deprecation")
	private void processDiagramTable(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		String sContent = "";
		// attention, there exists a stereotype called "DiagramTable" from the MagicDraw standard profile.
		// but there is also a DocBook stereotype called "tableDiagram"
		Project targetProject = Application.getInstance().getProjectsManager().getActiveProject();
		Stereotype theDocBookTDStereo = theUtilities.getTheTableDiagramStereotype();
		Stereotype theDiagramTableStereo = theUtilities.getTheMDDiagramTableStereotype();
		Element elementByID = null;
		int rowIdx = 1;
		int rowIndex = 0;
		Vector<String> columnIds = new Vector<String>();
		Vector<Integer> columnWidth = new Vector<Integer>();
		Vector<String> rowElementsId = new Vector<String>();

		Object diagramObject = StereotypesHelper
				.getStereotypePropertyFirst(el,
						theDocBookTDStereo,
						"diagramTable");
		logDebugIndent(el,"is DiagramTable");
		if(theDiagramTableStereo == null) { 
			System.err.println("ERROR: The stereotype for  DiagramTable appears to be empty.");
		}
		if (diagramObject == null) { 
			logDebugIndent(el,"ERROR: the referenced element is null");
			return;
		}

		if (! (diagramObject instanceof Diagram)) {
			logDebugIndent(el,"ERROR: the referenced element is not a Diagram!");			
			return;
		}

		int width = 80;
		String captionText = "";
		Boolean useDocumentation = false;
		Object captionTextObj = StereotypesHelper.getStereotypePropertyFirst(el,theDocBookTDStereo,"captionText");
		Object useDocumentationObj = StereotypesHelper.getStereotypePropertyFirst(
				el, theDocBookTDStereo,"useDocumentation");
		if(useDocumentationObj != null) {
			useDocumentation = (Boolean)useDocumentationObj;
		}

		Object widthObj = StereotypesHelper.getStereotypePropertyFirst(el, theDocBookTDStereo,"width");
		if (widthObj != null) {
			width = (Integer) widthObj;
		}


		Diagram theDiagram = (Diagram) diagramObject;
		DiagramPresentationElement dpe = theUtilities.getPROJECT().getDiagram(theDiagram);
		//if(! dpe.isLoaded()) {
		// I am forced to open each and every GenericTable in the model since otherwise the internal MD query which lists
		// all the elements won't be executed.
		if(! dpe.isDiagramWindowOpen()) { 
			dpe.open();
		}

		if(! StereotypesHelper.hasStereotypeOrDerived(theDiagram, theDiagramTableStereo)) { 
			System.err.println("WARN: " + theDiagram.getName() + " is not stereotyped by DiagramTable??");
			for(Stereotype ste: StereotypesHelper.getStereotypes(theDiagram) ) { 
				System.out.println(theDiagram.getName() + " is stereotyped by " + ste.getName());
			}

			System.err.println("Proceeding with image generation instead...");
			sContent = provideGeneralPurposeImageReferenceContent(el, prefix, postfix,
					sContent, captionText, theDiagram,width,useDocumentation); 
			content.insert(0,sContent);
			return;
		}				

		if (captionTextObj != null) {
			captionText += (String) captionTextObj;
		} else {
			captionText += theDiagram.getName();
		}

		String tableHeader = "", tablePrefix = "", colspec = "";

		// Integer type
		columnWidth.addAll(StereotypesHelper.getStereotypePropertyValue(theDiagram,
				theDiagramTableStereo, "columnWidth"));
		int columns = 0, totColumnsWidth = 0;
		for (int ownedElement : columnWidth) {
			if (ownedElement != 0) {
				columns++;
				// bug in MD which sets width to -1 if the user has
				// not yet resized it
				if (ownedElement < 0) {
					ownedElement = 1;
					logDebugIndent(el,"isDiagramTable: width=0 of col#" + columns);
				}
				totColumnsWidth += ownedElement;
			}
		}
		int colIdx = 1;
		for (int ownedElement : columnWidth) {
			if (ownedElement != 0) {
				if (ownedElement < 0) {
					ownedElement = 1;
				}
				// create columns width proportional to diagram
				// table
				colspec += "<colspec colnum=\""
						+ colIdx
						+ "\" colname=\"col"
						+ colIdx
						+ "\" colwidth=\""
						+ (int) (((ownedElement * 1.0) / (totColumnsWidth * 1.0)) * 100.0)
						+ "*\"/>";
				colIdx++;
			}
		}

		for(Iterator<Element> it = GenericTableManager.getRowElements(theDiagram).iterator(); it.hasNext(); ) { 
				rowElementsId.add(it.next().getID());
		}

		tablePrefix = lE + "<table annotations=\"Diagram Table  rows:" + rowElementsId.size() + "\" " + "xml:id=\""
				+ Utilities.uniqueID(el) + "\" "
				+ "frame=\"all\">" + lE + "<title>" + (String) captionTextObj
				+ "</title>";


		columnIds.addAll(GenericTableManager.getVisibleColumnIds(theDiagram));

		// prepare the header
		logDebugIndent(el,"columnsIds size:"+columnIds.size() );	
		String firstCol = "#";
		columnIds.set(0, firstCol);

		tableHeader += lE + "<thead>" + lE + "<row>" + lE;
		for (String colId : columnIds) {
			if (colId.lastIndexOf(':') >= 0) {
				tableHeader += 
							"<entry align=\"left\" annotations=\"colId="+ colId+ "\" >" + GenericTableManager.getColumnNameById(theDiagram, colId) + "</entry>" + lE;
			} else {
				// if Element is used it would return for example "Property SatisfiedBy"
				// with NamedElement getName returns just SatisfiedBy
				NamedElement cel = (NamedElement) targetProject.getElementByID(colId);
				String n = null;
				if (cel == null) {
					n = colId;							
				} else {
					n = cel.getName();
				}

				tableHeader += "<entry align=\"left\">"	+ n + "</entry>" + lE;
			}
		}
		tableHeader += "</row>"+ lE +"</thead>" + lE;

		tablePrefix += lE + "<tgroup cols=\"" + columns + "\">"
				+ colspec + tableHeader;

		sContent += tablePrefix + lE + "<tbody>" + lE;

		// remove the _NUMBER_ columns ID
		columnIds.removeElementAt(0);

		// LOOP OVER ROWS		
		for (String objectID : rowElementsId) {

			elementByID = (Element) targetProject.getElementByID(objectID);
			// iterate over all selected columns
			if(elementByID == null) {
				logDebugIndent(el,"Cannot retrieve element for given ID " + objectID);
				continue;
			}
			sContent += "<row>" +lE;
			sContent += "<entry align=\"left\">" + rowIdx++	+ "</entry>" + lE;
			
			// LOOP OVER COLUMNS
			for (String colId : columnIds) {
				// use MD diagramtabletool to retrieve elementInfo
				com.nomagic.magicdraw.properties.Property theProp = GenericTableManager.getCellValue(theDiagram, elementByID, colId);
				if(theProp == null) { 
					logDebugIndent(el,"Null property returned for " + colId + " skipping");
					continue;
				}
				//logDebugIndent(el,"Property is of type: " + theProp.getClass().getName());
				String elementInfo =  null;
				if(theProp instanceof com.nomagic.magicdraw.properties.StringProperty ) { 
					elementInfo = theProp.getValueStringRepresentation();
					elementInfo = Utilities.convertHTML2DocBook(elementInfo, false);
					elementInfo = "<entry align=\"left\" annotations=\"StringProperty\" >"	+ elementInfo + "</entry>" +lE;
				} else if(theProp instanceof com.nomagic.magicdraw.properties.ElementListProperty ) {
					String subTable = "";
					com.nomagic.magicdraw.properties.ElementListProperty elp = (com.nomagic.magicdraw.properties.ElementListProperty)theProp;
					Element[] theSubEle = elp.getValue();
					if(theSubEle.length > 0) {
						subTable += "<entrytbl cols='1'>"+ lE + "<tbody>" + lE;
						for(int i = 0; i < theSubEle.length; i++ ) {
							Element theEle = theSubEle[i];
							if(theEle != null) {
								String theRep = null;
								if(theEle.getHumanType().equals("Literal String")) {
									LiteralString ls = (LiteralString)theEle;
									theRep = ls.getValue();	
								} else if(theEle.getHumanType().equals("Instance Value")){ 
									InstanceValue iv = (InstanceValue)theEle;
									theRep = iv.getInstance().getName();
								} else {
									String name = "";
									String type = "";
									if(theEle instanceof NamedElement) { 
										name = ((NamedElement)theEle).getName();
									}
									if(theEle instanceof TypedElement) { 
										type = ((TypedElement)theEle).getType().getName();
									}
									theRep = name + ":"+ type;
								}

								subTable += "<row><entry>" + Utilities.transformSpecialCharacter(theRep) + "</entry></row>"+lE;
								//logDebugIndent(el, " "+theEle.getClass().getName()+ " " + theEle.getHumanType());
							}
						}

						subTable += "</tbody>" + lE + "</entrytbl>" + lE;
					}
					elementInfo = subTable;
					
				} else if(theProp instanceof com.nomagic.magicdraw.properties.ElementProperty ) {
					if(theProp.getName().equals("Type")) { 
						TypedElement te = (TypedElement)elementByID;
						elementInfo = "<entry align=\"left\" annotations=\"ElementProperty (Type)\" >"	+ 
								Utilities.convertHTML2DocBook(te.getType().getName(), false) 
									+ "</entry>" +lE;
					} else {
						elementInfo = "<entry align=\"left\" annotations=\"ElementProperty\" >"	+ 
								Utilities.convertHTML2DocBook(theProp.getValueStringRepresentation(), false) 
									+ "</entry>" +lE;
					}
				} else {
					//logDebugIndent(el, " WARNING: unidentified type: " + theProp.getClass().getName() + " for column: " + colId);
					Object theUnknown = theProp.getValue();
					if(theUnknown instanceof Object[]) {
						String subTable = "<entrytbl cols='1'>"+ lE + "<tbody>" + lE;								
						Object[] theArray = (Object[])theUnknown;
						for(int i = 0; i < theArray.length;i++) {
							String theAnno = null;
							String theRep = "fault";
							if(theArray[i] instanceof com.nomagic.magicdraw.properties.StringProperty) {
								theAnno = theArray[i].getClass().getName();
								com.nomagic.magicdraw.properties.StringProperty sp = (com.nomagic.magicdraw.properties.StringProperty)theArray[i];
								theRep = sp.getValueStringRepresentation();
							}
							subTable += "<row>" + lE + "<entry annotation=\"" + theAnno + "\" >" + Utilities.transformSpecialCharacter(theRep) + "</entry>" 
									+ lE + "</row>"+lE;									
						}
						subTable += "</tbody>" + lE + "</entrytbl>" + lE;
						elementInfo = subTable;

					} else {
						elementInfo = "<entry align=\"left\" annotations=\"" + 
								theProp.getClass().getName() + "\" >"	+ 
									theProp.getValueStringRepresentation() + "</entry>" +lE;
					}
				}

				if (elementByID != null) {
					sContent += elementInfo;
				} else {
					sContent += "<entry align=\"left\">"	+ "NULL" + "</entry>";
				}
			} // loop over column id
			rowIndex++;
			sContent += "</row>" + lE;
		} // loop over ObjectID
		sContent += "</tbody></tgroup></table>" + lE;
		content.insert(0,sContent);
	}


	private String provideGeneralPurposeImageReferenceContent(Element el, StringBuffer prefix,
			StringBuffer postfix, String sContent, String captionText,
			Diagram theDiagram, int width, boolean useDocumentation) {
		prefix.insert(0,"<figure annotations=\"figure diagram\" xml:id=\"" + Utilities.uniqueID(el)
				+ "\">" + lE + "<title>" + captionText
				+ "</title>" + lE + "<mediaobject>" + lE
				+ "<imageobject>" + lE);
		postfix.insert(0, "</imageobject>" + lE + "</mediaobject>" + lE
				+ "</figure>" + lE);
		if(useDocumentation) { 
			postfix.append("<para>" + Utilities.getDocumentation(theDiagram) + "</para>"+lE);
		}

		String fileName = "";
		DiagramPresentationElement diagramPE = theUtilities.getPROJECT().getDiagram(theDiagram);
		// need to set the file to the same directory where the
		// final XML file will be stored.
		
		// [HM] get options
		Application application = Application.getInstance(); 
		EnvironmentOptions options = application.getEnvironmentOptions(); 
		MBSEOptionsGroup mbse_environment_options = (MBSEOptionsGroup) options.getGroup("options.mbse");
		DiagramGraphicsFormat dgf = mbse_environment_options.getDiagramGraphicsFormat();
		
		fileName = cleanUpString4FileName(Utilities.shortUniqueID(theDiagram)) + dgf.getFileExtension();
		File imageFile = new File(baseDir, fileName);
		scheduleImageFileForDeletion(imageFile);

		sContent += "<imagedata fileref=\""
				+ imageFile.getName() + "\" width=\"" + width 
				+ "%\" scalefit=\"1\" align=\"center\"/>" + lE;
		
		// due to  	MDUMLCS-13047 the diagram, if of type dependency matrix, needs to be closed first.
		// this might be applicable also for other auto-computed diagrams, but I cannot know it now

		if(StereotypesHelper.hasStereotype(theDiagram, theUtilities.getTheMDDiagramTableStereotype())) { 
			if (MatrixDataHelper.isRebuildNeeded(theDiagram )) {
					  MatrixDataHelper.buildMatrix(theDiagram );
			} // I suspect this will modify the model, but we have no alternatives for the time being.	
		}	
		
		
		try {
			ImageExporter.export(diagramPE, dgf.getValueImageExporter(),
					imageFile, false);
			logDebugIndent(el,"\tWrote to file "
					+ imageFile.getName());
		} catch (java.io.IOException ioe) {
			//
			displayWarning(ioe.toString() + lE + fileName);
		}
		return sContent;
	}

	private void processParagraph(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		String sContent = null;
		String token = null;
		String pattern = "<head>.+</head>";
		Matcher ma = null;
		Pattern pa = Pattern.compile(pattern, Pattern.DOTALL);
		boolean lDebug = false;
		boolean HTML = false;

		// enter here all required postprocessing of HTML
		sContent = ((Comment) el).getBody();

		sContent = Utilities.transformSpecialCharacter(sContent);

		if (sContent.indexOf("<html>") != -1) {
			token = tokenNameHTML;
			HTML = true;
			logDebug(lDebug," [ contains HTML ] ");
			prefix.insert(0,"");
			postfix.insert(0, "");
			sContent = Utilities.convertHTML2DocBook(sContent, false);
			//
			ma = pa.matcher(sContent);
			// content = content.replaceAll("<head></head>","");
			if (ma.find()) {
				sContent = ma.replaceFirst("");
			}

		} else {
			token = tokenName;

			logDebug(lDebug," [ does not contain HTML ] ");
			prefix.insert(0,"<para annotations=\"paragraph-no-html\">" + lE);
			// logDebug("Size is:" + value.size());
			postfix.insert(0,"</para>" + lE);
		}

		logDebugIndent(el," is Paragraph");
		
		
		List value = StereotypesHelper.getStereotypePropertyValue(
				el, theUtilities.getTheParagraphStereotype(), "xref");
		sContent = processXref(value,sContent,token);
		// precaution against no xref in the element
		if (value.size() == 0) {
			sContent = sContent.replaceAll(token, "UNDEFINED TOKEN");
		}
		// final check that the content contains a paragraph construct
		if (!sContent.contains("<para") && HTML) {
			sContent = "<para annotations=\"last-resort\">" + sContent + "</para>" + lE;
		}
		content.insert(0, sContent);

	}
	
	private void processProgramListing(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		String sContent = null;

		boolean lDebug = false;

		// enter here all required postprocessing of HTML
		sContent = ((Comment) el).getBody();

		sContent = sContent.replaceAll("&", "&amp;");
		sContent = sContent.replaceAll("<", "&lt;");
		sContent = sContent.replaceAll(">", "&gt;");
		//sContent = Utilities.transformSpecialCharacter(sContent);
		// sContent = Utilities.convertHTML2DocBook(sContent, false);


		logDebug(lDebug," [ does not contain HTML ] ");
		prefix.insert(0,"<programlisting annotations=\"\">" + lE);
		postfix.insert(0,"</programlisting>" + lE);


		logDebugIndent(el," is programlisting");

		content.insert(0, sContent);

	}
	
	
	private void processFigureDiagram(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		Diagram theDiagram = null;
		int width = 100, contentdepth = 100;
		Object widthObj = null, contentdepthObj = null;
		Utilities.TEXTUSAGEKIND useText;
		String sContent = "";

		Object diagramObject = StereotypesHelper.getStereotypePropertyFirst(
						el, theUtilities.getTheFigureDiagramStereotype(), "diagram");
		logDebugIndent(el," is Figure Diagram");
		useText = theUtilities.isTextToBeUsed(el, theUtilities.getTheFigureDiagramStereotype(), "useText");

		if (diagramObject != null) {
			widthObj = StereotypesHelper.getStereotypePropertyFirst(
					el, theUtilities.getTheFigureDiagramStereotype(),"width");
			if (widthObj != null) {
				width = (Integer) widthObj;
			}
			contentdepthObj = StereotypesHelper.getStereotypePropertyFirst(
					el, theUtilities.getTheFigureDiagramStereotype(), "contentdepth");
			if (contentdepthObj != null) {
				contentdepth = (Integer) contentdepthObj;
			}

			if (diagramObject instanceof Diagram) {
				theDiagram = (Diagram) diagramObject;
				String captionText = "";
				logDebugIndent(el,"\tFound a diagram ("
						+ theDiagram.getName() + ")");
				boolean useDocumentation = false;
				Object captionTextObj = StereotypesHelper.getStereotypePropertyFirst(
						el, theUtilities.getTheFigureDiagramStereotype(),"captionText");
				
				if (captionTextObj != null) {
					captionText += (String) captionTextObj;
				} else {
					captionText += theDiagram.getName();
				}
				
				if(useText == Utilities.TEXTUSAGEKIND.none) {
					// the text of the Comment itself is not request, let's see now if the documentation from 
					// the diagram is
					Object useDocumentationObj = StereotypesHelper.getStereotypePropertyFirst(
							el, theUtilities.getTheFigureDiagramStereotype(),"useDocumentation");
					if(useDocumentationObj != null) {
						useDocumentation = (Boolean)useDocumentationObj;
					}

				}
				if (useText == Utilities.TEXTUSAGEKIND.before) {
					prefix.insert(0,"<para>" + Utilities.convertHTML2DocBook(((Comment) el).getBody(), false) + "</para> "+lE);
				}
				sContent = provideGeneralPurposeImageReferenceContent(el, prefix, postfix,
						sContent, captionText, theDiagram,width, useDocumentation);

				if (useText == Utilities.TEXTUSAGEKIND.after) {
					postfix.append("<para>" + Utilities.convertHTML2DocBook(
							((Comment) el).getBody(), false) + "</para>" + lE);
				}

			} else {
				displayWarning("Figure tag does not point to a Diagram?? ("
						+ el.getHumanName()
						+ ":"
						+ diagramObject.toString() + ")");
				// pech gehabt.
			}
		} else {
			System.out
					.println("\tWARNING: FigureDiagram has empty diagram"
							+ el.getHumanName()
							+ " "
							+ el.getHumanType()
							+ " in:  "
							+ el.getOwner().getHumanName());

		}
		content.insert(0, sContent);
	}
	
	
	private void processFigureImage(Element el, StringBuffer prefix, StringBuffer postfix, StringBuffer content, List navigateDown) { 
		Diagram theDiagram = null;
		Class theReferencedClass = null;
		int width = 100, contentdepth = 100;
		Object widthObj = null, contentdepthObj = null;
		String sContent = "";
		Utilities.TEXTUSAGEKIND useText;
		
		Object classObject = StereotypesHelper
				.getStereotypePropertyFirst(el,
						theUtilities.getTheFigureImageStereotype(),
						"imageContainer");

		logDebugIndent(el," is Figure Image");

		useText = theUtilities.isTextToBeUsed(el,
				theUtilities.getTheFigureImageStereotype(), "useText");
		if (classObject != null) {
			widthObj = StereotypesHelper
					.getStereotypePropertyFirst(el,
							theUtilities.getTheFigureImageStereotype(),
							"width");
			if (widthObj != null) {
				width = (Integer) widthObj;
			}
			contentdepthObj = StereotypesHelper
					.getStereotypePropertyFirst(el,
							theUtilities.getTheFigureImageStereotype(),
							"contentdepth");
			if (contentdepthObj != null) {
				contentdepth = (Integer) contentdepthObj;
			}

			if (classObject instanceof Class) {
				Class theClass = (Class) classObject;
				File imageFile = new File(baseDir, Utilities
						.shortUniqueID(theClass).replaceAll(" ", "")
						+ ".png");

				Icon icon = ElementImageHelper
						.getIconFromCustomImageProperty(theClass);
				if (icon != null) {
					Iterator<ImageWriter> writers = ImageIO
							.getImageWritersByFormatName("png");
					BufferedImage bufferedImage = new BufferedImage(
							icon.getIconWidth(), icon.getIconHeight(),
							BufferedImage.TYPE_INT_RGB);
					Graphics g = bufferedImage.getGraphics();
					icon.paintIcon(null, g, 0, 0);
					g.dispose();

					ImageWriter imageWriter = writers.next();
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					try {
						ImageOutputStream ios = ImageIO
								.createImageOutputStream(bytes);
						imageWriter.setOutput(ios);
						imageWriter.write(new IIOImage(bufferedImage,
								null, null));
						ios.close();
						imageWriter.dispose();

						BufferedOutputStream out = new BufferedOutputStream(
								new FileOutputStream(imageFile));
						out.write(bytes.toByteArray());
						out.close();
						String captionText = "";
						Object captionTextObj = StereotypesHelper
								.getStereotypePropertyFirst(
										el,
										theUtilities
												.getTheFigureImageStereotype(),
										"captionText");
						if (captionTextObj != null) {
							captionText += (String) captionTextObj;
						} else {
							captionText += theClass.getName();
						}
						prefix.insert(0,"<figure annotations=\"figureimage\" xml:id=\""
								+ Utilities.uniqueID(el) + "\">" + lE
								+ "<title>" + captionText + "</title>"
								+ "<mediaobject>" + lE
								+ "<imageobject>" + lE);

						if (useText == Utilities.TEXTUSAGEKIND.before) {
							prefix.insert(0,"<para>" + Utilities.convertHTML2DocBook(
									((Comment) el).getBody(), false) + "</para>"
									+ lE);
						}

						postfix.insert(0,"</imageobject>" + lE
								+ "</mediaobject>" + lE + "</figure>"
								+ lE);


						sContent += "<imagedata fileref=\""
								+ imageFile.getName()
								+ "\" width=\""
								+ width
								+ "%\" scalefit=\"1\" align=\"center\"/>"
								+ lE;
						if (useText == Utilities.TEXTUSAGEKIND.after) {
							postfix.append("<para>" + Utilities.convertHTML2DocBook(
									((Comment) el).getBody(), false) + "</para>"
									+ lE);
						}
						
						scheduleImageFileForDeletion(imageFile);
					} catch (IOException ioe) {
						displayWarning("Could not write to file:  ("
								+ ioe.toString() + ")");
					}

				} else {
					System.out
							.println("\tThe image container does not seem to contain an image!");
				}
			}
		}
		content.insert(0, sContent);
	}

	public static void setDebug(boolean debug) {
		Debug = debug;
	}
	
	@Override
	public void run(ProgressStatus ps) {
		theProgressStatus = ps;
		theProgressStatus.init("Generating...", 100);
		theProgressStatus.setIndeterminate(true);
		generate();
	}
	
	
	private void scheduleImageFileForDeletion(File image) { 
		imageFilesForDeletion.add(image);
		System.out.println("Scheduled " + image.getName() + " for cleanup.");
	}
	
	public void cleanUpImageFiles() {

		Application application = Application.getInstance(); 
		EnvironmentOptions options = application.getEnvironmentOptions(); 
		MBSEOptionsGroup mbse_environment_options = (MBSEOptionsGroup) options.getGroup("options.mbse");
		boolean delendaEst = mbse_environment_options.getImageDeleteMode();
		if(! delendaEst) { 
			System.out.println("Not deleting image files.");
			return;
		}
		for(File theFile: imageFilesForDeletion) { 
			if(theFile.exists()) { 
				theFile.delete();
				System.out.println("File:"+ theFile.getName() + " deleted.");
			}
		}
		for(File theFile: imageFilesForDeletion) { 
			if(theFile.exists()) { 
				imageFilesForDeletion.remove(theFile);
	
			}
		}

	}

}
