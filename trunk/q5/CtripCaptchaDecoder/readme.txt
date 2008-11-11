本验证码专门针对携程网(http://www.ctrip.com)编写,仅用于该网站验证码的识别.

使用方法很简单,代码如下所示:

File modelFile = new File("model");
CtripCaptchaDecoder decoder = new CtripCaptChaDecoder(modelFile);

BufferedImage image = ImageIO.read(new File("test.jpg"));
String code = decoder.decode(image);

其中: modelFile是保存预先训练好的编码模式的文件(文件名为model), image是与待解码的携程网验证码图片相对应的BufferedImage;

另外, 本软件包仅适于单线程使用; 如果多线程使用, 请在每个线程中都创建一个CtripCaptchaDecoder.