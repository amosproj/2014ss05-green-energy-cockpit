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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>



<%

// this file is a nondisplayable webpage
// it is the main handler for this project. Every internal webpage can only be accessed
// through this handler. It verifies if the user is logged in and gives acces to the page or doesn't


	System.out.println("[intern.jsp]: called intern ["+request.getRequestURL()+"]");

	//User is stored in the session

	//compare value for "being logged in" with the value stored in the current session
	if(Const.SessionAttributs.LoginState.Valeus.LOGGED_IN.equals(session.getAttribute(Const.SessionAttributs.LoginState.NAME))){
		//session valid -> give access -> forward
		request.getRequestDispatcher("/WEB-INF/intern"+request.getPathInfo()).forward(request,response);
	}else{
		//session invalid -> set session explicit to "not being logged in" -> redirect to loginPage
		session.setAttribute(Const.SessionAttributs.LoginState.NAME,Const.SessionAttributs.LoginState.Valeus.NOT_LOGGED_IN);
		response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	}
%>
</body>
</html>