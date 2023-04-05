import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 *
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author nathanmcallister for ps-1 submission
 * @author nikhilpande for ps-1 submission
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;                // how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50;                // how many points in a region to be worth considering

	private static final Color targetColor = new Color(130, 100, 100);    // rough color of baker tower
	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	public BufferedImage seen;

	public ArrayList<ArrayList<Point>> regions;            // a region is a list of points
	// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
		this.seen = null;
		regions = new ArrayList<>();
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
		this.seen = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		regions = new ArrayList<>();
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// TODO: YOUR CODE HERE

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) { // loop over all the pixels in the image
				if (seen.getRGB(x, y) == 0) { // ensure the pixel has not been visited
					if (colorMatch(new Color(image.getRGB(x, y)), targetColor)) { // if the pixel matches intended color
						createRegion(x, y, targetColor);
					}
				}
			}
		}
	}

	public void createRegion(int cx, int cy, Color targetColor) {

		ArrayList<Point> region = new ArrayList<>(); 		// initialize list of points for our region
		ArrayList<Point> toVisit = new ArrayList<>(); 		// initialize list of points to be checked for inclusion

		toVisit.add(new Point(cx, cy));
		seen.setRGB(cx, cy, 1);

		while (!toVisit.isEmpty()) {
			Point temp = toVisit.remove(0); 			// remove it from toVisit
			region.add(temp);								// add to region
			for (int x = temp.x - 1; x <= temp.x + 1; x++) {
				for (int y = temp.y - 1; y <= temp.y + 1; y++) { 			// loop over all neighbors
					if (x != temp.x && y != temp.y && x < image.getWidth() && y < image.getHeight() && x >= 0 && y >= 0) {
						// the above line checks for bounds, ensures points are within image dimensions
						if (seen.getRGB(x, y) == 0) {
							if (colorMatch(new Color(image.getRGB(x, y)), targetColor)) {
								toVisit.add(new Point(x, y));
								seen.setRGB(x, y, 1);
							}
						}
					}
				}
			}
		}
		if (region.size() >= minRegion) { // if region is above the size threshold, add it to list of regions
			regions.add(region);
		}
	}


	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// TODO: YOUR CODE HERE
		int c1r = c1.getRed();
		int c1g = c1.getGreen();
		int c1b = c1.getBlue();

		int c2r = c2.getRed();
		int c2g = c2.getGreen();
		int c2b = c2.getBlue();

		if (Math.abs(c1r - c2r) < maxColorDiff && Math.abs(c1g - c2g) < maxColorDiff && Math.abs(c1b - c2b) < maxColorDiff) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 * method initializes the biggest region as the first one
	 * loops through each region, and if it is bigger than the current max, it becomes the new max
	 */
	public ArrayList<Point> largestRegion() {
		// TODO: YOUR CODE HERE
		ArrayList<Point> max = regions.get(0);
		int size = regions.size();

		for (int i = 0; i < size; i++) {
			ArrayList<Point> temp = regions.get(i);
			if (temp.size() > max.size()) {
				max = temp;
			}
		}
		return max;
	}

	/**
	 * Sets recoloredImage to be a copy of image,
	 * but with each region a uniform random color,
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		// TODO: YOUR CODE HERE

		for (int i = 0; i < regions.size(); i++) {  // loop over all regions
			ArrayList<Point> temp = regions.get(i);
			Color color = new Color((int) (16777216 * Math.random()));  // create random color
			for (int j = 0; j < temp.size(); j++) { // loop over all points in the region
				Point p = temp.get(j);
				recoloredImage.setRGB(p.x, p.y, color.getRGB());    // set all points in region to this random color
			}
		}
	}
}

