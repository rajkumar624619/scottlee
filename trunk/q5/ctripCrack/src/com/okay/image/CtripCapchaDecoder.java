package com.okay.image;
import ij.IJ;
import ij.process.FloatProcessor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class CtripCapchaDecoder {
	private BufferedImage img;
	private int width;
	private int height;
	private boolean[][] isWhite;
	private int[][] aroundCount;
	private LinearHT ht;
	
	public CtripCapchaDecoder(BufferedImage img) throws NullPointerException {
		this.img = img;
		this.width = img.getWidth();
		this.height = img.getHeight();
		this.isWhite = new boolean[width][height];
		this.aroundCount = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (isTooBright(x, y)) {
					isWhite[x][y] = true;
				}
			}
		}
		computeAroundCount();
	}
	
	private void computeAroundCount() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!isWhite[x][y]) {
					int count = 0;
					for (int dx = -1; dx <= 1; dx++) {
						for (int dy = -1; dy <= 1; dy++) {
							if (dx == 0 && dy == 0) {
								continue;
							}
							if (x + dx >= 0 && x + dx < width && y + dy >= 0 && y + dy < height) {
								if (!isWhite[x + dx][y + dy]) {
									count++;
								}
							}
						}
					}
					aroundCount[x][y] = count;
				}
			}
		}
	}
	
	class LinearHT {
		
		BufferedImage img;	// reference to original image
		int xCtr, yCtr; 	// x/y-coordinate of image center
		int nAng;	// number of steps for the angle  (a = 0 ... PI)
		int nRad; 	// number of steps for the radius (r = -r_max ... +r_max)
		int cRad;	// center of radius axis (r = 0)
		double dAng;	// increment of angle
		double dRad; 	// increment of radius
		int[][] houghArray; // Hough accumulator
		int[][] localMaxArray; // Hough accumulator
		
		List[][] pixels;
		int width;
		int height;

		//constructor method:
		public LinearHT () {
			
		}
		
		public LinearHT(BufferedImage img, int nAng, int nRad) {
			this.img = img;
			this.xCtr = img.getWidth()/2; 
			this.yCtr = img.getHeight()/2;
			this.nAng = nAng; 
			this.dAng = Math.PI / nAng;
			this.nRad = nRad;
			this.cRad = nRad / 2;
			double rMax = Math.sqrt(xCtr * xCtr + yCtr * yCtr);
			this.dRad = (2.0 * rMax) / nRad;
			this.houghArray = new int[nAng][nRad];
			this.pixels = new List[nAng][nRad];
			
			this.width = img.getWidth();
			this.height = img.getHeight();
			
			for (int i = 0; i < nAng; i++) {
				this.pixels[i] = new List[nRad];
			}
			
			fillHoughAccumulator();
			findLocalMaxima();
		}
		
		void fillHoughAccumulator() {
			int h = img.getHeight();
			int w = img.getWidth();
			for (int v = 0; v < h; v++) {
				for (int u = 0; u < w; u++) {
					if (!isWhite[u][v] && aroundCount[u][v] < 4) {
						doPixel(u, v);
					}
				}
			}
		}

		void doPixel(int u, int v) {
			int x = u - xCtr,  y = v - yCtr;
			for (int i = 0;	i < nAng; i++) {
				double theta = dAng * i;
				int r =  cRad + (int) Math.rint
					((x*Math.cos(theta) + y*Math.sin(theta)) / dRad);
				if (r >= 0 && r < nRad) {
					houghArray[i][r]++;
					List list = pixels[i][r];
					if (list == null) {
						list = new ArrayList(1);
						pixels[i][r] = list;
					}
					list.add(new int[] {u, v});
				}
			}
		}
		
		public double realAngle(int a) {	//return real angle for angle index a
			return a*dAng;
		}
		
		public double realRadius(int r) {	
			return (r-nRad/2)*dRad;
		}

		
		public FloatProcessor getAccumulatorImage() {
			if (houghArray == null)
				throw new Error("houghArray is not initialized");
			return new FloatProcessor(houghArray);
		}
		
		public FloatProcessor getLocalMaxImage() {
			if (localMaxArray == null)
				throw new Error("localMaxArray is not initialized");
			return new FloatProcessor(localMaxArray);
		}
		
		public void findLocalMaxima() {
//			IJ.log("finding local maxima");
			//creates a new image with 0/1
			//where 1 = local maximum
			localMaxArray = new int[nAng][nRad]; //initialized to zero
			for (int a = 0; a < nAng; a++) {
				// we treat the angle dimension cyclically:
				int a1 = (a - 1 + nAng) % nAng;	// al = (a-1) mod nAng
				int a2 = (a + 1) % nAng;		// ar = (a+1) mod nAng
				for (int r = 1; r < nRad - 1; r++) {
					int ha = houghArray[a][r];
					// this test is problematic if 2 identical cell values
					// appear next to each other!
					boolean ismax =
						ha > houghArray[a1][r-1] &&
						ha > houghArray[a1][r] &&
						ha > houghArray[a1][r+1] &&
						ha > houghArray[a][r-1] &&
						ha > houghArray[a][r+1] &&
						ha > houghArray[a2][r-1] &&
						ha > houghArray[a2][r] &&
						ha > houghArray[a2][r+1] ;
					if (ismax)
						localMaxArray[a][r] = ha;
				}
			}
		}

		// returns the n strongest lines (with max. pixel counts)
		public List<HoughLine> getStraightLines(FloatProcessor hmax, int threshold) {
			List<HoughLine> lines = new ArrayList<HoughLine>();
			for (int a = 0; a < nAng; a++) {
				for (int r = 0; r < nRad; r++) {
					int hcount = (int) Float.intBitsToFloat(hmax.getPixel(a, r));
					if (hcount > 0 && hcount > threshold) {
						HoughLine line = new HoughLine();
						line.angle = a;
						line.radius = r;
						line.count = hcount;
						lines.add(line);
					}
				}
			}
			return lines;
		}
		
		public void drawLines(List<HoughLine> lines, BufferedImage img) {
			for (HoughLine hl : lines) {
				hl.remove(img);
			}
		}
		
//		 ---------------- OBSOLETE !
		
		// create a FloatProcessor for viewing the accumulator array
		public FloatProcessor makeFloatProcessor() {
			int[] fpixels = new int[nAng * nRad];
			for (int a = 0; a < nAng; a++) {
				for (int r = 0; r < nRad; r++) {
					fpixels[r * nAng + a] = houghArray[a][r];
				}
			}
			FloatProcessor fp = new FloatProcessor(nAng, nRad, fpixels);
			return fp;
		}
		
		public FloatProcessor findLocalMaxima(FloatProcessor fp) {
			//creates a new image with 0/1
			//where 1 = local maximum
			int w = fp.getWidth();
			int h = fp.getHeight();
			float[] pix = (float[]) fp.getPixels();
			float[] lmax = new float[pix.length]; //initialized to zero
			for (int v = 1; v < h - 1; v++) {
				int r0 = v - 1;
				int r1 = v;
				int r2 = v + 1;
				for (int u = 1; u < w - 1; u++) {
					int c0 = u - 1;
					int c1 = u;
					int c2 = u + 1;
					float cp = pix[r1 * w + c1];
					boolean ismax =
						cp > pix[r0 * w + c0]
							&& cp > pix[r0 * w + c1]
							&& cp > pix[r0 * w + c2]
							&& cp > pix[r1 * w + c0]
							&& cp > pix[r1 * w + c2]
							&& cp > pix[r2 * w + c0]
							&& cp > pix[r2 * w + c1]
							&& cp > pix[r2 * w + c2];
					if (ismax)
						lmax[r1 * w + c1] = cp;
				}
			}
			return new FloatProcessor(w, h, lmax, null);
		}
		
		// ------------------------------------------------------------
		
		public class HoughLine implements Comparable {
			// must be comparable for sorting (by count)
			private int angle;
			private int radius;
			private int count;
			
			HoughLine() {}
			
			HoughLine(int angle, int radius, int count){
				this.angle  = angle;
				this.radius = radius;
				this.count  = count;	//number of contributing image points
			}
			
			// construct a line in Hessian normal form from two given points
			// (x1,y1) nd (x2,y2).
			public HoughLine(double x1, double y1, double x2, double y2) {
				
			}
			
			public double getAngle() {
				return angle * dAng;
			}
			
			public double getRadius() {
				return (radius - nRad/2) * dRad;
			}
			
			public int getCount() {
				return count;
			}
			
			public int compareTo (Object o){
				HoughLine hn1 = this;
				HoughLine hn2 = (HoughLine) o;
				if (hn1.count > hn2.count)
					return -1;
				else if (hn1.count < hn2.count)
					return 1;
					else
						return 0;
			}
			
			public String toString() {
				return 
					this.getClass().getSimpleName() + ":" +
					" angle=" + getAngle() +
					" radius=" + getRadius() +
					" count=" + count;
			}
			
			// ----- drawing -------
			
			// draw this line given in Hessian normal form
			public void remove(BufferedImage img) {
				List dots = pixels[this.angle][this.radius];
				if (dots == null) {
					return;
				}
				for (Object obj : dots) {
					int[] coordinates = (int[]) obj;
					int x = coordinates[0], y = coordinates[1];
					img.setRGB(x, y, -1);
					isWhite[x][y] = true;
				}
			}
			
			// ----- finding points of intersection -----
			
			public double[] intersectWith (HoughLine hn2) {
				HoughLine hn1 = this;
				if (hn1.angle == hn2.angle)
					return null;
				double th1 = hn1.getAngle();
				double th2 = hn2.getAngle();
				double r1  = hn1.getRadius();
				double r2  = hn2.getRadius();
				
				double s = 1 / Math.sin(th2 - th1);
				double x = s * (r1 * Math.sin(th2) - r2 * Math.sin(th1));
				double y = s * (r2 * Math.cos(th1) - r1 * Math.cos(th2));
				
				return new double[] {x,y};
			}
		} // end of class HoughLine
	}
	
	private static int getBlueValue(int value) {
		return value & 0xFF;
	}
	private static int getGreenValue(int value) {
		return (value >> 8) & 0xFF;
	}
	private static int getRedValue(int value) {
		return (value >> 16) & 0xFF;
	}
	private static boolean isTooBright(int value) {
		int blueValue = getBlueValue(value); 
		int greenValue = getGreenValue(value); 
		int redValue = getRedValue(value);
		int average = (blueValue + greenValue + redValue) / 3;
		return average > 230;
	}
	
	private boolean isTooBright(int x, int y) {
		if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
			return true;
		}
		return isTooBright(img.getRGB(x, y));
	}
	
	public void removeStraightLines() {
		ht = new LinearHT(img, 180, 80);
		FloatProcessor hip = ht.makeFloatProcessor();
		hip.flipHorizontal();
		FloatProcessor hmax = ht.findLocalMaxima(hip);
		List<LinearHT.HoughLine> hs = ht.getStraightLines(hmax, 50);
		for (LinearHT.HoughLine line : hs) {
			line.remove(img);
		}
	}
	
	public void deSpeckle(int round) {
		for (int i = 0; i < round; i++) {
			computeAroundCount();
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (isWhite[x][y] || aroundCount[x][y] < 4) {
						img.setRGB(x, y, -1);
						isWhite[x][y] = true;
					}
				}
			}
		}
	}
	
	public void toFile(File file) throws IOException {
		ImageIO.write(img, "jpg", file);
	}
	public BufferedImage toImage() {
		return img;
	}
	
	public void removeNoise() {
		removeStraightLines();
		deSpeckle(3);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("Usage: java CtripCapchaDecoder <�����ļ���> <����ļ���>");
			System.exit(1);
		}
		String inputFilename = args[0], outputFilename = args[1];
		File input = new File(inputFilename);
		File output = new File(outputFilename);
		BufferedImage img = ImageIO.read(input);
		CtripCapchaDecoder decoder = new CtripCapchaDecoder(img);
		decoder.removeNoise();
		decoder.toFile(output);
//		File inputDir = new File("ctrip_img");
//		File outputDir = new File("output_img");
//		if (!outputDir.exists()) {
//			outputDir.mkdirs();
//		}
//		File[] inputFiles = inputDir.listFiles();
//		int count = 0;
//		for (File inputFile : inputFiles) {
//			File outputFile = new File(outputDir, inputFile.getName());
//			BufferedImage img = ImageIO.read(inputFile);
//			CtripCapchaDecoder decoder = null;
//			try {
//				decoder = new CtripCapchaDecoder(img);
//			} catch (NullPointerException e) {
//				continue;
//			}
//			decoder.removeNoise();
//			decoder.toFile(outputFile);
//			count++;
//			if (count % 100 == 0) {
//				System.out.println("Record: " + count);
//			}
//		}
	}
}
