package test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import junit.framework.TestCase;
import maintain.Split;

import com.asprise.util.ocr.OCR;
import com.okay.image.ImageData;
import com.okay.recognize.CtripDecoder;
import com.okay.validate.ImageFileFilter;
import com.okay.validate.Util;

public class ImageDecoderTest extends TestCase {
	
	public void setUp(){
		File temp=new File("tmp");
		System.out.println(temp.getAbsolutePath());
		Util.delete(temp);
		temp.mkdir();
	}

	public void atestSingle() throws IllegalArgumentException, IOException, IllegalAccessException{
		long start =System.currentTimeMillis();
		File f = new File("d://21.jpg");

		String code = CtripDecoder.decode(f);
		long end = System.currentTimeMillis();
		System.out.println(f.getName()+" "+ code +" "+(end-start) +" ms consumed.");
	}
	
	public void testSingle2() throws IllegalArgumentException, IOException, IllegalAccessException{
		long start =System.currentTimeMillis();
		File f = new File("D:\\frk4aD.jpg");
//		File f = new File("D:\\work\\CtripTest\\base\\sample\\c2\\n4enMj.jpg");
		String code = CtripDecoder.decode(f);
		long end = System.currentTimeMillis();
		System.out.println(f.getName()+" "+ code +" "+(end-start) +" ms consumed.");
	}
	
	public void testDecode() throws FileNotFoundException, IOException, IllegalArgumentException, IllegalAccessException {
		long start =System.currentTimeMillis();
		File sampleDir = new File("D:\\work\\CtripTest\\base\\sample\\c2");
//		File sampleDir = new File("D:\\work\\ctripCrack\\WebContent\\imgs\\tmp");
		File[] ff = sampleDir.listFiles(new ImageFileFilter());
		float right_count=0;
		float count =0;
		for(File f: ff){
			count++;
//			String code = ImageDecoder.decode(f);
			String code = CtripDecoder.decode(f);
			System.out.println(f.getName()+" "+ code);
			if(code.equals(Util.getNameOnly(f.getName()))){
				right_count++;
			}
		}
		System.out.println("Count: "+ count+ " right: "+ right_count);
		System.out.println("Rate: "+ right_count/count);
		long end = System.currentTimeMillis();
		System.out.println((end-start) +" ms consumed.");
	}
	
	
	public void atestOCR() throws IOException{
		OCR ocr = new OCR();
		BufferedImage result=new BufferedImage(180,40, BufferedImage.TYPE_INT_RGB);
		BufferedImage image=ImageIO.read(new File("d://11.jpg"));
		
		BufferedImage[] bia = Split.simpleSplit(image, 6);
		Graphics2D g = result.createGraphics();
		
		for(int i = 0; i< result.getWidth();i++){
			for(int j=0; j<result.getHeight();j++){
				result.setRGB(i, j, 0xffffff);
			}
		}
		
		// ImageData _img = new ImageData(ImageFactory.otsuThreshold(bi));
		int w=0;
		for (BufferedImage b : bia) {
			ImageData _img = new ImageData(b);
//			_img.show();
			_img.removeHorizontalLine();
			_img.removeVerticalLine();
//			_img.show();
			ImageData[] d = _img.split(1);
			d[0].show();
			
			g.drawImage(d[0].getImage(), null, w, 40-d[0].h);
			w+=d[0].w;
		}
		ImageIO.write(result, "JPEG", new File("d://222.jpg"));
		Util.writeTiff(result, "d://222.tif");

		String code = ocr.recognizeEverything(image);
		System.out.println("ocr code: "+code);
	}

//	public void testDecode2() throws FileNotFoundException, IOException {
//		File sampleDir = new File("base/sample");
//		File[] ff = sampleDir.listFiles(new ImageFileFilter());
//		for(File f: ff){
//			String code = ImageDecoder.decode2(f);
//			System.out.println(f.getName()+" "+ code);
//		}
//	}
	
//	public void testDecode3() throws IOException{
//		// TODO Auto-generated method stub
//		long timestamp=System.currentTimeMillis();
//		BufferedImage bi=ImageIO.read(new File("D:\\21.jpg"));
//		System.out.println(new Recognize().recognizeString(bi, 6));
//		System.out.println(System.currentTimeMillis()-timestamp);
//		
//		
//	}
//	
//	public void testGen() throws IOException{
//		CreateDataModel cdm = new CreateDataModel();
//		cdm.setSavePath("D:\\work\\CtripTest\\base\\data\\xie\\");
//		cdm.setShowDetail(true);
//		cdm.createAll();
//	}
	
	
//	public void testSingleChar() throws IOException, IllegalArgumentException, IllegalAccessException{
//		Recognize re = new Recognize();
//		BufferedImage bi = ImageIO.read(new File("D:\\work\\CtripTest\\base\\sample\\split_1\\2FbDnI_1.jpg"));
//		String c = re.recognizeChar(bi);
//		System.out.println("Char: "+ c);
//		
//	}
}
