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
 *    $Id: Utilities.java 713 2015-02-11 13:40:54Z mzampare $
 */

package org.eso.sdd.mbse.doc.algo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.impl.EnumerationLiteralImpl;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;

public class Utilities {
	private Stereotype theParagraphStereotype = null;
	private Stereotype theChapterStereotype = null;
	private Stereotype theSectionStereotype = null;
	private Stereotype theQueryStereotype = null;
	private Stereotype theFigureImageStereotype = null;
	private Stereotype theFigureDiagramStereotype = null;
	private Stereotype theBookStereotype = null;
	private Stereotype theBlockStereotype = null;
	private Stereotype thePartStereotype = null;
	private Stereotype thePrefaceStereotype = null;
	private Stereotype theBibliographyStereotype = null;
	private Stereotype theBiblioEntryStereotype = null;
	private Stereotype theTableStereotype = null;
	private Stereotype theTableDiagramStereotype = null;
	private Stereotype theTableParagraphStereotype = null;
	private Stereotype theProgramListingStereotype = null;
	// from SysML
	private Stereotype theRequirementStereotype  = null;
	private Stereotype theFlowPropertyStereotype  = null;
	private Stereotype theRevisionEntryStereotype = null;
	private Stereotype theRevisionHistoryStereotype = null;
	
	
	// from MD
	private Stereotype theMDDiagramTableStereotype = null;
	
	private Profile docBookProfile = null;
	private Profile SysMLProfile = null;
	private Profile UMLStandardProfile = null;
	
	private List<Stereotype> theStereoCollection = null;

	public static String lE = System.getProperty("line.separator");
	private Project PROJECT = null;

	public enum TEXTUSAGEKIND {
		none, before, after
	};

	private Stereotype theAuthorStereotype = null;



	public Utilities() {

		setPROJECT(Application.getInstance().getProject());

		docBookProfile = StereotypesHelper.getProfile(Application.getInstance()
				.getProject(), "DocBookProfile");
		SysMLProfile = StereotypesHelper.getProfile(Application.getInstance()
				.getProject(), "SysML");
		UMLStandardProfile = StereotypesHelper.getProfile(Application.getInstance()
				.getProject(), "MagicDraw Profile");

		theStereoCollection = new ArrayList<Stereotype>();

		// DOCBOOK
		if (docBookProfile == null) {
			// LOG ERROR
			System.err.println("MBSE: DocBookProfile is null.");
		} else {
			theBookStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "book", docBookProfile);
			theStereoCollection.add(theBookStereotype);

			thePrefaceStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "preface", docBookProfile);
			theStereoCollection.add(thePrefaceStereotype);

			thePartStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "part", docBookProfile);
			theStereoCollection.add(thePartStereotype);

			theParagraphStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "paragraph", docBookProfile);
			theStereoCollection.add(theParagraphStereotype);

			theSectionStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "section", docBookProfile);
			theStereoCollection.add(theSectionStereotype);

			theChapterStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "chapter", docBookProfile);
			theStereoCollection.add(theChapterStereotype);

			theQueryStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "query", docBookProfile);
			theStereoCollection.add(theQueryStereotype);

			theFigureDiagramStereotype = StereotypesHelper.getStereotype(
					Application.getInstance().getProject(), "figureDiagram",
					docBookProfile);
			theStereoCollection.add(theFigureDiagramStereotype);

			theFigureImageStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "figureImage", docBookProfile);
			theStereoCollection.add(theFigureImageStereotype);

			theBibliographyStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "bibliography", docBookProfile);
			theStereoCollection.add(theBibliographyStereotype);

			theBiblioEntryStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "biblioEntry", docBookProfile);
			// not adding the above to the collection

			theTableStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "table", docBookProfile);

			theTableDiagramStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "tableDiagram", docBookProfile);
			
			theTableParagraphStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "tableParagraph", docBookProfile);
			
			theProgramListingStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "programListing", docBookProfile);

			theAuthorStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "author", docBookProfile);
			// not adding the above to the collection
			
			theRevisionEntryStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "revisionEntry", docBookProfile);
			theRevisionHistoryStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "revhistory", docBookProfile);
			theStereoCollection.add(theRevisionHistoryStereotype);
			
		}

		// SYSML 
		if (SysMLProfile == null) {
			// LOG ERROR
			System.err.println("MBSE: SysML Profile is null.");
		} else {
			theRequirementStereotype = StereotypesHelper.getStereotype (Application
					.getInstance().getProject(), "Requirement", SysMLProfile);
			theFlowPropertyStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "FlowProperty", SysMLProfile);

			theBlockStereotype = StereotypesHelper.getStereotype(Application
					.getInstance().getProject(), "Block",SysMLProfile );

		}


		theMDDiagramTableStereotype = StereotypesHelper.getStereotype(Application
				.getInstance().getProject(), "DiagramTable",UMLStandardProfile );
		
	}

	public List<Stereotype> getStereotypesList() {
		return theStereoCollection;

	}

	public static void insertElementInTaggedValueList(Element father,
			Element addendum, Element after, Stereotype theStereotype,
			String tagName) {
		List<Element> nList = StereotypesHelper.getStereotypePropertyValue(
				father, theStereotype, tagName, true);
		for (int i = 0; i < nList.size(); i++) {
			if (nList.get(i).equals(after)) {
				nList.add(i + 1, addendum);
				break;
			}
			// System.out.println("Position " + i + ":" +
			// ((Element)nList.get(i)).getHumanName());
		}
		StereotypesHelper.setStereotypePropertyValue(father, theStereotype, tagName, nList, false);
	}

	public static void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);
	}

	public Stereotype getTheTableDiagramStereotype() {
		// TODO Auto-generated method stub
		return theTableDiagramStereotype;
	}
	
	public Stereotype getTheMDDiagramTableStereotype() {
		return theMDDiagramTableStereotype;
	}
	
	public Stereotype getTheTableParagraphStereotype() {
		// TODO Auto-generated method stub
		return theTableParagraphStereotype;
	}
	
	public Stereotype getTheProgramListingStereotype() {
		// TODO Auto-generated method stub
		return theProgramListingStereotype;
	}
	

	public Stereotype getTheBlockStereotype() {
		return theBlockStereotype;
	}

	public Stereotype getTheBookStereotype() {
		return theBookStereotype;
	}

	public Stereotype getTheParagraphStereotype() {
		return theParagraphStereotype;
	}

	public Stereotype getThePartStereotype() {
		return thePartStereotype;
	}

	public Stereotype getTheChapterStereotype() {
		return theChapterStereotype;
	}

	public Stereotype getTheSectionStereotype() {
		return theSectionStereotype;
	}

	public Stereotype getTheQueryStereotype() {
		return theQueryStereotype;
	}

	public Stereotype getTheFigureImageStereotype() {
		return theFigureImageStereotype;

	}

	public Stereotype getTheFigureDiagramStereotype() {
		return theFigureDiagramStereotype;
	}

	public Stereotype getThePrefaceStereotype() {
		return thePrefaceStereotype;
	}

	public Stereotype getTableStereotype() {
		return theTableStereotype;
	}

	public Stereotype getTheBibliographyStereotype() {
		return theBibliographyStereotype;
	}

	public Stereotype getTheBiblioEntryStereotype() {
		return theBiblioEntryStereotype;
	}

	public Stereotype getTheRequirementStereotype() {
		return theRequirementStereotype;
	}
	
	public Stereotype getTheFlowPropertyStereotype() {
		return theFlowPropertyStereotype;
	}

	public Stereotype getTheRevisionHistoryStereotype() {
		return theRevisionHistoryStereotype;
	}
	public Stereotype getTheRevisionEntryStereotype() {
		return theRevisionEntryStereotype;
	}

	public static boolean isPackage(Element ne) {
		return ne.getHumanType().equals("Package");
	}

	public static boolean isComment(Element e) {
		return e instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
	}

	public static boolean isBook(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "book");
	}

	public static boolean isSection(Element ne) {	
		return StereotypesHelper.hasStereotype(ne, "section");
	}

	public static boolean isChapter(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "chapter");
	}

	public static boolean isParagraph(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "paragraph");
	}

	public static boolean isFigure(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "figure");
	}

	public static boolean isFigureDiagram(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "figureDiagram");
	}

	public static boolean isFigureImage(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "figureImage");
	}

	public static boolean isQuery(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "query");
	}

	public static boolean isRefentry(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "refentry");
	}

	public static boolean isPreface(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "preface");
	}

	public static boolean isReference(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "reference");
	}

	public static boolean isPart(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "part");
	}

	public static boolean isDivision(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "division");
	}

	public static boolean isPartintro(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "partintro");
	}

	public static boolean isComponent(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "component");
	}

	public static boolean isXReference(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "xreference");
	}

	public static boolean isRequirement(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "Requirement");
	}

	public static boolean isConstraintBlock(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "ConstraintBlock");
	}

	public static boolean isBibliography(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "bibliography");
	}

	public static boolean isRevisionHistory(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "revhistory");
	}
	
	public static boolean isRevisionEntry(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "revisionEntry");
	}

	public static boolean isBiblioEntry(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "biblioEntry");
	}

	public static boolean isTable(Element ne) {
		return StereotypesHelper.hasStereotype(ne, "table");
	}

	public static boolean isDiagramTable(Element el) {
		return StereotypesHelper.hasStereotypeOrDerived(el, "tableDiagram");
	}

	public static boolean isProgramListing(Element el) {
		return StereotypesHelper.hasStereotypeOrDerived(el, "programListing");
	}
	
	public static boolean isTableParagraph(Element el) {
		return StereotypesHelper.hasStereotypeOrDerived(el, "tableParagraph");
	}


	public static boolean hasDocumentation(Element el) {
		return el.has_commentOfAnnotatedElement();
	}

	public static boolean isAuthor(Element ne) {
		return StereotypesHelper.hasStereotypeOrDerived(ne, "author");
	}

	public static String getFirstElementString(Element theElement,
			Stereotype theStereotype, String key) {
		Object tmpObject = null;
		String retVal = "";
		if (!StereotypesHelper.getStereotypePropertyValue(theElement,
				theStereotype, key).isEmpty()) {

			tmpObject = StereotypesHelper.getStereotypePropertyValue(
					theElement, theStereotype, key).get(0);
			if (tmpObject instanceof EnumerationLiteralImpl) {
				retVal = ((EnumerationLiteralImpl) tmpObject).getName();
			} else {
				retVal = (String) tmpObject;
			}
		}
		if(retVal.indexOf("<html") == -1) { 
			return removeSpecialCharacters(retVal);			
		}
		return retVal;
	}

	public List<Author> getAuthorFromBook(Element ne, String key) {
		Author theAuthor = null;
		Object theElement = null;
		List<?> theFolksInvolved = StereotypesHelper
				.getStereotypePropertyValue(ne, this.getTheBookStereotype(),
						key);
		List<Author> auList = new ArrayList<Author>();
		Iterator<?> it = theFolksInvolved.iterator();

		for (; it.hasNext();) {
			theElement = it.next(); // theFolksInvolved.get(0);
			if (theElement instanceof Element && isAuthor((Element) theElement)) {
				String tmpString;
				theAuthor = new Author();
				// (Element)theElement,this.getTheAuthorStereotype(),"firstname";
				tmpString = getFirstElementString((Element) theElement,
						this.getTheAuthorStereotype(), "firstname");
				theAuthor.setFirstName(tmpString);

				tmpString = getFirstElementString((Element) theElement,
						this.getTheAuthorStereotype(), "surname");
				theAuthor.setSurName(tmpString);

				tmpString = getFirstElementString((Element) theElement,
						this.getTheAuthorStereotype(), "organization");
				theAuthor.setOrganization(tmpString);

				auList.add(theAuthor);
			}
		}
		return auList;
	}

	public Stereotype getTheAuthorStereotype() {
		return theAuthorStereotype;
	}

	// do NOT remove &, <, >, ', or " here because then HTML will not be properly
	// converted
	public static String transformSpecialCharacter(String content){
		content = content
				.replaceAll("\u00B9", "<superscript>1</superscript>") 
				.replaceAll("\u00B2", "<superscript>2</superscript>")
				.replaceAll("\u00B3", "<superscript>3</superscript>")
				.replaceAll("\u02C9", "<superscript>-</superscript>")
				.replaceAll("\u00B0", "<superscript>o</superscript>")
				.replaceAll("\u00AB", "&#171;")
				.replaceAll("\u00BB", "&#187;")
				.replaceAll("\u201C", "\"")
				.replaceAll("\u201D", "\"")
				.replaceAll("\u00ae", "&#174;")
				.replaceAll("\u00A9", "&#169;")
				.replaceAll("\u00C4", "&#196;")
				.replaceAll("\u00e4", "&#228;")
				.replaceAll("\u00DC", "&#220;")
				.replaceAll("\u00FC", "&#252;")
				.replaceAll("\u00d6", "&#214;")
				.replaceAll("\u03bc", "&micro;")
				.replaceAll("\u00F6", "&#246;");
		content = content.replaceAll("[^\\x20-\\x7e]", "");
		return content;
	}

	public static String removeSpecialCharacters(String content) {
		
		String[] charArray = {"\u00B9","\u00B2","\u00B3","\u02C9","\u00B0","\u00AB","\u00BB","\u201C","\u201D","\u00ae","\u00A9","\u00C4","\u00e4","\u00DC","\u00FC","\u00d6","\u00F6"};

		for(String sc: charArray) { 
			content = content.replaceAll(sc,"");
		}
		content = content.replaceAll("[^\\x20-\\x7e]", "")
				.replaceAll("\"", "")
				.replaceAll("'", "").replaceAll("&", "").replaceAll("<", "")
				.replaceAll(">", "");
		return content;

	}

	public static String replaceBracketCharacters(String input) {
		return transformSpecialCharacter (input.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;"));
	}

	
	public static String convertHTML2DocBook(String content, boolean noparas) {
		String paraRepStart = "";
		String paraRepEnd = "";
		
		String pattern = "<table([^\\>]+)id=\"\\w*\"";
		Matcher ma = null;
		Pattern pa = Pattern.compile(pattern, Pattern.DOTALL);
		// in case of non HTML we replace the brackets with &lt; and that like
		// but will all the HTML possibilities be spotted by this check?
		
		if(! ( content.contains("<html>") || content.contains("<body>") ) ) {
			content = replaceBracketCharacters(content);
		}
		
		if (!noparas) {
			paraRepStart = "<para annotations=\"html converted\">";
			paraRepEnd = "</para>" + lE;
		}

		content = content.replaceFirst("</body>", "").replaceFirst("</html>",
				"");
		content = content.replaceFirst("<body>", "").replaceFirst("<html>", "");

		content = content.replaceAll("<head>", "");
		content = content.replaceAll("</head>", "");
		// <font face="Times New Roman">
		content = content.replaceAll("<font[^\\>]*>", "");
		content = content.replaceAll("</font>", "");


		content = content.replaceAll("<strong[^\\>]*>", "<emphasis role=\"bold\">");
		content = content.replaceAll("</strong>", "</emphasis>");

		content = content.replaceAll("<h[1-6]+>", "<emphasis role=\"bold\">");
		content = content.replaceAll("</h[1-6]+>", "</emphasis>");

		content = content.replaceAll("<a [^\\>]*>", "<emphasis role=\"underline\">");
		content = content.replaceAll("</a>", "</emphasis>");
		
		ma = pa.matcher(content);
		
		if (ma.find()) {
				//ma.group
			content = ma.replaceAll("<table "+ma.group(1) );
		}

		
		

		// <style type="text/css">
		// content.replaceAll
		content = content.replaceAll("<style[^\\>]*>[\\w|\\W|\\n]*</style>", "");

		// I wish there were a special character groups for punctuation marks

		content = content.replaceAll("<p[\\s|\\w|:|;|\\.|=|\\-\"]*>",
				paraRepStart);

		// getting rid of divs
		content = content.replaceAll("<div[\\s|\\w|:|;|\\.|=|\\-\"]*>", "");
		content = content.replaceAll("</div>", "");

		// content = content.replaceAll("<p>","<para>");
		// content = content.replaceAll("</p","</para");
		content = content.replaceAll("</p>", paraRepEnd);

		content = content.replaceAll("<li>", "<listitem>" + lE + paraRepStart
				+ lE);
		content = content.replaceAll("</li>", paraRepEnd + lE + "</listitem>"
				+ lE);

		content = content.replaceAll("<ul>", "<itemizedlist>");
		content = content.replaceAll("</ul>", "</itemizedlist>");

		content = content.replaceAll("<ol>", "<orderedlist>");
		content = content.replaceAll("</ol>", "</orderedlist>");

		content = content.replaceAll("<b>", "<emphasis role=\"bold\">");
		content = content.replaceAll("</b>", "</emphasis>");

		content = content.replaceAll("<u>", "<emphasis role=\"underline\">");
		content = content.replaceAll("</u>", "</emphasis>");

		content = content.replaceAll("<i>", "<emphasis role=\"italic\">");
		content = content.replaceAll("</i>", "</emphasis>");
		
		content = content.replaceAll("<sup>", "<superscript>");
		content = content.replaceAll("</sup>", "</superscript>");
		

		content = content.replaceAll("<a href",
				"<link xmlns:ns1=\"http://www.w3.org/1999/xlink\" ns1:href");
		content = content.replaceAll("</a>", "</link>");

		content = content.replaceAll("&#8221;", "\"");
		content = content.replaceAll("&#8220;", "\"");
		content = content.replaceAll("&#8217;", "\'");
		content = transformSpecialCharacter (content);
		if(!noparas) { 
			content = content.replaceAll(paraRepStart+"[\\s]*"+paraRepEnd, "");
		}
		return content;
	}


	public static String uniqueID(Element el) {
		String retVal = "";
		if (el instanceof NamedElement) {
			retVal = ((NamedElement) el).getQualifiedName();
		} else {
			if (el.getOwner() instanceof NamedElement) {
				retVal = ((NamedElement) el.getOwner()).getQualifiedName()
						+ "_" + el.getID();
			}
		}
		retVal = retVal.replaceAll("\\W","_");
		//retVal = retVal.replace(' ','_');
		return removeSpecialCharacters(retVal);
	}

	public static String shortUniqueID(Element el) {
		String retVal = "";
		if (el instanceof NamedElement) {
			retVal = ((NamedElement) el).getName() + "_" + el.getID();
		} else {
			retVal = el.getID();
		}
		return removeSpecialCharacters(retVal);
	}

	public static String getDocumentation(Element el) {
		Iterator<?> it2 = null;
		String rval = "";
		for (it2 = el.get_commentOfAnnotatedElement().iterator(); it2.hasNext();) {
			Comment theComment = ((Comment) it2.next());
			;
			/*
			 * System.out.println("Found a comment as documentation: \""+
			 * theComment.getBody()+ "\" [" +
			 * theComment.getOwner().getHumanName() + "] for "+
			 * el.getHumanName());
			 */
			// unfortunately no method exists to understand if the comment is in HTML or not.
			rval += theComment.getBody();
		}	
		return sanitizeFOP(convertHTML2DocBook(rval, false));	
	}
	
	private static String sanitizeFOP(String s) {
		String retVal = s;
		String pattern = "<itemlist>.*</itemlist>";
		s = "<head>" + "<itemlist>" + "<listitem>content</listitem>" + "</itemlist>" +  "</head>" +  "<andagain>" ;
		Pattern pa = Pattern.compile(pattern,Pattern.DOTALL);
		Matcher m = pa.matcher(s);
		if (m.find()) { 
			
//			System.out.println("Matched (" + s + " with " + pattern);
//			System.out.println("");
//			System.out.println("Match: "+  m.group() + " starts at " +m.start() + " ends at " +  m.end());

			if(! m.group().contains("<listitem")) {
				//System.out.println("Needs to be removed");
				retVal = s.substring(0, m.start()) + s.substring(m.end());
				//System.out.println("NEW STRING: " + s);
			}
		} else {
			//System.out.println("Did NOT match ("+ s + ")");
		}

		return retVal;
	}
	

	public void setPROJECT(Project pROJECT) {
		PROJECT = pROJECT;
	}

	public Project getPROJECT() {
		return PROJECT;
	}

	public Profile getDocBookProfile() {
		return docBookProfile;
	}

	public TEXTUSAGEKIND isTextToBeUsed(Element el, Stereotype theStereotype,
			String tag) {
		// int rval = 0;
		Boolean rval = false;
		// a missing attribute means paragraph
		List<?> list = StereotypesHelper.getStereotypePropertyValue(el,
				theStereotype, tag);
		if (list.size() > 0) {
			Object eLit = null;
			eLit = list.get(0);

			if (eLit instanceof Boolean) {
				rval = (Boolean) eLit;
				if (rval.booleanValue()) {
					return TEXTUSAGEKIND.before;
				} else {
					return TEXTUSAGEKIND.none;
				}
			} else if (eLit instanceof EnumerationLiteral) {

				if (((EnumerationLiteral) eLit).getName().equals("before")) {
					return TEXTUSAGEKIND.before;
				} else if (((EnumerationLiteral) eLit).getName()
						.equals("after")) {
					return TEXTUSAGEKIND.after;
				} else {
					return TEXTUSAGEKIND.none;
				}
			} else {
				System.out
						.println("MBSE: isTextToBeUsed: there is not a Boolean in the useQueryText parameter of the query"
								+ el.getHumanName());
			}
		}
		return TEXTUSAGEKIND.none;
	}

	



}
