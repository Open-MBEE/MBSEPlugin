package org.eso.sdd.mbse.variants.gui.internal;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.eso.sdd.mbse.doc.algo.genUtility;

public class HTMLPage {

	/** Shows HTML page content in its own frame
	 * 
	 * @param classRelativeLocation a path to the html page, relative to the position of 
	 * the HTMLPage class in the jar file it is contained in.
	 */
	public static void showHTML(String classRelativeLocation) {
		JTextPane tp = new JTextPane();
		tp.setEditable(false);
		JScrollPane js = new JScrollPane();
		js.getViewport().add(tp);
		JFrame jf = new JFrame();
		jf.getContentPane().add(js);
		jf.setSize(800, 600);
		jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jf.setVisible(true);

		try {
			URL url = HTMLPage.class.getResource(classRelativeLocation);
			tp.setPage(url);
		} catch (Exception e) {
			e.printStackTrace();
    		StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);

			genUtility.displayWarning(sw.toString());
		}
	}

}
