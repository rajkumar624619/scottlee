package com.okay.validate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

public class ImageMatrixCompatorTest extends TestCase {

	public void testCompareFeature() throws FileNotFoundException, IOException {
		String fileName1 = "D:\\temp\\MODEL_BOLD_3.jpg";

//		File f1 = process1(new File(fileName1));
		File f1=new File(fileName1);
		String fileName2 = "";
		File baseDir = new File("D:\\temp\\");
		File ff[] = baseDir.listFiles(new ImageFileFilter());

		double min = 1;
		String bestGuess = "";
		for (File f : ff) {
			File f2 = f;
			fileName2 = f.getAbsolutePath();
			Imagemanager file1 = new Imagemanager();
			Imagemanager file2 = new Imagemanager();
			try {
				file1.load(f1.getAbsolutePath(), 1);
				file2.load(f2.getAbsolutePath(), 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			ImageMatrixCompator compator = new ImageMatrixCompator();
			double dis = compator.ComputDis(file1.Get128Feature(), file2
					.Get128Feature());
			if (dis != 0 && dis < min) {
				min = dis;
				bestGuess = fileName2;
			}
			System.out.println(fileName1 + " " + fileName2 + ": " + dis);
		}

		System.out.println("min: " + min + " bestGuess:" + bestGuess);
	}

	public File process1(File img) throws FileNotFoundException, IOException {

		ImageData[] id = UnCodebase.split(img.getAbsolutePath(), 1);

		File f = new File("/temp", img.getName());
		for (ImageData d : id) {
			// d.show();
			d.writeImg(f, 5);
		}
		return f;
	}
}
