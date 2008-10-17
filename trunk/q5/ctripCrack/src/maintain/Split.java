package maintain;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.okay.validate.ImageData;
import com.okay.validate.ImageFileFilter;
import com.okay.validate.UnCodebase;
import com.okay.validate.Util;
import com.okay.validate.WhiteFilter;
import com.okay.validate.srm.copy.Srmjava;

public class Split {
	public static final String sampleDir = "base/sample/ctrip_img/";
	public static final String srmedDir = "base/sample/srmed/";
	public static final String baseImgDir = "base/imgs/";
	public static final String splitDir = "base/sample/split_1/";

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		Util.delete(new File(splitDir));
		// splitTest(new File(sampleDir,"2FbDnI.jpg"));

		// splitSample2(); //not using srm

		// splitSampleImg();

		simpleSplit(new File(srmedDir, "6RIFbB.jpg"), 6);
		simpleSplit(new File(srmedDir, "2FbDnI.jpg"), 6);

		// splitTestWithSrm(new File(sampleDir,"2FbDnI.jpg"));
		// splitSample3(); //using srm

		// simpleSplitWithoutModify(new File(sampleDir, "2FbDnI.jpg"), 6);
	}

	public static BufferedImage[] simpleSplitWithoutModify(File img, int column)
			throws IOException {
		BufferedImage sbi = new BufferedImage(50, 150,
				BufferedImage.TYPE_INT_RGB);
		BufferedImage bi = ImageIO.read(img);

		// corp
		bi = bi.getSubimage(10, 0, bi.getWidth() - 10, bi.getHeight());

		UnCodebase uc = new UnCodebase(bi);
		uc.grayByPixels();
		int grayValue = uc.GetDgGrayValue(bi);
		uc.GetSingleBmpCode(grayValue);

		int w = bi.getWidth();
		int h = bi.getHeight();
		BufferedImage subs[] = new BufferedImage[column];
		for (int i = 0; i < column; i++) {
			subs[i] = bi.getSubimage(i * (w / column), 0, w / column, h);
			String name = img.getName().replaceAll(".jpg", "");
			File sub = new File(splitDir, name + "_" + i + ".jpg");
			ImageIO.write(subs[i], "JPEG", sub);
		}
		return subs;
	}

	public static BufferedImage[] simpleSplit(BufferedImage bi, int column)
			throws IOException {
		BufferedImage sbi = new BufferedImage(50, 150,
				BufferedImage.TYPE_INT_RGB);
		// Util.writeTiff(bi, splitDir+ Util.getNameOnly(img.getName()) +
		// ".tif");
		// corp
		bi = bi.getSubimage(10, 0, bi.getWidth() - 10, bi.getHeight());

		int w = bi.getWidth();
		int h = bi.getHeight();
		BufferedImage subs[] = new BufferedImage[column];
		for (int i = 0; i < column; i++) {
			subs[i] = bi.getSubimage(i * (w / column), 0, w / column, h);

			UnCodebase uc = new UnCodebase(subs[i]);
			uc.grayByPixels();
			int grayValue = uc.GetDgGrayValue(subs[i]);
			uc.GetSingleBmpCode(grayValue);
		}
		return subs;
	}

	public static BufferedImage[] simpleSplit(File img, int column)
			throws IOException {
		BufferedImage sbi = new BufferedImage(50, 150,
				BufferedImage.TYPE_INT_RGB);
		BufferedImage bi = ImageIO.read(img);
		Util.writeTiff(bi, splitDir + Util.getNameOnly(img.getName()) + ".tif");
		// corp
		bi = bi.getSubimage(10, 0, bi.getWidth() - 10, bi.getHeight());

		int w = bi.getWidth();
		int h = bi.getHeight();
		BufferedImage subs[] = new BufferedImage[column];
		for (int i = 0; i < column; i++) {
			subs[i] = bi.getSubimage(i * (w / column), 0, w / column, h);
			String name = img.getName().replaceAll(".jpg", "");
			File sub = new File(splitDir, name + "_A_" + i + ".jpg");
			ImageIO.write(subs[i], "JPEG", sub);

			UnCodebase uc = new UnCodebase(subs[i]);
			uc.grayByPixels();
			int grayValue = uc.GetDgGrayValue(subs[i]);
			uc.GetSingleBmpCode(grayValue);
			File sub2 = new File(splitDir, name + "_B_" + i + ".jpg");
			ImageIO.write(subs[i], "JPEG", sub2);

			Util.writeTiff(subs[i], splitDir + name + "_c_" + i + ".tif");
			putInBase(img, subs, i);

			ImageData[] id = UnCodebase.split(sub.getAbsolutePath(), 1, false);
			for (ImageData d : id) {
				// d.show();
				d.writeImg(new File(splitDir, Util.getNameOnly(img.getName())
						+ "_" + (i) + ".jpg"), 5);
			}
		}
		return subs;
	}

	private static void putInBase(File img, BufferedImage[] subs, int i)
			throws IOException {
		String c = img.getName().substring(i, i + 1);
		System.out.println("c: " + c);
		char ch = c.charAt(0);
		if (ch >= 'a') {
			c = c + 1;
		}
		File dir = new File(baseImgDir + c);
		if (!dir.exists()) {
			dir.mkdir();
		}
		ImageIO.write(subs[i], "JPEG", new File(dir, Util.getNameOnly(img
				.getName())
				+ "_" + (i) + ".jpg"));
	}

	public static BufferedImage[] simpleSplit2(File img, int column)
			throws IOException {
		BufferedImage sbi = new BufferedImage(50, 150,
				BufferedImage.TYPE_INT_RGB);
		BufferedImage bi = ImageIO.read(img);

		// corp
		bi = bi.getSubimage(10, 0, bi.getWidth() - 10, bi.getHeight());

		int w = bi.getWidth();
		int h = bi.getHeight();
		BufferedImage subs[] = new BufferedImage[column];
		for (int i = 0; i < column; i++) {
			subs[i] = bi.getSubimage(i * (w / column), 0, w / column, h);
			String name = img.getName().replaceAll(".jpg", "");
			File sub = new File(splitDir, name + "_" + i + ".jpg");
			ImageIO.write(subs[i], "JPEG", sub);
			// Graphics2D g = (Graphics2D)sbi.getGraphics();
			// g.drawImage(subs[i], null, 10, 20);
			// ImageIO.write(sbi, "JPEG", new File(splitdDir, name + "_" + i
			// + "_a.jpg"));
			// ImageIO.write(sbi, "JPEG", new File(splitdDir, name + "_" + i
			// + "_a.jpg"));

			ImageData[] id = UnCodebase.split(sub.getAbsolutePath(), 1);
			for (ImageData d : id) {
				d.show();
				d.writeImg(new File(splitDir, Util.getNameOnly(img.getName())
						+ "_" + (i) + ".jpg"), 5);
			}
		}
		return subs;
	}

	public static void splitSample3() throws FileNotFoundException, IOException {
		// File f = new File(sampleDir);
		File f = new File(srmedDir);
		File[] ff = f.listFiles(new ImageFileFilter());

		for (File img : ff) {
			simpleSplit(img, 6);
		}

	}

	public static void splitSample2() throws FileNotFoundException, IOException {
		File f = new File(sampleDir);
		File[] ff = f.listFiles(new ImageFileFilter());

		for (File img : ff) {
			splitTest(img);
		}

	}

	public static void splitTestWithSrm(File image)
			throws FileNotFoundException, IOException {

		// File f = image;
		// BufferedImage bi = ImageIO.read(f);
		// UnCodebase uc = new UnCodebase(bi);
		// uc.grayByPixels();
		// int grayValue=uc.GetDgGrayValue(bi);
		// // System.out.println(grayValue);
		// uc.GetSingleBmpCode(grayValue);
		// BufferedImage sbi = uc._bi.getSubimage(0, 0, uc.w, uc.h);
		//		
		// LineFilter filter = new LineFilter();
		// filter.filterDeleteLines(sbi);
		// filter.filterHolesFill(sbi);
		//		
		//		
		// ImageData imageData = new ImageData(sbi, new WhiteFilter());
		// imageData.modify();
		//		
		// imageData.show();

		Srmjava srm = new Srmjava();
		String processedImg = (new File(srmedDir, image.getName()))
				.getAbsolutePath();
		int q = 200;
		srm.convert(image.getAbsolutePath(), processedImg, q);
		ImageData[] id = UnCodebase.split(processedImg, 6);

		// ImageData[] id = imageData.split(6, 1);
		int count = 0;
		for (ImageData d : id) {
			d.show();
			d.writeImg(new File(splitDir, Util.getNameOnly(image.getName())
					+ "_" + (count++) + ".jpg"), 5);
		}

		// ImageData[] id = UnCodebase.split(image.getAbsolutePath(), 0);
		// int count=0;
		// for(ImageData d :id){
		// d.writeImg(new File(splitdDir, (count++)+"_"+image.getName()));
		// }

	}

	public static void splitTest(File image) throws FileNotFoundException,
			IOException {

		File f = image;
		BufferedImage bi = ImageIO.read(f);
		UnCodebase uc = new UnCodebase(bi);
		uc.grayByPixels();
		int grayValue = uc.GetDgGrayValue(bi);
		// System.out.println(grayValue);
		uc.GetSingleBmpCode(grayValue);
		BufferedImage sbi = uc._bi.getSubimage(0, 0, uc.w, uc.h);

		LineFilter filter = new LineFilter();
		filter.filterDeleteLines(sbi);
		filter.filterHolesFill(sbi);
		ImageData imageData = new ImageData(sbi, new WhiteFilter());
		imageData.modify();

		imageData.show();

		ImageData[] id = imageData.split(6);
		int count = 0;
		for (ImageData d : id) {
			d.show();
			d.writeImg(new File(splitDir, Util.getNameOnly(image.getName())
					+ "_" + (count++) + ".jpg"), 5);
		}

		// ImageData[] id = UnCodebase.split(image.getAbsolutePath(), 0);
		// int count=0;
		// for(ImageData d :id){
		// d.writeImg(new File(splitdDir, (count++)+"_"+image.getName()));
		// }

	}

	private static void splitSampleImg() throws IOException,
			FileNotFoundException {
		File f = new File(sampleDir);
		File[] ff = f.listFiles(new ImageFileFilter());

		for (File img : ff) {

			Srmjava srm = new Srmjava();
			String processedImg = (new File(srmedDir, img.getName()))
					.getAbsolutePath();
			int q = 200;
			srm.convert(img.getAbsolutePath(), processedImg, q);
			ImageData[] id = UnCodebase.split(processedImg, 0);

			// ImageData[] id = UnCodebase.split(img.getAbsolutePath(), 0);

			int index = 0;
			for (ImageData data : id) {
				String c = img.getName().substring(index, ++index);
				System.out.println("c: " + c);
				char ch = c.charAt(0);
				if (ch >= 'a') {
					c = c + 1;
				}
				File dir = new File(baseImgDir + c);
				if (!dir.exists()) {
					dir.mkdir();
				}
				data.writeImg(new File(dir, ch + "_" + img.getName()));
			}
		}
	}
}
