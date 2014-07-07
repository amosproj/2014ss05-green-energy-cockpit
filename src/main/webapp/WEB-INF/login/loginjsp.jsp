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

//call User.exists with Username and password to check if user and password match
int ret=User.exists(request.getParameter(Const.RequestParameters.LOGIN_USERNAME),request.getParameter(Const.RequestParameters.LOGIN_PASSWORD));
switch(ret){
case Const.UserReturns.SUCCESS:
	//credentials valid -> reditrect to internal main-page
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.LOGGED_IN);
	session.setAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME,request.getParameter(Const.RequestParameters.LOGIN_USERNAME));

	response.sendRedirect(request.getContextPath()+Const.URL.INTERN_HOME);
	break;
case Const.UserReturns.USERNAME_INVALID:
case Const.UserReturns.USERNAME_UNKNOWN:
	//username unknown -> redirect to loginpage
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.USERNAME_UNKNOWN);
	response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	break;
case Const.UserReturns.PASSWORD_INVALID:
case Const.UserReturns.PASSWORD_WRONG:
	//password wrong -> redirect to loginpage
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.PASSWORD_WRONG);
	response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	break;
case Const.UserReturns.FAILED:
	//User.java can't check user name -> internal error -> redirect to loginpage
	session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.FAILURE);
	response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	break;


}
%>

</body>
</html>