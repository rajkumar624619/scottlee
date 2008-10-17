package maintain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LineFilter {
	public void filterFloodFill(BufferedImage _bi,int _x,int _y,int _r,int _g,int _b){
		ColorModel model=ColorModel.getRGBdefault();
		int oldr, oldg, oldb;
	    int nextr, nextg, nextb;
	    
	    if(_x < 0 || _y < 0 || _x >= _bi.getWidth() || _y >= _bi.getHeight())
	        return;
	    
	    int oldrgb=_bi.getRGB(_x,_y);
	    oldr=model.getRed(oldrgb);
	    oldg=model.getGreen(oldrgb);
	    oldb=model.getBlue(oldrgb);
	    _bi.setRGB(_x, _y, new Color(_r,_g,_b).getRGB());
	    
	    int nextrgb=_bi.getRGB(_x+1, _y);
	    nextr=model.getRed(nextrgb);
	    nextg=model.getGreen(nextrgb);
	    nextb=model.getBlue(nextrgb);
	    if(oldr==nextr && oldg==nextg && oldb==nextb)
	    	filterFloodFill(_bi,_x+1, _y, _r,_g,_b);
	    
	    nextrgb=_bi.getRGB(_x-1, _y);
	    nextr=model.getRed(nextrgb);
	    nextg=model.getGreen(nextrgb);
	    nextb=model.getBlue(nextrgb);
	    if(oldr==nextr && oldg==nextg && oldb==nextb)
	    	filterFloodFill(_bi,_x-1, _y, _r,_g,_b);
	    
	    nextrgb=_bi.getRGB(_x, _y+1);
	    nextr=model.getRed(nextrgb);
	    nextg=model.getGreen(nextrgb);
	    nextb=model.getBlue(nextrgb);
	    if(oldr==nextr && oldg==nextg && oldb==nextb)
	    	filterFloodFill(_bi,_x, _y+1, _r,_g,_b);
	    
	    nextrgb=_bi.getRGB(_x, _y-1);
	    nextr=model.getRed(nextrgb);
	    nextg=model.getGreen(nextrgb);
	    nextb=model.getBlue(nextrgb);
	    if(oldr==nextr && oldg==nextg && oldb==nextb)
	    	filterFloodFill(_bi,_x, _y-1, _r,_g,_b);
	    
	}
	
	//缩放算法
	public void filterScal(){
		
	}
	
	public void filterHolesFill(BufferedImage _bi){
		ColorModel model=ColorModel.getRGBdefault();
		int w=_bi.getWidth();
		int h=_bi.getHeight();
		for(int y = 0; y < h; y++){
	        for(int x = 2; x < w - 2; x++)
	        {
	            int c1, c2, c3, c4, c5;
	            c1=model.getRed(_bi.getRGB(x-2, y));
	            c2=model.getRed(_bi.getRGB(x-1, y));
	            c3=model.getRed(_bi.getRGB(x, y));
	            c4=model.getRed(_bi.getRGB(x+1, y));
	            c5=model.getRed(_bi.getRGB(x+2, y));
	            
	            if(c1 < 127 && c2 < 127 && c3 > 128 && c4 < 127)
	                c3 = (c1 + c2 + c4) / 3;
	            else if(c2 < 127 && c3 > 128 && c4 < 127 && c5 < 127)
	                c3 = (c2 + c4 + c5) / 3;
	            _bi.setRGB( x, y, new Color(c3, c3, c3).getRGB());
	        }
		}
		
	    for(int x = 0; x < w; x++)
	        for(int y = 2; y < h - 2; y++)
	        {
	            int c1, c2, c3, c4, c5;
	            
	            c1=model.getRed(_bi.getRGB(x, y-2));
	            c2=model.getRed(_bi.getRGB(x, y-1));
	            c3=model.getRed(_bi.getRGB(x, y));
	            c4=model.getRed(_bi.getRGB(x, y+1));
	            c5=model.getRed(_bi.getRGB(x, y+2));
	            
	            if(c1 < 127 && c2 < 127 && c3 > 128 && c4 < 127)
	                c3 = (c1 + c2 + c4) / 3;
	            else if(c2 < 127 && c3 > 128 && c4 < 127 && c5 < 127)
	                c3 = (c2 + c4 + c5) / 3;
	            _bi.setRGB( x, y, new Color(c3, c3, c3).getRGB());
	        }
	}
	
	public void filterDeleteLines(BufferedImage _bi) {
		ColorModel model = ColorModel.getRGBdefault();
		int w = _bi.getWidth();
		int h = _bi.getHeight();

		int x, y;
		int r, ra, rb;

		/* Remove white lines */
		for (y = 0; y < h; y++)
			for (x = 0; x < w; x++) {
				if (y > 0 && y < h - 1) {
					ra = model.getRed(_bi.getRGB(x, y - 1));
					r = model.getRed(_bi.getRGB(x, y));
					rb = model.getRed(_bi.getRGB(x, y + 1));

					if (r > ra && (r - ra) * (r - rb) > 5000)
						_bi.setRGB(x, y, new Color(ra, ra, ra).getRGB());
				}
			}

		/* Remove black lines */
		for (y = 0; y < h; y++)
			for (x = 0; x < w; x++) {
				if (y > 0 && y < h - 1) {
					ra = model.getRed(_bi.getRGB(x, y - 1));
					r = model.getRed(_bi.getRGB(x, y));
					rb = model.getRed(_bi.getRGB(x, y + 1));
					if (r < ra && (r - ra) * (r - rb) > 500)
						_bi.setRGB(x, y, new Color(ra, ra, ra).getRGB());
				}
			}

	}
	
	public static void main(String[] args) throws IOException {
		LineFilter filter=new LineFilter();
		BufferedImage _bi=ImageIO.read(new File("D:\\21.jpg"));
		filter.filterDeleteLines(_bi);
		filter.filterHolesFill(_bi);
//		filter.filterDeleteLines(_bi);
		ImageIO.write(_bi, "png", new File("D:\\21.png"));
	}
}
