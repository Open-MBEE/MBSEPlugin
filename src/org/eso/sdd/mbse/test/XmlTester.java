package org.eso.sdd.mbse.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import org.custommonkey.xmlunit.*;

public class XmlTester extends XMLTestCase {

	protected String oldf = "C:/Users/Chakajkla/Desktop/IDP/Req Sysml/MBSE-Req.xml";
	protected String newf = "C:/Users/Chakajkla/Desktop/IDP/Req Sysml/SysML-DocBook.xml";

	private static String dest;
	private boolean lDebug = false;
	private List<Difference> allDiff = null;

	public XmlTester(String name) {
		super(name);
		
		//ignores whitespace and comments difference
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreComments(true);
		
	}

	public void testAllDifferences() throws Exception {

		String myControlXML = readFile(oldf);
		String myTestXML = readFile(newf);
		DetailedDiff myDiff = new DetailedDiff(compareXML(myControlXML,
				myTestXML));

		int ct = 1;
		// list the changes if exists
		allDiff = myDiff.getAllDifferences();
		for (int i = 0; i < allDiff.size(); i++) {
			if(lDebug) { System.out.println("Change " + ct + " : " + allDiff.get(i)); } 
			ct++;
		}

		// assert True if changes are reflected in the XML file
		assertNotSame(myDiff.toString(), 0, allDiff.size());
	}

	public boolean checkDiff(String expected, String obtained, String destDir)
			throws Exception {

		dest = destDir;

		String myExpectedXML = readFile(expected);
		String myObtainedXML = readFile(obtained);
		DetailedDiff myDiff = new DetailedDiff(compareXML(myExpectedXML,myObtainedXML));

		// list the changes if exists
		allDiff = myDiff.getAllDifferences();

		if (allDiff.size() == 0) {
			return false;
		} else {
			System.out.println("PASSED: Integration test passed - rep similar to ref.");
			return true;
		}

		// assert True if changes are reflected in the XML file
		// assertNotSame(myDiff.toString(), 0, allDiff.size());
	}
	
	
	public void dumpAllDifferences(String fileName) { 
		int ct = 1;
		for (int i = 0; i < allDiff.size(); i++) {
			if(lDebug) { System.out.println("Changes " + i + 1 + " : " + allDiff.get(i).toString()); }
			writeFile(fileName,"Change " + ct + " : " + allDiff.get(i).toString() + "\n\n\n\n\n");
			ct++;
		}
		writeFile(fileName,"====================================================="
				+ "\n\n\n\n\n");

	}

	public boolean checkSimilar(String expected, String obtained, String destDir)
			throws Exception {

		dest = destDir;

		String myExpectedXML = readFile(expected);
		String myObtainedXML = readFile(obtained);
		DetailedDiff myDiff = new DetailedDiff(compareXML(myExpectedXML,myObtainedXML));

		// list of changes if exists
		allDiff = myDiff.getAllDifferences();
		
		if (allDiff.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void writeFile(String fileName, String text) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
			writer.write(text);
			writer.close();

		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private static String readFile(String filePath) throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
}
