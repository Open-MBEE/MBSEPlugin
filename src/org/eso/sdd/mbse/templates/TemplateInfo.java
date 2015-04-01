package org.eso.sdd.mbse.templates;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;

public  class TemplateInfo  { 
	String pStereo;
	String dStereo;
	String dType;
	String dName; 
	Diagram diagram = null;
	Package pkg = null;

	public TemplateInfo(String dpS, String dT,String dS,String dN) {
		// p for package
		// d for diagram
		pStereo    = dpS;
		dType    = dT; // this is the SYSML diagram type
		dName    = dN;
		dStereo  = dS; // this is the SE2 diagram stereotype
	}
}




