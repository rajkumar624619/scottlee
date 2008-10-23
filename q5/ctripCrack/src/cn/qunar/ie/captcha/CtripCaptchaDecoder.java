package cn.qunar.ie.captcha;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

import javax.imageio.ImageIO;

import cn.qunar.ie.captcha.util.FeatureExtract;
import cn.qunar.ie.captcha.util.ImageSegment;
import cn.qunar.ie.captcha.util.NoiseRemove;
import cn.qunar.ie.captcha.util.FeatureExtract.Pair;

import com.okay.validate.ImageFileFilter;

public class CtripCaptchaDecoder {
	public static List<FeatureExtract.Pair> profiles;
	public static void init(File modelFile) throws IOException, ClassNotFoundException {
//		profiles = new ArrayList<Pair>();
//		File[] subDirs = dir.listFiles();
//		for (File subDir : subDirs) {
//			if (!subDir.isDirectory()) {
//				continue;
//			}
//			String label = subDir.getName();
//			File[] files = subDir.listFiles();
//			for (File file : files) {
//				if (!file.getName().endsWith(".bmp")) {
//					continue;
//				}
//				String filename = file.getName();
//				String style = filename.substring(0, filename.length() - 4);
//				BufferedImage img = ImageIO.read(file);
//				int[][] feature = FeatureExtract.getSampleFeature(img);
//				FeatureExtract.Pair pair = new FeatureExtract.Pair(label, style, feature);
//				profiles.add(pair);
//			}
//		}
		FileInputStream fis = new FileInputStream(modelFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		profiles = (List<FeatureExtract.Pair>) ois.readObject();
		ois.close();
	}
	
	public static BufferedImage[] getDenoisedSubImages(BufferedImage rgbImage) {
		BufferedImage[] denoisedSubImages = new BufferedImage[6];
		BufferedImage[] subImgs = ImageSegment.segment(rgbImage);
		for (int i = 0; i < subImgs.length; i++) {
			denoisedSubImages[i] = NoiseRemove.removeNoise(subImgs[i]);
			try {
				ImageIO.write(denoisedSubImages[i], "BMP", new File("d:/"+i+".bmp"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return denoisedSubImages;
	}
	
	public static String getLabel(BufferedImage img, List<Pair> profiles) {
		int[][] feature = FeatureExtract.getSampleFeature(img);
		double minDistance = -1;
		String minLabel = null;
		String minStyle = null;
		for (Pair profile : profiles) {
			double distance = FeatureExtract.getDistance(feature, profile.feature);
			if (minDistance < 0 || distance < minDistance) {
				minDistance = distance;
				minLabel = profile.label;
				minStyle = profile.style;
			}
		}
		return minLabel;
	}
	
	public static String decode(BufferedImage rgbImage) {
		BufferedImage[] subImgs = getDenoisedSubImages(rgbImage);
		String result = "";
		for (BufferedImage subImg : subImgs) {
			String label = getLabel(subImg, profiles);
			int index;
			if ((index = label.indexOf("_")) >= 0) {
				String trunk = label.substring(0, index);
				String lowerOrUpper = label.substring(index + 1);
				if (lowerOrUpper.equals("lower")) {
					label = trunk.toLowerCase();
				} else {
					label = trunk.toUpperCase();
				}
			}
			result += label;
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		File modelFile = new File("data/model");
		CtripCaptchaDecoder.init(modelFile);
		
		File testDir = new File("ctrip_images");
		File[] testFiles = testDir.listFiles(new ImageFileFilter());
		int total = 0, success = 0;
		long then = System.currentTimeMillis();
		for (File testFile : testFiles) {
			String testFilename = testFile.getName();
			String answer = testFilename.substring(0, testFilename.length() - 4);
			System.out.print(testFile.getName() + " : ");
			BufferedImage img = ImageIO.read(testFile);
			BufferedImage[] subImgs = getDenoisedSubImages(img);
			String result = "";
			for (BufferedImage subImg : subImgs) {
				String label = getLabel(subImg, profiles);
				int index;
				if ((index = label.indexOf("_")) >= 0) {
					String trunk = label.substring(0, index);
					String lowerOrUpper = label.substring(index + 1);
					if (lowerOrUpper.equals("lower")) {
						label = trunk.toLowerCase();
					} else {
						label = trunk.toUpperCase();
					}
				}
				System.out.print(label);
				result += label;
			}
			System.out.println();
			total++;
			if (result.equals(answer)) {
				success++;
			}
		}
		long elapsedTime = System.currentTimeMillis() - then;
		long avgTime = elapsedTime / total;
		System.out.printf("Total: %d, Success: %d, Accuracy: %.4f%%, Averaged Time: %d\n", total, success, success * 1.0 / total * 100, avgTime);
	}
}
