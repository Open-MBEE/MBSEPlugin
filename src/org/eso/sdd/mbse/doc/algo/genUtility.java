package org.eso.sdd.mbse.doc.algo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

public class genUtility {

	public genUtility() {

	}

	public static void displayWarning(String text) {
		JOptionPane.showMessageDialog(MDDialogParentProvider.getProvider()
				.getDialogParent(), text);
		
		
	}
	
	public static boolean checkType(Element ownedElement) {

		if (Utilities.isPackage(ownedElement)
				|| Utilities.isComment(ownedElement)
				|| ownedElement.getHumanType().equals("Class")
				|| Utilities.isChapter(ownedElement)
				|| Utilities.isSection(ownedElement)
				|| Utilities.isParagraph(ownedElement)
				|| Utilities.isAuthor(ownedElement)
				|| Utilities.isBibliography(ownedElement)
				|| Utilities.isRevisionHistory(ownedElement)			
				|| Utilities.isRevisionEntry(ownedElement)							
				|| Utilities.isPart(ownedElement)
				|| Utilities.isBiblioEntry(ownedElement)
	            || Utilities.isQuery(ownedElement)
				|| Utilities.isFigureDiagram(ownedElement)
				|| Utilities.isFigureImage(ownedElement)
			    || Utilities.isDiagramTable(ownedElement)
			    || Utilities.isTableParagraph(ownedElement)) {

			return true;
		}

		return false;
	}
	
	public static String getReferredType(Element ownedElement) {
		if (Utilities.isChapter(ownedElement)) {
			return "chapter";
		}
		if (Utilities.isBook(ownedElement)) {
			return "book";
		}
		if (Utilities.isPreface(ownedElement)) {
			return "preface";
		}
		if (Utilities.isSection(ownedElement)) {
			return "section";
		}
		if (Utilities.isParagraph(ownedElement)) {
			return "paragraph";
		}
		if (Utilities.isProgramListing(ownedElement)) {
			return "programListing";
		}
		if (Utilities.isAuthor(ownedElement)) {
			return "author";
		}
		if (Utilities.isBibliography(ownedElement)) {
			return "biblioEntry";
		}
		if (Utilities.isRevisionHistory(ownedElement)) {
			return "revisionHistory";
		}
		if (Utilities.isRevisionEntry(ownedElement)) {
			return "revisionEntry";
		}
		if (Utilities.isPart(ownedElement)) {
			return "part";
		}
		if (Utilities.isBiblioEntry(ownedElement)) {
			return "biblioEntry";
		}
		if (Utilities.isQuery(ownedElement)) {
			return "query";
		}
		if (Utilities.isFigureDiagram(ownedElement)) {
			return "figureDiagram";
		}
		if (Utilities.isFigureImage(ownedElement)) {
			return "figureImage";
		}
		if (Utilities.isDiagramTable(ownedElement)) {
			return "tableDiagram";
		}
		if (Utilities.isTableParagraph(ownedElement)) {
			return "tableParagraph";
		}

		return "";
	}

	public static boolean checkMatch(DocBookNode parent, DocBookNode child) {

		if (Utilities.isBook(parent.getElement())
				&& (       Utilities.isChapter(child.getElement())
						|| Utilities.isPreface(child.getElement())
						|| Utilities.isPart(child.getElement()) )){
			return true;
		}

		else if (Utilities.isPreface(parent.getElement())
				|| Utilities.isPart(parent.getElement())
				|| Utilities.isChapter(parent.getElement())
				   && (Utilities.isSection(child.getElement())
				    || Utilities.isParagraph(child.getElement()))) {
			return true;
		}
		
		else if ((Utilities.isChapter(parent.getElement())
				|| Utilities.isSection(parent.getElement()))
				&& (Utilities.isQuery(child.getElement())
						|| Utilities.isFigureImage(child.getElement())
						|| Utilities.isFigureDiagram(child.getElement())
						|| Utilities.isParagraph(child.getElement())
						||Utilities.isDiagramTable(child.getElement()))){
			return true;
		}

		else if (Utilities.isSection(parent.getElement())
				&& ( Utilities.isBibliography(child.getElement())
				||Utilities.isSection(child.getElement()))){ //subsection
			return true;		
		}
		
		else if (Utilities.isBibliography(parent.getElement())
				&& Utilities.isBiblioEntry(child.getElement())){
			return true;		
		}
		
		else if (Utilities.isPart(parent.getElement()) 
				&& (Utilities.isChapter(child.getElement())||Utilities.isPreface(child.getElement()))){
			return true;		
		}
		
		return false;
	}

	public static void main(String[] args) {

		/*//final String srcOne = "<html asdhisadohso wew=\\sdasds dsasdosdsdsad> shit <head>\r\n<script src=\"http://test.com/some.js\"/>\r\n</head>head</html>";
		final String srcTwo = "<html xmlns=http://www.w3.org/1999/xhtml><head><meta http-equiv=Content-Type content=text/html; charset=utf-8 /><title>???TITLE???</title><meta name=generator content=DocBook XSL 2.0 Stylesheets V2.0.0 /><link rel=stylesheet type=text/css href=http://docbook.github.com/latest/css/default.css /></head>";
		//final String tag = "<html>\r\n<head>\r\n<script>\r\nfunction() {\r\n\talert('hi');\r\n}\r\n</script>\r\n</head></html>";
		//final String tagAndSrc = "<html>\r\n<head>\r\n<script src=\"http://test.com/some.js\">\r\nfunction() {\r\n\talert('hi');\r\n}\r\n</script>\r\n</head></html>";

		StringBuilder test = getContents(new File(
				"test/ModelBasedDocumentGeneration-DocBook.html"));

		test.trimToSize();

		final String regex = "<head\\b[^>]*>[^<]*(?:(?!</?object\\b)<[^<]*)*</head\\s*>";
		final String regex2 = "<html[^>]*>";
		final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE
				| Pattern.DOTALL);
		final Pattern pattern2 = Pattern.compile(regex2,
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		Matcher matcher = pattern.matcher(srcTwo);

		String resultString = null;
		while (matcher.find()) {
			resultString = matcher.replaceAll("");
			matcher = pattern.matcher(resultString);
		}
		Matcher matcher2 = pattern2.matcher(resultString);
		while (matcher2.find()) {
			resultString = matcher2.replaceAll("");
			matcher2 = pattern2.matcher(resultString);
		}
		// System.out.println("<html>" + resultString);

		test.trimToSize();
		String html = test.toString();
		int startIndex = html.indexOf("<body>");
		html = html.substring(startIndex);

		System.out.println("<html><head></head>" + html);

		try {
			// Create file
			FileWriter fstream = new FileWriter(
					"test/ModelBasedDocumentGeneration-DocBook.html");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<html><head></head>" + html);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}*/

		String content = "<html><head></head><body><table cellpadding='0' width='100%' cellspacing='0'><tr> <td>head1 </td><td>head2</td></tr><tr> <td>row2,col1</td><td>row2,col2</td></tr><tr> <td>row3,col1</td><td>row3,col2</td></tr></table></body></html>";

		String singleRow = content.substring(content.indexOf("<tr>"),content.indexOf("</tr>"));
		int numRow = content.split("\\Q"+"<tr>"+"\\E", -1).length - 1;
		int numColumn = singleRow.split("\\Q"+"<td>"+"\\E", -1).length - 1;;
		
		System.out.println(provideTableParagraphContent(content));
		
		for(String k:singleRow.split("</td>")){

			System.out.println(k.replaceAll("\\<.*?>", "").trim());
		}
		
	
		
	}

	static public StringBuilder getContents(File aFile) {
		// ...checks on aFile are elided
		StringBuilder contents = new StringBuilder();

		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(aFile));
			try {
				String line = null; // not declared within while loop

				while ((line = input.readLine()) != null) {
					// System.out.println(line);
					// removeBadHTML(line);
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		}

		return contents;
	}

	public static ArrayList<JMenuItem> getMBSEMenu(Element ne) {

		ArrayList<JMenuItem> mItems = new ArrayList<JMenuItem>();

		if(Utilities.isSection(ne)){
			mItems.add(new JMenuItem("create Section"));
			mItems.add(new JMenuItem("create Paragraph"));
			mItems.add(new JMenuItem("create ProgramListing"));
			mItems.add(new JMenuItem("create Bibliography"));
			mItems.add(new JMenuItem("create Query"));
			mItems.add(new JMenuItem("create FigureDiagram"));
			mItems.add(new JMenuItem("create FigureImage"));
			mItems.add(new JMenuItem("create tableDiagram"));
			mItems.add(new JMenuItem("create tableParagraph"));
		}
		else if(Utilities.isChapter(ne)){
			mItems.add(new JMenuItem("create Section"));
			mItems.add(new JMenuItem("create ProgramListing"));
			mItems.add(new JMenuItem("create Paragraph"));
			mItems.add(new JMenuItem("create FigureDiagram"));
			mItems.add(new JMenuItem("create FigureImage"));
			mItems.add(new JMenuItem("create tableDiagram"));
			mItems.add(new JMenuItem("create tableParagraph"));
		}
		else if(Utilities.isBibliography(ne)){
			mItems.add(new JMenuItem("create Biblioentry"));
		}
		else if(Utilities.isParagraph(ne) || Utilities.isQuery(ne)){
			mItems.add(new JMenuItem("insert Paragraph after"));
			mItems.add(new JMenuItem("insert Query after"));
		}
		else if(Utilities.isPreface(ne)){
			mItems.add(new JMenuItem("create Section"));
			mItems.add(new JMenuItem("create Paragraph"));
			mItems.add(new JMenuItem("create ProgramListing"));
			mItems.add(new JMenuItem("create tableParagraph"));
		}
		else if(Utilities.isPart(ne)){
			mItems.add(new JMenuItem("create Chapter"));
			mItems.add(new JMenuItem("create Preface"));
		}
		else if(Utilities.isBook(ne)){
			mItems.add(new JMenuItem("create Chapter"));
			mItems.add(new JMenuItem("create Preface"));
			mItems.add(new JMenuItem("create Part"));
			mItems.add(new JMenuItem("create Revision History"));
		}
		else if(Utilities.isRevisionHistory(ne)){
			mItems.add(new JMenuItem("create Revision entry"));
		}
		
		return mItems;
	}
	
private static String provideTableParagraphContent(String content) {

        
		String tablePrefix = "<table " + "xml:id=\""
				+ "asdasds" + "\" "
				+ "frame=\"all\"><title> TableParagraph </title>";
		
		int numRow = 0;
		int numColumn = 0;
		
		String firstRow = content.substring(content.indexOf("<tr>"),content.indexOf("</tr>"));
		
		numRow = content.split("\\Q"+"<tr>"+"\\E", -1).length - 1;
		numColumn = firstRow.split("\\Q"+"<td>"+"\\E", -1).length - 1;;
		
        content = content.substring(content.indexOf("</tr>")+5,content.length());
        System.out.println(content);
		String[] eachRowContent = content.split("</tr>");
		System.out.println(eachRowContent.length + " " + numRow);
		
		String tableTGroup = "<tgroup cols=\""+numColumn+"\" align=\"left\" colsep=\"1\" rowsep=\"1\">";
		
		String tableHeaderStart = "<thead><row>";
		
		String tableHeaderBody = "";
		
		String[] header = firstRow.split("</td>");
		
		for(String k:header){
			tableHeaderBody += "<entry align=\"center\">" + k.replaceAll("\\<.*?>", "").trim() + "</entry>";
		}
				
		tableHeaderBody += "</row></thead>";
		
		String tableBody = "<tBody>";
		
		  for(int i = 0;i<numRow-1;i++){
			  tableBody += "<row>";
			  String pS = eachRowContent[i];
			  String[] rower = pS.split("</td>");
			  
			  for(String j:rower){
				  tableBody += "<entry align=\"center\">" + j.replaceAll("\\<.*?>", "").trim() + "</entry>";
					
			  }
			  tableBody += "</row>";
		  }
		  
		  tableBody += "</tBody>";  
		
		String tableEnd = "</tgroup></table>";
		
		
		return tablePrefix + tableTGroup + tableHeaderStart + tableHeaderBody + tableBody  + tableEnd;
	}
}
