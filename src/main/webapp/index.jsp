<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="de.fau.amos.*"%>
<!DOCTYPE html>
<html>
<%System.out.println("called indexpage"); %>
<head>
<title>AMOS project 5</title>
<link rel="stylesheet" type="text/css" href="styles/style.css">
</head>

<body>

	<header>
		<h1>AMOS PROJECT</h1>
		<h2>Green Energy Cockpit</h2>
	</header>

	<div id="completeContentBox">

		<%
			String state = (String) session.getAttribute(Const.SessionAttributs.LoginState.NAME);
			if (state == null) { %>
				<div id="welcomeMsgBox">
					Welcome to the Log-in page of <i>Green Energy Cockpit</i>.
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.PASSWORD_WRONG)) { %>
				<div id="errorMsgBox">
					Password wrong! Please try again.		
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.NOT_LOGGED_IN)) { %>
				<div id="errorMsgBox">
					Access denied! 			
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.USERNAME_UNKNOWN)) { %>
				<div id="errorMsgBox">
					Username unknown! Register or retry.
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.LOGGED_IN)) { 
				response.sendRedirect(request.getContextPath()+ Const.URL.INTERN_HOME);
			} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.FAILURE)) { %>
				<div id="errorMsgBox">
					Systemfailure! Contact Systemadministrator or try again.
				</div>
		<%	} %>

		<div id="loginBox">
			Please login to proceed.

			<form method="post" action="login/login">
				<fieldset title="loginInformation">
					<label for "name">Name: </label>
					<input type="text" name="<%=Const.RequestParameters.LOGIN_USERNAME%>" id="name"><br>
					<label for "password">Password: </label>
					<input type="password" name="<%=Const.RequestParameters.LOGIN_PASSWORD%>" id="password">
				</fieldset>
				<fieldset>
					<input type="hidden"
						name="<%=Const.RequestParameters.COMING_FROM_LOGINPAGE%>"
						value="true" /> <input id="LoginButton" type="submit"
						value="Login">
				</fieldset>
			</form>
			<a href="login/reg"">Not registered yet?</a>
		</div>



	</div>
</body>
</html>