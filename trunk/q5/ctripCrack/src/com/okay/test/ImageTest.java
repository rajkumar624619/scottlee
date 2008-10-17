// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ImageTest.java

package com.okay.test;

import com.okay.image.ImageData;
import com.okay.image.ImageFactory;
import com.okay.recognize.Recognize;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageTest
{

	public ImageTest()
	{
	}

	public static void main(String args[])
		throws IOException
	{
		long timestamp = System.currentTimeMillis();
		java.awt.image.BufferedImage bi = ImageIO.read(new File("E:\\ctrip_image\\DkjtFk.jpg"));
		System.out.println((new Recognize()).recognizeStringWithThread(bi, 6));
		System.out.println(System.currentTimeMillis() - timestamp);
	}

	public static void test(String filePath)
		throws IOException
	{
		java.awt.image.BufferedImage bi = ImageIO.read(new File(filePath));
		System.out.println(filePath);
		ImageFactory factory = new ImageFactory();
		java.awt.image.BufferedImage gbi = ImageFactory.otsuThreshold(bi);
		ImageData _img = new ImageData(gbi);
		_img.removeHorizontalLine();
		_img.removeVerticalLine();
		_img.show();
		ImageData _imgs[] = _img.split(6);
		for (int i = 0; i < _imgs.length; i++)
			_imgs[i].show();

	}
}
