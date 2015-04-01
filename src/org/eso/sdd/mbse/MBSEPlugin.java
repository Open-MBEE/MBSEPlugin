/*
 * 
 *    (c) European Southern Observatory, 2011
 *    Copyright by ESO 
 *    All rights reserved
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA
 *    
 *    $Id: MBSEPlugin.java 709 2015-01-20 16:01:56Z mzampare $
 *
 */

package org.eso.sdd.mbse;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup;
import org.eso.sdd.mbse.safety.algo.HazardRiskListener;
import javax.jmi.reflect.RefObject;
import org.eso.sdd.mbse.utilities.MBSEDiagramConfigurator;

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.core.options.ProjectOptionsConfigurator;
import com.nomagic.magicdraw.core.project.ProjectEventListenerAdapter;
import com.nomagic.magicdraw.plugins.Plugin;
import com.nomagic.magicdraw.properties.FileProperty;
import com.nomagic.magicdraw.properties.PropertyResourceProvider;
import com.nomagic.magicdraw.properties.Property;

import com.nomagic.magicdraw.sysml.util.SysMLConstants;

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.transaction.TransactionManager;

/**
 * Example of using actions in MagicDraw UML. This example shows how to: Add
 * action to main menu. Add action to main toolbar. Add action to browser tree.
 * Add action to class diagram.
 * 
 * @version $Date: 2015-01-20 16:01:56 +0000 (Tue, 20 Jan 2015) $ $Revision:
 *          2954 $
 * @author Michele Zamparelli
 */
public class MBSEPlugin extends Plugin {
	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#init() Adding actions on plugin
	 *      init.
	 */

	private EnvironmentOptions.EnvironmentChangeListener mEnvironmentOptionsListener;
	
	private static final String pdfPropID = "PDF_XSL_PROPERTY_ID";
	private static final String pdfGroup = "MBDG";
	
	private final String DT_BDD = SysMLConstants.SYSML_BLOCK_DEFINITION_DIAGRAM;
	private final String DT_IBD = SysMLConstants.SYSML_INTERNAL_BLOCK_DIAGRAM;
	private final String DT_PMD = SysMLConstants.SYSML_PARAMETERIC_DIAGRAM;
	private final String DT_RD  = SysMLConstants.SYSML_REQUIREMENTS_DIAGRAM;
	private final String DT_PKD = SysMLConstants.SYSML_PACKAGE_DIAGRAM;
	private final String serverName = Application.getInstance().getEnvironmentOptions().getFloatingOptions().getFloatingServerName();

	@Override
	public void init() {
		ActionsConfiguratorsManager manager = ActionsConfiguratorsManager
				.getInstance();
		MBSEBrowserConfigurator mbseBrConfigurator = null;
		MBSEDiagramConfigurator mbseDiConfigurator = null;

		// maybe we could use only one configurator and add all the actions in
		// there.
		mbseBrConfigurator = new MBSEBrowserConfigurator();
		mbseDiConfigurator = new MBSEDiagramConfigurator();

		manager.addContainmentBrowserContextConfigurator(mbseBrConfigurator);
		if(serverName != null && serverName.contains("eso.org")) { 
			manager.addDiagramContextConfigurator(DT_BDD, mbseDiConfigurator);
			manager.addDiagramContextConfigurator(DT_IBD, mbseDiConfigurator);
		}
		// manager.addContainmentBrowserShortcutsConfigurator( mbseConfigurator
		// );
		/*
		 * THIS PART ADDED TO ATTEMPT ADDING A SEPARATE WINDOW TO SHOW THE
		 * PREVIEW NEVER REALLY SERIOUSLY ATTEMPTED ProjectWindowsManager
		 * windowsManager =
		 * Application.getInstance().getMainFrame().getProjectWindowsManager();
		 * WindowComponentInfo info = new WindowComponentInfo("MY_WINDOW",
		 * "My Window", null, WindowsManager.SIDE_SOUTH,
		 * WindowsManager.STATE_DOCKED, true);
		 */
		// windowsManager.addWindow(new ProjectWindow(info, new
		// org.eso.sdd.mbse.doc.DocStructureTab ));

	    //PROJECT PROPERTY
        //configureProjectProp();
		// ENVIRONMENT CONFIGURATOR
		configureProjectEnv();
		configureListeners();

	}
	
	private void configureListeners() { 
        //add listener for getting events about opened project
        Application.getInstance().addProjectEventListener(new ProjectEventListenerAdapter()   {
            @Override
			public void projectOpened(Project project)  {
            	HazardRiskListener myListener = new HazardRiskListener();
                //add listener for getting events about opened diagram
            	if(project.getRepository().getEventSupport().isEnableEventFiring()) {
            		//project.getRepositoryListenerRegistry().addPropertyChangeListener(myListener, (RefObject)null);
            		TransactionManager transactionManager = project.getRepository().getTransactionManager();
            		transactionManager.addTransactionCommitListener(myListener);
            	} 
            }
        });

    }

		
		
	

	private void configureProjectProp() {
		ProjectOptions.addConfigurator(new ProjectOptionsConfigurator() {
			@Override
			public void configure(ProjectOptions projectOptions) {
				com.nomagic.magicdraw.properties.Property property = projectOptions
						.getProperty(ProjectOptions.PROJECT_GENERAL_PROPERTIES,
								pdfPropID);
				if (property == null) {
					System.out.println("sideways");
					// create property, if does not exist
					property = new FileProperty(pdfPropID,
							"lib/xsl/fo/ESOTransform.xsl");
					// group
					property.setGroup(pdfGroup);
					// custom resource provider
					property.setResourceProvider(new PropertyResourceProvider() {
						@Override
						public String getString(String string, Property property) {
							if (pdfPropID.equals(string)) {
								// translate ID
								return "XSL File";
							}
							if ("TEST_PROPERTY_ID_DESCRIPTION".equals(string)) {
								// translate description
								return "XSL File used for PDF generation.";
							}
							if (pdfGroup.equals(string)) {
								// translate group
								return "MBDG";
							}
							return string;
						}
					});
					// add property
					projectOptions
							.addProperty(
									ProjectOptions.PROJECT_GENERAL_PROPERTIES,
									property);
					
				}
			}

			public void afterLoad(ProjectOptions arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	private void configureProjectEnv() {

		Application application = Application.getInstance();
		EnvironmentOptions options = application.getEnvironmentOptions();
		options.addGroup(new MBSEOptionsGroup());

		mEnvironmentOptionsListener = new EnvironmentOptions.EnvironmentChangeListener() {
			@Override
			public void updateByEnvironmentProperties(List<Property> props) {
				System.out.println("Environment options changed:");

				for (Property p : props) {
					System.out.println(p);
				}				
			}
		};

		options.addEnvironmentChangeListener(mEnvironmentOptionsListener);

	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#close()
	 */
	@Override
	public boolean close() {
		return true;
	}

	/**
	 * @see com.nomagic.magicdraw.plugins.Plugin#isSupported()
	 */
	@Override
	public boolean isSupported() {
		return true;
	}

}
