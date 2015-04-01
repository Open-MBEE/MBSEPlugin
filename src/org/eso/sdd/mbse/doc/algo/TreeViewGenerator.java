package org.eso.sdd.mbse.doc.algo;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.magicdraw.export.image.ImageExporter;
import javax.swing.Icon;

import org.eso.sdd.mbse.doc.actions.MBSEShowEditPanelAction;

import com.nomagic.uml2.ext.jmi.helpers.ElementImageHelper;
import com.nomagic.task.ProgressStatus;
import com.nomagic.task.RunnableWithProgress;

public class TreeViewGenerator implements RunnableWithProgress {

	private static String lE = System.getProperty("line.separator");
	private static int sectionDepth = 1;
	private static int biblioCounter = 1;
	private static String biblioAbbrev = "Same as parent's";
	private static boolean Debug = true;
	private static NamedElement theStartElement = null;

	private static DocBookNode root = null;
	private static DocBookNode parent = null;

	private boolean ABORTED;
	private int status = 2;
	private static Stereotype theBibliographyStereotype = null;
	private static Stereotype theBiblioEntryStereotype = null;
	private ProgressStatus theProgressStatus = null;

	private int chapCount = 0;
	private int partCount = 1;
	private int secCount = 1;
	private int subSecCount = 1;
	private int prefaceSecCount = 1;

	private int diagramTableCount = 1;
	private int imageCount = 1;
	private int diagCount = 1;

	// static String dirPath = System.getProperty("user.home") + "/MBSE/tmp";
	static String dirPath = com.nomagic.utils.Utilities.getTempDir();

	ArrayList<String> tester = new ArrayList<String>();

	private static Utilities theUtilities = null;

	public static void setStartElement(NamedElement theElement) {
		theStartElement = theElement;

	}

	public static void setMainNode(DocBookNode nd) {
		root = nd;

	}

	public DocBookNode generate() {
		ABORTED = false;
		theUtilities = new Utilities();
		theBibliographyStereotype = theUtilities.getTheBibliographyStereotype();
		theBiblioEntryStereotype = theUtilities.getTheBiblioEntryStereotype();

		if (!Utilities.isBook(theStartElement)) {
			displayWarning("This element " + theStartElement.getName()
					+ " is not a book, forget it!");
			return null;
		}

		sectionDepth = 1;
		recurseDocument(theStartElement, root);
		if (ABORTED) {
			System.out.println("** DocBook generation cancelled by user");
			status = 1;
		} else {
			System.out.println("DocBook generation COMPLETED");

		}
		return root;
	}

	/*
	 * @todo: is there a better way to create IDs?
	 */
	private void recurseDocument(Element el, DocBookNode r) {
		Iterator<Object> it3 = null;
		List<Object> navigateDown = null;
		String recIdent = "";
		recIdent = el.getHumanName() + " ";

		if (Debug) {
			System.out.print("*** recursing on: " + recIdent);
		} else {
			System.out.print("");
		}

		if (ABORTED) {
			return;
		} else if (theProgressStatus.isCancel()) {
			// displaying the warning is care of the Action
			ABORTED = true;
			return;
		}

		parent = r;

		String content = "";

		if (Utilities.isBook(el)) {

			Vector<Object> bcVect = new Vector<Object>();

			bcVect.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheBookStereotype(), "bookComponent"));
			if (bcVect.size() == 0) {
				System.out.println("Warning, book " + el.getHumanName()
						+ " has no components");
			}
			if (theUtilities.getTheBookStereotype() == null) {
				System.out
						.println("MBSE: Generate: Warning, book stereotype is empty!");
			}

			parent.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement(((NamedElement) el).getName());
			tmp.setID(el.getID());
			// //data.addElement(tmp);
			parent.setRep(tmp);
			parent.setElement(el);

			// initial attempt at composing different types of elements for
			// recursion.
			bcVect.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheBookStereotype(), "revhistory"));
			bcVect.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheBookStereotype(), "divisions"));
			navigateDown = bcVect;
			navigateDown.add(parent);

			if (Debug) {
				System.out.println(" is Book (" + bcVect.size() + ")");
			}
			;

		} else if (Utilities.isPart(el)) {

			String cont = Utilities.replaceBracketCharacters(((NamedElement) el)
					.getName());
			if (cont.length() > 70)
				cont = cont.substring(0, 70);

			DocBookNode nd = new DocBookNode(cont + " <<" + genUtility.getReferredType(el)
					+ ">>");
			nd.setType(genUtility.getReferredType(el));

			partCount = MBSEShowEditPanelAction.getChildCount(parent, "part");
			System.out.println("qwerty" + partCount);
			DocBookVector tmp = new DocBookVector();
			tmp.addElement("Part "
					+ partCount
					+ " : "
					+ Utilities.replaceBracketCharacters(((NamedElement) el)
							.getName()));
			nd.setCount(Integer.toString(partCount));
			tmp.setID(el.getID());
			// data.addElement(tmp);
			// partCount++;

			nd.setType("part");
			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			Vector<Object> tmpVe = new Vector<Object>();
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getThePartStereotype(), "components"));
			navigateDown = tmpVe;
			if (navigateDown != null) {
				navigateDown.add(nd);
			}

			if (Debug) {
				System.out.println(" is Part");
			}
			;

		} else if (Utilities.isChapter(el)) {

			int chapNo;

			String cont = Utilities.replaceBracketCharacters(((NamedElement) el)
					.getName());
			if (cont.length() > 70)
				cont = cont.substring(0, 70);

			DocBookNode nd = new DocBookNode(cont + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			if (parent.getType().equals("part")) {
				chapNo = MBSEShowEditPanelAction.getChildCount(parent, "chapter");
			} else {
				chapCount++;
				chapNo = chapCount;
			}

			DocBookVector tmp = new DocBookVector();
			tmp.addElement("Chapter "
					+ chapNo
					+ " : "
					+ Utilities.replaceBracketCharacters(((NamedElement) el)
							.getName()));
			nd.setCount(Integer.toString(chapNo));
			tmp.setID(el.getID());
			// data.addElement(tmp);
			secCount = 1;

			nd.setType("chapter");
			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			Vector<Object> tmpVe = new Vector<Object>();
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheChapterStereotype(), "blockelements"));
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheChapterStereotype(), "sections"));
			// initial attempt at composing different types of elements for
			// recursion.
			navigateDown = tmpVe;
			if (navigateDown != null) {
				navigateDown.add(nd);
			}

			// parent = nd;

			if (Debug) {
				System.out.println(" is Chapter (" + tmpVe.size() + ")");
			}

		} else if (Utilities.isSection(el)) {

			String cont = Utilities.replaceBracketCharacters(((NamedElement) el)
					.getName());
			if (cont.length() > 70)
				cont = cont.substring(0, 70);

			DocBookNode nd = new DocBookNode(cont + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();

			if (Utilities.isSection(el)
					&& Utilities.isSection(parent.getElement())) {
				String sct = parent.getCount() + "."
						+ subSecCount;

				tmp.addElement("Section "
						+ sct
						+ " : "
						+ Utilities.replaceBracketCharacters(((NamedElement) el)
								.getName()));
				nd.setCount(sct);
				subSecCount++;
			} else if (Utilities.isSection(el)
					&& Utilities.isPreface(parent.getElement())) {
				tmp.addElement("Section "
						+ prefaceSecCount
						+ " : "
						+ Utilities.replaceBracketCharacters(((NamedElement) el)
								.getName()));
				nd.setCount(Integer.toString(prefaceSecCount));
				prefaceSecCount++;
			} else {
				String sct = parent.getCount() + "." + secCount;

				tmp.addElement("Section "
						+ sct
						+ " : "
						+ Utilities.replaceBracketCharacters(((NamedElement) el)
								.getName()));
				nd.setCount(sct);
				secCount++;
			}

			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			Vector<Object> tmpVe = new Vector<Object>();

			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheSectionStereotype(), "blockelements"));
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheSectionStereotype(), "subsection"));
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheSectionStereotype(), "bibliography"));
			navigateDown = tmpVe;
			if (navigateDown != null) {
				navigateDown.add(nd);
			}

			if (Debug) {
				System.out.println(" is Section (" + tmpVe.size() + ")");
			}
			;
			sectionDepth++;
			System.out.println("Section depth increased to " + sectionDepth);

		} else if (Utilities.isRevisionHistory(el)) {

			DocBookNode nd = new DocBookNode(((NamedElement) el).getName()
					+ " <<" +  genUtility.getReferredType(el) + ">>");

			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement("Revision History : "
					+ ((NamedElement) el).getName());
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			Vector<Object> tmpVe = new Vector<Object>();
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheRevisionHistoryStereotype(),
					"revisionEntry"));
			navigateDown = tmpVe;

			if (navigateDown != null) {
				navigateDown.add(nd);
			}

		} else if (Utilities.isPreface(el)) {

			String cont = Utilities.replaceBracketCharacters(((NamedElement) el)
					.getName());
			if (cont.length() > 70)
				cont = cont.substring(0, 70);

			DocBookNode nd = new DocBookNode(cont + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement("Preface : "
					+ Utilities.replaceBracketCharacters(((NamedElement) el)
							.getName()));
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			// reset prefaceSectin count
			prefaceSecCount = 1;

			Vector<Object> tmpVe = new Vector<Object>();
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getThePrefaceStereotype(), "prefacePara"));

			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getThePrefaceStereotype(), "prefaceSection"));
			navigateDown = tmpVe;
			if (navigateDown != null) {
				navigateDown.add(nd);
			}
			// parent = nd;

			if (Debug) {
				System.out.println(" is Preface (" + tmpVe.size() + ")");
			}
			;

		} else if (Utilities.isBibliography(el)) {

			String cont = Utilities.replaceBracketCharacters(((NamedElement) el)
					.getName());
			if (cont.length() > 70)
				cont = cont.substring(0, 70);

			DocBookNode nd = new DocBookNode(cont + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement("Bibliography : "
					+ Utilities.replaceBracketCharacters(((NamedElement) el)
							.getName()));
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			Vector<Object> tmpVe = new Vector<Object>();
			biblioCounter = 1;
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheBibliographyStereotype(), "biblioEntry"));
			navigateDown = tmpVe;
			if (navigateDown != null) {
				navigateDown.add(nd);
			}
			// parent = nd;
			if (Debug) {
				System.out.println(" is Bibliography (" + tmpVe.size() + ") "
						+ biblioAbbrev);
			}
			;

		} else if (Utilities.isRevisionEntry(el)) {

			DocBookNode nd = new DocBookNode(((NamedElement) el).getName()
					+ " <<" +  genUtility.getReferredType(el) + ">>");

			nd.setType(genUtility.getReferredType(el));

			Stereotype revEntryStereo = theUtilities
					.getTheRevisionEntryStereotype();
			Stereotype authorStereo = theUtilities.getTheAuthorStereotype();
			NamedElement ne = (NamedElement) el;

			// find author
			Element theAuthor = null;
			String authorTxt = "";
			List bcVect = StereotypesHelper.getStereotypePropertyValue(ne,
					revEntryStereo, "author");
			if (!bcVect.isEmpty()) {
				theAuthor = (Element) (bcVect.get(0));

				authorTxt = "Author : "
						+ Utilities.getFirstElementString(theAuthor,
								authorStereo, "firstname")
						+ " "
						+ Utilities.getFirstElementString(theAuthor,
								authorStereo, "surname") + ", ";
			}

			DocBookVector tmp = new DocBookVector();
			tmp.addElement("Revision Entry : "
					+ ne.getName()
					+ ", "
					+ "Number : "
					+ Utilities.getFirstElementString(ne, revEntryStereo,
							"revnumber")
					+ ", "
					+ "Date : "
					+ Utilities.getFirstElementString(ne, revEntryStereo,
							"date")
					+ " "
					+ "Description : "
					+ Utilities.getFirstElementString(ne, revEntryStereo,
							"revdescription")
					+ ", "
					+ authorTxt
					+ "Remarks : "
					+ Utilities.getFirstElementString(ne, revEntryStereo,
							"revremark"));
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			Vector<Object> tmpVe = new Vector<Object>();
			tmpVe.addAll(StereotypesHelper.getStereotypePropertyValue(el,
					theUtilities.getTheRevisionHistoryStereotype(),
					"revisionEntry"));
			navigateDown = tmpVe;

			if (navigateDown != null) {
				navigateDown.add(nd);
			}

		} else if (Utilities.isParagraph(el)
				&& ((Comment) el).getBody() != null) {

			content = getParaContent(el);

			String treeNodeContent = content;

			if (treeNodeContent.indexOf("<html>") != -1) {

				String pattern = "<head>.+</head>";

				Matcher ma = null;
				Pattern pa = Pattern.compile(pattern, Pattern.DOTALL);
				ma = pa.matcher(treeNodeContent);

				if (ma.find()) {
					treeNodeContent = ma.replaceFirst("");
				}
			}
			treeNodeContent = treeNodeContent.replaceAll("\\<.*?>", "");
			if (treeNodeContent.length() > 70) {
				treeNodeContent = treeNodeContent.substring(0, 70);
				System.out.println("heck "
						+ treeNodeContent.replaceAll("\\<.*?>", ""));
			}

			DocBookNode nd = new DocBookNode(treeNodeContent.trim() + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement(content.trim() + "  ");
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

		} // end paragraph
		else if (Utilities.isProgramListing(el)
				&& ((Comment) el).getBody() != null)
		{
			content = getParaContent(el);

			String treeNodeContent = content;

			treeNodeContent = treeNodeContent.replaceAll("\\<.*?>", "");
			if (treeNodeContent.length() > 70) {
				treeNodeContent = treeNodeContent.substring(0, 70);
				System.out.println("heck "
						+ treeNodeContent.replaceAll("\\<.*?>", ""));
			}

			DocBookNode nd = new DocBookNode(treeNodeContent.trim() + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement("<pre>" + content.trim() + "  " + "</pre>");
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);
		} // end program listing
		else if (Utilities.isTableParagraph(el)
				&& ((Comment) el).getBody() != null) {

			content = ((Comment) el).getBody();

			DocBookNode nd = new DocBookNode("tableParagraph" + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement(content.trim() + "  ");
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

		} // end tableParagraph

		else if (Utilities.isFigure(el)) {

			if (Utilities.isFigureDiagram(el)) {

				String[] txt = getFigureDiagramText(el, false);
				
		
				DocBookNode nd = new DocBookNode(txt[0] + " <<"
						+  genUtility.getReferredType(el) + ">>");
				nd.setType(genUtility.getReferredType(el));
				nd.setImageName(txt[1]);

				DocBookVector tmp = new DocBookVector();
				tmp.addElement(null);
				tmp.setID(el.getID());
				tmp.setImage(null);
				tmp.setImageText("FigureDiagram " + diagCount + ":" + txt[0]);
				
			
				diagCount++;
				
				// create a new docbook node for the documentation of a diagram figure. It refers to the same
				// model element so all actions are related to the diagram figure
				String documentation = null;
				Utilities.TEXTUSAGEKIND  useText = theUtilities.isTextToBeUsed(el, theUtilities.getTheFigureDiagramStereotype(), "useText");
				documentation = getParaContent(el);
				tmp.setDocumentationText(documentation, useText);

				DocBookNode nd1 = new DocBookNode(txt[0] + " << doc of FigDiag >>");
				nd1.setType(genUtility.getReferredType(el));

				DocBookVector tmp1 = new DocBookVector();
				tmp1.addElement(documentation.trim() + "  ");
				tmp1.setID(el.getID());

				nd1.setRep(tmp1);
				nd1.setElement(el);
				
				if (useText == Utilities.TEXTUSAGEKIND.before) {
					parent.add(nd1);
				}

				nd.setRep(tmp);
				nd.setElement(el);
				parent.add(nd); 

				if (useText == Utilities.TEXTUSAGEKIND.after) {
					parent.add(nd1);	
				}
			} // end case is FigureDiagram

			if (Utilities.isFigureImage(el)) {

				String[] txt = getFigureImageText(el, false);

				DocBookNode nd = new DocBookNode(txt[0] + " <<"
						+  genUtility.getReferredType(el) + ">>");
				nd.setType(genUtility.getReferredType(el));

				DocBookVector tmp = new DocBookVector();
				tmp.addElement(null);
				tmp.setID(el.getID());
				tmp.setImage(null);
				tmp.setImageText("FigureImage " + imageCount + ":" + txt[0]);
				

				imageCount++;

				
				// create a new docbook node for the documentation of a figure image. It refers to the same
				// model element so all actions are related to the figure image
				String documentation = null;
				Utilities.TEXTUSAGEKIND  useText = theUtilities.isTextToBeUsed(el, theUtilities.getTheFigureImageStereotype(), "useText");
				documentation = getParaContent(el);
				tmp.setDocumentationText(documentation, useText);

				DocBookNode nd1 = new DocBookNode(txt[0] + " << doc of FigImg >>");
				nd1.setType(genUtility.getReferredType(el));

				DocBookVector tmp1 = new DocBookVector();
				tmp1.addElement(documentation.trim() + "  ");
				tmp1.setID(el.getID());

				nd1.setRep(tmp1);
				nd1.setElement(el);
				
				if (useText == Utilities.TEXTUSAGEKIND.before) {
					parent.add(nd1);
				}


				nd.setRep(tmp);
				nd.setElement(el);
				nd.setImageName(txt[1]);
				parent.add(nd);

				if (useText == Utilities.TEXTUSAGEKIND.after) {
					parent.add(nd1);	
				}

			} // end case is FigureImage

		} else if (Utilities.isDiagramTable(el)) {
			// the code in this if shall go in a separate method
			System.out.println(" isDiagramTable ()");

			String captionText = getDiagramTableText(el);

			DocBookNode nd = new DocBookNode(
					Utilities.replaceBracketCharacters(captionText + " <<"
							+  genUtility.getReferredType(el) + ">>"));
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement(null);
			tmp.setID(el.getID());
			tmp.setImage(null);
			tmp.setImageText("DiagramTable " + diagramTableCount + " : "
					+ captionText);

			// data.addElement(tmp);
			diagramTableCount++;

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

		} else if (Utilities.isBiblioEntry(el)) {

			String biblioEntryText = getBiblioentryText(el);

			if (biblioEntryText.length() > 70)
				biblioEntryText = biblioEntryText.substring(0, 70);

			DocBookNode nd = new DocBookNode(biblioEntryText + " <<"
					+  genUtility.getReferredType(el) + ">>");

			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();
			tmp.addElement("Biblioentry : " + biblioEntryText);
			tmp.setID(el.getID());
			// data.addElement(tmp);

			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);

			/*
			 * content += provideBiblioText((NamedElement) el, biblioAbbrev +
			 * Integer.valueOf(biblioCounter));
			 */
			if (Debug) {
				System.out.println(" is BiblioEntry ()");
			}
			;
			biblioCounter++;

		} else if (Utilities.isQuery(el)) {

			// insert refactored code here
			/*
			 * try { Query theQuery = new Query(el, Debug); content +=
			 * theQuery.provideDocBookForQuery(); } catch (NullPointerException
			 * e) { }
			 */

			String body = ((Comment) el).getBody();

			if (body.contains("</html>")) {

				String pattern = "<head\\b[^>]*>[^<]*(?:(?!</?object\\b)<[^<]*)*</head\\s*>";

				Matcher ma = null;
				Pattern pa = Pattern.compile(pattern, Pattern.DOTALL);
				ma = pa.matcher(body);

				if (ma.find()) {
					body = ma.replaceFirst("");
				}
			}

			String header;

			if (body.length() > 70) {
				header = body.replaceAll("\\<.*?>", "");
				int l = header.length();
				if (l > 70) l = 70;
				header = header.substring(0, l).trim();
			} else {
				header = body.replaceAll("\\<.*?>", "").trim();
			}

			DocBookNode nd = new DocBookNode(header + " <<"
					+  genUtility.getReferredType(el) + ">>");
			nd.setType(genUtility.getReferredType(el));

			DocBookVector tmp = new DocBookVector();

			tmp.addElement("Query : " + body/* .replaceAll("\\<;.*?&gt;", "") */);
			tmp.setID(el.getID());
			// data.addElement(tmp);
			
			nd.setRep(tmp);
			nd.setElement(el);
			parent.add(nd);
		} else { // end query case

			// UNHANDLED CASE
			System.out
					.println("This element is not handled with a known DocBook stereotype:"
							+ el.getHumanName());
			if (el instanceof Comment) {
				System.out.println("Unhandled case Comment: "
						+ el.getOwner().getHumanName());
			}
			// content = el.getHumanName();
		}

		System.out.println("foundtab " + el.getHumanType());

		if (navigateDown != null) {

			DocBookNode nav = null;
			nav = (DocBookNode) navigateDown.remove(navigateDown.size() - 1);

			boolean ch = true;

			for (it3 = navigateDown.iterator(); it3.hasNext();) {
				Element ownedElement = (Element) it3.next();
				System.out.println(ownedElement.getHumanType() + "Here");

				// restricting recursion to elements of type package or comment.
				if (genUtility.checkType(ownedElement)) {

					if (Utilities.isSection(el)
							&& Utilities.isSection(ownedElement) && ch) {

						System.out.println("awesome " + el.getHumanName()
								+ " == " + ownedElement.getHumanName() + "=="
								+ secCount);
						subSecCount = 1;
						ch = false;
					}

					if (nav != null) {
						recurseDocument(ownedElement, nav);
					}
				}
			}
		} else {
			// termination condition
			;
		}

		if (Utilities.isSection(el)) {
			sectionDepth--;
			System.out.println("Section depth decreased to " + sectionDepth);
		}
		if (Utilities.isBibliography(el)) {
			biblioCounter = 1;
			System.out.println("Biblio completed");
		}

		System.out.println("*** completed recursion on " + recIdent);
		;

	}

	

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		}
		
		System.err.println("Couldn't find file: " + path);
		return null;
	}

	public static BufferedImage getFigureDiagramBufferedImage(Element el) {
		Diagram theDiagram = null;
		Object widthObj = null;
		int width = 400;

		BufferedImage bufImage = null;

		Object diagramObject = StereotypesHelper.getStereotypePropertyFirst(el,
				theUtilities.getTheFigureDiagramStereotype(), "diagram");

		if (diagramObject != null) {

			if (diagramObject instanceof Diagram) {
				theDiagram = (Diagram) diagramObject;
				widthObj = StereotypesHelper.getStereotypePropertyFirst(el,
						theUtilities.getTheFigureDiagramStereotype(), "width");
				if (widthObj != null) {
					width = (Integer) widthObj;
				}
				System.out.println("imper " + width);

				// import image
				DiagramPresentationElement diagramPE = theUtilities
						.getPROJECT().getDiagram(theDiagram);

				try {
					bufImage = ImageExporter.export(diagramPE, 50);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);
					System.out.println(e.getMessage());
					displayWarning(sw.toString());
				}

			}
		} else {
			System.out.println("**RETURNED DIAGRAM OBJECT IS NULL FOR: " + el.getHumanName());
			displayWarning("**RETURNED DIAGRAM OBJECT IS NULL FOR: " + el.getHumanName());
		}
		return bufImage;
		/*
		 * if(bufImage.getHeight()>400 || bufImage.getWidth()>400){ float
		 * nPercentW = 0; float nPercentH = 0; float nPercent = 0;
		 * 
		 * nPercentW = ((float)bufImage.getWidth()/400); nPercentH =
		 * ((float)bufImage.getHeight()/400); nPercent = (nPercentW +
		 * nPercentH)/2;
		 * 
		 * double w = (bufImage.getWidth() / nPercent); double h =
		 * (bufImage.getHeight() / nPercent); System.out.println("imper width"+w
		 * +"height"+h); return resize(bufImage,(int)w,(int)h); }
		 * 
		 * return resize(bufImage,bufImage.getWidth(),bufImage.getHeight());
		 */
	}

	public static BufferedImage getDiagramTableBufferedImage(Element el) {
		Diagram theDiagram = null;
		BufferedImage bufImage = null;

		Object diagramObject = StereotypesHelper.getStereotypePropertyFirst(el,
				theUtilities.getTheTableDiagramStereotype(), "diagramTable");

		if (diagramObject != null) {

			if (diagramObject instanceof Diagram) {
				theDiagram = (Diagram) diagramObject;

				// import image
				DiagramPresentationElement diagramPE = theUtilities
						.getPROJECT().getDiagram(theDiagram);
				diagramPE.open();
				// diagramPE.ensureLoaded();

				try {
					bufImage = ImageExporter.export(diagramPE, 50);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					e.printStackTrace(pw);

					displayWarning(sw.toString());
				}

			}
		}
		return bufImage;
		/*
		 * if(bufImage.getHeight()>400 || bufImage.getWidth()>400){ float
		 * nPercentW = 0; float nPercentH = 0; float nPercent = 0;
		 * 
		 * nPercentW = ((float)bufImage.getWidth()/400); nPercentH =
		 * ((float)bufImage.getHeight()/400); nPercent = (nPercentW +
		 * nPercentH)/2;
		 * 
		 * double w = (bufImage.getWidth() / nPercent); double h =
		 * (bufImage.getHeight() / nPercent); System.out.println("imper width"+w
		 * +"height"+h); return resize(bufImage,(int)w,(int)h); }
		 * 
		 * return resize(bufImage,bufImage.getWidth(),bufImage.getHeight());
		 */
	}

	public static String getDiagramTableText(Element el) {
		Object diagramObject = StereotypesHelper.getStereotypePropertyFirst(el,
				theUtilities.getTheTableDiagramStereotype(), "diagramTable");
		String captionText = "";
		if (diagramObject != null) {
			if (diagramObject instanceof Diagram) {
				Object captionTextObj = StereotypesHelper
						.getStereotypePropertyFirst(el,
								theUtilities.getTheTableDiagramStereotype(),
								"captionText");
				Diagram theDiagram = null;
				theDiagram = (Diagram) diagramObject;

				if (captionTextObj != null) {
					captionText += (String) captionTextObj;
				} else {
					captionText += theDiagram.getName();
				}
			}
		}
		return captionText;
	}

	public static String getBiblioentryText(Element el) {
		return provideBiblioText((NamedElement) el, biblioAbbrev);
	}

	public static String[] getFigureImageText(Element el, boolean imp) {

		int width;
		Object widthObj = null;
		String imageName = null;

		Object classObject = StereotypesHelper.getStereotypePropertyFirst(el,
				theUtilities.getTheFigureImageStereotype(), "imageContainer");

		String captionText = "";

		if (classObject != null) {
			widthObj = StereotypesHelper.getStereotypePropertyFirst(el,
					theUtilities.getTheFigureImageStereotype(), "width");
			if (widthObj != null) {
				width = (Integer) widthObj;
				System.out.println("specified image width :" + width);
			}
			System.out.println(" Found an FigureImage dunno wat to do with it");

			if (classObject instanceof Class) {
				Class theClass = (Class) classObject;
				Object captionTextObj = StereotypesHelper
						.getStereotypePropertyFirst(el,
								theUtilities.getTheFigureDiagramStereotype(),
								"captionText");
				;
				if (captionTextObj != null) {
					captionText += (String) captionTextObj;
				} else {
					captionText += theClass.getName();
				}
			}

		}// clasobj null

		String[] txt = new String[2];
		txt[0] = captionText;
		txt[1] = imageName;

		return txt;
	}

	public static BufferedImage getFigureImageBufferedImage(Element el) {

		int width;
		Object widthObj = null;
		BufferedImage bufImage = null;

		Object classObject = StereotypesHelper.getStereotypePropertyFirst(el,
				theUtilities.getTheFigureImageStereotype(), "imageContainer");

		if (classObject != null) {
			widthObj = StereotypesHelper.getStereotypePropertyFirst(el,
					theUtilities.getTheFigureImageStereotype(), "width");
			if (widthObj != null) {
				width = (Integer) widthObj;
				System.out.println("specified image width :" + width);
			}
			System.out.println(" Found an FigureImage");
			// image
			Class theClass = (Class) classObject;

			Icon icon = ElementImageHelper
					.getIconFromCustomImageProperty(theClass);
			if (icon != null) {
				bufImage = new BufferedImage(icon.getIconWidth(),
						icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics g = bufImage.getGraphics();
				icon.paintIcon(null, g, 0, 0);
				g.dispose();

			}

		}// clasobj null

		if (bufImage.getHeight() > 400 || bufImage.getWidth() > 400) {
			float nPercentW = 0;
			float nPercentH = 0;
			float nPercent = 0;

			nPercentW = ((float) bufImage.getWidth() / 400);
			nPercentH = ((float) bufImage.getHeight() / 400);
			nPercent = (nPercentW + nPercentH) / 2;

			double w = (bufImage.getWidth() / nPercent);
			double h = (bufImage.getHeight() / nPercent);
			System.out.println("imper width" + w + "height" + h);
			return resize(bufImage, (int) w, (int) h);
		}

		return resize(bufImage, bufImage.getWidth(), bufImage.getHeight());
	}

	public static String[] getFigureDiagramText(Element el, boolean imp) {
		Diagram theDiagram = null;
		String fileName = "";
		// find stereotype

		Object diagramObject = StereotypesHelper.getStereotypePropertyFirst(el,
				theUtilities.getTheFigureDiagramStereotype(), "diagram");
		if (Debug) {
			System.out.println(" is Figure Diagram");
		}
		;

		String captionText = "";

		if (diagramObject != null) {

			if (diagramObject instanceof Diagram) {
				theDiagram = (Diagram) diagramObject;
				Object captionTextObj = StereotypesHelper
						.getStereotypePropertyFirst(el,
								theUtilities.getTheFigureDiagramStereotype(),
								"captionText");
				;
				if (captionTextObj != null) {
					captionText += (String) captionTextObj;
				} else {
					captionText += theDiagram.getName();
				}

			}
		}

		String[] txt = new String[2];
		txt[0] = captionText;
		txt[1] = fileName;

		return txt;
	}

	public static String getParaContent(Element el) {

		String content;

		content = ((Comment) el).getBody();

		if (content.indexOf("<html>") == -1) {
			content = content.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		return content;
	}

	private static void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);
	}

	public int getStatus() {
		return status;
	}

	private static String provideBiblioText(NamedElement ne, String abbrev) {
		String theResult = "Biblioentry id=\"" + Utilities.uniqueID(ne) + lE
				+ " Abbrevation :" + abbrev;
		theResult += " Title : "
				+ Utilities.getFirstElementString(ne, theBiblioEntryStereotype,
						"title");
		theResult += " Product Number :"
				+ Utilities.getFirstElementString(ne, theBiblioEntryStereotype,
						"productNumber");

		theResult += " Issue Number :"
				+ Utilities.getFirstElementString(ne, theBiblioEntryStereotype,
						"issueNumber");

		theResult += " Publication Date :"
				+ Utilities.getFirstElementString(ne, theBiblioEntryStereotype,
						"pubDate");

		return theResult;
	}

	public static BufferedImage resize(BufferedImage image, int width,
			int height) {
		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
				.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

	@Override
	public void run(ProgressStatus ps) {
		theProgressStatus = ps;
		theProgressStatus.init("Generating...", 100);
		theProgressStatus.setIndeterminate(true);
		generate();

	}

}
