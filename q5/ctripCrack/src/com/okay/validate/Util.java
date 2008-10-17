package com.okay.validate;

import java.awt.image.RenderedImage;
import java.io.File;

import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

public class Util {

	public static void delete(File f){
		if(f.isDirectory()){
			File[] fs = f.listFiles();
			for(File ff: fs){
				delete(ff);
			}
		}else{
			f.delete();
		}
	}

	
	public static void writeTiff(RenderedImage image, String filename){
	    String format = "TIFF";
	    RenderedOp op = JAI.create("filestore", image,
	                               filename, format);
	}
	
	public static String getNameOnly(String name){
		name = name.substring(0, name.lastIndexOf("."));
		return name; 
	}

}
