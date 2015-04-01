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
*/

package org.eso.sdd.mbse;

import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eso.sdd.mbse.doc.actions.CreateAuthorAction;
import org.eso.sdd.mbse.doc.actions.CreateBibliographyAction;
import org.eso.sdd.mbse.doc.actions.CreateBookAction;
import org.eso.sdd.mbse.doc.actions.CreateChapterAction;
import org.eso.sdd.mbse.doc.actions.CreateFigureDiagramAction;
import org.eso.sdd.mbse.doc.actions.CreateFigureImageAction;
import org.eso.sdd.mbse.doc.actions.CreateParagraphAction;
import org.eso.sdd.mbse.doc.actions.CreatePartAction;
import org.eso.sdd.mbse.doc.actions.CreatePrefaceAction;
import org.eso.sdd.mbse.doc.actions.CreateProgramListingAction;
import org.eso.sdd.mbse.doc.actions.CreateQueryAction;
import org.eso.sdd.mbse.doc.actions.CreateRevisionHistoryAction;
import org.eso.sdd.mbse.doc.actions.CreateSectionAction;
import org.eso.sdd.mbse.doc.actions.CreateTableDiagramAction;
import org.eso.sdd.mbse.doc.actions.CreateTableParagraphAction;
import org.eso.sdd.mbse.doc.actions.MBSEXMLAction;
import org.eso.sdd.mbse.doc.actions.MBSEPdfAction;
import org.eso.sdd.mbse.doc.actions.MBSERTFAction;
import org.eso.sdd.mbse.doc.actions.MBSEPdfDirectAction;
import org.eso.sdd.mbse.doc.actions.MBSERTFDirectAction;
import org.eso.sdd.mbse.doc.actions.MBSEShowEditPanelAction;

import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.reasoner.actions.MBSEApplyPatternAction;
import org.eso.sdd.mbse.reasoner.actions.MBSECostAction;
import org.eso.sdd.mbse.reasoner.actions.MBSEMassAction;
import org.eso.sdd.mbse.reasoner.actions.MBSEPowerAction;
import org.eso.sdd.mbse.reasoner.actions.MBSEUnapplyPatternAction;
import org.eso.sdd.mbse.safety.actions.MBSESafetyComputationAction;
import org.eso.sdd.mbse.safety.actions.MBSESafetyCreateTableAction;
import org.eso.sdd.mbse.templates.MBSEActionGetPartCatalogueTemplate;
import org.eso.sdd.mbse.templates.MBSEActionGetTemplate;
import org.eso.sdd.mbse.templates.MBSEActionRepairTemplate;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.MenuAction;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;



/**
 * Configurator for configuring actions manager used for browser tree popup and browser tree shortcuts.
 * @version $Date: 2015-03-20 16:15:58 +0000 (Fri, 20 Mar 2015) $ $Revision: 715 $
 * @author Michele Zamparelli
 */
public class MBSEBrowserConfigurator implements BrowserContextAMConfigurator, AMConfigurator {

	/**
	 * Action which should be added to the tree.
	 */
	private MDActionsCategory cat = null;
	
    private DefaultBrowserAction generateDocAction     = new MBSEXMLAction();
    private DefaultBrowserAction generatePDF     = new MBSEPdfAction();
    private DefaultBrowserAction generateRTF     = new MBSERTFAction();
    private DefaultBrowserAction generatePDFdirect     = new MBSEPdfDirectAction();
    private DefaultBrowserAction generateRTFdirect     = new MBSERTFDirectAction();
    private DefaultBrowserAction generateTree    = new MBSEShowEditPanelAction();
	private Utilities theUtilities = null;
	private org.eso.sdd.mbse.safety.algo.Utilities RAMSUtilities = null;
	private Logger logger = null;
	/**
	 * Creates configurator for adding given action.
	 * @param action action to be added to manager.
	 */
	public MBSEBrowserConfigurator()	{
		//this.ac = ac;
	       
        // ADDING ACTION TO CONTAINMENT BROWSER
    	logger = Logger.getLogger("org.eso.sdd.mbse");
    	// This request is enabled, because WARN >= INFO.
    	logger.setLevel(Level.DEBUG);
    	try {
    		logger.addAppender(new FileAppender(new PatternLayout(), "MBSE.log"));
    	} catch (IOException e) {
    		e.printStackTrace(); 
    	}
		
 
	}

	/**
	 * @see com.nomagic.magicdraw.actions.BrowserContextAMConfigurator#configure(com.nomagic.actions.ActionsManager, com.nomagic.magicdraw.ui.browser.Tree)
	 */
	@Override
	public void configure(ActionsManager mngr, Tree tree) 	{
		if(tree.getSelectedNode() == null) { 
			return;
		}
		ActionsCategory  cat = new ActionsCategory(null,null);

		if(Application.getInstance().getProject() == null) { 
			return;
		}
		theUtilities = new Utilities();
		RAMSUtilities = new org.eso.sdd.mbse.safety.algo.Utilities();
		
		final MDAction configureVariationsAction = new MenuAction(
				"org.eso.sdd.mbse.variants.tooladapter.magicdraw.MenuAction",
				org.eso.sdd.mbse.variants.gui.IGUILabels.TITLE);
     
        cat = new MDActionsCategory("MBSE","MBSE");
		cat.setNested(true);

		Object userObject = tree.getSelectedNode().getUserObject();		
		
		// for a Package we add the action to create a book
		// plus the getTemplate facility.
		//

		if ( userObject instanceof Package) { 
				if( ((Element)userObject).isEditable() && 
						! StereotypesHelper.hasStereotype(((Element) userObject), theUtilities.getStereotypesList() ) )  {
		        DefaultBrowserAction addBookAction     = new CreateBookAction();
				DefaultBrowserAction systemElementTemplateAction    = new MBSEActionGetTemplate();			
				DefaultBrowserAction partCatalogueTemplateAction    = new MBSEActionGetPartCatalogueTemplate();				
				DefaultBrowserAction addAuthorAction = new CreateAuthorAction();
				cat.addAction(systemElementTemplateAction);
				cat.addAction(partCatalogueTemplateAction);
				if(theUtilities.getTheBookStereotype() != null) { 
					cat.addAction(addBookAction);
					cat.addAction(addAuthorAction);
				}
				}
				if(RAMSUtilities.getTheHazardStereotype() != null) { 
					DefaultBrowserAction safetyComp  = new MBSESafetyComputationAction();
					DefaultBrowserAction safetyTable = new MBSESafetyCreateTableAction();
					cat.addAction(safetyComp);
					cat.addAction(safetyTable);
				}
		} 
		
		
		// for a Block we do the things which we can do with a Block !
		if (userObject instanceof Element && StereotypesHelper.hasStereotype(((Element) userObject), "Block")) { 
			DefaultBrowserAction templateAction    = new MBSEActionGetTemplate();
			DefaultBrowserAction repairTemplateAction    = new MBSEActionRepairTemplate(logger);
			DefaultBrowserAction costAction        = new MBSECostAction();
			DefaultBrowserAction powerAction       = new MBSEPowerAction();
			DefaultBrowserAction massAction       = new MBSEMassAction();
			DefaultBrowserAction reasonerAction       = new MBSEApplyPatternAction();
			DefaultBrowserAction unapplyAction       = new MBSEUnapplyPatternAction();

			if( ((Element)userObject).isEditable() )  {
				cat.addAction(templateAction);
				cat.addAction(repairTemplateAction);
			}		 		       
			cat.addAction(powerAction);
			cat.addAction(massAction);
			cat.addAction(costAction);
			cat.addAction(reasonerAction);
			cat.addAction(unapplyAction);
		} 
		
		if (userObject instanceof Element && StereotypesHelper.hasStereotype(((Element) userObject), "book")   
				 )  {
		       DefaultBrowserAction partAction              = new CreatePartAction();
		       DefaultBrowserAction chapterAction           = new CreateChapterAction();
		       DefaultBrowserAction prefaceAction           = new CreatePrefaceAction();
		       DefaultBrowserAction revisionHistoryAction   = new CreateRevisionHistoryAction();		       		       
		       if( ((Element)userObject).isEditable() ) {
		    	   cat.addAction(partAction);						
		    	   cat.addAction(chapterAction);						
		    	   cat.addAction(prefaceAction);	
		    	   cat.addAction(revisionHistoryAction);			    	   
		       }
		       cat.addAction(generateDocAction);
		       cat.addAction(generatePDFdirect);
		       cat.addAction(generateRTFdirect);
		       cat.addAction(generateTree);
		       cat.addAction(generatePDF);
		       cat.addAction(generateRTF);

		} 

		if (userObject instanceof Element && StereotypesHelper.hasStereotype(((Element) userObject), "part")   
				&& ((Element)userObject).isEditable() )  {
			   DefaultBrowserAction chapterAction     = new CreateChapterAction();
		       DefaultBrowserAction prefaceAction     = new CreatePrefaceAction();

		       cat.addAction(chapterAction);						
		       cat.addAction(prefaceAction);						
	        } 

		if (userObject instanceof Element && 
				 StereotypesHelper.hasStereotype(((Element) userObject), "chapter")					
				&& ((Element)userObject).isEditable() )  {
		       DefaultBrowserAction sectionAction         = new CreateSectionAction();
		       DefaultBrowserAction programListingAction  = new CreateProgramListingAction();
		       DefaultBrowserAction paragraphAction       = new CreateParagraphAction();		       
               DefaultBrowserAction figureImageAction     = new CreateFigureImageAction();
		       DefaultBrowserAction figureDiagramAction   = new CreateFigureDiagramAction();
		       DefaultBrowserAction tableDiagramAction    = new CreateTableDiagramAction();
		       DefaultBrowserAction tableParagraphAction  = new CreateTableParagraphAction();	
			   
		       cat.addAction(sectionAction);				
		       cat.addAction(paragraphAction);		       
		      // cat.addAction(queryAction);						
		       cat.addAction(figureImageAction);								       
		       cat.addAction(figureDiagramAction);	
		       cat.addAction(tableDiagramAction);
		       cat.addAction(tableParagraphAction);
		       cat.addAction(programListingAction);		       		       		       
	        } 

		if (userObject instanceof Element && 
				StereotypesHelper.hasStereotype(((Element) userObject), "preface") ) {
		       DefaultBrowserAction sectionAction         = new CreateSectionAction();
		       DefaultBrowserAction programListingAction  = new CreateProgramListingAction();
		       DefaultBrowserAction paragraphAction       = new CreateParagraphAction();		       
		       DefaultBrowserAction tableParagraphAction  = new CreateTableParagraphAction();	
			   
		       cat.addAction(sectionAction);				
		       cat.addAction(paragraphAction);	
		       cat.addAction(tableParagraphAction);
		       cat.addAction(programListingAction);		       		       		       
		}

		
		
		if (userObject instanceof Element && StereotypesHelper.hasStereotype(((Element) userObject), "section")
				&& ((Element)userObject).isEditable() )  {
		       DefaultBrowserAction paragraphAction       = new CreateParagraphAction();
		       DefaultBrowserAction programListingAction  = new CreateProgramListingAction();		       
		       DefaultBrowserAction figureImageAction     = new CreateFigureImageAction();
		       DefaultBrowserAction figureDiagramAction   = new CreateFigureDiagramAction();		       
		       DefaultBrowserAction queryAction           = new CreateQueryAction();
		       DefaultBrowserAction sectionAction         = new CreateSectionAction();
		       DefaultBrowserAction bibliographyAction    = new CreateBibliographyAction();		       
			   DefaultBrowserAction tableDiagramAction    = new CreateTableDiagramAction();	
			   DefaultBrowserAction tableParagraphAction  = new CreateTableParagraphAction();	
			   
		      // DefaultBrowserAction tableAction   = new org.eso.sdd.mbse.doc.ui.CreateTableAction();		       
			     
		       
		       cat.addAction(sectionAction);				
		       cat.addAction(paragraphAction);						
		       cat.addAction(bibliographyAction);								       
		       cat.addAction(queryAction);						
		       cat.addAction(figureImageAction);								       
		       cat.addAction(figureDiagramAction);
		       cat.addAction(tableDiagramAction);
		       cat.addAction(tableParagraphAction);
		       cat.addAction(programListingAction);		       		       		       
		      // cat.addAction(tableAction);	
	        } 

		if (userObject instanceof Element && 
				StereotypesHelper.hasStereotype(((Element) userObject), "bibliography") ) {
		       DefaultBrowserAction biblioEntryAction    = new org.eso.sdd.mbse.doc.actions.CreateBiblioEntryAction();
		       cat.addAction(biblioEntryAction);		       
		}
		
		if (userObject instanceof Element && 
				StereotypesHelper.hasStereotype(((Element) userObject), "revhistory") ) {
		       DefaultBrowserAction revisionEntryAction  = new org.eso.sdd.mbse.doc.actions.CreateRevisionEntryAction();
		       cat.addAction(revisionEntryAction);		       
		}
		
		
		if (userObject instanceof Element && 
				( Utilities.isParagraph((Element) userObject) || Utilities.isQuery((Element) userObject) ) 
				&& (((Element)userObject).getOwner()).isEditable() )  {
		       
		       DefaultBrowserAction paragraphAfterAction  = new org.eso.sdd.mbse.doc.actions.InsertParagraphAfterAction();		       
		       DefaultBrowserAction queryAfterAction      = new org.eso.sdd.mbse.doc.actions.InsertQueryAfterAction();		       
		       cat.addAction(paragraphAfterAction);		       
		       cat.addAction(queryAfterAction);		       
	        } 
		
		// Always add the variations management functionality
		cat.addAction(configureVariationsAction);
		mngr.addCategory(cat);

		
	}

	/**
	 * @see com.nomagic.actions.AMConfigurator#configure(com.nomagic.actions.ActionsManager)
	 */
	@Override
	public void configure(ActionsManager mngr)	{
		// adding action separator
		mngr.addCategory(cat);
	}

	@Override
	public int getPriority() {
		return AMConfigurator.MEDIUM_PRIORITY;
	}
}
