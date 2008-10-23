package cn.qunar.ie.captcha.util;

import java.awt.image.BufferedImage;

public class ImageSegment {
	public static BufferedImage[] segment(BufferedImage bi) {
		BufferedImage[] subImages = new BufferedImage[6];
		for (int i = 0; i < 6; i++) {
			BufferedImage subImage = bi.getSubimage(i * 50, 0, 50 + (i == 5 ? 0 : 10), 150);
			subImages[i] = subImage;
		}
		return subImages;
	}
}
