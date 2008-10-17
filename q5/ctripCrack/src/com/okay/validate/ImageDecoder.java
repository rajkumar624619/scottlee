package com.okay.validate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import maintain.Split;

import com.okay.validate.srm.copy.BaseImageReader;
import com.okay.validate.srm.copy.Srmjava;

public class ImageDecoder {

	final static String tmpDir = "tmp";
	
	public static String decode(File img) throws FileNotFoundException, IOException{
		StringBuffer code =new StringBuffer();
		
		// Do SRM
		Srmjava srm = new Srmjava();
		String processedImg=(new File(tmpDir, img.getName())).getAbsolutePath();
		int q=200;
		srm.convert(img.getAbsolutePath(), processedImg, q);
		
		// Split
		ImageData[] data= UnCodebase.split(processedImg, 6);
		int index=0;
		for(ImageData d : data){
			String c="#";
			try{
			 c= BaseImageReader.getBestGuess(d);
			}catch(Exception e){
				e.printStackTrace();
			}
			code.append(c);
		}
		return code.toString();
	}
	
	public static String decode2(File img) throws FileNotFoundException, IOException{
		StringBuffer code =new StringBuffer();
		
		Srmjava srm = new Srmjava();
		String processedImg=(new File(tmpDir, img.getName())).getAbsolutePath();
		int q=200;
		srm.convert(img.getAbsolutePath(), processedImg, q);
		
		BufferedImage sbi = new BufferedImage(50, 150,
				BufferedImage.TYPE_INT_RGB);
		BufferedImage bi = ImageIO.read(new File(processedImg));

		// corp
		bi = bi.getSubimage(10, 0, bi.getWidth() - 10, bi.getHeight());

		int w = bi.getWidth();
		int h = bi.getHeight();
		BufferedImage subs[] = new BufferedImage[6];
		for (int i = 0; i < 6; i++) {
			subs[i] = bi.getSubimage(i * (w / 6), 0, w / 6, h);
			String name = img.getName().replaceAll(".jpg", "");
			UnCodebase uc = new UnCodebase(subs[i]);
			uc.grayByPixels();
			int grayValue=uc.GetDgGrayValue(subs[i]);
			uc.GetSingleBmpCode(grayValue);
			File sub2 = new File(Split.splitDir, name + "_B_" + i + ".jpg");
			ImageIO.write(subs[i], "JPEG", sub2);
			String c="#";
			try{
			 c= BaseImageReader.getBestGuess(sub2.getAbsolutePath());
			}catch(Exception e){
				e.printStackTrace();
			}
			code.append(c);
		}
		return code.toString();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File img = new File("D://11.jpg");
		System.out.println(" code: "+ decode(img));
	}
}
