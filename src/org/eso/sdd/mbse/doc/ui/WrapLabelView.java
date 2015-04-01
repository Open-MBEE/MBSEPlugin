package org.eso.sdd.mbse.doc.ui;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.Position.Bias;

public class WrapLabelView extends View {

	public WrapLabelView(Element elem) {
		super(elem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getPreferredSpan(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Shape modelToView(int arg0, Shape arg1, Bias arg2)
			throws BadLocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paint(Graphics arg0, Shape arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public int viewToModel(float arg0, float arg1, Shape arg2, Bias[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
