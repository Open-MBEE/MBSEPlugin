package org.eso.sdd.mbse.doc.ui;

import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.Position.Bias;

public class NoWrapParagraphView extends View {

	public NoWrapParagraphView(Element elem) {
		super(elem);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getPreferredSpan(int axis) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Shape modelToView(int pos, Shape a, Bias b)
			throws BadLocationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		// TODO Auto-generated method stub

	}

	@Override
	public int viewToModel(float x, float y, Shape a, Bias[] biasReturn) {
		// TODO Auto-generated method stub
		return 0;
	}

}
