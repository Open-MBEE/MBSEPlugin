package org.eso.sdd.mbse.safety.algo;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceSpecification;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralReal;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Slot;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.impl.PropertyNames;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.uml.ExtendedPropertyNames;
//import com.nomagic.utils.Utilities;
import com.nomagic.magicdraw.core.Project;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.eso.sdd.mbse.safety.algo.Utilities;


import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.transaction.TransactionCommitListener;

import java.util.Collection;

/**
 * Property change listener which shows message, if "Is Derived" property is changed into true.
 *
 * @author Michele Zamparelli
 */
public class HazardRiskListener implements TransactionCommitListener, PropertyChangeListener {
	private Logger  logger = null;
	private Utilities theUtilities = null;
	private Stereotype theHazardStereo = null;
	private Stereotype theFaultStereo = null;	
	private Stereotype theFTANodeStereo = null;
	private String targetTag = null;

//	public void propertyChange1(PropertyChangeEvent evt)     {
//		if (PropertyNames.IS_DERIVED.equals(evt.getPropertyName()) &&
//				Utilities.isEqual(evt.getNewValue(), Boolean.TRUE))         {
//			Application.getInstance().getGUILog().showMessage("Attribute is derived");
//		}
//	}

	// this part must be consistent with the profile
	private final String FaultSeverityName = "criticality";
	private final String FaultSeverityWDName = "criticalityWithDetection";
	private final String FaultDetectionName = "detection Likelihood";
	private final String FaultProbabilityName = "occurrence";
	private final String FaultImpactName = "impact";
	
	private final String HazardProbabilityName = "probability";
	private final String HazardImpactName = "severity";
	private final String HazardSeverityName = "risk";
	
	
	@Override
	public void propertyChange(PropertyChangeEvent evt)    	{
		processEvent(evt);
		
	}
	
	private void processEvent(PropertyChangeEvent evt)    	{
		
		if(theUtilities == null) { 
			theUtilities = new Utilities();
			theHazardStereo = theUtilities.getTheHazardStereotype(); 
			theFaultStereo = theUtilities.getTheFaultStereotype(); 
			theFTANodeStereo = theUtilities.getTheFTANodeStereotype(); 
			logger = Logger.getLogger("org.eso.sdd.mbse.safety");
			// This request is enabled, because WARN >= INFO.
			logger.setLevel(Level.DEBUG);
		}
		if(theHazardStereo == null) { 
			logger.error("Hazard stereotype is null.");
			return;
		}
		if(theFaultStereo == null) { 
			logger.error("Fault stereotype is null.");
			return;
		}

		//Application.getInstance().getGUILog().showMessage(evt.getPropertyName() + " is Changed");
		if(evt.getSource() instanceof NamedElement) { 
			NamedElement ne = (NamedElement)(evt.getSource());
			String pref = "Changed: " + ne.getHumanName() + " PROPERTY: " + evt.getPropertyName() + " ";
			if(ne.getOwner() != null) {
				Element father = ne.getOwner();
				String fatherName = father.getHumanName();
				//logger.debug(pref + " father name: " + fatherName);
				if(fatherName.equals("Slot") &&	 father.getOwner() != null ) {
					// ne.getHumanName().equals("Literal Real") &&
					Slot theSlot = (Slot)father;
					
					Element grandfather = father.getOwner();
					Element theRAMSEle = grandfather.getOwner();

					if(StereotypesHelper.hasStereotype(theRAMSEle, theHazardStereo)) { 
						logger.debug(pref + ",owner: " + fatherName);
						logger.debug("Defininig Feature for SLOT " + theSlot.getDefiningFeature().getQualifiedName());
						String fqTagName = theSlot.getDefiningFeature().getQualifiedName();
						targetTag = HazardSeverityName;
						// the chosen stereotype depends on the Safety Profile itself, like the names of the tag
						Double prob = getEnumeration(theRAMSEle, theFTANodeStereo,HazardProbabilityName);
						
						
						Double impact = getEnumeration(theRAMSEle, theHazardStereo, HazardImpactName);
						logger.debug("Current Hazard prob value " + prob);
						logger.debug("Current Hazard severity value " + impact);

						
						Double theRiskLevel = prob * impact;
						if (SessionManager.getInstance().isSessionCreated()) {

							StereotypesHelper.setStereotypePropertyValue(theRAMSEle,theHazardStereo, targetTag, theRiskLevel, false);
							logger.debug("Risk set to " + theRiskLevel + " for theHazard" + theRAMSEle.getHumanName());
						} else {
							logger.error("SESSION NOT EXISTING!");
						}
					}	else if(StereotypesHelper.hasStereotypeOrDerived(theRAMSEle, theFaultStereo)) { 
							logger.debug("Defininig Feature for SLOT " + theSlot.getDefiningFeature().getQualifiedName());
							String fqTagName = theSlot.getDefiningFeature().getQualifiedName();
							targetTag = FaultSeverityName;
							Double prob = getEnumeration(theRAMSEle, theFaultStereo,FaultProbabilityName);
							Double impact = getEnumeration(theRAMSEle, theFaultStereo, FaultImpactName);
							Double detection = getEnumeration(theRAMSEle, theFaultStereo, FaultDetectionName);
							// identification
							logger.debug("Current Fault prob value " + prob);
							logger.debug("Current Fault severity value " + impact);
							logger.debug("Current Fault detection value " + detection);
							Double theRiskLevel = prob * impact;
							// 1 is almost impossible
							// 4 is almost certain
							Double theRiskLevelWD = prob * impact * (1.0d / detection);
							if (SessionManager.getInstance().isSessionCreated()) {

								StereotypesHelper.setStereotypePropertyValue(theRAMSEle,theFaultStereo, targetTag, theRiskLevel, false);
								logger.debug("Risk set to " + theRiskLevel + " for theFault" + theRAMSEle.getHumanName());
								StereotypesHelper.setStereotypePropertyValue(theRAMSEle,theFaultStereo, FaultSeverityWDName, theRiskLevelWD, false);
								logger.debug("Risk with detection set to " + theRiskLevelWD + " for theFault" + theRAMSEle.getHumanName());
							} else {
								logger.error("SESSION NOT EXISTING!");
							}
					}  else {
//						logger.error("the element " + theRAMSEle.getHumanName() + " is not stereotyped by " + 
//								theHazardStereo.getName() + " nor by " + theFaultStereo.getName());	
					}
				} else {
					// logger.debug(pref + " SLOT CONDITION FAILED");
				}
				//
			} else {
				//logger.debug(pref + ", NO OWNER" ); //" owner: " + ne.getOwner().getHumanName());				
			}
			
		} else {
			//logger.debug("No change in NamedElement");
		}
	}

	
	private Double getEnumeration(Element h, Stereotype theStereo,String label) { 
		Double ret = 0.0;
		List theList = StereotypesHelper.getStereotypePropertyValue(h,theStereo,label);
		if(theList != null && theList.size() > 0) { 
			Object elObj = theList.get(0);
			if(elObj instanceof EnumerationLiteral) {
				int index = ((EnumerationLiteral)elObj).getEnumeration().getOwnedLiteral().indexOf(elObj);
				logger.debug("Index of enumeration: " + index );
				ret = (double)index + 1.0d;
			} else if(elObj instanceof LiteralReal ) { 
				ret = ((LiteralReal)elObj).getValue();
			} else if(elObj instanceof Double){
				ret = (Double)elObj;
			} else {
				logger.error("Unknown value type (" + elObj.getClass().getName() + ") for tag " + label + " for " + h.getHumanName());
			}

		} else {
			logger.error("empty list for " + h.getHumanName() + " ("+ theStereo.getName() + ":" + label + ")");
		}
		
		return ret;
	}

/**
 *
 * @author Michele Zampareli
 */
	@Override
	public Runnable transactionCommited(final Collection<PropertyChangeEvent> events)
	{
		return new Runnable()	{
			@Override
			public void run()	{
				for (PropertyChangeEvent event : events) {
//					if (SessionManager.getInstance().isSessionCreated()) {
//						System.out.println("SESSION ALREADY EXISTING IN TRANSACTION COMMITTED");
//					}
					processEvent(event);
				}
			}
		};
	}
}
