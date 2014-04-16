<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="de.fau.amos.*"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<%
	System.out.println("called publicIntern "+request.getRequestURL());
	if(Const.SessionAttributs.LoginState.Valeus.LOGGED_IN.equals(session.getAttribute(Const.SessionAttributs.LoginState.NAME))){
		System.out.println("session valid -> want to load "+request.getPathInfo() );
		request.getRequestDispatcher("/WEB-INF/intern"+request.getPathInfo()).forward(request,response);
	}else{
		//session invalid -> redirect to loginPage
		session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.NOT_LOGGED_IN);
		response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	}
%>
</body>
</html>