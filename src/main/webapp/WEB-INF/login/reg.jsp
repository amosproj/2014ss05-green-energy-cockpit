<!-- 
 - Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob H?bler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
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
<!DOCTYPE html>
<html>
<%User.init(getServletConfig()); %>
<head>
<title>AMOS project 5 register page</title>
<link rel="stylesheet" type="text/css" href="../styles/style.css">
</head>

<body>

	<header>
		<h1>AMOS PROJECT</h1>
		<h2>Green Energy Cockpit</h2>
	</header>

	<div id="completeContentBox">

	<% 
		
		session.removeAttribute(Const.SessionAttributs.LoginState.NAME);
		
		String username="";
		
		//registration is a multistepprogress -> need a variable to store current state
		String regState=(String)session.getAttribute(Const.SessionAttributs.RegistrationState.NAME);

		if(regState==null){ 
			//registration has not started -> introduce user
		%>
			<div id="welcomeMsgBox">
				In order to get access to this site you need to be registred.
			</div>
		<%	
		}else if(regState.equals(""+Const.UserReturns.FAILED)){ 
			//registration failed -> inform user
		%>
			<div id="errorMsgBox">
				Systemfailure! Contact Systemadministrator or try again		
			</div>
		<%	
		}else if(regState.equals(""+Const.UserReturns.USERNAME_EXISTS)){ 
			//username allready in use, can't register user with this name
		%>
			<div id="errorMsgBox">
				Username already in use!		
			</div>
		<%	
		}else if(regState.equals(""+Const.UserReturns.USERNAME_INVALID)){ 
			//username contains invalid characters
		%>
			<div id="errorMsgBox">
				Username invalid! You may only use letters and numbers [a-z,A-Z,0-9]		
			</div>
		<%	
		}else if(regState.equals(""+Const.SessionAttributs.RegistrationState.PASSWORD_MISSMATCH)){ 
			username=(String)session.getAttribute(Const.SessionAttributs.RegistrationUsername.NAME); 
			//passwordfields contain different passwords
		%>
			<div id="errorMsgBox">
				Passwords don't match!		
			</div>
		<%
		}else if(regState.equals(""+Const.UserReturns.SUCCESS)){ 
			//registration complete -> inform user
		%>
			<div id="welcomeMsgBox">
				Account successfully created! You can now go back and login		
			</div>
		<%	
		}else{ 
			//unknown step -> asume no steps were taken yet -> start with registrationinformation
		%>
			<div id="welcomeMsgBox">
				In order to get access to this site you need to be registred.
			</div>
		<%	
		}
		session.removeAttribute(Const.SessionAttributs.RegistrationUsername.NAME); 
		%>

		<div id="regBox">
			Create a new Account. <br>
			<form method="post" action="regJsp">
				<fieldset>
					<label for="<%= Const.RequestParameters.REGISTER_USERNAME %>">Name: </label>
					<input type="text" name="<%= Const.RequestParameters.REGISTER_USERNAME %>" id="regName"><br>
					<label for="<%= Const.RequestParameters.REGISTER_PASSWORD_1 %>">Password: </label>
					<input type="password" name="<%= Const.RequestParameters.REGISTER_PASSWORD_1 %>" id="regPassword"><br>
					<label for="<%= Const.RequestParameters.REGISTER_PASSWORD_2 %>">Confirm Password: </label>
					<input type="password" name="<%= Const.RequestParameters.REGISTER_PASSWORD_2 %>" id="regPassword2">
				</fieldset>
				<fieldset id= "RegisterButtonField">
					<input id="RegisterButton" type="submit" value="Register">					
				</fieldset>
			</form>
			<form method="post" action="../">
				<fieldset id= "BackButtonField">
					<input id="BackButton" type="submit" value="Back">
				</fieldset>
			</form>
		</div>
	</div>
</body>
</html>