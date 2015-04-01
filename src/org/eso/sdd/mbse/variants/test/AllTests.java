package org.eso.sdd.mbse.variants.test;


import java.io.File;
import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import com.nomagic.magicdraw.commandline.CommandLine;

public class AllTests extends CommandLine {
	
	// Some magicdraw projects the tests can play around with
	public final static String FOLDER_MAGICDRAW_PROJECTS = "test\\variantmodels";
	public final static String FILENAME_0VARIATIONSPROJECT = FOLDER_MAGICDRAW_PROJECTS + "\\VariationsProject_0Variations.mdxml";
	public final static String FILENAME_2VARIATIONSPROJECT = FOLDER_MAGICDRAW_PROJECTS + "\\VariationsProject_2Variations.mdxml";
	public final static String FILENAME_NESTEDVARIATIONSASPECTPROJECT = FOLDER_MAGICDRAW_PROJECTS + "\\VariationsProject_2VariationsAspects.mdxml";
	public final static String FILENAME_MORETHAN1VARIATIONSASPECTPROJECT = FOLDER_MAGICDRAW_PROJECTS + "\\VariationsProject_MoreThan1VariationsAspect.mdxml";
	public final static String FILENAME_NONEXISTINGPROJECT = FOLDER_MAGICDRAW_PROJECTS + "\\VariationsProject_______ABCXYZLLLDDCIS.mdxml";
	
	public final static String NAME_VARIATIONS_MODEL = "VariationsModel";
	public final static String NAME_VARIATIONS_ASPECT = "SampleVariationsAspect";
	public final static String NAME_VARIATIONS_ASPECT_A = "SampleVariationsAspect_A";
	public final static String NAME_VARIATIONS_ASPECT_B = "SampleVariationsAspect_B";
	public final static String NAME_VARIATION_1 = "SampleVariation1";
	public final static String NAME_VARIATION_2 = "SampleVariation2";
	public final static String NAME_VARIANT_1_1 = "SampleVariant1";
	public final static String NAME_VARIANT_1_2 = "SampleVariant2";
	public final static String NAME_NESTED_VARIATIONS_ASPECT = "SampleNestedVariationsAspect";
	public final static String NAME_NESTED_VARIATION_1 = "SampleNestedVariation1";
	public final static String NAME_NESTED_VARIATION_2 = "SampleNestedVariation2";
	public final static String NAME_NESTED_VARIANT = "SampleNestedVariant";
	
	public final static String QNAME_VARIATIONS_ASPECT = NAME_VARIATIONS_MODEL + "::" + NAME_VARIATIONS_ASPECT ;
	public final static String QNAME_VARIATION_1 = QNAME_VARIATIONS_ASPECT + "::" + NAME_VARIATION_1;
	public final static String QNAME_VARIATION_2 = QNAME_VARIATIONS_ASPECT + "::" + NAME_VARIATION_2;
	public final static String QNAME_VARIANT_1_1 = QNAME_VARIATION_1 + "::" + "SampleVariant1";
	public final static String QNAME_VARIANT_1_2 = QNAME_VARIATION_1 + "::" + "SampleVariant2";
	
	public static void main(String[] args) {

		// launch MagicDraw
		new AllTests().launch(args);
	}

	@Override
	protected void run() {
		
		System.out.println("STARTING JUNIT TESTS. CURRENT DIRECTORY: " + new File(".").getAbsolutePath());
		
		Result result = org.junit.runner.JUnitCore
				.runClasses(
						VariationsAspectTest.class
						,MagicDrawToolAdapterTest.class);
		
		
		if(result.getFailureCount() > 0){
			System.out.println("Test Failures: ");
			List<Failure> failures = result.getFailures();
			for (Failure failure : failures) {
				System.out.println(failure.getDescription());
				System.out.println(failure.getTrace());
			}
		}else{
			System.out.println("ALL JUNIT TESTS SUCCESSFUL !");
		}
	}
}
