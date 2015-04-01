package org.eso.sdd.mbse.test;

import com.nomagic.magicdraw.commandline.CommandLine;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.runtime.ApplicationExitedException;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.nomagic.magicdraw.uml.ElementFinder;

import org.eso.sdd.mbse.doc.algo.CommonGenerator;
import org.eso.sdd.mbse.doc.algo.genUtility;
import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup;
import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup.DiagramGraphicsFormat;

public class BatchRun extends CommandLine {

	protected File mDestinationDir;
	protected static String destDirName;

	protected static String expectedURL;
	protected static String obtainedURL;
	protected static String diffURL;
	

	private static final String xmlExt = ".xml";

	private static String[] projFiles = null;
	private static String[] bookNames = null;
	private boolean cleanupOutput = true;

	private EnvironmentOptions options = null;
	private MBSEOptionsGroup mbseEnvironmentOtions = null;
	
	public BatchRun(String[] bNames, String[] pFiles,String dName) {

		projFiles = pFiles;
		bookNames = bNames;
		destDirName = dName;
		
	}

	@Override
	protected void run() {
		
		
		System.out.println("Integration Tests Starting ("+projFiles.length+")");		
		for(int i= 0 ; i < projFiles.length; i++) { 
			Project project = null;
			Model theModel = null;
			NamedElement book = null;
			CommonGenerator theGenerator = null;
			XmlTester xmlTester = null;
			String mProjectFile = projFiles[i];
			String bookElementName = bookNames[i];
			File theProjectFile = null;
			String mProjFWOExt = mProjectFile.substring(0,mProjectFile.indexOf('.',0));

			mProjectFile = "samples" +File.separator+mProjectFile;
			obtainedURL = destDirName+File.separator+mProjFWOExt+"-integrationTest_out"+xmlExt;
			expectedURL = destDirName+File.separator+mProjFWOExt+"-integrationTest_ref"+xmlExt;
			diffURL     = destDirName+File.separator+mProjFWOExt+"-integrationTest_diff.txt";

			System.out.println("INTEGRATION TEST ["+(i+1)+ "] Starting for: ");
			System.out.println("  "+ mProjectFile);
			System.out.println("  "+ bookElementName);
			System.out.println("  "+obtainedURL);
			System.out.println("  "+expectedURL);

			options = Application.getInstance().getEnvironmentOptions();
			mbseEnvironmentOtions = (MBSEOptionsGroup) options.getGroup("options.mbse");

			mbseEnvironmentOtions.setDiagramGraphicsFormat(DiagramGraphicsFormat.PNG );
			
			theProjectFile = new File(mProjectFile);
			if(!theProjectFile.exists()) {
				System.out.println("ERROR: the input project file "+mProjectFile+ " does not exist. Bailing out\n");				
				continue;
			} else {
				System.out.println("Input project file "+mProjectFile+" found.");
			}
			if(!new File(expectedURL).exists()) { 
				System.out.println("WARNING: the reference XML file "+expectedURL+ " does not exist.\n");				
			}

			ProjectDescriptor projectDescriptor = 
					ProjectDescriptorsFactory.createProjectDescriptor(theProjectFile.toURI());

			System.out.println("Project descriptor loaded..");

			ProjectsManager projectsManager = 
					Application.getInstance().getProjectsManager();

			System.out.println("Project manager loaded..");

			// loads project
			projectsManager.loadProject(projectDescriptor, true);

			System.out.println("Project " + mProjectFile+ " loaded..");

			// initialize project and model
			project = projectsManager.getActiveProject();
			theModel = project.getModel();
			book = null;

			// get the book element
			
			book = (NamedElement)ElementFinder.find(theModel, Package.class,bookElementName,true);
			
			if(book == null) {
				System.out.println("ERROR: Could not find book named "+ bookElementName + " in project " + mProjectFile);
				continue;
			} else {
				System.out.println("Found book element..");				
			}


			// generate docbook
			theGenerator = new CommonGenerator();
			File obtainedFile = new File(obtainedURL);
			CommonGenerator.setDestFile(obtainedFile);
			CommonGenerator.setStartElement(book);
			CommonGenerator.setDebug(false);
			theGenerator.generate();
			//ProgressStatusRunner.runWithProgressStatus(theGenerator,
			//"Model Based Document Generation", false, 1000);

			projectsManager.closeProject();
			System.out.println("\n\nProject " + mProjectFile+  " closed..");
			if(! obtainedFile.exists() ) { 
				System.out.println("ERROR: the generated file "+obtainedURL+ " does not exist. Bailing out\n");
				continue;
			}
			System.out.println("Doc " + obtainedURL + " generated..");
			
			System.out.println("Performing integration test rep vs. ref..");

			// performing comparison test
			xmlTester = new XmlTester("test-"+mProjectFile); // any name will actually do
			try {
				//k.checkDiff(oldURL, newURL, destinationDirName);
				if(xmlTester.checkSimilar(expectedURL, obtainedURL, destDirName)) { 
					System.out.println("PASSED ("+mProjFWOExt+")");	
				} else {
					System.out.println("FAILED ("+mProjFWOExt+")");
					xmlTester.dumpAllDifferences(diffURL);
					System.out.println("\tdifferences dumped in "+diffURL);					
				}
			} catch (Exception e) {
				e.printStackTrace();
	    		StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				sw.toString(); // stack trace as a string

				genUtility.displayWarning(sw.toString());
			}
			// deleting the obtained file.
			if(cleanupOutput) { 
				deleteFile(obtainedURL);
				deleteGeneratedImages();
			}
		
		} // end loop over projects

		try {
			Application.getInstance().shutdown();
		} catch (ApplicationExitedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		}
	}
	
	private void deleteFile(String fileName) { 
		File rep = new File(fileName);
		boolean success = rep.delete();
		if (!success) {
			System.out.println(fileName+ " Deletion failed.");
		} else {
			System.out.println(fileName+ " deleted.");
		}
	}
	
	private void deleteGeneratedImages() { 
		File outDir = new File(destDirName);
		final String ext = mbseEnvironmentOtions.getDiagramGraphicsFormat().getFileExtension();
		System.out.println("Deleting all *"+ext+" files ...");
		String[] delendaNames = outDir.list(new FilenameFilter() { 
			public boolean accept(File dir,String name) {
				if(name.endsWith(ext)) { 
					return true;
				} else {
					return false;
				}
			}
				
		});
		
		for(int i= 0; i < delendaNames.length; i++) { 
			// System.out.println(" About to delete: " + delendaNames[i]);
			deleteFile(destDirName+File.separator+ delendaNames[i]);
		}
	}

	public void setCleanUp(boolean b) {
		// TODO Auto-generated method stub
		cleanupOutput = b;
	}

}
