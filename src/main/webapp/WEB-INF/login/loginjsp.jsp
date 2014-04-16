<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="de.fau.amos.*"%> 
    
    <%
		User.init(getServletConfig());
	%>
    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Insert title here</title>
</head>
<body>
<%  
System.out.println("called login.jsp");
int ret=User.exists(request.getParameter(Const.RequestParameters.LOGIN_USERNAME),request.getParameter(Const.RequestParameters.LOGIN_PASSWORD));
switch(ret){
case Const.UserReturns.SUCCESS:
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.LOGGED_IN);
	session.setAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME,request.getParameter(Const.RequestParameters.LOGIN_USERNAME));
	System.out.println("login successfull -> forward from login.jsp");

	response.sendRedirect(request.getContextPath()+Const.URL.INTERN_HOME);
	break;
case Const.UserReturns.USERNAME_INVALID:
case Const.UserReturns.USERNAME_UNKNOWN:
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.USERNAME_UNKNOWN);
	response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	break;
case Const.UserReturns.PASSWORD_INVALID:
case Const.UserReturns.PASSWORD_WRONG:
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.PASSWORD_WRONG);
	response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	break;
case Const.UserReturns.FAILED:
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.FAILURE);
	response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	break;


}
out.println(""+User.exists(request.getParameter("username"),request.getParameter("password")));
%>

<%//session.setAttribute("loggedin","something"); %>
</body>
</html>