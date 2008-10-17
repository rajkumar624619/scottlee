package com.okay.recognize;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.okay.image.CtripCapchaDecoder;

public class CtripDecoder {
	
	public static String decode(String f) throws IOException, IllegalArgumentException, IllegalAccessException{
		return decode(new File(f));
	}
	public static String decode(File f) throws IOException, IllegalArgumentException, IllegalAccessException{
		BufferedImage bi=ImageIO.read(f);
		return decode(bi);
	}
	
	
	public static String decode(BufferedImage bi){
		CtripCapchaDecoder decoder = new CtripCapchaDecoder(bi);
//		decoder.removeNoise();
		decoder.removeStraightLines();
		bi = decoder.toImage();
//		String code = new Recognize().recognizeStringWithThread(bi, 6);
		String code = new Recognize().recognizeString(bi, 6);
		return code;
	}
}
