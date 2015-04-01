package org.eso.sdd.mbse.utilities;

import java.util.List;

import org.eso.sdd.mbse.doc.actions.CreateAuthorAction;
import org.eso.sdd.mbse.doc.actions.CreateBookAction;
import org.eso.sdd.mbse.doc.actions.MBSEPdfAction;
import org.eso.sdd.mbse.doc.actions.MBSEPdfDirectAction;
import org.eso.sdd.mbse.doc.actions.MBSEShowEditPanelAction;
import org.eso.sdd.mbse.doc.actions.MBSEXMLAction;
import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.templates.MBSEActionGetPartCatalogueTemplate;
import org.eso.sdd.mbse.templates.MBSEActionGetTemplate;
import org.eso.sdd.mbse.variants.tooladapter.magicdraw.MenuAction;

import com.nomagic.magicdraw.actions.DiagramContextAMConfigurator;
import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.actions.MDActionsCategory;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;


public class MBSEDiagramConfigurator implements DiagramContextAMConfigurator , AMConfigurator {
	private MDActionsCategory cat = null;
	
    private MBSEActionAddCID setCIDAction     = new MBSEActionAddCID();
 	private Utilities theUtilities = null;
	private Profile SysMLProfile = null;
	private Profile SE2Profile = null;
	private String cidStereoName = "Configuration Item";

	
	public MBSEDiagramConfigurator()	{
 
	}

	/**
	 * @see com.nomagic.magicdraw.actions.BrowserContextAMConfigurator#configure(com.nomagic.actions.ActionsManager, com.nomagic.magicdraw.ui.browser.Tree)
	 */
	@Override
	public void configure(ActionsManager mngr,  DiagramPresentationElement diagram,   PresentationElement[] selected,   
            PresentationElement requestor) { 
		Element userObject = null;
		Stereotype blockStereotype  = null;		
		Stereotype cidStereotype = null;
		ActionsCategory  cat = new ActionsCategory(null,null);

		if(Application.getInstance().getProject() == null) { 
			return;
		}
		theUtilities = new Utilities(); 	
		SysMLProfile = StereotypesHelper.getProfile(Application
				.getInstance().getProject(), "SysML");
		SE2Profile = StereotypesHelper.getProfile(Application
				.getInstance().getProject(), "SE2Profile");
		
		if(SysMLProfile == null) {
			return;
		}
		blockStereotype = StereotypesHelper.getStereotype(
				Application.getInstance().getProject(), "Block",
				SysMLProfile);
		cidStereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(), cidStereoName);
		if(cidStereotype == null) { 
			cidStereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(), 
						cidStereoName,SE2Profile);
		}
		if(cidStereotype == null) { 
			System.out.println("MBSE INFO: Could not find stereotype "+ cidStereoName + " anywhere in this project");
			return;
		}
		
		if(blockStereotype == null) { 
			System.out.println("MBSE INFO: Could not find stereotype Block anywhere in this project");
			return;
		}
		
        cat = new MDActionsCategory("MBSE2","MBSE2");
		//cat.setNested(true);

		List<PresentationElement> theSelEle = diagram.getSelected();
		
		if(theSelEle.size() > 1 || theSelEle.size() < 1) { 
			//System.out.println("MBSEDiagramConfigurator: Empty or too large selection array");
			
			// do nothing
			return;
		}
		
		userObject = theSelEle.get(0).getElement();
		
		if(userObject == null) { 
			//System.out.println("MBSEDiagramConfigurator: Empty User Object");
			return;
		}
		
		if ( userObject.isEditable() &&	 StereotypesHelper.hasStereotype(userObject, blockStereotype) )   {
				cat.addAction(setCIDAction);
				mngr.addCategory(cat);
				// System.out.println("added action to DIAGRAM Configurator");

		} else { 
			 // System.out.println("MBSE INFO: element does not fullfill requirements");			
		}
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
