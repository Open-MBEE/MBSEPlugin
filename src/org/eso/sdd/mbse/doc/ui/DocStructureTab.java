package org.eso.sdd.mbse.doc.ui;

import java.awt.Component;

import javax.swing.JTextArea;

import com.nomagic.magicdraw.ui.browser.WindowComponentContent;

public class DocStructureTab implements WindowComponentContent {

	private JTextArea mComponent = new JTextArea("This is my Text Area");
	
	
	
	@Override
	public Component getWindowComponent()    {
		return mComponent;
	}

	@Override
	public Component getDefaultFocusComponent()    {
		return mComponent;
	}	
	

	


}