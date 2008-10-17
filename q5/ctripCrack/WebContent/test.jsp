<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="org.apache.commons.httpclient.methods.GetMethod"%>
<%@ page import="java.awt.image.BufferedImage"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.IOException"%>

<%@ page import="javax.imageio.ImageIO"%>

<%@ page import="org.apache.commons.httpclient.HttpClient"%>

<%@ page import="javax.servlet.jsp.JspWriter"%>
<%@ page import="com.okay.recognize.*"%>
<%@ page import="com.okay.image.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ctrip Crack Test Page</title>
</head>
<body>
<%
long start = System.currentTimeMillis();
getCode(out, 1); 
long end = System.currentTimeMillis();
out.println("<br>"+ (end-start) +" ms consumed total.");
%>
</body>
</html>

<%!public String getCode(JspWriter out, int i) throws Exception {
		long start = System.currentTimeMillis();
		HttpClient client = new HttpClient();
		String name = System.currentTimeMillis()+"";
		String path = "D:\\work\\ctripCrack\\WebContent\\imgs\\tmp\\" + name + ".jpg";
		
		String code = "";
		
		//GetMethod image = new GetMethod("http://www.elong.com/flights/ValidateToken.aspx");
		GetMethod image = new GetMethod(
				"http://hotels.ctrip.com/Domestic/RandomValidateImage.aspx");

		try {
			client.executeMethod(image);
			BufferedImage bi = ImageIO.read(image.getResponseBodyAsStream());
			image.releaseConnection();
			ImageIO.write(bi, "bmp", new File(path));
			long end = System.currentTimeMillis();
			BufferedImage bi2 = ImageIO.read(new File(path));
			
			CtripCapchaDecoder decoder = new CtripCapchaDecoder(bi);
//			decoder.removeNoise();
			decoder.removeStraightLines();
			bi2 = decoder.toImage();
			code = new Recognize().recognizeString(bi2, 6);
			code = code.trim();
			out.println("<img src='imgs/tmp/"+name+".jpg' "+ "/>");
			out.println("<br>"+code);
			out.println("<br>"+ (end-start) +" ms consumed to get img from ctrip.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return code;
	}%>