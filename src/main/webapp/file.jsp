<!-- 
 - Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob Hübler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
 -
 - This file is part of the Green Energy Cockpit for the AMOS Project.
 -
 - This program is free software: you can redistribute it and/or modify
 - it under the terms of the GNU Affero General Public License as
 - published by the Free Software Foundation, either version 3 of the
 - License, or (at your option) any later version.
 -
 - This program is distributed in the hope that it will be useful, 
 - but WITHOUT ANY WARRANTY; without even the implied warranty of
 - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 - GNU Affero General Public License for more details.
 -
 - You should have received a copy of the GNU Affero General Public
 - License along with this program. If not, see
 - <http://www.gnu.org/licenses/>.
 -->

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

//this file is a nondisplayable webpage.
//when called it forwards depending on the request call -> it's a handler


System.out.println("[file.jsp]: called file with ["+request.getRequestURL()+"]");

String pathinfo=request.getPathInfo();
if(pathinfo==null){
	//called file.jsp with no more information -> call invalid
	response.sendRedirect(request.getContextPath());	
}else if(pathinfo.startsWith("/upload")){
	//want to upload something -> forward to FileUpload.java
	//replace "upload" with "FileUpload" to get access to FileUpload and don't loose information
	request.getRequestDispatcher("/FileUpload"+pathinfo.replace("/upload","")).forward(request,response);
}else if(pathinfo.equals("/download")){
	//want to download something -> forward to FileDownload.java
	request.getRequestDispatcher("/FileDownload").forward(request,response);
}else{
	//called file.jsp with invalid information
	response.sendRedirect(request.getContextPath());
}


%>

</body>
</html>