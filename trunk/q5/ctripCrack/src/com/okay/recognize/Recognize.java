// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Recognize.java

package com.okay.recognize;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import maintain.Split;

import com.okay.image.ImageData;
import com.okay.image.ImageFactory;

// Referenced classes of package com.okay.recognize:
//			Data, Distance

public class Recognize {

	public Recognize() {
		for (int i = 0; i < 6; i++) {
			// ess[i]=Executors.newFixedThreadPool(field.length/4+1);
			ess[i] = Executors.newFixedThreadPool(10);
		}
	}

	public static void main(String args1[]) {
	}

	public String recognizeString(BufferedImage bi, int charNum) {
		StringBuffer result = new StringBuffer();
		try {

			BufferedImage[] bia = Split.simpleSplit(bi, 6);
			// ImageData _img = new ImageData(ImageFactory.otsuThreshold(bi));
			long start = System.currentTimeMillis();
			for (BufferedImage img : bia) {
				ImageData _img = new ImageData(img);
				// _img.show();
				_img.removeHorizontalLine();
				// _img.removeVerticalLine();
				_img.modify();
				ImageData[] d = _img.split(1);
				// d[0].show();
				String simg = d[0].toString();
				String s = recognizeChar(simg);
				result.append(s);
				long end = System.currentTimeMillis();
				System.out.println("recognizeChar cost " + (end - start)
						+ " ms.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	public String recognizeStringWithThread(BufferedImage bi, int charNum) {
		StringBuffer result = new StringBuffer();
		try {

			BufferedImage[] bia = Split.simpleSplit(bi, 6);
			// ImageData _img = new ImageData(ImageFactory.otsuThreshold(bi));

			ExecutorService es = Executors.newFixedThreadPool(6);
			Map<String, String> resultMap = new HashMap<String, String>();

			int index = 0;
			for (BufferedImage b : bia) {

				es.execute(new Handler(index++, resultMap, b));
			}

			shutDown(es);
			// System.out.println("b: "+resultMap);
			for (int i = 0; i < 6; i++) {
				result.append(resultMap.get("" + i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	private void shutDown(ExecutorService es) {
		es.shutdown(); // Disable new tasks from being submitted
		try {
			// Wait a while for existing tasks to terminate
			if (!es.awaitTermination(60, TimeUnit.SECONDS)) {
				es.shutdownNow(); // Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!es.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			es.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

//	public String recognizeString2(BufferedImage bi, int charNum) {
//		StringBuffer result = new StringBuffer();
//
//		ImageData _img = new ImageData(ImageFactory.otsuThreshold(bi));
//		// ImageData _img = new ImageData(bi);
//		_img.show();
//		_img.removeHorizontalLine();
//		_img.removeVerticalLine();
//		_img.show();
//		ImageData _imgs[] = _img.split(charNum);
//
//		for (int i = 0; i < _imgs.length; i++) {
//			_imgs[i].show();
//			result.append(recognizeChar(_imgs[i].toString()));
//		}
//
//		return result.toString();
//	}

	class Handler implements Runnable {
		Map result;
		int index;
		BufferedImage bImg;

		Handler(int index, Map result, BufferedImage bImg) {
			this.index = index;
			this.result = result;
			this.bImg = bImg;
		}

		public void run() {

			long start = System.currentTimeMillis();

			ImageData _img = new ImageData(bImg);
			// _img.show();
			_img.removeHorizontalLine();
			// _img.removeVerticalLine();
			_img.modify();
			_img.show();
			ImageData[] d = _img.split(1);
//			 d[0].show();
			String simg = d[0].toString();
			String s = recognizeChar(simg);

			// String s = recognizeCharWithThreads(simg, index);
			long end = System.currentTimeMillis();
			System.out.println("recognizeChar cost " + (end - start) + " ms.");

			result.put("" + index, s);
			// System.out.println(simg +" "+ index+ ": "+ result);
		}
	}

	final Field field[] = Data.class.getDeclaredFields();
	ExecutorService[] ess = new ExecutorService[6];

	public String recognizeCharWithThreads(String simg, int index) {
		String result = "";

		long start = System.currentTimeMillis();
		ExecutorService es = ess[index];
		// ExecutorService es = Executors.newFixedThreadPool(1);

		TreeMap<Integer, String> resultMap = new TreeMap<Integer, String>();

		for (int i = 0; i < field.length; i++) {
			long start1 = System.currentTimeMillis();
			Runnable r = new RecognizeCharHandler(simg, field[i], resultMap);
			long end1 = System.currentTimeMillis();
			if (end1 - start1 > 0) {
				System.out.println((end1 - start1)
						+ " ms consumed in new Runnable.");
			}

			es.execute(r);
			// es.submit(r);
			long end2 = System.currentTimeMillis();
			if (end2 - end1 > 0) {
				System.out.println((end2 - end1) + " ms consumed in submit.");
			}
		}
		long end = System.currentTimeMillis();
		System.out.println((end - start) + " ms consumed in exec.");
		shutDown(es);
		// System.out.println(resultMap);
		Integer dis = resultMap.firstKey();
		if (dis.intValue() > Distance.THRESHOLD) {
			return "#";
		}
		String name = resultMap.get(dis);
		result = (new StringBuilder(String.valueOf(name
				.charAt(name.length() - 1)))).toString();
		if (name.indexOf("LOWER") != -1) {
			result = result.toLowerCase();
		} else {
			result = result.toUpperCase();
		}

		System.out.println("result: " + result);

		return result;
	}

	public String recognizeChar(String simg) {
		Data d = new Data();
		Field field[] = Data.class.getDeclaredFields();
		int distance = 200;
		String s_name = "";
		try {
			for (int i = 0; i < field.length; i++) {
//				int temp = FastEditDistance.LD(simg, field[i].get(d).toString());
				int temp = Distance.LD(simg, field[i].get(d).toString());
				
				if (i == 0) {
					distance = temp;
					s_name = field[i].getName();
				} else if (distance > temp) {
					distance = temp;
					s_name = field[i].getName();
				}
				
//				if(distance<50){
//					System.out.println(s_name+ " distance: "+distance);
//					break;
//				}
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (s_name.indexOf("LOWER") != -1)
			s_name = (new StringBuilder(String.valueOf(s_name.charAt(s_name
					.length() - 1)))).toString().toLowerCase();
		else
			s_name = (new StringBuilder(String.valueOf(s_name.charAt(s_name
					.length() - 1)))).toString();
		return s_name;
	}

	// public String recognizeChar(BufferedImage bi)
	// throws IllegalArgumentException, IllegalAccessException {
	// Data d;
	// Field field[];
	// String simg;
	// int distance;
	// String s_name;
	// d = new Data();
	// field = Data.class.getDeclaredFields();
	// ImageData img = new ImageData(bi);
	// img = img.split(1)[0];
	// simg = img.toString();
	// System.out.println("simg: " + simg);
	// distance = 0;
	// s_name = "";
	// for (int i = 0; i < field.length; i++) {
	// System.out.println("field[i].get(d).toString(): "
	// + field[i].get(d).toString());
	// int temp = Distance.LD(simg, field[i].get(d).toString());
	// if (i == 0) {
	// distance = temp;
	// s_name = field[i].getName();
	// } else if (distance > temp) {
	// distance = temp;
	// s_name = field[i].getName();
	// }
	// System.out.println(s_name + " " + temp);
	// }
	//
	// // if (distance > 100)
	// // return " ";
	// if (s_name.indexOf("LOWER") != -1)
	// s_name = (new StringBuilder(String.valueOf(s_name.charAt(s_name
	// .length() - 1)))).toString().toLowerCase();
	// else
	// s_name = (new StringBuilder(String.valueOf(s_name.charAt(s_name
	// .length() - 1)))).toString();
	// return s_name;
	// }
}
