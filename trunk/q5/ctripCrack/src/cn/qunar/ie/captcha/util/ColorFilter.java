package cn.qunar.ie.captcha.util;
import java.awt.image.BufferedImage;
import java.util.List;


public class ColorFilter {
	public int getCount() {
		return count;
	}

	public double getMeanOfHues() {
		return meanOfHues;
	}

	public double getMeanOfSaturations() {
		return meanOfSaturations;
	}

	public static int RGBtoHSI(int rgb)
    {
        // Initialise HSI values
        double h = 0;
        double s = 0;
        double i = 0;
 
        // Seperate RGB values
        int r = (rgb & 0x00FF0000) >> 16;
        int g = (rgb & 0x0000FF00) >> 8;
        int b = (rgb & 0x000000FF);
 
        // Calculate maximum, and minimum of the RGB component values
        int max, min;
        if (r>g && r>b)
        {
            max = r;
            min = Math.min(g,b);
        }
        else
        {
            if (g>b)
            {
                max = g;
                min = Math.min(r,b);
            }
            else
            {
                max = b;
                min = Math.min(r,g);
            }
        }
 
        // Calculate intensity
        i=Math.round(((float)max/255)*100);
        if (i==0) return (int)(((int)h << 16) + ((int)s << 8) + (int)i);                    // No intensity - Colour is black
 
        // Calculate saturation
        if (max==min) return (int)(((int)h << 16) + ((int)s << 8) + (int)i);                // No saturation - Colour is grey
        s = Math.round((((float)max/255) - ((float)min/255))/((float)max/255)*100);
 
        // Calculate hue
        double dbl_hue = Math.acos((0.5*((r-g)+(r-b)))/(Math.sqrt(Math.pow((r-g),2)+(r-b)*(g-b))));
        if (b>g) dbl_hue = (2*Math.PI)-dbl_hue;
        dbl_hue = Math.toDegrees(dbl_hue);
        h = Math.round(Math.round(dbl_hue));
 
        return (int)(((int)h << 16) + ((int)s << 8) + (int)i);
    }
	
	private static double getMean(int[] values, int count) {
		long total = 0;
		for (int i = 0; i < count; i++) {
			total += values[i];
		}
		return total * 1.0 / count;
	}
	private static double getVariance(int[] values, int count, double mean) {
		double total = 0;
		for (int i = 0; i < count; i++) {
			total += Math.pow(values[i] - mean, 2);
		}
		total /= (count - 1);
		return Math.sqrt(total);
	}
	private static boolean isNormal(int value, double mean, double variance) {
		return Math.abs((value - mean) / variance) < 2;
	}
	
	BufferedImage image;
	List pixels;
	int count;
	int[] hues;
	int[] saturations;
	double meanOfHues, varianceOfHues;
	double meanOfSaturations, varianceOfSaturations;
	
	private static int getHue(int hsi) {
		return (hsi & 0xFF0000) >> 16;
	}
	private static int getSaturation(int hsi) {
		return (hsi & 0xFF00) >> 8;
	}
	
	private int[] getHueAndSaturation(int x, int y) {
		int[] result = new int[2];
		int rgb = image.getRGB(x, y);
		int hsi = RGBtoHSI(rgb);
		result[0] = getHue(hsi);
		result[1] = getSaturation(hsi);
		return result;
	}
	public ColorFilter(List pixels, BufferedImage image) {
		this.image = image;
		this.pixels = pixels;
		hues = new int[pixels.size()];
		saturations = new int[pixels.size()];
		count = 0;
		for (Object obj : pixels) {
			int[] xy = (int[]) obj;
			int x = xy[0], y = xy[1];
			int[] hueAndSaturation = getHueAndSaturation(x, y);
			int hue = hueAndSaturation[0];
			int saturation = hueAndSaturation[1];
			if (hue == 0) {
				continue;
			}
			hues[count] = hue;
			saturations[count] = saturation;
			count++;
		}
		meanOfHues = getMean(hues, count);
		varianceOfHues = getVariance(hues, count, meanOfHues);
		
		meanOfSaturations = getMean(saturations, count);
		varianceOfSaturations = getVariance(saturations, count, meanOfSaturations);
	}
	
	public boolean isNormal(int x, int y) {
		int[] hueAndSaturation = getHueAndSaturation(x, y);
		return isNormal(hueAndSaturation[0], meanOfHues, varianceOfHues) /*&& isNormal(hueAndSaturation[1], meanOfSaturations, varianceOfSaturations)*/;
	}
}
