package org.eso.sdd.mbse.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class IntegrationTest {

	static BatchRun runner = null;
	
    private static String genFileName = "MBSE plugin Requirements and design"; //generated XML file name && name of book to test
	
    private static String destDir = "test"; //test directory of output.txt
    
	private static String[] projFiles = new String[4];
	private static String[] bookNames = new String[4];

    private static String cleanupOutputProperty = "CLEANUP";

	public static void main(String[] args) {
		
		//test setUp
		//copyfile(oldURL,newURL);

		System.out.println("Running batch test...");
		
		projFiles[0] = "ModelBasedDocumentGenerationUserManual.mdzip";
		bookNames[0] = "Model Based Document Generation User Manual";
		
		projFiles[1] = "VariantManagementUserManual.mdzip";
		bookNames[1] = "Variant Management User Manual";

		projFiles[2] = "SystemsReasonerUserManual.mdzip";
		bookNames[2] = "Systems Reasoner User Manual";
		
		projFiles[3] = "Cookbook for MBSE with SysML.mdzip";
		bookNames[3] = "Cookbook for MBSE with SysML";

		//running integration test
		//runner = new BatchRun(genFileName, projDest2 , destDir, oldURL, newURL);
		runner = new BatchRun(bookNames, projFiles , destDir);
		
      	ArrayList argsList = new ArrayList(Arrays.asList(args));
    	for (Iterator<String> it = argsList.iterator(); it.hasNext();)	    {
    		String arg = it.next();
    		if (checkArgument(arg, cleanupOutputProperty)) { 
    			it.remove();
    		}
    	}
		
    	if(System.getProperty(cleanupOutputProperty) != null) { 
    		if(System.getProperty(cleanupOutputProperty).equals("off")) { 
    			runner.setCleanUp(false);
    			System.out.println(" Output cleanup switched off");
    		} else { 
    			System.out.println(" Output cleanup switched on");    			
    		}
    	} else {
    		System.out.println(" cleanup property missing");    			    		
    	}

		
		runner.launch(args);
		
		System.out.println("Returned...");
	}


	   private static boolean checkArgument(String arg, String propertyName) {
		    String start = propertyName + "=";
		    if (arg.startsWith(start)) {
		       String s = arg.substring(start.length());
		       System.setProperty(propertyName, s);
		       return true;
		    }
		    return false;
	    }
		

	private static void checkIfFileExists(String srFile) {
		try {
		    File file = new File(srFile);

		    // Create file if it does not exist
		    boolean success = file.createNewFile();
		    if (success) {
		        // File did not exist and was created
		    } else {
		        // File already exists
		    }
		} catch (IOException e) {
		}
		
	}

}
