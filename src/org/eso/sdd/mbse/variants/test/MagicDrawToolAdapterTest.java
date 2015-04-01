package org.eso.sdd.mbse.variants.test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;

import org.eso.sdd.mbse.variants.tooladapter.magicdraw.MagicDrawToolAdapter;
import org.junit.Before;
import org.junit.Test;

public class MagicDrawToolAdapterTest {

	private MagicDrawToolAdapter toolAdapter;

	@Before
	public void setUp() throws Exception {
		// create the adapter to the modeling tool.
		toolAdapter = new MagicDrawToolAdapter();
	}
	
	/**
	 *  Basic Flow Tests
	 */
	@Test
	public void testLoadProject() throws Exception{
		// Here's a project known to exist. Load it.
		String fileName = AllTests.FILENAME_0VARIATIONSPROJECT;
		toolAdapter.loadProject(fileName);
	}
	
	@Test
	public void testSaveProject() throws Exception {
		// Here's a project known to exist. Load it.
		String loadFileName = AllTests.FILENAME_0VARIATIONSPROJECT;
		toolAdapter.loadProject(loadFileName);
		
		// Create a temp file where this file can be saved to.
		File saveFile = File.createTempFile("vpl", null);
		// Get the file's file name, then delete it.
		String saveFileName = saveFile.getAbsolutePath();
		if(!saveFile.delete()) fail();
		
		// Save the project to the temporary location.
		toolAdapter.saveProject(saveFileName);
		// Check if the file exists.
		saveFile = new File(saveFileName);
		assertTrue(saveFile.exists());
		
		// Try to overwrite the file. This must be possible (silent overwrite).
		toolAdapter.saveProject(saveFileName);
		assertTrue(saveFile.exists());
	}
	
	/**
	 * Exception Flow Tests
	 */
	
	@Test(expected=FileNotFoundException.class)
	public void testLoadNonExistingProject() throws Exception{
		// Here's a project known not to exist.
		String fileName = AllTests.FILENAME_NONEXISTINGPROJECT;
		toolAdapter.loadProject(fileName);
	}

}