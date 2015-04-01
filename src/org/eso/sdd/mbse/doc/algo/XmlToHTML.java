package org.eso.sdd.mbse.doc.algo;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlToHTML {

	public XmlToHTML() {

	}

	public static void simpleTransform(String sourcePath, String xsltPath,
			String resultDir) throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance(
				"net.sf.saxon.TransformerFactoryImpl", null);
	
			Transformer transformer = tFactory.newTransformer(new StreamSource(
					new File(xsltPath)));

			transformer.transform(new StreamSource(new File(sourcePath)),
					new StreamResult(new File(resultDir)));
	
	}

	public static void main(String[] args) {

		
		  try {
			simpleTransform("test/SysML-DocBook.xml",
			  "lib/xsl2/xslt/base/html/docbook.xsl", "test/test.html");
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		}
		 

	}
}
