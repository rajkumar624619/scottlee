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

public class CtripCaptchaDecoder {
	private List<FeatureExtract.Pair> profiles;
	public CtripCaptchaDecoder(File modelFile) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(modelFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		this.profiles = (List<FeatureExtract.Pair>) ois.readObject();
		ois.close();
	}
	public BufferedImage[] getDenoisedSubImages(BufferedImage rgbImage) {
		BufferedImage[] denoisedSubImages = new BufferedImage[6];
		BufferedImage[] subImgs = ImageSegment.segment(rgbImage);
		for (int i = 0; i < subImgs.length; i++) {
			denoisedSubImages[i] = NoiseRemove.removeNoise(subImgs[i]);
		}
		return denoisedSubImages;
	}
	
	public String getLabel(BufferedImage img) {
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
	
	public String decode(BufferedImage rgbImage) {
		BufferedImage[] subImgs = getDenoisedSubImages(rgbImage);
		String result = "";
		for (BufferedImage subImg : subImgs) {
			String label = getLabel(subImg);
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
		File modelFile = new File("model");
		CtripCaptchaDecoder decoder = new CtripCaptchaDecoder(modelFile);
		File testDir = new File("ctrip_images");
		File[] testFiles = testDir.listFiles();
		int total = 0, success = 0;
		long then = System.currentTimeMillis();
		for (File testFile : testFiles) {
			String testFilename = testFile.getName();
			String answer = testFilename.substring(0, testFilename.length() - 4);
			System.out.print(testFile.getName() + " : ");
			BufferedImage img = ImageIO.read(testFile);
			String guess = decoder.decode(img);
			System.out.println(guess);
			total++;
			if (guess.equals(answer)) {
				success++;
			}
		}
		long elapsedTime = System.currentTimeMillis() - then;
		long avgTime = elapsedTime / total;
		System.out.printf("Total: %d, Success: %d, Accuracy: %.4f%%, Averaged Time: %d\n", total, success, success * 1.0 / total * 100, avgTime);
	}
}
