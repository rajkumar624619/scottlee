// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   CreateDataModel.java

package com.okay.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

// Referenced classes of package com.okay.image:
//			ImageData

public class CreateDataModel
{

	public static final int MODEL_PLAIN = 0;
	public static final int MODEL_ITALIC = 2;
	public static final int MODEL_BOLD = 3;
	public static final int MODEL_BOLD_ITALIC = 5;
	static final int FONT_SIZE = 48;
	private String data_cneter;
	private boolean showDetail;

	public CreateDataModel()
	{
		data_cneter = "";
	}

	public static void main(String args[])
	
	{
		CreateDataModel cdm = new CreateDataModel();
		cdm.setSavePath("d://temp//");
		cdm.setShowDetail(true);
		try
		{
			cdm.createAll();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String createStr()
	{
		return "";
	}

	public void createAll()
		throws IOException
	{
		if (data_cneter.equals(""))
		{
			System.err.println("Please set imageModel targetpath.(e.g. E:\\dataCenter)");
			return;
		}
		(new File(data_cneter)).mkdirs();
		int allStyle[] = {
			0, 2, 3, 5
		};
		String allStyle_s[] = {
			"MODEL_PLAIN", "MODEL_ITALIC", "MODEL_BOLD", "MODEL_BOLD_ITALIC"
		};
//		String source = "0123456789abcdefghijklmnopqrstuvwxyz";
		String source = "23456789abdefghijklmnqrt";
		for (int i = 0; i < allStyle.length; i++)
		{
			for (int j = 0; j < source.length(); j++)
			{
				char c = source.charAt(j);
				if (c >= '2' && c <= '9')
				{
					BufferedImage bi = getCharImage((new StringBuilder()).append(c).toString(), allStyle[i]);
					ImageData img = new ImageData(bi);
					img.modify();
					ImageData _img[] = img.split(1);
					ImageIO.write(_img[0].getImage(), "jpeg", new File((new StringBuilder(String.valueOf(data_cneter))).append(allStyle_s[i]).append("_").append(c).append(".jpg").toString()));
					if (isShowDetail())
						System.out.println((new StringBuilder("final static String ")).append(allStyle_s[i]).append("_").append(c).append(" =\"").append(_img[0].toString()).append("\";").toString());
				} else
				if (c >= 'a' && c <= 't')
				{
					String c_s = (new StringBuilder()).append(c).toString().toUpperCase();
					BufferedImage bi = getCharImage((new StringBuilder()).append(c).toString(), allStyle[i]);
					ImageData img = new ImageData(bi);
					img.modify();
					ImageData _img[] = img.split(1);
					ImageIO.write(_img[0].getImage(), "jpeg", new File((new StringBuilder(String.valueOf(data_cneter))).append(allStyle_s[i]).append("_LOWERCASE_").append(c_s).append(".jpg").toString()));
					if (isShowDetail())
						System.out.println((new StringBuilder("final static String ")).append(allStyle_s[i]).append("_LOWERCASE_").append(c_s).append(" =\"").append(_img[0].toString()).append("\";").toString());
					BufferedImage ubi = getCharImage((new StringBuilder()).append(c).toString().toUpperCase(), allStyle[i]);
					ImageData uimg = new ImageData(ubi);
					uimg.modify();
					ImageData _uimg[] = uimg.split(1);
					ImageIO.write(_uimg[0].getImage(), "jpeg", new File((new StringBuilder(String.valueOf(data_cneter))).append(allStyle_s[i]).append("_UPPERCASE_").append(c_s).append(".jpg").toString()));
					if (isShowDetail())
						System.out.println((new StringBuilder("final static String ")).append(allStyle_s[i]).append("_UPPERCASE_").append(c_s).append(" =\"").append(_uimg[0].toString()).append("\";").toString());
				}
			}

		}

	}

	public BufferedImage getCharImage(String _char, int style)
	{
		int width = 60;
		int height = 60;
		BufferedImage bi = new BufferedImage(width, height, 4);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		Font f = null;
		switch (style)
		{
		case 3: // '\003'
			f = new Font("Courier New", 1, 48);
			break;

		case 5: // '\005'
			f = new Font("Courier New", 3, 48);
			break;

		case 2: // '\002'
			f = new Font("Courier New", 2, 48);
			break;

		case 0: // '\0'
			f = new Font("Courier New", 0, 48);
			break;

		case 1: // '\001'
		case 4: // '\004'
		default:
			f = new Font("Courier New", 0, 48);
			break;
		}
		g.setFont(f);
		g.setColor(Color.black);
		g.drawString(_char, 10, height - 10);
		g.dispose();
		return bi;
	}

	public void setSavePath(String data_cneter)
	{
		this.data_cneter = data_cneter;
	}

	public boolean isShowDetail()
	{
		return showDetail;
	}

	public void setShowDetail(boolean showDetail)
	{
		this.showDetail = showDetail;
	}
}
