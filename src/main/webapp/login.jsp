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
<%@ page import="de.fau.amos.Const"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<%

	//this page is a nondisplayable webpage.
	//it is a handler for login

	System.out.println("[login.jsp] called loginHandler ["+request.getPathInfo()+"]");

	String pathInfo=request.getPathInfo();
	if(pathInfo==null){
		//no valid information -> redirect to main-page
		response.sendRedirect(request.getContextPath());
	}else if(pathInfo.equals("/reg")){		
		//regstration handler
		request.getRequestDispatcher(Const.URL.REGISTRTION_PAGE).forward(request,response);		
	}else if(pathInfo.equals("/regJsp")){		
		//registration frontend
		request.getRequestDispatcher(Const.URL.REGISTRATION_JSP).forward(request,response);
	}else if(pathInfo.equals("/change")){
		//change password hangler
		//not implemented yet
	}else if(pathInfo.equals("/changeJsp")){		
		//change password frontend
		//not implemented yet
	}else if(pathInfo.equals("/logout")){
		//logout -> set session to "not being logged in" -> remove username from session -> redirect to main-page
		session.removeAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME);
		session.removeAttribute(Const.SessionAttributs.LoginState.NAME);
		response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	}else if(pathInfo.equals("/login")){
		//user wants to login
		
		//is a user logged in to this session?
		if((Const.SessionAttributs.LoginState.Valeus.LOGGED_IN.equals(session.getAttribute(Const.SessionAttributs.LoginState.NAME)))
			&&(request.getParameter(Const.RequestParameters.LOGIN_USERNAME).equals(session.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME)))){
			//user is allready logged in -> can forward to internal main-page
			response.sendRedirect(request.getContextPath()+Const.URL.INTERN_HOME);
		}else{
			//no user logged in
			if(request.getParameter(Const.RequestParameters.COMING_FROM_LOGINPAGE)!=null){
				//coming from loginPage -> forward to login.jsp
				request.getRequestDispatcher(Const.URL.LOGIN_JSP).forward(request,response);
			}else{
				//don't know from where the user called this page
				
				//forward to loginPage
				session.removeAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME);
				session.removeAttribute(Const.SessionAttributs.LoginState.NAME);
				request.getRequestDispatcher(request.getContextPath()+Const.URL.LOGIN_PAGE).forward(request,response);
			}
		}
	}
%>
</body>
</html>