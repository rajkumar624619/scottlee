package com.okay.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import junit.framework.TestCase;

import com.okay.validate.UnCodebase;

public class ImageFactoryTest extends TestCase {

	
	public static int scanThreeColor(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		
		ColorModel cm =ColorModel.getRGBdefault();
		int [] ca = new int[256];
		
		TreeMap<String, Integer> map = new TreeMap();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int rgb = img.getRGB(i, j);
				int red = cm.getRed(rgb);
				ca[red]++;
				System.out.println(rgb);
				map.put("" + rgb, map.get("" + rgb) == null ? 1
						: ((Integer) map.get("" + rgb)).intValue() + 1);
			}
		}

		int max=0;
		int maxIndex=0;
		for (int i = 0; i < ca.length; i++) {
			if(max<ca[i]){
				max=ca[i];
				maxIndex=i;
			}
		}
		
		return maxIndex;

	}
	public static int scanColor(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		TreeMap<String, Integer> map = new TreeMap();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int rgb = img.getRGB(i, j);
				System.out.println(rgb);
				map.put("" + rgb, map.get("" + rgb) == null ? 1
						: ((Integer) map.get("" + rgb)).intValue() + 1);
			}
		}

		int max = 0;
		String maxColor = "";
		for (String key : map.keySet()) {
			if (!key.equalsIgnoreCase("-1")) {
				int value = map.get(key);
				// max=max<value?value:max;
				if (max < value) {
					max = value;
					maxColor = key;
				}
			}

		}
		System.out.println(map);
		System.out.println(maxColor + " " + max);
		return Integer.parseInt(maxColor);

	}
	
	public static BufferedImage handleRedColor(BufferedImage img, int color) {
		BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		int w = img.getWidth();
		int h = img.getHeight();
		ColorModel cm =ColorModel.getRGBdefault();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int rgb = img.getRGB(i, j);
				int red = cm.getRed(rgb);
				if(red!=color){
					newImg.setRGB(i, j, 0xffffff);
				}else{
					newImg.setRGB(i, j, -1);
				}
			}
		}
		return newImg;
	}
	
	public static BufferedImage handleColor(BufferedImage img, int color) {
		BufferedImage newImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		int w = img.getWidth();
		int h = img.getHeight();
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int rgb = img.getRGB(i, j);
				if(rgb!=color){
					newImg.setRGB(i, j, 0xffffff);
					
				}else{
					newImg.setRGB(i, j, 0);
				}
			}
		}
		return newImg;
	}
	

	public static void main(String[] args) throws IOException {

		ImageFactory im = new ImageFactory();
		BufferedImage img = ImageIO.read(new File(
				"C:\\Program Files\\SecureCRT\\download\\133466375432.jpg"));
		BufferedImage img2 = im.otsuThreshold(img);

		ImageIO.write(img2, "BMP", new File("d:/ctrip.bmp"));

		int color = scanColor(img);
		BufferedImage img3 = handleColor(img, color);
		ImageIO.write(img3, "BMP", new File("d:/ctrip3.bmp"));

//		int color = scanThreeColor(img);
//		BufferedImage img3 = handleRedColor(img, color);
//		ImageIO.write(img3, "BMP", new File("d:/ctrip3.bmp"));
		
		UnCodebase uc = new UnCodebase(img);
		uc.grayByPixels();
		// int grayValue=uc.GetDgGrayValue(img);
		// System.out.println(grayValue);
		uc.GetSingleBmpCode(152);
		ImageIO.write(img, "BMP", new File("d:/ctrip2.bmp"));

	}
}
