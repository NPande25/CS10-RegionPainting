import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 * @author nikhilpande for ps-1 submission
 * @author nathanmcallister for ps-1 submission
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece
	private ArrayList<Point> largest;		// the largest region of specified color found by regionFinder
	private BufferedImage recoloredImage;	// image that fills the regions
	private boolean drawing;				// keeps track of if the brush is drawing or not

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		if (displayMode == 'w') {
			g.drawImage(image, 0, 0, null);
		}
		if (displayMode == 'p') {
			g.drawImage(painting, 0, 0, null);
		}
		if (displayMode == 'r') {
			g.drawImage(recoloredImage, 0, 0, null);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage( ) {
		// TODO: YOUR CODE HERE
		finder = new RegionFinder(image);

		if (drawing) {
			if (targetColor != null && image != null) {
				finder.findRegions(targetColor); 					// find the regions of matching color
				finder.recolorImage();								// recolor all these regions
				recoloredImage = finder.getRecoloredImage();
				largest = finder.largestRegion();					// find the largest region of matching color

				// loop over all the points in largest region and set them to brush color
				for (int i = 0; i < largest.size(); i++) {
					Point p = largest.get(i);
					painting.setRGB(p.x, p.y, paintColor.getRGB());
				}
			}


		}


	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		// TODO: YOUR CODE HERE
		targetColor = new Color(image.getRGB(x, y));
		drawing = true;
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else if (k == 'd') { // turn on brush (brush = down)
			drawing = true;
		}
		else if (k == 'u') { // turn off brush (brush = up)
			drawing = false;
		}
		else if (k == 'g') { // make color green
			paintColor = Color.GREEN;
		}
		else if (k == 'y') { // make color yellow
			paintColor = Color.YELLOW;
		}
		else if (k == 'b') { // make color blue
			paintColor = Color.blue;
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
