package org.eso.sdd.mbse.safety.algo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JOptionPane;

import com.nomagic.magicdraw.core.Application;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.nomagic.magicdraw.dependencymatrix.configuration.MatrixDataHelper;
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.Tree;
import com.nomagic.magicdraw.ui.dialogs.MDDialogParentProvider;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Comment;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Relationship;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.magicdraw.export.image.ImageExporter;
import javax.swing.Icon;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eso.sdd.mbse.doc.algo.Utilities;
import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup;
import org.eso.sdd.mbse.doc.options.MBSEOptionsGroup.DiagramGraphicsFormat;
import org.python.antlr.PythonParser.for_stmt_return;

import com.nomagic.uml2.ext.jmi.helpers.ElementImageHelper;
import com.nomagic.uml2.impl.ElementsFactory;

import com.nomagic.generictable.GenericTableManager;
import com.nomagic.reportwizard.tools.DiagramTableTool;
import com.nomagic.reportwizard.*;
import com.nomagic.task.RunnableWithProgress;
import com.nomagic.task.ProgressStatus;
import com.nomagic.generictable.GenericTableManager;
import com.nomagic.magicdraw.openapi.uml.SessionManager;

public class SafetyEngine {
	private  Utilities theUtilities = null;
	private Logger  logger = null;
	private StringBuffer repSB = null;
	private List<Element> theIdentifiedFaults = new ArrayList<Element>();
	
	
	
	public SafetyEngine()  {
    	logger = Logger.getLogger("org.eso.sdd.mbse.safety");
    	// This request is enabled, because WARN >= INFO.
    	logger.setLevel(Level.DEBUG);
		theUtilities = new Utilities();

//    	try {
//    		logger.addAppender(new FileAppender(new PatternLayout(), "MBSE.log"));
//    	} catch (IOException e) {
//    		
//    		e.printStackTrace(); 
//    	}
	}
	
	public void inspectPackage(Package p, StringBuffer sb, StringBuffer sbout) { 
		int hazardCount = 0;
		repSB = sbout;
		if(p.hasOwnedElement()) { 
			for(Element e : p.getOwnedElement()) {
				if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheHazardStereotype())) { 
					// print the hazard name
					traverse((NamedElement)e,sb);
					hazardCount++;
				}
			}
			logger.debug("Found: "+ hazardCount + " hazards.");
		} else { 
			logger.debug("No owned elements for "+ p.getName());
		}
	}
	
	
	
	private void traverse(NamedElement e, StringBuffer sborg) {
		StringBuffer sb = new StringBuffer(sborg); 
		if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheHazardStereotype())) { 
			// hazard
			// implementation decision: we only care about associations
			logger.debug("Found a HAZARD " + e.getName());
			sb.append(e.getName()+",");
		} else if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheBinaryOperatorStereotype())) { 
			// bin op
			logger.debug("Found a BINARY OPERATOR " + e.getName());
			Vector<Element> v = new Vector<Element>();
			v.add(e);
			if(StereotypesHelper.getAllAssignedStereotypes(v).size() != 1 ) { 
				logger.error("TOO MANY STEREOTYPES FOR " + e.getName());
				return;
			} else {
				String opName = StereotypesHelper.getAllAssignedStereotypes(v).iterator().next().getName();
				sb.append(opName+",");
			}
		} else if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheFaultStereotype())) { 
			// fault
			logger.debug("Found a FAULT " + e.getName());
			if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheBasicFaultStereotype())) {
				logger.debug("\tBASIC FAULT " + e.getName());
				sb.append(e.getName()+",");
				// termination condition
				logger.info(sb.toString());
				appendToReport(sb.toString());
				return;
			}
			if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheUndevelopedFaultStereotype())) {
				logger.debug("\tUNDEVELOPED FAULT " + e.getName());
				sb.append(e.getName()+",");
				// termination condition
				logger.info(sb.toString());
				appendToReport(sb.toString());				
				return;
			}
		} else if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheResultingConditionStereotype())) { 
			// resulting condition
			logger.debug("RESULTING CONDITION " + e.getName() );
			sb.append(e.getName()+",");
		}else {
			logger.debug("UNIDENTIFIED ELEMENT: " + e.getName());
			return;
		}

		if(e.has_relationshipOfRelatedElement()) {
			List<Relationship> eList = null;
			eList = (List<Relationship>)e.get_relationshipOfRelatedElement();
			for(Relationship r: eList) {
				if(r instanceof Association) {
					NamedElement origin = null,dest = null;
					Association a = (Association)r;
					dest = a.getMemberEnd().get(0).getType();
					origin = a.getOwnedEnd().get(0).getType();
					//logger.debug(origin.getName() + " ==> " + dest.getName());
					// we should check for the direction.
					if(origin != e) { 
						traverse(origin,sb);
					}
				}
			}

		} else {
			// undeveloped model
			appendToReport(sb.toString());
			logger.info(sb.toString());
		}
		
	}
	private void appendToReport(String string) {
		// TODO Auto-generated method stub
		repSB.append(string + "<p>");
	}

	
	
	public void listRAMSTable(Project project, Package thePackage, String tableName) {
		
		Diagram theGenericTable = null;
		Diagram dia = null;
		List<Object> tableElementTypes = null;
		//

		logger.debug("Listing Generic Table content.");
		//
		if(thePackage.hasOwnedDiagram()) { 
			for(Iterator<Diagram> it = thePackage.getOwnedDiagram().iterator();; ) { 
				dia = it.next();
				if(dia.getName().equals(tableName)) { 
					theGenericTable = dia;
					break;
				}
			}		
		}
		if(theGenericTable == null) {
			logger.error("Could not locate the table called " + tableName + " in the package " + thePackage.getName());
			return;
		}
		tableElementTypes = GenericTableManager.getTableElementTypes(theGenericTable);
		for(Iterator<Object> it = tableElementTypes.iterator(); it.hasNext();) {
			Object theType = it.next();
			logger.debug("Type: " + theType.toString());
		}
		
		for(Iterator<String> it = GenericTableManager.getColumnNames(theGenericTable).iterator();it.hasNext();) { 
			logger.info("Column Name: " + it.next());
		}
		for(Iterator<String> it = GenericTableManager.getColumnIds(theGenericTable).iterator();it.hasNext();) { 
			logger.info("Column Id: " + it.next());
		}

		
	}

	public void createRAMSTable(Project project, Package thePackage, String tableName) {
		Diagram theGenericTable = null;
		Stereotype BFs = theUtilities.getTheBasicFaultStereotype();
		Stereotype UFs = theUtilities.getTheUndevelopedFaultStereotype();
		Browser browser = Application.getInstance().getMainFrame().getBrowser();		
		Tree activeTree = browser.getContainmentTree();

		String[] desiredCols = {"name","probability","MTBF", "MTBFUnits"};
		
		//Set<Property> thepSet = StereotypesHelper.getPropertiesWithDerived(BFs);
		List<Property> thepSet = BFs.getOwnedAttribute();
		logger.debug("The BasicFault stereotype has "+ thepSet.size() + " properties.");
		for(Iterator<Property> it = thepSet.iterator(); it.hasNext();  ) {
			Property theProp = it.next();
			logger.debug("Property for BF " + theProp.getName() + " "+ theProp.getHumanType());
		}

		if (!SessionManager.getInstance().isSessionCreated()) {
			SessionManager.getInstance().createSession("MBSE-RAMS-Table");

			try { 
				theGenericTable = GenericTableManager.createGenericTable(project, tableName);
			} catch(ReadOnlyElementException roee) {
				logger.info("Attempted to create RAMS table in read/only element");
				return;
			}
			if(theGenericTable == null) {
				logger.error("Empty returned Generic Table");
				return;
			}

//			tableName = JOptionPane.showInputDialog(MDDialogParentProvider
//					.getProvider().getDialogParent(), "", "Enter Table Name",
//					JOptionPane.QUESTION_MESSAGE);
			try { 
				ModelElementsManager.getInstance().addElement(theGenericTable,thePackage);
			} catch(ReadOnlyElementException roee ) {
				logger.info("Attempted to create RAMS table in read/only element");					
				SessionManager.getInstance().closeSession();
				return;
			}

			Set<Object> set = new HashSet<Object>();
			recurseSearchAllFaults(thePackage);
			logger.debug("Identified Faults are:" + theIdentifiedFaults.size());
			logger.debug("Identified different types are:" + set.size());

	
            //Add element to table
			for (int i = 0; i < theIdentifiedFaults.size(); i++) {
				Element element = theIdentifiedFaults.get(i);
				int bfIndex, ufIndex = -1;
				Object addendum = null;
				if((bfIndex = StereotypesHelper.getStereotypes(element).indexOf(BFs)) != -1) { 
					logger.info(element.getHumanName() + " is a BF ("+bfIndex+")");
					addendum = StereotypesHelper.getStereotypes(element).get(bfIndex);					
				} else {
					if((ufIndex = StereotypesHelper.getStereotypes(element).indexOf(UFs)) != -1) { 
						logger.info(element.getHumanName() + " is a UF");
						logger.info(StereotypesHelper.getStereotypes(element).get(ufIndex).getClassType());						
						addendum = StereotypesHelper.getStereotypes(element).get(StereotypesHelper.getStereotypes(element).indexOf(UFs));
					} else {
						continue;
					}
				}
	            set.add(addendum);
				//Set table element types for shown elements
				List<Object> tableElementTypes = new ArrayList<Object>();
				tableElementTypes.addAll(set);
				GenericTableManager.setTableElementTypes(theGenericTable,tableElementTypes );
				
                //Add element to table
				
				GenericTableManager.addRowElement(theGenericTable, element);
				logger.debug("Added " + element.getHumanName() + " " + element.getClassType().getName() + ":" + GenericTableManager.getRowElements(theGenericTable).size());
			}

			
            List<Element> elements = new ArrayList<Element>();
            List<Object> types = GenericTableManager.getTableElementTypes( theGenericTable);

            //Handle just first element type for example
            //Can add all element types to elements list
            if (!types.isEmpty())            	{
            	for(int i = 0; i < types.size(); i++) { 
            		Object obj = types.get(i);
            		if (obj instanceof Element) {
            			elements.add((Element) obj);
            		} else if (obj instanceof java.lang.Class) {
            			java.lang.Class clazz = (java.lang.Class) obj;
            			if (StereotypesHelper.getUML2MetaClassByName(project, clazz.getSimpleName()) != null)  {
            				elements.add(StereotypesHelper.getUML2MetaClassByName(project, clazz.getSimpleName()));
            			}
            		}	
            	}
            }


			if (!elements.isEmpty())   {
				//Columns will be shown for first found element type
				//Can add columns from all elements if wanted
				List<String> columnList = GenericTableManager.getPossibleColumnIDs(elements.get(0));

				//Show all available columns for element
				int columnCount = columnList.size();
				List<String> fewColumns = new ArrayList<String>();
				for (int i = 0; i < columnCount; i++) {
					String colName = columnList.get(i);
					String shortColName = colName;
					shortColName = colName.substring(colName.lastIndexOf(':')+1);
					// tedious and error prone, I would very much like to be able to reliably distinguish between Properties which
					// are tag definitions from those which are not but I do not know how to do it.
					logger.debug("Found a column: " + colName);
					for(int j = 0; j < desiredCols.length ; j++) { 
						if(shortColName.equals(desiredCols[j])) { 
							fewColumns.add(colName);
							logger.debug("\tI will add it.");
						} else {
							logger.debug("\tbut I won't add it.");
						}
					}
				}
				//Add columns to table
				GenericTableManager.addColumnsById(theGenericTable, fewColumns);
			}
			}
			SessionManager.getInstance().closeSession();
			DiagramPresentationElement diagramPE = theUtilities.getPROJECT().getDiagram(theGenericTable);
			diagramPE.open();
			activeTree.openNode(theGenericTable,true,true);

	}	


	private void recurseSearchAllFaults(Package p) { 
		int faultCount = 0;
    	logger = Logger.getLogger("org.eso.sdd.mbse.safety");
    	// This request is enabled, because WARN >= INFO.
    	logger.setLevel(Level.DEBUG);
    	try {
    		logger.addAppender(new FileAppender(new PatternLayout(), "MBSE.log"));
    	} catch (IOException e) {
    		e.printStackTrace(); 
    	}

		theUtilities = new Utilities();
		if(p.hasOwnedElement()) { 
			for(Element e : p.getOwnedElement()) {
				if(StereotypesHelper.hasStereotypeOrDerived(e, theUtilities.getTheFaultStereotype())) {
					logger.debug("Found: "+e.getHumanName() + " which is a Fault  " + e.getClassType() + "");
					theIdentifiedFaults.add(e);
					faultCount++;
				}
				if(e instanceof Package) { 
					recurseSearchAllFaults((Package)e);
				}
			}
			logger.debug("Found: "+ faultCount + " hazards.");
		} 
	}


	/**
	 * @param args
	 */
	/*
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	*/

}
