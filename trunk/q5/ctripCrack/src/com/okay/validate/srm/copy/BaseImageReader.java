package com.okay.validate.srm.copy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.okay.validate.ImageData;
import com.okay.validate.ImageFileFilter;
import com.okay.validate.ImageMatrixCompator;
import com.okay.validate.Imagemanager;
import com.okay.validate.StringMatch;
import com.okay.validate.UnCodebase;
import com.okay.validate.WhiteFilter;

public class BaseImageReader {

	public static Logger logger = Logger.getLogger(BaseImageReader.class
			.getName());
	public static final String baseImgsDir = "base/imgs";
	public static final String baseDataDir = "base/data";

	public static ImageData readImg(File file) throws IOException {
		BufferedImage bi = ImageIO.read(file);
		UnCodebase uc = new UnCodebase(bi);
		uc.grayByPixels();
		int grayValue = uc.GetDgGrayValue(bi);
		uc.GetSingleBmpCode(grayValue);
		BufferedImage sbi = uc._bi.getSubimage(0, 0, uc.w, uc.h);
		ImageData imageData = new ImageData(bi, new WhiteFilter());
		return imageData;
	}

	public static ImageData readImg(String fileName) throws IOException {
		return readImg(new File(fileName));
	}

	/**
	 * @deprecated
	 * @param a
	 * @param b
	 * @return
	 */
	public static int getMatchRate(ImageData a, ImageData b) {
		StringMatch _match = new StringMatch();
		_match.setOne(a.toString());
		_match.setTwo(b.toString());
		_match.DoMatch();
		return _match.MatchRate;

	}

	public static String getBestGuess(ImageData data) throws IOException {
		String imgName = "tmp/" + System.currentTimeMillis() + ".jpg";
		data.writeImg(new File(imgName));
		return getBestGuess(imgName);
	}

	public static String getBestGuess2(String imgFile) {
		long start =System.currentTimeMillis();
		String fileName1 = imgFile;
		String fileName2 = "";
		File ff[] = new File(baseImgsDir).listFiles(new ImageFileFilter());

		double min = 1;
		File bestGuess = new File("");
		for (File f : ff) {

			fileName2 = f.getAbsolutePath();
			Imagemanager file1 = new Imagemanager();
			Imagemanager file2 = new Imagemanager();
			try {
				file1.load(fileName1, 1);
				file2.load(fileName2, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ImageMatrixCompator compator = new ImageMatrixCompator();
			double dis = compator.ComputDis(file1.Get128Feature(), file2
					.Get128Feature());
			if (dis != 0 && dis < min) {
				min = dis;
				bestGuess = f;
			}
			
			System.out.println(fileName1 + " " + fileName2 + ": " + dis);

		}

		String guess = bestGuess.getName();
		guess = guess.replaceAll(".jpg", "");
		if (guess.lastIndexOf('\\') != -1) {
			guess = guess.substring(guess.lastIndexOf('\\'));
		}
		if (guess.lastIndexOf('_') != -1) {
			guess = guess.substring(0, guess.indexOf("_"));
		}
		long end =System.currentTimeMillis();
		System.out.println("img: " + imgFile + " min: " + min + " bestGuess:"
				+ guess +" "+ (end-start)+" ms consumed.");
		return guess;
	}

	public static String getBestGuess(String imgFile) {
		String fileName1 = imgFile;
		String fileName2 = "";
		File ff[] = new File(baseImgsDir).listFiles();

		double min = 1;
		File bestGuess = new File("");
		for (File fff : ff) {
			if (fff.isDirectory()) {
				for (File f : fff.listFiles(new ImageFileFilter())) {
					fileName2 = f.getAbsolutePath();
					double dis = compare(fileName1, fileName2);
					if (dis != 0 && dis < min) {
						min = dis;
						bestGuess = f;
					}
					System.out
							.println(fileName1 + " " + fileName2 + ": " + dis);
				}
			}

		}

		String guess = bestGuess.getName();
		guess = guess.replaceAll(".jpg", "");
		if (guess.lastIndexOf('\\') != -1) {
			guess = guess.substring(guess.lastIndexOf('\\'));
		}
		if (guess.lastIndexOf('_') != -1) {
			guess = guess.substring(0, guess.indexOf("_"));
		}
		System.out.println("img: " + imgFile + " min: " + min + " bestGuess:"
				+ guess);
		return guess;
	}

	public static double compare(String fileName1, String fileName2){

		Imagemanager file1 = new Imagemanager();
		Imagemanager file2 = new Imagemanager();
		try {
			file1.load(fileName1, 1);
			file2.load(fileName2, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ImageMatrixCompator compator = new ImageMatrixCompator();
		double dis = compator.ComputDis(file1.Get128Feature(),
				file2.Get128Feature());
		return dis;
	}
	public static void main(String[] args) throws IOException {
		// ImageData a = readImg(new File(baseImgsDir, "2_1.jpg"));
		// ImageData b = readImg(new File(baseImgsDir, "A_3.jpg"));
		// logger.info("MatchRate: "+getMatchRate(a, b));
		// convertBaseImgToData();
		long start = System.currentTimeMillis();
//		String fileName1 = "base/imgs/M_2.jpg";
//		getBestGuess(fileName1);
	
		
		
		System.out.println(compare("base/imgs2/t_3.jpg", "base/imgs2/t.jpg"));
		long end = System.currentTimeMillis();	
		System.out.println((end - start) + "ms consumed.");

	}

	public static void convertBaseImgToData() throws IOException,
			FileNotFoundException {
		File f = new File(baseImgsDir);
		File[] fs = f.listFiles();
		for (File ff : fs) {
			if (ff.getName().endsWith(".jpg")) {
				ImageData data = readImg(ff);
				data.writeData(new File(baseDataDir, ff.getName().replaceAll(
						".jpg", "")));
				data.show();
			}
		}
	}

}
