package org.eso.sdd.mbse.variants.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eso.sdd.mbse.variants.control.XMLConfigurationManager;
import org.eso.sdd.mbse.variants.domain.Configuration;
import org.eso.sdd.mbse.variants.domain.Variant;
import org.eso.sdd.mbse.variants.domain.Variation;
import org.eso.sdd.mbse.variants.domain.VariationsAspect;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.MagicDrawToolAdapter;
import org.junit.Before;
import org.junit.Test;

public class VariationsAspectTest {

	private MagicDrawToolAdapter toolAdapter;
	private XMLConfigurationManager manager;
	
	@Before
	public void setUp() throws Exception {
		// create the adapter to the modeling tool.
		toolAdapter = new MagicDrawToolAdapter();
		
		// Create the manager for configurations.
		manager = new XMLConfigurationManager();
	}

	/**
	 *  Basic Flow Tests
	 */
	
	@Test
	public void testGetVariations() throws Exception{
		// Create access to the tool and the variant configuration.
		Configuration configuration = manager.createConfiguration(toolAdapter, AllTests.FILENAME_2VARIATIONSPROJECT);
		
		// Check if the code actually creates two Variation instances for the "two variations project"
		List<VariationsAspect> variationsAspectList = configuration.getVariationsAspects();
		for (VariationsAspect variationsAspectIt : variationsAspectList){
			Collection<Variation> variations = variationsAspectIt.getVariations();
			assertEquals(2, variations.size());
			
			// Check if the two Variation instances are correctly named
			Iterator<Variation> it = variations.iterator();
			String variation1 = it.next().getName();
			String variation2 = it.next().getName();
			assertEquals(AllTests.NAME_VARIATION_1, variation1);
			assertEquals(AllTests.NAME_VARIATION_2, variation2);
		}
	}	

	@Test
	public void testGetVariants() throws Exception{
		// Create access to the tool and the variant configuration.
		Configuration configuration = manager.createConfiguration(toolAdapter, AllTests.FILENAME_2VARIATIONSPROJECT);
		
		// Access the variations.
		//VariationsAspect variationsAspect = configuration.getRootVariationsAspect();
		List<VariationsAspect> variationsAspectList = configuration.getVariationsAspects();
		for (VariationsAspect variationsAspectIt : variationsAspectList){
			Collection<Variation> variations = variationsAspectIt.getVariations();
			assertEquals(2, variations.size());
	
			// Get the variants of the first variation.
			Iterator<Variation> variationsIt = variations.iterator();
			Variation variation1 = variationsIt.next();
			List<Variant> variantsOfV1 = variation1.getVariants();
			
			// Assert that there are actually two variants for the first variation.
			assertEquals(2, variantsOfV1.size());
			
			// Check if the two Variation instances are correctly named
			Iterator<Variant> variantsOfV1It = variantsOfV1.iterator();
			String variant1_1 = variantsOfV1It.next().getName();
			String variant1_2 = variantsOfV1It.next().getName();	
			assertEquals(AllTests.NAME_VARIANT_1_1, variant1_1);
			assertEquals(AllTests.NAME_VARIANT_1_2, variant1_2);
			
			// Check if the second variation contains no variants.
			Variation variation2 = variationsIt.next();
			List<Variant> variantsOfV2 = variation2.getVariants();
			assertEquals(0, variantsOfV2.size());
		}
	}
	
	@Test
	public void testGetVariationsAspects() throws Exception{
		// Create access to the tool and the variant configuration.
		Configuration configuration = manager.createConfiguration(toolAdapter, AllTests.FILENAME_MORETHAN1VARIATIONSASPECTPROJECT);
		
		// Access the variations.
		List<VariationsAspect> variationsAspectList = configuration.getVariationsAspects();
		assertEquals(2, variationsAspectList.size());
		Iterator<VariationsAspect> variationsAspectIt = variationsAspectList.iterator();
		String variationsAspectA = variationsAspectIt.next().getName();
		String variationsAspectB = variationsAspectIt.next().getName();
		assertEquals(AllTests.NAME_VARIATIONS_ASPECT_A, variationsAspectA);
		assertEquals(AllTests.NAME_VARIATIONS_ASPECT_B, variationsAspectB);
	}

	@Test
	public void testGetNestedVariationAspects() throws Exception{
		// Create access to the tool and the variant configuration.
		Configuration configuration = manager.createConfiguration(toolAdapter, AllTests.FILENAME_NESTEDVARIATIONSASPECTPROJECT);
		
		// Access the variations.
		List<VariationsAspect> variationsAspectList = configuration.getVariationsAspects();
		for (VariationsAspect variationsAspect : variationsAspectList){
			Collection<Variation> variations = variationsAspect.getVariations();
			// Get the variants of the first variation.
			Iterator<Variation> variationsIt = variations.iterator();
			Variation variation1 = variationsIt.next();
			List<Variant> variantsOfV1 = variation1.getVariants();
			
			// Assert that there are actually 2 variants for the first variation.
			assertEquals(2, variantsOfV1.size());
			
			// Access the first variant
			Iterator<Variant> variantsOfV1It = variantsOfV1.iterator();
			Variant variant1_1 = variantsOfV1It.next();
			assertNotNull(variant1_1);
			Variant variant1_2 = variantsOfV1It.next();
			assertNull(variant1_2.getVariationAspect());
			
			// Check whether the first variant has a nested variation aspect
			// and that nested aspect has the right name.
			VariationsAspect nestedVariationAspect = variant1_1.getVariationAspect();
			assertNotNull(nestedVariationAspect);
			assertEquals(AllTests.NAME_NESTED_VARIATIONS_ASPECT, nestedVariationAspect.getName());
			
			// Get the nested variations.
			List<Variation> nestedVariations = nestedVariationAspect.getVariations();
			assertEquals(2, nestedVariations.size());
			Variation sampleNestedVariation1 = nestedVariations.get(0);
			Variation sampleNestedVariation2 = nestedVariations.get(1);
			assertEquals(AllTests.NAME_NESTED_VARIATION_1, sampleNestedVariation1.getName());
			assertEquals(AllTests.NAME_NESTED_VARIATION_2, sampleNestedVariation2.getName());
			assertEquals(0, sampleNestedVariation1.getVariants().size());
			assertEquals(1, sampleNestedVariation2.getVariants().size());
			
			// Get the nested variant.
			Variant nestedVariant = sampleNestedVariation2.getVariants().get(0);
			assertEquals(AllTests.NAME_NESTED_VARIANT, nestedVariant.getName());
		}
	}
	
	/**
	 * Exception Flow Tests
	 */
	
	@Test
	public void testInvalidVariantModel() throws Exception{
		// Create access to the tool and the variant configuration.
		Configuration configuration = manager.createConfiguration(toolAdapter, AllTests.FILENAME_0VARIATIONSPROJECT);
		
		// Access the variations.
		List<VariationsAspect> variationsAspectList = configuration.getVariationsAspects();
		for (VariationsAspect variationsAspect : variationsAspectList){
			Collection<Variation> variations = variationsAspect.getVariations();
			assertEquals(0, variations.size());
		}
	}
}
