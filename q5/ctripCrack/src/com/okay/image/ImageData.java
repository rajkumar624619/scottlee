// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   ImageData.java

package com.okay.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

// Referenced classes of package com.okay.image:
//			WhiteFilter, Filter

public class ImageData {

	public int data[][];
	public int w;
	public int h;
	public char code;

	public ImageData() {
	}

	public ImageData(BufferedImage bi) {
		this(bi, ((Filter) (new WhiteFilter())));
	}

	public ImageData(BufferedImage bi, Filter filter) {
		h = bi.getHeight();
		w = bi.getWidth();
		data = new int[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int p = bi.getRGB(j, i);
				data[i][j] = p;
			}

		}

		filter.doFilter(data);
	}

	public ImageData clone(int x, int y, int w0, int h0) {
		ImageData ia = new ImageData();
		ia.w = w0;
		ia.h = h0;
		ia.data = new int[ia.h][ia.w];
		for (int i = 0; i < h0; i++) {
			for (int j = 0; j < w0; j++)
				ia.data[i][j] = data[i + y][j + x];

		}

		return ia;
	}

	public void removeHorizontalLine() {
		for (int i = 0; i < h; i++)
			if (i == 0 || i == h - 1) {
				for (int j = 0; j < w; j++)
					data[i][j] = 1;

			} else {
				for (int j = 0; j < w; j++)
					if (j == 0 || j == w - 1) {
						data[i][j] = 1;
					} else {
						if (data[i][j] == 0 && data[i - 1][j] == 1
								&& data[i + 1][j] == 1)
							data[i][j] = 1;
						if (data[i][j] == 1 && data[i - 1][j] == 0
								&& data[i + 1][j] == 0)
							data[i][j] = 0;
					}

			}

	}

	public void removeVerticalLine() {
		for (int i = 0; i < w; i++)
			if (i == 0 || i == w - 1) {
				for (int j = 0; j < h; j++)
					data[j][i] = 1;

			} else {
				for (int j = 0; j < h; j++)
					if (j == 0 || j == h - 1) {
						data[j][i] = 1;
					} else {
						if (data[j][i] == 0 && data[j][i - 1] == 1
								&& data[j][i + 1] == 1)
							data[j][i] = 1;
						if (data[j][i] == 1 && data[j][i - 1] == 0
								&& data[j][i + 1] == 0)
							data[j][i] = 0;
					}

			}

	}

	public void corpBlank() {
		this.split(1);
	}

	public String toString() {
		StringBuffer _buf = new StringBuffer();
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++)
				_buf.append(data[i][j]);

		}

		return _buf.toString();
	}

	public BufferedImage getImage() {
		if (w <= 0 || h <= 0)
			return new BufferedImage(10, 10, 4);
		BufferedImage bi = new BufferedImage(w, h, 4);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int rgb = data[j][i] != 0 ? 0xffffff : 0;
				bi.setRGB(i, j, rgb);
			}

		}

		return bi;
	}

	public void modify() {
		for (int i = 0; i < h; i++)
			if (i == 0 || i == h - 1) {
				for (int j = 0; j < w; j++)
					data[i][j] = 1;

			} else {
				for (int j = 0; j < w; j++)
					if (j == 0 || j == w - 1)
						data[i][j] = 1;
					else if (data[i][j] == 0
							&& data[i - 1][j - 1] + data[i][j - 1]
									+ data[i + 1][j - 1] + data[i - 1][j]
									+ data[i + 1][j] + data[i - 1][j + 1]
									+ data[i][j + 1] + data[i + 1][j + 1] > 5)
						data[i][j] = 1;

			}

	}

	public void show() {
		System.out.println();
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++)
				System.out.print((new StringBuilder(String
						.valueOf(data[i][j] != 1 ? " " : "1"))).toString());

			System.out.println();
		}

		System.out.println();
	}

	public int[] getSplitPointsX2(int charNum) {
		int[] posx = new int[charNum * 2];
		int subW = w / charNum;
		for (int i = 0; i < charNum; i++) {
			posx[2 * i] = i * subW;
			posx[2 * i + 1] = (i + 1) * subW;
			// System.out.println(i + " " + posx[i]);
		}
		return posx;
	}

	public int[] getSplitPointsX3(int charNum) {

		int minThreshHold = 20;
		int maxThreshHold = 60;

		int[] posx = new int[charNum * 2];

		try {
			StringBuffer _buf = new StringBuffer();
			for (int i = 0; i < w; i++) {
				int temp = 0;
				for (int j = 0; j < h; j++) {
					temp += data[j][i];
				}
				_buf.append(Math.abs(temp - h) < 2 ? "#" : "@");
			}
			String sample = _buf.toString();
			String[] str = sample.split("#{5,}");
			if (str.length <= charNum) {
				return getSplitPointsX2(charNum);
			}
			int[] length = new int[str.length];
			for (int i = 0; i < str.length; i++) {
				// System.out.println(str[i].length() + "|" + str[i]);
				length[i] = str[i].length();
			}

			Arrays.sort(length);
			int minWidth = length[length.length - charNum] - 1;
			int maxWidth = length[length.length - 1];
			if (minWidth < minThreshHold || maxWidth > maxThreshHold) {
				return getSplitPointsX2(charNum);
			}

			int indexs = 0;
			int tempx = 0;
			for (int i = 0; i < str.length; i++) {
				if (str[i].length() > minWidth) {
					tempx = sample.indexOf("#" + str[i] + "#", tempx + 1);
					posx[indexs] = tempx + 1;
					indexs++;
					posx[indexs] = posx[indexs - 1] + str[i].length();
					indexs++;
				}
			}
			// System.out.println("-------------");
			// for (int i = 0; i < posx.length; i++) {
			// System.out.print("\t" + posx[i]);
			// }
			// System.out.println("");
			// System.out.println("-------------");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return posx;
	}

	public int[] getSplitPointsX(int charNum) {
		int minWidth = 0;
		StringBuffer _buf = new StringBuffer();
		for (int i = 0; i < w; i++) {
			int temp = 0;
			for (int j = 0; j < h; j++)
				temp += data[j][i];

			_buf.append(Math.abs(temp - h) != 0 ? "@" : "#");
		}

		String sample = _buf.toString();
		String str[] = sample.split("#+");
		int lens[] = new int[str.length];
		for (int i = 0; i < str.length; i++)
			lens[i] = str[i].length();

		Arrays.sort(lens);
		minWidth = lens[lens.length - charNum - 1] + 1;
		int posx[] = new int[charNum * 2];
		int indexs = 0;
		int tempx = 0;
		for (int i = 0; i < str.length; i++) {
			if (str[i].length() > minWidth && indexs < posx.length) {
				tempx = sample.indexOf((new StringBuilder("#")).append(str[i])
						.append("#").toString(), tempx + 1);
				posx[indexs] = tempx + 1;
				indexs++;
				posx[indexs] = posx[indexs - 1] + str[i].length();
				indexs++;
			}
			tempx = tempx != 0 ? tempx : 1;
		}
//		System.out.println("w: " + w + "h: " + h + " posx: " + posx[0] + " "
//				+ posx[1]);
		return posx;
	}

	public int[] getSplitPointsY() {
		System.out.println("w: "+w +" h: "+h);
		int minWidth = 0;
		StringBuffer _buf = new StringBuffer();
		for (int i = 0; i < h; i++) {
			int temp = 0;
			for (int j = 0; j < w; j++){
				temp += data[i][j];
			}
			_buf.append(Math.abs(temp - w) != 0 ? "@" : "#");
		}

		String sample = _buf.toString();
		System.out.println("sample: "+sample);
		String str[] = sample.split("#+");
		int lens[] = new int[str.length];
		for (int i = 0; i < str.length; i++){
			lens[i] = str[i].length();
		}

		Arrays.sort(lens);
		try {
			minWidth = lens[lens.length - 1] - 1;
		} catch (RuntimeException e) {
			e.printStackTrace();
			minWidth = 0;
			Logger.getLogger("ImageDate.class").log(Level.WARNING,
					"Can't split!May be error!");
		}
		int posy[] = new int[2];
		int indexs = 0;
		int tempx = 0;
		for (int i = 0; i < str.length; i++)
			if (str[i].length() > minWidth && indexs < posy.length) {
				tempx = sample.indexOf((new StringBuilder("#")).append(str[i])
						.append("#").toString(), tempx + 1);
				posy[indexs] = tempx + 1;
				indexs++;
				posy[indexs] = posy[indexs - 1] + str[i].length();
				indexs++;
			}

		return posy;
	}

	public ImageData[] split(int charNum) {
		ImageData sub[] = new ImageData[charNum];
		int splitPoints[] = getSplitPointsX(charNum);
		for (int i = 0; i < charNum; i++) {
			int lbound = splitPoints[2 * i];
			int ubound = splitPoints[2 * i + 1];
			int posx = lbound;
			int posy = 0;
			int posx1 = ubound;
			int posy1 = h;
//			System.out.println(posx+" "+ posy+" "+ (posx1 - posx)+" "+ (posy1 - posy));
			sub[i] = clone(posx, posy, posx1 - posx, posy1 - posy);
			int posys[] = sub[i].getSplitPointsY();
			sub[i] = clone(posx, posys[0], posx1 - posx, posys[1] - posys[0]);
		}

		return sub;
	}
	
	public void writeImg(File imgName) throws IOException {
		try {
			BufferedImage img = new BufferedImage(this.w, this.h,
					BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					int rgb = 0;
					if (data[j][i] == 0) {
						rgb = 0;
					} else {
						rgb = 0xffffff;
					}
					img.setRGB(i, j, rgb);
				}
			}
			ImageIO.write(img, "BMP", imgName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
