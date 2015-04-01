package org.eso.sdd.mbse.doc.algo;

import java.awt.image.BufferedImage;
import java.util.Vector;

import org.eso.sdd.mbse.doc.algo.Utilities.TEXTUSAGEKIND;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

@SuppressWarnings("serial")
public class DocBookVector extends Vector<Object> {

	private String ID = null;
	private Element elem = null;
	private BufferedImage image = null;
	private String imageTxt = null;
	private String documentationText = null;
	private TEXTUSAGEKIND documentationUsage = Utilities.TEXTUSAGEKIND.none;
	
	public DocBookVector(){
		super();
	}
	
	public void setID(String id){
		ID = id;
	}
	
	public String getID(){
		return ID;
	}
	public void setElement(Element el) {
		elem = el;
	}
	
	public Element getElement() {
		return elem;
	}

	public void setImage(BufferedImage img) {
		image = img;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImageText(String txt) {
		imageTxt = txt;
	}
	
	public String getImageText() {
		return imageTxt;
	}

	public void setDocumentationText(String documentation, TEXTUSAGEKIND useText) {
		documentationText = documentation;
		documentationUsage = useText;
	}
	
	public String getDocumentationText() {
		return documentationText;
	}

	public TEXTUSAGEKIND getDocumentationUsage() {
		return documentationUsage;
	}

}
