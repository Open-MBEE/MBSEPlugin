package org.eso.sdd.mbse.doc.algo;

//Java
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

//JAXP
import javax.swing.JOptionPane;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

//FOP
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.task.ProgressStatus;
import com.nomagic.task.RunnableWithProgress;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;

public class xsltFOP implements RunnableWithProgress {

	/** Determines which targets shall be generated. */
	public enum GENERATION_MODE { XML_AND_PDF, PDF_ONLY, RTF_ONLY,XML_AND_RTF };

	private static File xmlfile = null;
	private static File outputfile = null;
	private static File rtffile = null;
	private static Object userObject = null;

	private ProgressStatus theProgressStatus = null;

	// private static boolean ABORTED = false;

	private static int status = 2;
	private final GENERATION_MODE genMode;

	public xsltFOP(GENERATION_MODE mode) {
		genMode = mode;
	}

	public static void setXMLFile(File xml) {
		xmlfile = xml;
	}

	public static void setPDFFile(File pdf) {
		outputfile = pdf;
	}

	public static void setRTFFile(File rtf) {
		outputfile = rtf;
	}

	public static void setUserObject(Object obj) {
		userObject = obj;
	}

	public int getStatus() {
		return status;
	}
	
	
	public void generateGeneric(String type) { 
		String tMIME = null;
		
		if(type.equals("PDF")) { 
			tMIME = MimeConstants.MIME_PDF;
		} else {
			tMIME = MimeConstants.MIME_RTF;			
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		}// sleep for 1000 ms

		if (theProgressStatus.isCancel()) {
			System.out.println("** DocBook "+ type +" generation cancelled by user");
			status = 1;
			return;
		} else {

			CommonGenerator theGenerator = new CommonGenerator();
		

			if(GENERATION_MODE.XML_AND_PDF == genMode  || 
					GENERATION_MODE.XML_AND_RTF == genMode  ) {
				CommonGenerator.setDestFile(xmlfile);
				CommonGenerator.setStartElement((NamedElement) userObject);
				theGenerator.generate();
			}

			try {
				System.out.println("FOP Transformation\n");
				System.out.println("Preparing...");
				
				Application application = Application.getInstance(); 
				EnvironmentOptions options = application.getEnvironmentOptions(); 
				MBSEOptionsGroup g = (MBSEOptionsGroup) options.getGroup("options.mbse"); 

				String fn = g.getTransformationPropertyValue(); 
				/* need to extract filename if it is a full path */ 
				String fileName = new File(fn).getName();
				File xsltfile = new File("lib/xsl/fo/" + fileName); 

				//JOptionPane.showMessageDialog(null, "lib/xsl/fo/" + fileName);

				System.out.println("Input: XML (" + xmlfile + ")");
				System.out.println("Stylesheet: " + xsltfile);
				System.out.println("Output: "+ type +" (" + outputfile + ")");
				System.out.println();
				System.out.println("Transforming...");

				// configure fopFactory as desired
				FopFactory fopFactory = FopFactory.newInstance();

				FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
				// configure foUserAgent as desired

				// Setup output
				OutputStream out = new java.io.FileOutputStream(outputfile);
				out = new java.io.BufferedOutputStream(out);

				try {
					

					// Setup XSLT
					TransformerFactory factory = TransformerFactory
							.newInstance();
					Transformer transformer = factory
							.newTransformer(new StreamSource(xsltfile));
					
					//test run to get total pagecount
					Fop fop = fopFactory.newFop(tMIME,	foUserAgent, out);
					Source src = new StreamSource(xmlfile);
					Result res = new SAXResult(fop.getDefaultHandler());
					transformer.transform(src, res);

					//Get total page count
				      String pageCount = "UNAVAILABLE"; 
				      if(fop.getResults() != null) { 
				    	  	pageCount = Integer.toString(fop.getResults().getPageCount()); 
				      }
				      System.out.println("totalcc "+ pageCount);
				      		      
				     //real run
					 // Construct fop with desired output format
					fop = fopFactory.newFop(tMIME,	foUserAgent, out);
						
                       //set draft parameter
						String draftImageFn = new File("lib/xsl/images/draft.png").getAbsolutePath();
						transformer.setParameter("draft.watermark.image", draftImageFn); 
						System.out.println(transformer.getParameter("draft.watermark.image"));
						
						if (g.isDraftMode())    { 
						        transformer.setParameter("draft.mode", "yes"); 
						} 	else  { 
							transformer.setParameter("draft.mode", "no"); 
						} 
						
						transformer.setParameter("draft.mode", pageCount); 
						
						//set pagecount parameter
						  transformer.setParameter("ebnf.statement.terminator", pageCount); 


						// Resulting SAX events (the generated FO) must be piped
						// through
						// to FOP
						res = new SAXResult(fop.getDefaultHandler());

						// Start XSLT transformation and FOP processing
						transformer.transform(src, res);
				      
				      
				} finally {
					out.close();
					theGenerator.cleanUpImageFiles();
					status = 0;
				}

				System.out.println("Success!");
				System.out.println("DocBook "+type+" generation COMPLETED");
				
			} catch (Exception e) {
				e.printStackTrace(System.err);
	    		StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);

				genUtility.displayWarning("Character encoding exception of bad XSL file:\n" + sw.toString());
				status = 2;
				return;
			}
		}
	}

	public void genPDF() {
		generateGeneric("PDF");
	}

	public void genRTF() {
		generateGeneric("RTF");
	}


	@Override
	public void run(ProgressStatus ps) {
		theProgressStatus = ps;
		theProgressStatus.init("Generating...", 100);
		theProgressStatus.setIndeterminate(true);
		if(genMode == GENERATION_MODE.PDF_ONLY || 	genMode == GENERATION_MODE.XML_AND_PDF) {
			genPDF();
		} else {
			genRTF();
		}
	}	

}
