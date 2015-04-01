package org.eso.sdd.mbse.doc.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel {

	private static BufferedImage image = null;

	public ImagePanel(String fileName) {
		/*try {
			// Create new (blank) image of required (scaled) size
			//image = ImageIO.read(new File(fileName));
			//image = resize(image, 400, 400);

		} catch (IOException ex) {
			// handle exception...
		}*/
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null); // see javadoc for more info on the
										// parameters

	}

	public static void setImage(BufferedImage img) {
		if (img == null) {
			image = null;
		} else {
			image = resize(img, 400, 400);
		}
		// repaint();

	}

	public static BufferedImage getImage(){
		return image;
	}
	
	private static BufferedImage resize(BufferedImage image, int width,
			int height) {
		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
				.getType();
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

}