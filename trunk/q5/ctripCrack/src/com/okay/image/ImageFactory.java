package com.okay.image;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageFactory {
	static BufferedImage image;

	static BufferedImage grayImage;

	static BufferedImage thresholdImage;

	static BufferedImage otsuImage;

	static BufferedImage entropyImage;

	static BufferedImage robertsImage;

	static BufferedImage prewittImage;

	static BufferedImage sobelImage;

	static BufferedImage convolutionImage;

	static BufferedImage gaussImage;

	static BufferedImage meanImage;

	static BufferedImage medianImage;

	static BufferedImage bDialationImage;

	static BufferedImage bErosionImage;

	static BufferedImage bOpeningImage;

	static BufferedImage bClosingImage;

	static BufferedImage gDialationImage;

	static BufferedImage gErosionImage;

	static BufferedImage gOpeningImage;

	static BufferedImage gClosingImage;

	static BufferedImage dTn8Image;

	static BufferedImage dTn4Image;

	static BufferedImage skeletonImage;

	static BufferedImage edgeSImage;

	static BufferedImage edgeExImage;

	static BufferedImage edgeInImage;

	static BufferedImage gradientSImage;

	static BufferedImage gradientExImage;

	static BufferedImage gradientInImage;

	static BufferedImage mSmoothImage;

	static BufferedImage dSmoothImage;

	static BufferedImage bTTImage;

	static BufferedImage wTTImage;

	static final int[][] struct_element_binary = { { 1, 1, 1 }, { 1, 1, 1 },
			{ 1, 1, 1 } };

	static final Point p_binary = new Point(1, 1);

	static final int[][] struct_element_gray = { { 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0 } };

	static final Point p_gray = new Point(2, 2);

	static boolean isImported = false;

	static int[] pixel;

	static int[] grayPixel;

	static int[] thresholdPixel;

	static int[] otsuPixel;

	static int[] entropyPixel;

	static int[] robertsPixel;

	static int[] prewittPixel;

	static int[] sobelPixel;

	static int[] convolutionPixel;

	static int[] gaussPixel;

	static int[] meanPixel;

	static int[] medianPixel;

	static int[] bDialationPixel;

	static int[] bErosionPixel;

	static int[] bOpeningPixel;

	static int[] bClosingPixel;

	static int[] gDialationPixel;

	static int[] gErosionPixel;

	static int[] gOpeningPixel;

	static int[] gClosingPixel;

	static int[] dTn8Pixel;

	static int[] dTn4Pixel;

	static int[] skeletonPixel;

	int value;

	static int otsu_val;

	static int entropy_val;

	final static int TOTAL = 256;

	final static double MIN = 0.0001;

	static int[] hist256 = new int[TOTAL];

	public ImageFactory() {
		value = -1;
	}

	public static BufferedImage gray() {
		WritableRaster raster = image.getRaster();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] temp_pixel = raster.getPixels(0, 0, width, height, (int[]) null);

		pixel = new int[temp_pixel.length];
		for (int i = 0; i < pixel.length; i++) {
			pixel[i] = temp_pixel[i];
		}
		grayPixel = new int[pixel.length / 3];
		grayImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		for (int offset = 0; offset < pixel.length - 2; offset += 3) {
			grayPixel[offset / 3] = (int) ((pixel[offset] * 77
					+ pixel[offset + 1] * 150 + pixel[offset + 2] * 29) >> 8);
			// grayImagePixel[offset/3] = (int)((pixel[offset] * 0.3 +
			// pixel[offset + 1] * 0.59 + pixel[offset + 2] * 0.11));
		}

		// when the pic has alpha raster, there will be an error!
		WritableRaster grayRaster = grayImage.getRaster();
		grayRaster.setPixels(0, 0, width, height, grayPixel);
		grayImage.setData(grayRaster);
		return grayImage;
	}

	public static boolean importImageGrayPixels(BufferedImage img) {
		if (img != null) {
			image = img;
			isImported = true;

			WritableRaster raster = image.getRaster();
			int width = image.getWidth();
			int height = image.getHeight();
			pixel = raster.getPixels(0, 0, width, height, (int[]) null);
			int childPixelLength = (int) (pixel.length / 3);
			grayPixel = new int[childPixelLength];

			// filter into the child pixel array
			for (int offset = 0; offset < childPixelLength; offset++) {
				grayPixel[offset] = (int) ((pixel[offset * 3] * 77
						+ pixel[offset * 3 + 1] * 150 + pixel[offset * 3 + 2] * 29) >> 8);
			}
			return isImported;
		}
		return isImported;
	}

	public static BufferedImage intoGrayImage(BufferedImage img) {
		WritableRaster raster = img.getRaster();
		int width = img.getWidth();
		int height = img.getHeight();
		int[] pixels = raster.getPixels(0, 0, width, height, (int[]) null);
		int childPixelLength = pixels.length / 3;
		int[] grayPixels = new int[childPixelLength];

		// filter into the child pixel array
		for (int offset = 0; offset < pixels.length - 2; offset += 3) {
			grayPixels[offset / 3] = (int) ((pixels[offset] * 77
					+ pixels[offset + 1] * 150 + pixels[offset + 2] * 29) >> 8);
		}
		grayImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster grayRaster = grayImage.getRaster();
		grayRaster.setPixels(0, 0, width, height, grayPixels);
		grayImage.setData(grayRaster);
		return grayImage;
	}

	public static void setGrayHistogram() {
		for (int i = 0; i < hist256.length; i++) {
			hist256[i] = 0;
		}
		for (int offset = 0; offset < grayPixel.length; offset++) {
			for (int value = 0; value < hist256.length; value++) {
				if (grayPixel[offset] == value) {
					hist256[value]++;
				}
			}
		}
	}

	// -------------thresholding start------------------
	public static BufferedImage threshold(BufferedImage img, int val) {
		importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		thresholdPixel = new int[pixel.length / 3];
		for (int offset = 0; offset < thresholdPixel.length; offset++) {
			if (grayPixel[offset] <= val) {
				thresholdPixel[offset] = 0;
			} else {
				thresholdPixel[offset] = 255;
			}
		}
		for (int offset = 0; offset < hist256.length; offset++) {
			hist256[offset] = 0;
		}
		thresholdImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster threholdRaster = thresholdImage.getRaster();
		threholdRaster.setPixels(0, 0, width, height, thresholdPixel);
		thresholdImage.setData(threholdRaster);
		return thresholdImage;
	}

	private static BufferedImage intoOtsuImage(BufferedImage img) {
		WritableRaster grayRaster = ImageFactory.intoGrayImage(img).getRaster();
		int width = ImageFactory.intoGrayImage(img).getWidth();
		int height = ImageFactory.intoGrayImage(img).getHeight();
		int[] grayPixels = grayRaster.getPixels(0, 0, width, height,
				(int[]) null);

		hist256 = new int[TOTAL];
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
		for (int offset = 0; offset < TOTAL; offset++) {
			sum += (double) offset * (double) hist256[offset];
			n += hist256[offset];
		}
		for (int offset = 0; offset < TOTAL; offset++) {
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
			// bbg: note: can be optimized.
			if (sb > fmax) {
				fmax = sb;
				thresholdValue = offset;
			}
		}
		otsu_val = thresholdValue;
//		System.out.println("OtsuValue:" + thresholdValue);
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
		try {
			ImageIO.write(otsuImage, "JPEG", new File(
					"D:\\work\\CtripTest\\tmp/a.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return otsuImage;
	}

	public static BufferedImage otsuThreshold(BufferedImage img) {
		return intoOtsuImage(img);
	}

	public static BufferedImage maxEntropyThreshold(BufferedImage img) {
		ImageFactory.importImageGrayPixels(img);
		ImageFactory.setGrayHistogram();
		double[] histgram256 = new double[TOTAL];
		for (int i = 0; i < TOTAL; i++) {
			histgram256[i] = (double) hist256[i];
		}
		int width = image.getWidth();
		int height = image.getHeight();
		int pixelNumbers = width * height;
		entropyImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);
		entropyPixel = new int[pixel.length / 3];

		for (int offset = 0; offset < TOTAL; offset++) {
			// count every level's probability
			histgram256[offset] /= pixelNumbers;
		}
		// cmp at every level
		double temp = 0.0, temp1 = 0.0, temp2 = 0.0;
		double[] sum = new double[TOTAL];
		for (int offset = 0; offset < TOTAL; offset++) {
			temp = temp1 = temp2 = 0.0;
			for (int i = 0; i <= offset; i++) {
				temp += histgram256[i];
			}
			if (Math.abs(temp - 0.0) < MIN || temp >= 1.0) {
				continue;
			} else {
				for (int i = 0; i <= offset; i++) {
					if (Math.abs(histgram256[i] - 0.0) < MIN)
						continue;
					else {
						// 目标灰度分布:p0/Pt，p1/Pt，…，pt/Pt
						// Pt = Σ(i=0~t)pi
						temp1 += (histgram256[i] / temp * Math
								.log(histgram256[i] / temp));
					}
				}
				for (int i = offset + 1; i < TOTAL; i++) {
					if (Math.abs(histgram256[i] - 0.0) < MIN)
						continue;
					else {
						// 背景灰度分布:pt+1/(1-Pt)，…，pL-1/(1-Pt)
						temp2 += (histgram256[i] / (1 - temp) * Math
								.log(histgram256[i] / (1 - temp)));
					}
					sum[offset] = -(temp1 + temp2);
				}
			}
		}
		// find the gray level value which makes entropy largest
		double maxValue = -1.0;
		int thresholdValue = 0;
		for (int offset = 0; offset < TOTAL; offset++) {
			if (maxValue < sum[offset]) {
				maxValue = sum[offset];
				thresholdValue = offset;
			}
		}
		// entropy arithm ended
		entropy_val = thresholdValue;
		System.out.println("EntropythresholdValue:" + thresholdValue);
		for (int offset = 0; offset < entropyPixel.length; offset++) {
			if (grayPixel[offset] <= thresholdValue) {
				entropyPixel[offset] = 0;
			} else {
				entropyPixel[offset] = 255;
			}
		}
		for (int offset = 0; offset < hist256.length; offset++) {
			hist256[offset] = 0;
		}
		WritableRaster entropyRaster = entropyImage.getRaster();
		entropyRaster.setPixels(0, 0, width, height, entropyPixel);
		entropyImage.setData(entropyRaster);
		return entropyImage;
	}

	// -----------edge detecting start-----------------
	public static BufferedImage robertsEdge(BufferedImage img) {
		ImageFactory.importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		robertsImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < tempPixel.length; row++) {
			for (int col = 0; col < tempPixel[row].length; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < tempPixel1.length; row++) {
			for (int col = 0; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		int Gx = 0, Gy = 0;
		for (int row = 0; row < tempPixel1.length - 2; row++) {
			for (int col = 0; col < tempPixel1[row].length - 2; col++) {
				// Gx = z9-z5; Gy - z8-z6
				Gx = tempPixel[row + 2][col + 2] - tempPixel[row + 1][col + 1];
				Gy = tempPixel[row + 2][col + 1] - tempPixel[row + 1][col + 2];
				tempPixel1[row + 1][col + 1] = square(Gx, Gy);
			}
		}
		for (int row = 1; row <= 1; row++) {
			for (int col = 1; col <= 1; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		for (int row = tempPixel1.length - 2; row < tempPixel1.length; row++) {
			for (int col = tempPixel1[row].length - 2; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}

		robertsPixel = new int[grayPixel.length];
		for (int offset = 0; offset < robertsPixel.length; offset++) {
			robertsPixel[offset] = tempPixel1[offset / width][offset % width];
		}

		WritableRaster robertsRaster = robertsImage.getRaster();
		robertsRaster.setPixels(0, 0, width, height, robertsPixel);
		robertsImage.setData(robertsRaster);
		return robertsImage;
	}

	public static BufferedImage prewittEdge(BufferedImage img) {
		ImageFactory.importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		prewittImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < tempPixel.length; row++) {
			for (int col = 0; col < tempPixel[row].length; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < tempPixel1.length; row++) {
			for (int col = 0; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		int Gx = 0, Gy = 0;
		for (int row = 0; row < tempPixel1.length - 2; row++) {
			for (int col = 0; col < tempPixel1[row].length - 2; col++) {
				// Gx = (z7+z8+z9)-(z1+z2+z3); Gy = (z3+z6+z9)-(z1+z4+z7)
				Gx = tempPixel[row + 2][col] + tempPixel[row + 2][col + 1]
						+ tempPixel[row + 2][col + 2] - tempPixel[row][col]
						- tempPixel[row][col + 1] - tempPixel[row][col + 2];
				Gy = tempPixel[row][col + 2] + tempPixel[row + 1][col + 2]
						+ tempPixel[row + 2][col + 2] - tempPixel[row][col]
						- tempPixel[row + 1][col] - tempPixel[row + 2][col];
				tempPixel1[row + 1][col + 1] = square(Gx, Gy);
			}
		}
		for (int row = 1; row <= 1; row++) {
			for (int col = 1; col <= 1; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		for (int row = tempPixel1.length - 2; row < tempPixel1.length; row++) {
			for (int col = tempPixel1[row].length - 2; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}

		prewittPixel = new int[grayPixel.length];
		for (int offset = 0; offset < prewittPixel.length; offset++) {
			prewittPixel[offset] = tempPixel1[offset / width][offset % width];
		}

		WritableRaster prewittRaster = prewittImage.getRaster();
		prewittRaster.setPixels(0, 0, width, height, prewittPixel);
		prewittImage.setData(prewittRaster);
		return prewittImage;
	}

	public static BufferedImage sobelEdge(BufferedImage img) {
		ImageFactory.importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		sobelImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < tempPixel.length; row++) {
			for (int col = 0; col < tempPixel[row].length; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < tempPixel1.length; row++) {
			for (int col = 0; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		int Gx = 0, Gy = 0;
		for (int row = 0; row < tempPixel1.length - 2; row++) {
			for (int col = 0; col < tempPixel1[row].length - 2; col++) {
				// Gx = (z7+2z8+z9)-(z1+2z2+z3); Gy = (z3+2z6+z9)-(z1+2z4+z7)
				Gx = tempPixel[row + 2][col] + 2 * tempPixel[row + 2][col + 1]
						+ tempPixel[row + 2][col + 2] - tempPixel[row][col] - 2
						* tempPixel[row][col + 1] - tempPixel[row][col + 2];
				Gy = tempPixel[row][col + 2] + 2 * tempPixel[row + 1][col + 2]
						+ tempPixel[row + 2][col + 2] - tempPixel[row][col] - 2
						* tempPixel[row + 1][col] - tempPixel[row + 2][col];
				tempPixel1[row + 1][col + 1] = square(Gx, Gy);
			}
		}
		for (int row = 1; row <= 1; row++) {
			for (int col = 1; col <= 1; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		for (int row = tempPixel1.length - 2; row < tempPixel1.length; row++) {
			for (int col = tempPixel1[row].length - 2; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}

		sobelPixel = new int[grayPixel.length];
		for (int offset = 0; offset < sobelPixel.length; offset++) {
			sobelPixel[offset] = tempPixel1[offset / width][offset % width];
		}

		WritableRaster sobelRaster = sobelImage.getRaster();
		sobelRaster.setPixels(0, 0, width, height, sobelPixel);
		sobelImage.setData(sobelRaster);
		return sobelImage;
	}

	// convolution
	public static BufferedImage convolution(BufferedImage img, double[][] kernel) {
		ImageFactory.importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		convolutionImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		// 将kernel转180度再计算
		int SIZE = kernel.length;
		double[][] reversedKenel = new double[SIZE][SIZE];
		for (int row = 0; row < reversedKenel.length; row++) {
			for (int col = 0; col < reversedKenel[row].length; col++) {
				reversedKenel[row][col] = kernel[SIZE - 1 - row][SIZE - 1 - col];
			}
		}
		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < tempPixel.length; row++) {
			for (int col = 0; col < tempPixel[row].length; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		double[][] tempPixel1 = new double[height][width];
		for (int row = 0; row < tempPixel1.length; row++) {
			for (int col = 0; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// calculate
		for (int row = 0; row <= tempPixel1.length - SIZE; row++) {
			for (int col = 0; col <= tempPixel1[row].length - SIZE; col++) {
				for (int ki = 0; ki < SIZE; ki++) {
					for (int kj = 0; kj < SIZE; kj++) {
						tempPixel1[row + SIZE / 2][col + SIZE / 2] += ((double) tempPixel[row
								+ ki][col + kj])
								* reversedKenel[ki][kj];
					}
				}
				if (tempPixel1[row + SIZE / 2][col + SIZE / 2] < 0.001) {
					tempPixel1[row + SIZE / 2][col + SIZE / 2] = 0.0;
				}
				if (tempPixel1[row + SIZE / 2][col + SIZE / 2] > 249.999) {
					tempPixel1[row + SIZE / 2][col + SIZE / 2] = 255.0;
				}
			}
		}
		for (int row = 0; row < SIZE / 2; row++) {
			for (int col = 0; col < SIZE / 2; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		for (int row = tempPixel1.length - SIZE + 1; row < tempPixel1.length; row++) {
			for (int col = tempPixel1[row].length - SIZE + 1; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}

		convolutionPixel = new int[grayPixel.length];
		for (int offset = 0; offset < convolutionPixel.length; offset++) {
			convolutionPixel[offset] = (int) tempPixel1[offset / width][offset
					% width];
		}

		WritableRaster convolutionRaster = convolutionImage.getRaster();
		convolutionRaster.setPixels(0, 0, width, height, convolutionPixel);
		convolutionImage.setData(convolutionRaster);
		return convolutionImage;
	}

	// ----5*5 kernel-----
	public static BufferedImage gaussian(BufferedImage img, double tao) {
		ImageFactory.importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		gaussImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		// calculate 5*5 kenrel,then use it to multiply org,and total the 9
		// numbers
		double[][] kernel = new double[5][5];
		for (int i = 0; i < kernel.length; i++) {
			for (int j = 0; j < kernel[i].length; j++) {
				kernel[i][j] = G(i, j, tao);
			}
		}
		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < tempPixel.length; row++) {
			for (int col = 0; col < tempPixel[row].length; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < tempPixel1.length; row++) {
			for (int col = 0; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// calculate
		for (int row = 2; row < tempPixel1.length - 2; row++) {
			for (int col = 2; col < tempPixel1[row].length - 2; col++) {
				for (int ki = -2; ki <= 2; ki++) {
					for (int kj = -2; kj <= 2; kj++) {
						tempPixel1[row][col] += (tempPixel[row + ki][col + kj] * kernel[ki + 2][kj + 2]);
					}
				}
			}
		}

		gaussPixel = new int[grayPixel.length];
		for (int offset = 0; offset < gaussPixel.length; offset++) {
			gaussPixel[offset] = tempPixel1[offset / width][offset % width];
		}

		WritableRaster gaussRaster = gaussImage.getRaster();
		gaussRaster.setPixels(0, 0, width, height, gaussPixel);
		gaussImage.setData(gaussRaster);
		return gaussImage;
	}

	public static BufferedImage meanFilter(BufferedImage img) {
		ImageFactory.importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		meanImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < tempPixel.length; row++) {
			for (int col = 0; col < tempPixel[row].length; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < tempPixel1.length; row++) {
			for (int col = 0; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// calculate
		for (int row = 2; row < tempPixel1.length - 2; row++) {
			for (int col = 2; col < tempPixel1[row].length - 2; col++) {
				for (int ki = -2; ki <= 2; ki++) {
					for (int kj = -2; kj <= 2; kj++) {
						tempPixel1[row][col] += tempPixel[row + ki][col + kj];
					}
				}
				tempPixel1[row][col] /= 25;
			}
		}
		meanPixel = new int[grayPixel.length];
		for (int offset = 0; offset < meanPixel.length; offset++) {
			meanPixel[offset] = tempPixel1[offset / width][offset % width];
		}

		WritableRaster meanRaster = meanImage.getRaster();
		meanRaster.setPixels(0, 0, width, height, meanPixel);
		meanImage.setData(meanRaster);
		return meanImage;
	}

	public static BufferedImage medianFilter(BufferedImage img) {
		ImageFactory.importImageGrayPixels(img);
		int width = image.getWidth();
		int height = image.getHeight();
		medianImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < tempPixel.length; row++) {
			for (int col = 0; col < tempPixel[row].length; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < tempPixel1.length; row++) {
			for (int col = 0; col < tempPixel1[row].length; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// calculate
		for (int row = 2; row < tempPixel1.length - 2; row++) {
			for (int col = 2; col < tempPixel1[row].length - 2; col++) {
				int[] temp_kernel = new int[25];
				int k = 0;
				while (k < temp_kernel.length) {
					for (int ki = -2; ki <= 2; ki++) {
						for (int kj = -2; kj <= 2; kj++) {
							temp_kernel[k] = tempPixel[row + ki][col + kj];
							k++;
						}
					}
				}
				tempPixel1[row][col] = median(temp_kernel);
			}
		}
		medianPixel = new int[grayPixel.length];
		for (int offset = 0; offset < medianPixel.length; offset++) {
			medianPixel[offset] = tempPixel1[offset / width][offset % width];
		}

		WritableRaster medianRaster = medianImage.getRaster();
		medianRaster.setPixels(0, 0, width, height, medianPixel);
		medianImage.setData(medianRaster);
		return medianImage;
	}

	// binary morphology---------
	public static BufferedImage bDialation(BufferedImage img,
			int[][] struct_element, Point p) {
		return dialationB(intoOtsuImage(img), struct_element, p);
	}

	public static BufferedImage bErosion(BufferedImage img,
			int[][] struct_element, Point p) {
		return erosionB(intoOtsuImage(img), struct_element, p);
	}

	// opening means firstly erosion then dialation
	public static BufferedImage bOpening(BufferedImage img,
			int[][] struct_element, Point p) {
		return erosionB(dialationB(intoOtsuImage(img), struct_element, p),
				struct_element, p);
	}

	// closing means firstly dialation then erosion
	public static BufferedImage bClosing(BufferedImage img,
			int[][] struct_element, Point p) {
		return dialationB(erosionB(intoOtsuImage(img), struct_element, p),
				struct_element, p);
	}

	// binary application-----------
	// N8
	public static BufferedImage CityBlockDistanceTransform(BufferedImage img) {
		int[][] struct_element = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
		Point p = new Point(1, 1);
		dTn8Image = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		dTn8Image = intoOtsuImage(img);
		WritableRaster raster = dTn8Image.getRaster();
		int height = img.getHeight(), width = img.getWidth();
		int[] otsuPixels = raster.getPixels(0, 0, width, height, (int[]) null);
		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel[row][col] = otsuPixels[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// recursive erosion
		recursivebErosion(tempPixel, tempPixel1, width, height, struct_element,
				p);

		dTn8Pixel = new int[otsuPixels.length];
		for (int offset = 0; offset < dTn8Pixel.length; offset++) {
			dTn8Pixel[offset] = tempPixel1[offset / width][offset % width];
		}
		WritableRaster n8Raster = dTn8Image.getRaster();
		n8Raster.setPixels(0, 0, width, height, dTn8Pixel);
		dTn8Image.setData(n8Raster);
		return dTn8Image;
	}

	// something wrong with N4
	// maybe should use graylevel erosion to recurve
	public static BufferedImage ChessboardDistanceTransform(BufferedImage img) {
		int[][] struct_element = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
		Point p = new Point(1, 1);
		ImageFactory.importImageGrayPixels(img);
		dTn4Image = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		int height = image.getHeight(), width = image.getWidth();

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel[row][col] = grayPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// recursive
		recurivegErosion(tempPixel, tempPixel1, width, height, struct_element,
				p);

		dTn4Pixel = new int[grayPixel.length];
		for (int offset = 0; offset < dTn4Pixel.length; offset++) {
			dTn4Pixel[offset] = tempPixel1[offset / width][offset % width];
		}
		WritableRaster dTn4Raster = dTn4Image.getRaster();
		dTn4Raster.setPixels(0, 0, width, height, dTn4Pixel);
		dTn4Image.setData(dTn4Raster);
		return dTn4Image;
	}

	public static BufferedImage skeleton(BufferedImage img) {
		int[][] struct_element = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
		Point p = new Point(1, 1);
		ImageFactory.otsuThreshold(img);
		skeletonImage = new BufferedImage(image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		int height = image.getHeight(), width = image.getWidth();
		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel[row][col] = otsuPixel[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// recursive erosion
		recurivegErosion(tempPixel, tempPixel1, width, height, struct_element,
				p);
		skeletonPixel = new int[grayPixel.length];
		for (int offset = 0; offset < skeletonPixel.length; offset++) {
			skeletonPixel[offset] = tempPixel1[offset / width][offset % width];
		}
		/*
		 * dTn8Pixel = new int[otsuPixel.length]; for (int offset = 0; offset <
		 * dTn8Pixel.length; offset++) { dTn8Pixel[offset] = tempPixel1[offset /
		 * width][offset % width]; } skeletonPixel = new int[otsuPixel.length];
		 * for (int offset = 0; offset < skeletonPixel.length; offset++) { if
		 * (dTn8Pixel[offset]==255 || dTn8Pixel[offset]==250) {
		 * skeletonPixel[offset] = 255; } else { skeletonPixel[offset] = 0; } }
		 */
		WritableRaster skeletonRaster = skeletonImage.getRaster();
		skeletonRaster.setPixels(0, 0, width, height, skeletonPixel);
		skeletonImage.setData(skeletonRaster);
		return skeletonImage;
	}

	// grayscale morphology-----------
	public static BufferedImage gDialation(BufferedImage img,
			int[][] struct_element, Point p) {
		return dialationG(intoGrayImage(img), struct_element, p);
	}

	public static BufferedImage gErosion(BufferedImage img,
			int[][] struct_element, Point p) {
		return erosionG(intoGrayImage(img), struct_element, p);
	}

	public static BufferedImage gOpening(BufferedImage img,
			int[][] struct_element, Point p) {
		return erosionG(dialationG(intoGrayImage(img), struct_element, p),
				struct_element, p);
	}

	public static BufferedImage gClosing(BufferedImage img,
			int[][] struct_element, Point p) {
		return dialationG(erosionG(intoGrayImage(img), struct_element, p),
				struct_element, p);
	}

	// grayscale application------------
	// edge detection
	public static BufferedImage edgeDetectionStandard(BufferedImage img) {
		return minus(
				dialationG(intoGrayImage(img), struct_element_gray, p_gray),
				erosionG(intoGrayImage(img), struct_element_binary, p_binary));
	}

	public static BufferedImage edgeDetectionExternal(BufferedImage img) {
		return minus(
				dialationG(intoGrayImage(img), struct_element_gray, p_gray),
				intoGrayImage(img));
	}

	public static BufferedImage edgeDetectionInternal(BufferedImage img) {
		return minus(intoGrayImage(img), erosionG(intoGrayImage(img),
				struct_element_gray, p_gray));
	}

	// gradient
	public static BufferedImage gradientStandard(BufferedImage img) {
		return half(minus(dialationG(intoGrayImage(img), struct_element_gray,
				p_gray), erosionG(intoGrayImage(img), struct_element_gray,
				p_gray)));
	}

	public static BufferedImage gradientExternal(BufferedImage img) {
		return half(minus(dialationG(intoGrayImage(img), struct_element_gray,
				p_gray), intoGrayImage(img)));
	}

	public static BufferedImage gradientInternal(BufferedImage img) {
		return half(minus(intoGrayImage(img), erosionG(intoGrayImage(img),
				struct_element_gray, p_gray)));
	}

	// morphological smoothing-----
	public static BufferedImage MSmooth(BufferedImage img) {
		return erosionG(dialationG(dialationG(erosionG(intoGrayImage(img),
				struct_element_gray, p_gray), struct_element_gray, p_gray),
				struct_element_gray, p_gray), struct_element_gray, p_gray);
		// return
		// gClosing(gOpening(img,struct_element_gray,p_gray),struct_element_gray,p_gray);
	}

	public static BufferedImage DSmooth(BufferedImage img) {
		return half(add(dialationG(intoGrayImage(img), struct_element_gray,
				p_gray), erosionG(intoGrayImage(img), struct_element_gray,
				p_gray)));
	}

	// top-hat transform----------
	public static BufferedImage whiteTopHatTransform(BufferedImage img) {
		return minus(intoGrayImage(img), dialationG(erosionG(
				intoGrayImage(img), struct_element_gray, p_gray),
				struct_element_gray, p_gray));
	}

	public static BufferedImage blackTopHatTransform(BufferedImage img) {
		return minus(erosionG(dialationG(intoGrayImage(img),
				struct_element_gray, p_gray), struct_element_gray, p_gray),
				intoGrayImage(img));
	}

	public static int[] getHistogram256() {
		return hist256;
	}

	public static void setHistogram256(int[] gray256) {
		for (int i = 0; i < gray256.length; i++)
			hist256[i] = gray256[i];
	}

	public static int getEntropy_val() {
		return entropy_val;
	}

	public static void setEntropy_val(int entropy_val) {
		ImageFactory.entropy_val = entropy_val;
	}

	public static int getOtsu_val() {
		return otsu_val;
	}

	public static void setOtsu_val(int otsu_val) {
		ImageFactory.otsu_val = otsu_val;
	}

	private static int square(int x, int y) {
		return (int) (Math.sqrt(x * x + y * y));
	}

	private static int median(int[] array) {
		// ultilize quick sort
		quickSort(array, 0, array.length - 1);
		return array[array.length / 2];
	}

	private static void quickSort(int[] array, int i, int j) {
		int pivotIndex = (i + j) / 2;
		swap(array, pivotIndex, j);
		int k = partition(array, i - 1, j, array[j]);
		swap(array, k, j);
		if ((k - i) > 1)
			quickSort(array, i, k - 1);
		if ((j - k) > 1)
			quickSort(array, k + 1, j);
	}

	private static int partition(int[] array, int l, int r, int pivot) {
		do {
			while (array[++l] < pivot)
				;
			while ((r != 0) || (array[--r] > pivot))
				;
			swap(array, l, r);
		} while (l < r);
		swap(array, l, r);
		return l;
	}

	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	private static double G(int x, int y, double tao) {
		double dividee = Math.PI * 2 * tao * tao;
		double index = -(x * x + y * y) / (2 * tao * tao);
		double result = (Math.exp(index)) / dividee;
		return result;
	}

	private static int maximun(int[][] array) {
		int max = 0;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				if (i == 0 || j == 0) {
					max = array[0][0];
				} else {
					if (array[i][j] > max) {
						max = array[i][j];
					} else {
						// array[i][j] < max
					}
				}
			}
		}
		return max;
	}

	private static int minimun(int[][] array) {
		int min = 0;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				if (i == 0 || j == 0) {
					min = array[0][0];
				} else {
					if (array[i][j] < min) {
						min = array[i][j];
					} else {
						// array[i][j] >= min
					}
				}
			}
		}
		return min;
	}

	private static void recursivebErosion(int[][] sourcePixel,
			int[][] targetPixel, int width, int height, int[][] kernel, Point p) {
		int se_height = kernel.length, se_width = kernel[0].length;
		int x = p.x, y = p.y, recursive_times = 0;

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel[row][col] = sourcePixel[row][col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = targetPixel[row][col];
			}
		}
		// binary erosion
		boolean se_fit = true;
		boolean all_se_not_fit = true; // no more recursion if true
		for (int row = 0; row < height - se_height; row++) {
			for (int col = 0; col < width - se_width; col++) {
				se_fit = true;
				for (int ki = 0; ki < se_height; ki++) {
					for (int kj = 0; kj < se_width; kj++) {
						if ((kernel[ki][kj] == 1 || tempPixel[row + ki][col
								+ kj] == 255)
								|| (kernel[ki][kj] > 1 || tempPixel[row + ki][col
										+ kj] == (5 + 7 * (kernel[ki][kj] - 2)))) {
							// one fit, pass
						} else {
							se_fit = false;
							kj = se_width; // one not fit, break
						}
					}
				}
				if (se_fit) { // all structure element fit
					all_se_not_fit = false;
					if (kernel[y][x] == 1) {
						tempPixel1[row + y][col + x] = 5;
					} else if (kernel[y][x] > 1) {
						tempPixel1[row + y][col + x] += 7;
						if (tempPixel1[row + y][col + x] > 255) {
							// System.out.println(tempPixel1[row + y][col + x]);
							tempPixel1[row + y][col + x] = 255;
						}
					} else {
						tempPixel1[row + y][col + x] = 0;
					}
				} else {
					// structure element not fit in org
				}
			}
		}
		for (int i = 0; i < kernel.length; i++) {
			for (int j = 0; j < kernel[i].length; j++) {
				if (kernel[i][j] > 0) {
					kernel[i][j]++;
				}
			}
		}
		// System.out.println(kernel[y][x]);
		for (int i = 0; i < tempPixel1.length; i++) {
			for (int j = 0; j < tempPixel1[i].length; j++) {
				targetPixel[i][j] = tempPixel1[i][j];
				sourcePixel[i][j] = tempPixel1[i][j];
			}
		}

		if (!all_se_not_fit || recursive_times <= 30) {
			recursivebErosion(sourcePixel, targetPixel, width, height, kernel,
					p);
			recursive_times++;
		} else {
			return;
		}
	}

	private static void recurivegErosion(int[][] sourcePixel,
			int[][] targetPixel, int width, int height, int[][] kernel, Point p) {
		int se_height = kernel.length, se_width = kernel[0].length;
		int x = p.x, y = p.y, recursive_times = 0;

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel[row][col] = sourcePixel[row][col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = targetPixel[row][col];
			}
		}
		// calculate
		boolean all_se_not_fit = true; // no more recursion if true
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int[][] minus_square = new int[se_height][se_width];
				for (int minus_i = 0; minus_i < minus_square.length; minus_i++) {
					for (int minus_j = 0; minus_j < minus_square[minus_i].length; minus_j++) {
						minus_square[minus_i][minus_j] = 255;
					}
				}
				for (int offsety = -y; offsety < se_height - 1 - y; offsety++) {
					for (int offsetx = -x; offsetx < se_width - 1 - x; offsetx++) {
						if (0 <= (row + offsety) || (row + offsety) < height
								|| 0 <= (col + offsetx)
								|| (col + offsetx) < width) {
							minus_square[y + offsety][x + offsetx] = tempPixel[row
									+ offsety][col + offsetx]
									- kernel[y + offsety][x + offsetx] * 7;
						} else {
							// in orgPixels, out of bounds
						}
					}
				}
				int min = minimun(minus_square);
				if (min < 0) { // out of range 0-255
					tempPixel1[row][col] = 0;
				} else {
					tempPixel1[row][col] = min;
				}
			}
		}
		for (int i = 0; i < tempPixel1.length; i++) {
			for (int j = 0; j < tempPixel1[i].length; j++) {
				targetPixel[i][j] = tempPixel1[i][j];
				sourcePixel[i][j] = tempPixel1[i][j];
			}
		}
		if (recursive_times <= 10) {
			recurivegErosion(sourcePixel, targetPixel, width, height, kernel, p);
			recursive_times++;
		} else {
			return;
		}
	}

	private static BufferedImage add(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() == img2.getWidth()
				|| img1.getHeight() == img2.getHeight()) {
			int width = img1.getWidth(), height = img1.getHeight();
			WritableRaster raster1 = img1.getRaster();
			WritableRaster raster2 = img2.getRaster();
			int[] pixel1 = raster1.getPixels(0, 0, width, height, (int[]) null);
			int[] pixel2 = raster2.getPixels(0, 0, width, height, (int[]) null);
			int[] targetPixel = new int[pixel1.length];
			for (int i = 0; i < pixel1.length; i++) {
				if (pixel1[i] + pixel2[i] > 255) {
					targetPixel[i] = 255;
				} else {
					targetPixel[i] = pixel1[i] + pixel2[i];
				}
			}
			BufferedImage img = new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_GRAY);

			WritableRaster imgRaster = img.getRaster();
			imgRaster.setPixels(0, 0, width, height, targetPixel);
			img.setData(imgRaster);
			return img;
		} else {

		}
		return img1;
	}

	private static BufferedImage minus(BufferedImage img1, BufferedImage img2) {
		if (img1.getWidth() == img2.getWidth()
				|| img1.getHeight() == img2.getHeight()) {
			int width = img1.getWidth(), height = img1.getHeight();
			WritableRaster raster1 = img1.getRaster();
			WritableRaster raster2 = img2.getRaster();
			int[] pixel1 = raster1.getPixels(0, 0, width, height, (int[]) null);
			int[] pixel2 = raster2.getPixels(0, 0, width, height, (int[]) null);
			int[] targetPixel = new int[pixel1.length];
			for (int i = 0; i < pixel1.length; i++) {
				if (pixel1[i] - pixel2[i] < 0) {
					targetPixel[i] = 0;
				} else {
					targetPixel[i] = pixel1[i] - pixel2[i];
				}
			}
			BufferedImage img = new BufferedImage(width, height,
					BufferedImage.TYPE_BYTE_GRAY);

			WritableRaster imgRaster = img.getRaster();
			imgRaster.setPixels(0, 0, width, height, targetPixel);
			img.setData(imgRaster);
			return img;
		} else {

		}
		return img1;
	}

	private static BufferedImage half(BufferedImage img1) {
		int width = img1.getWidth(), height = img1.getHeight();
		WritableRaster raster1 = img1.getRaster();
		int[] pixel1 = raster1.getPixels(0, 0, width, height, (int[]) null);
		int[] targetPixel = new int[pixel1.length];
		for (int i = 0; i < pixel1.length; i++) {
			targetPixel[i] = pixel1[i] / 2;
		}
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster imgRaster = img.getRaster();
		imgRaster.setPixels(0, 0, width, height, targetPixel);
		img.setData(imgRaster);
		return img;
	}

	private static BufferedImage dialationB(BufferedImage img1,
			int[][] struct_element, Point p) {
		// gray 略
		int height = img1.getHeight(), width = img1.getWidth();
		int se_height = struct_element.length, se_width = struct_element[0].length;
		int x = p.x, y = p.y;

		WritableRaster raster1 = img1.getRaster();
		int[] pixel1 = raster1.getPixels(0, 0, width, height, (int[]) null);

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (pixel1[row * width + col] == 1) {
					tempPixel[row][col] = 255;
				} else {
					tempPixel[row][col] = 0;
				}
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// binary dialation
		for (int row = -y; row < height - y - 1; row++) {
			for (int col = -x; col < width - x - 1; col++) {
				if (struct_element[y][x] == 1
						|| tempPixel[row + y][col + x] == 255) {
					for (int ki = 0; ki < se_height; ki++) {
						for (int kj = 0; kj < se_width; kj++) {
							if (struct_element[ki][kj] == 1 || row >= 0
									|| row < height || col >= 0 || col < width) {
								tempPixel1[row + ki][col + kj] = 255;
							} else {
								// kernel'0 not be changed into org
							}
						}
					}
				} else {
					// kernel not equals
				}
			}
		}
		for (int offset = 0; offset < pixel1.length; offset++) {
			pixel1[offset] = tempPixel1[offset / width][offset % width];
		}
		bDialationImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster bdialationRaster = bDialationImage.getRaster();
		bdialationRaster.setPixels(0, 0, width, height, pixel1);
		bDialationImage.setData(bdialationRaster);
		return bDialationImage;
	}

	private static BufferedImage erosionB(BufferedImage img1,
			int[][] struct_element, Point p) {
		// gray 略
		int height = img1.getHeight(), width = img1.getWidth();
		int se_height = struct_element.length, se_width = struct_element[0].length;
		int x = p.x, y = p.y;

		WritableRaster raster1 = img1.getRaster();
		int[] pixel1 = raster1.getPixels(0, 0, width, height, (int[]) null);

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				if (pixel1[row * width + col] == 1) {
					tempPixel[row][col] = 255;
				} else {
					tempPixel[row][col] = 0;
				}
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// binary erosion
		boolean se_fit = true;
		for (int row = 0; row < height - se_height; row++) {
			for (int col = 0; col < width - se_width; col++) {
				se_fit = true;
				for (int ki = 0; ki < se_height; ki++) {
					for (int kj = 0; kj < se_width; kj++) {
						if ((struct_element[ki][kj] == 1 || tempPixel[row + ki][col
								+ kj] == 255)
								|| struct_element[ki][kj] == tempPixel[row + ki][col
										+ kj]/* =0 */) {
							;// one fit, pass
						} else {
							se_fit = false;
							kj = se_width; // one not fit, break
						}
					}
				}
				if (se_fit) { // all structure element fit
					if (struct_element[y][x] == 1) {
						tempPixel1[row + y][col + x] = 255;
					} else {
						tempPixel1[row + y][col + x] = 0;
					}
				} else {
					// structure element not fit in org
				}
			}
		}
		for (int offset = 0; offset < pixel1.length; offset++) {
			pixel1[offset] = tempPixel1[offset / width][offset % width];
		}
		bErosionImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster erosionRaster = bErosionImage.getRaster();
		erosionRaster.setPixels(0, 0, width, height, pixel1);
		bErosionImage.setData(erosionRaster);
		return bErosionImage;
	}

	private static BufferedImage dialationG(BufferedImage img1,
			int[][] struct_element, Point p) {
		// gray 略
		int height = img1.getHeight(), width = img1.getWidth();
		int se_height = struct_element.length, se_width = struct_element[0].length;
		int x = p.x, y = p.y;

		gDialationImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster1 = img1.getRaster();
		int[] pixel1 = raster1.getPixels(0, 0, width, height, (int[]) null);

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel[row][col] = pixel1[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// 将se沿中心对称得到turn_se,便于计算
		int[][] turn_se = new int[se_height][se_width];
		for (int row = 0; row < se_height; row++) {
			for (int col = 0; col < se_width; col++) {
				turn_se[row][col] = struct_element[se_height - 1 - row][se_width
						- 1 - col];
			}
		}
		int turn_y = se_height - 1 - y;
		int turn_x = se_width - 1 - x;
		// calculate
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int[][] add_square = new int[se_height][se_width];
				for (int offsety = -turn_y; offsety < y; offsety++) {
					for (int offsetx = -turn_x; offsetx < x; offsetx++) {
						if (0 <= (row + offsety) || (row + offsety) < height
								|| 0 <= (col + offsetx)
								|| (col + offsetx) < width) {
							add_square[turn_y + offsety][turn_x + offsetx] = tempPixel[row
									+ offsety][col + offsetx]
									+ turn_se[turn_y + offsety][turn_x
											+ offsetx];
						} else {
							// in orgPixels, out of bounds
						}
					}
				}
				int max = maximun(add_square);
				if (max > 255) { // out of range 0-255
					tempPixel1[row][col] = 255;
				} else {
					tempPixel1[row][col] = max;
				}
			}
		}

		for (int offset = 0; offset < pixel1.length; offset++) {
			pixel1[offset] = tempPixel1[offset / width][offset % width];
		}
		WritableRaster dialationRaster = gDialationImage.getRaster();
		dialationRaster.setPixels(0, 0, width, height, pixel1);
		gDialationImage.setData(dialationRaster);
		return gDialationImage;
	}

	private static BufferedImage erosionG(BufferedImage img1,
			int[][] struct_element, Point p) {
		// gray 略
		int height = img1.getHeight(), width = img1.getWidth();
		int se_height = struct_element.length, se_width = struct_element[0].length;
		int x = p.x, y = p.y;

		gErosionImage = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster raster1 = img1.getRaster();
		int[] pixel1 = raster1.getPixels(0, 0, width, height, (int[]) null);

		int[][] tempPixel = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel[row][col] = pixel1[row * width + col];
			}
		}
		int[][] tempPixel1 = new int[height][width];
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				tempPixel1[row][col] = 0;
			}
		}
		// calculate
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				int[][] minus_square = new int[se_height][se_width];
				for (int minus_i = 0; minus_i < minus_square.length; minus_i++) {
					for (int minus_j = 0; minus_j < minus_square[minus_i].length; minus_j++) {
						minus_square[minus_i][minus_j] = 255;
					}
				}
				for (int offsety = -y; offsety < se_height - 1 - y; offsety++) {
					for (int offsetx = -x; offsetx < se_width - 1 - x; offsetx++) {
						if (0 <= (row + offsety) || (row + offsety) < height
								|| 0 <= (col + offsetx)
								|| (col + offsetx) < width) {
							minus_square[y + offsety][x + offsetx] = tempPixel[row
									+ offsety][col + offsetx]
									- struct_element[y + offsety][x + offsetx];
						} else {
							// in orgPixels, out of bounds
						}
					}
				}
				int min = minimun(minus_square);
				if (min < 0) { // out of range 0-255
					tempPixel1[row][col] = 0;
				} else {
					tempPixel1[row][col] = min;
				}
			}
		}
		for (int offset = 0; offset < pixel1.length; offset++) {
			pixel1[offset] = tempPixel1[offset / width][offset % width];
		}
		WritableRaster erosionRaster = gErosionImage.getRaster();
		erosionRaster.setPixels(0, 0, width, height, pixel1);
		gErosionImage.setData(erosionRaster);
		return gErosionImage;
	}
}
