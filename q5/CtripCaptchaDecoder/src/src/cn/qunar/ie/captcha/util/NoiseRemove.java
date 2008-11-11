package cn.qunar.ie.captcha.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class NoiseRemove {
	
	/*输入BufferedImage的type是BufferedImage.TYPE_INT_ARGB*/
	/*输出BufferedImage的type是BufferedImage.TYPE_BYTE_GRAY*/
	public static BufferedImage intoGrayImage(BufferedImage rgbImage) {
		WritableRaster raster = rgbImage.getRaster();
		int width = rgbImage.getWidth();
		int height = rgbImage.getHeight();
		int[] pixels = raster.getPixels(0, 0, width, height, (int[]) null);
		int childPixelLength = pixels.length / 3;
		int[] grayPixels = new int[childPixelLength];

		// filter into the child pixel array
		for (int offset = 0; offset < pixels.length - 2; offset += 3) {
			grayPixels[offset / 3] = (int) ((pixels[offset] * 77
					+ pixels[offset + 1] * 150 + pixels[offset + 2] * 29) >> 8);
		}
		BufferedImage grayImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster grayRaster = grayImage.getRaster();
		grayRaster.setPixels(0, 0, width, height, grayPixels);
		grayImage.setData(grayRaster);
		return grayImage;
	}
	
	/*输入BufferedImage的type是BufferedImage.TYPE_BYTE_GRAY*/
	public static void medianFilter(BufferedImage grayImage) {
		int width = grayImage.getWidth();
		int height = grayImage.getHeight();
		WritableRaster raster = grayImage.getRaster();
		int[] grayPixel = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int index = y * width + x;
				int[] values = new int[1];
				raster.getPixel(x, y, values);
				grayPixel[index] = values[0];
			}
		}
		
		int[] tempPixel = new int[width * height];
		System.arraycopy(grayPixel, 0, tempPixel, 0, grayPixel.length);
		
		// calculate
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x == 0 || y == 0 || y == height - 1 || x == width - 1) {
					grayPixel[y * width + x] = 255;
				} else {
					int[] temp = new int[9];
					int k = 0;
					for (int dy = -1; dy <= 1; dy++) {
						for (int dx = -1; dx <= 1; dx++) {
							int index = (y + dy) * width + (x + dx);
							temp[k++] = tempPixel[index];
						}
					}
					Arrays.sort(temp);
					grayPixel[y * width + x] = temp[4];
				}
			}
		}
		raster.setPixels(0, 0, width, height, grayPixel);
		grayImage.setData(raster);
	}
	
	/*输入BufferedImage的type是BufferedImage.TYPE_BYTE_GRAY*/
	/*输出BufferedImage的type是BufferedImage.TYPE_BYTE_BINARY*/
	public static BufferedImage intoOtsuImage(BufferedImage grayImage) {
		WritableRaster grayRaster = grayImage.getRaster();
		int width = grayImage.getWidth();
		int height = grayImage.getHeight();
		int[] grayPixels = grayRaster.getPixels(0, 0, width, height,
				(int[]) null);

		int[] hist256 = new int[256];
		for (int offset = 0; offset < grayPixels.length; offset++) {
			for (int value = 0; value < hist256.length; value++) {
				if (grayPixels[offset] == value) {
					hist256[value]++;
				}
			}
		}
		BufferedImage otsuImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);
		int[] otsuPixels = new int[width * height];

		int thresholdValue = 0;
		int n1 = 0, n2 = 0, n = 0;
		double fmax = -1.0, sum = 0.0, csum = 0.0, m1, m2, sb;
		for (int offset = 0; offset < 256; offset++) {
			sum += (double) offset * (double) hist256[offset];
			n += hist256[offset];
		}
		for (int offset = 0; offset < 256; offset++) {
			n1 += hist256[offset];
			if (n1 == 0) {
				continue;
			}
			n2 = n - n1;
			if (n2 == 0) {
				break;
			}
			csum += (double) offset * hist256[offset];
			m1 = csum / n1;
			m2 = (sum - csum) / n2;
			sb = (double) n1 * (double) n2 * (m1 - m2) * (m1 - m2);
			if (sb > fmax) {
				fmax = sb;
				thresholdValue = offset;
			}
		}
		for (int offset = 0; offset < otsuPixels.length; offset++) {
			if (grayPixels[offset] <= thresholdValue) {
				otsuPixels[offset] = 0;
			} else {
				otsuPixels[offset] = 255;
			}
		}
		for (int offset = 0; offset < hist256.length; offset++) {
			hist256[offset] = 0;
		}
		WritableRaster otsuRaster = otsuImage.getRaster();
		otsuRaster.setPixels(0, 0, width, height, otsuPixels);
		otsuImage.setData(otsuRaster);
		return otsuImage;
	}

	/*BufferedImage的type必须是BufferedImage.TYPE_BYTE_BINARY*/
	public static void deSpeckle(BufferedImage binaryImage) {
		int height = binaryImage.getHeight(), width = binaryImage.getWidth();
		int[][] aroundCount = new int[width][height];
		boolean[][] isWhite = new boolean[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x == 0 || y == 0 || x == width - 1 || y == height - 1 || binaryImage.getRGB(x, y) == -1) {
					isWhite[x][y] = true;
				}
			}
		}
		for (int y0 = 0; y0 < height; y0++) {
			for (int x0 = 0; x0 < width; x0++) {
				if (!isWhite[x0][y0]) {
					for (int dy = -1; dy <= 1; dy++) {
						for (int dx = -1; dx <=1; dx++) {
							if (dx == 0 && dy == 0) {
								continue;
							}
							int x = x0 + dx, y = y0 + dy;
							if (x >= 0 && x < width && y >= 0 && y < height) {
								if (!isWhite[x][y]) {
									aroundCount[x0][y0]++;
								}
							}
						}
					}
				}
			}
		}
		
		Stack<int[]> stack = new Stack<int[]>(); 
		boolean[][] inStack = new boolean[width][height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!isWhite[x][y]) {
					if (aroundCount[x][y] < 3) {
						stack.push(new int[] {x, y});
						inStack[x][y] = true;
					}
				}
			}
		}
		while (!stack.empty()) {
			int[] xy = stack.pop();
			int x0 = xy[0], y0 = xy[1];
			binaryImage.setRGB(x0, y0, -1);
			isWhite[x0][y0] = true;
			for (int dy = -1; dy <= 1; dy++) {
				for (int dx = -1; dx <= 1; dx++) {
					if (dx == 0 && dy == 0) {
						continue;
					}
					int x = x0 + dx, y = y0 + dy;
					if (x >= 0 && x < width && y >= 0 && y < height) {
						if (!isWhite[x][y]) {
							aroundCount[x][y]--;
							if (!inStack[x][y]) {
								if (aroundCount[x][y] < 3) {
									stack.push(new int[] {x, y});
									inStack[x][y] = true;
								}
							}
						}
					}
				}
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (binaryImage.getRGB(x, y) != -1) {
					if (aroundCount[x][y] == 3) {
						if (x > 0 && y > 0 && x < width - 1 && !isWhite[x - 1][y - 1] && !isWhite[x][y - 1] && !isWhite[x + 1][y - 1]) {
							binaryImage.setRGB(x, y, -1);
						} else if (x < width - 1 && y < height - 1 && !isWhite[x + 1][y + 1] && !isWhite[x][y + 1] && !isWhite[x + 1][y + 1]) {
							binaryImage.setRGB(x, y, -1);
						} else if (x > 0 && y > 0 && y < height - 1 && !isWhite[x - 1][y - 1] && !isWhite[x - 1][y] && !isWhite[x - 1][y + 1]) {
							binaryImage.setRGB(x, y, -1);
						} else if (x < width - 1 && y > 0 && y < height - 1 && !isWhite[x + 1][y - 1] && !isWhite[x + 1][y] && !isWhite[x + 1][y + 1]) {
							binaryImage.setRGB(x, y, -1);
						}
					}
				}
			}
		}
	}
	
	/*BufferedImage的type必须是BufferedImage.TYPE_BYTE_BINARY*/
	public static void reserveMaximumArea(BufferedImage binaryImage) {
		int height = binaryImage.getHeight(), width = binaryImage.getWidth();
		int maxArea = 0;
		List<int[]> maxPixels = null;
		
		boolean[][]	hasExamined = new boolean[width][height];
		
		Queue<int[]> startPixels = new LinkedList<int[]>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (binaryImage.getRGB(x, y) != -1) {
					startPixels.add(new int[]{x, y});
				}
			}
		}
		while (startPixels.size() > 0) {
			int[] startPixel = startPixels.remove();
			if (hasExamined[startPixel[0]][startPixel[1]]) {
				continue;
			}
			
			Stack<int[]> stack = new Stack<int[]>();
			stack.push(startPixel);
			
			List<int[]> area = new ArrayList<int[]>();
			area.add(startPixel);
			while (!stack.empty()) {
				int[] pixel0 = stack.pop();
				int x0 = pixel0[0], y0 = pixel0[1];
				for (int dy = -1; dy <= 1; dy++) {
					for (int dx = -1; dx <= 1; dx++) {
						if (dx == 0 && dy == 0) {
							continue;
						}
						int x = x0 + dx;
						int y = y0 + dy;
						if (binaryImage.getRGB(x, y) != -1 && !hasExamined[x][y]) {
							int[] pixel = new int[] {x, y}; 
							stack.push(pixel);
							hasExamined[x][y] = true;
							area.add(pixel);
						}
					}
				}
			}
			
			if (area.size() > maxArea) {
				maxArea = area.size();
				maxPixels = area;
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				binaryImage.setRGB(x, y, -1);
			}
		}
		for (int[] pixel : maxPixels) {
			int x = pixel[0], y = pixel[1];
			binaryImage.setRGB(x, y, Color.BLACK.getRGB());
		}
	}
	
	public static BufferedImage crop(BufferedImage img) {
		int height = img.getHeight(), width = img.getWidth();
		int minX = -1, maxX = -1, minY = -1, maxY = -1;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (img.getRGB(x, y) != -1) {
					if (minX < 0 || x < minX) {
						minX = x;
					}
					if (maxX < 0 || x > maxX) {
						maxX = x;
					}
					if (minY < 0 || y < minY) {
						minY = y;
					}
					if (maxY < 0 || y > maxY) {
						maxY = y;
					}
				}
			}
		}
		return img.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}
	
	public static BufferedImage removeNoise(BufferedImage rgbImage) {
		BufferedImage grayImage = intoGrayImage(rgbImage);
		medianFilter(grayImage);
		BufferedImage binaryImage = intoOtsuImage(grayImage);
		deSpeckle(binaryImage);
		reserveMaximumArea(binaryImage);
		binaryImage = crop(binaryImage);
		return binaryImage;
	}
	
	
}
