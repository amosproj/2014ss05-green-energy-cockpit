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
	System.out.println("regjspjsp");
	String user=request.getParameter(Const.RequestParameters.REGISTER_USERNAME);
	String pwd1=request.getParameter(Const.RequestParameters.REGISTER_PASSWORD_1);
	String pwd2=request.getParameter(Const.RequestParameters.REGISTER_PASSWORD_2);
	if(pwd2==null||!pwd2.equals(pwd1)){
		if(user==null){
			user="";
		}
		session.setAttribute(Const.SessionAttributs.RegistrationUsername.NAME,user);
		session.setAttribute(Const.SessionAttributs.RegistrationState.NAME,""+Const.SessionAttributs.RegistrationState.PASSWORD_MISSMATCH);
		request.getRequestDispatcher("reg.jsp").forward(request,response);	
	}else{
		int ret=User.add(user,pwd1);
		switch(ret){
		case Const.UserReturns.USERNAME_EXISTS:
			session.setAttribute(Const.SessionAttributs.RegistrationUsername.NAME,user);
		case Const.UserReturns.FAILED:
		case Const.UserReturns.PASSWORD_INVALID:
		case Const.UserReturns.PASSWORD_WRONG:
		case Const.UserReturns.USERNAME_INVALID:
		case Const.UserReturns.SUCCESS:
			session.setAttribute(Const.SessionAttributs.RegistrationState.NAME,""+ret);
			System.out.println("set session to "+ret);
			request.getRequestDispatcher("reg.jsp").forward(request,response);
			break;
		}	
	}
%>

</body>
</html>