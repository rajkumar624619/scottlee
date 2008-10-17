<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ page import="java.io.File"%>
<%@ page import="javax.servlet.jsp.JspWriter"%>
<%@ page import="com.okay.recognize.*"%>
<%@ page import="com.okay.image.*"%>

<%
		try {
		String uid = request.getParameter("uid");
		if (uid != null && uid.length() <= 13) {
			String code = getCode(out, uid);
		} else {
			System.out.println("Wrong uid format: " + uid);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
%>

<%!public String getCode(JspWriter out, String uid) throws Exception {
		String path = "/server/cache8/ctrippic/" + uid + ".jpg";
		File f = new File(path);
		String code = "";
		if (f.exists() && f.canWrite()) {
			code = CtripDecoder.decode(path);
		} else {
			System.out.println("File not exist: " + path);
		}
		code = code.trim();
		out.println(code);
		f.delete();
		return code;
	}%>