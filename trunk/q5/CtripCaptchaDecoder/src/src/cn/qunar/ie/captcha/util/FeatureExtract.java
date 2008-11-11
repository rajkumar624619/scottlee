package cn.qunar.ie.captcha.util;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class FeatureExtract {
	public static class Pair implements Serializable {
		public String label;
		public String style;
		public int[][] feature;
		public Pair(String label, String style, int[][] feature) {
			this.label = label;
			this.style = style;
			this.feature = feature;
		}
	}
	public static int[][] getSampleFeature(BufferedImage img) {
		int height = img.getHeight(), width = img.getWidth();
		int sampleCount = 8;
		int[][] features = new int[sampleCount][sampleCount];
		for (int i = 0; i < sampleCount; i++) {
			for (int j = 0; j < sampleCount; j++) {
				int count = 0;
				int x0 = (int)Math.rint(width * i * 1.0 / sampleCount), y0 = (int)Math.rint(height * j * 1.0 / sampleCount);
				for (int x = x0; x < (int)Math.rint(width * (i + 1) * 1.0 / sampleCount); x++) {
					for (int y = y0; y < (int)Math.rint(height * (j + 1) * 1.0 / sampleCount); y++) {
						if (img.getRGB(x, y) != -1) {
							count++;
						}
					}
				}
				features[i][j] = count;
			}
		}
		return features;
	}
	
	public static double getDistance(int[][] feature1, int[][] feature2) {
		double norm2 = 0;
		int dimension = feature1.length;
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				norm2 += Math.pow(feature1[i][j] - feature2[i][j], 2);
			}
		}
		return norm2;
	}
}
