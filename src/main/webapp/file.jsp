<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<%

System.out.println("called file "+request.getRequestURL());

String pathinfo=request.getPathInfo();
if(pathinfo==null){
	response.sendRedirect(request.getContextPath());	
}else if(pathinfo.equals("/upload")){
	System.out.println("forward to uploadgedöns");
	request.getRequestDispatcher("/FileUpload").forward(request,response);
}else if(pathinfo.equals("/download")){
	System.out.println("want to download");
	request.getRequestDispatcher("/FileDownload").forward(request,response);
}


%>

</body>
</html>