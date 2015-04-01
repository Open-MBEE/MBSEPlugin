package org.eso.sdd.mbse.templates;

import java.util.Hashtable;

import com.nomagic.magicdraw.sysml.util.SysMLConstants;



public class TemplateStructure {
	public  Hashtable<String,TemplateInfo> struct = null;
	
	public final String[] metaStructure = { "Issues", "Comments", "Drawings",
			"External", "Parsed" };
	public final  String[] IBDs = { "Mechanical", "Optical", "Electrical", "Software" };

	public final String DT_BDD = SysMLConstants.SYSML_BLOCK_DEFINITION_DIAGRAM;
	public final String DT_IBD = SysMLConstants.SYSML_INTERNAL_BLOCK_DIAGRAM;
	public final String DT_PMD = SysMLConstants.SYSML_PARAMETERIC_DIAGRAM;
	public final String DT_RD  = SysMLConstants.SYSML_REQUIREMENTS_DIAGRAM;
	public final String DT_PKD = SysMLConstants.SYSML_PACKAGE_DIAGRAM;

	public TemplateStructure() { 
		struct = new Hashtable<String,TemplateInfo>();
		// package Stereotype, diagram SysML type, diagram Stereotype, diagram Name
		struct.put("Behaviour", new TemplateInfo("Behavior Aspect","","", ""));		
		struct.put("Conceptual", new TemplateInfo("",DT_BDD,"DefinitionDiagram","ConceptualDecomposition" ));		
		struct.put("Constraints", new TemplateInfo("System Constraints",DT_BDD,"DefinitionDiagram","Constraints_Definition" ));		
		struct.put("Data", new TemplateInfo("Data Aspect",DT_BDD,"DefinitionDiagram","Data_Definition" ));	
		struct.put("Interfaces", new TemplateInfo("System Interfaces",DT_BDD,"DefinitionDiagram","Interfaces_Definition" ));	
		struct.put("Items", new TemplateInfo("System Items",DT_BDD,"DefinitionDiagram","Items_Definition" ));	
		struct.put("Performance", new TemplateInfo("Performance Aspect",DT_PKD,"ContentDiagram","Performance_Content" ));		
		struct.put("Problems", new TemplateInfo("Problems","","","" ));		
		struct.put("Rationales", new TemplateInfo("Rationales","","","" ));		
		struct.put("Requirements", new TemplateInfo("Requirements Aspect",DT_RD,"DefinitionDiagram","Requirements" ));		
		struct.put("Structure", new TemplateInfo("Structure Aspect",DT_BDD,"ProductTreeDiagram","ProductTree" ));		
		struct.put("Traceability", new TemplateInfo("Model Traceability","","","" ));		
		struct.put("Variations", new TemplateInfo("Variations Aspect",DT_PKD,"ContentDiagram","Variations_Content"));		
		struct.put("Verification", new TemplateInfo("Verification Aspect","","","" ));		
	}
	
}
