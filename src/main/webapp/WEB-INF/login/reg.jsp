<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="de.fau.amos.*"%>
<!DOCTYPE html>
<html>
<%System.out.println("called regpage"); %>
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
		System.out.println("regjsp");
		
		session.removeAttribute(Const.SessionAttributs.LoginState.NAME);
		
		String username="";
		String regState=(String)session.getAttribute(Const.SessionAttributs.RegistrationState.NAME);
		System.out.println("got regstate "+regState);
		if(regState==null){ %>
			<div id="welcomeMsgBox">
				In order to get access to this site you need to be registred.
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.FAILED)){ %>
			<div id="errorMsgBox">
				Systemfailure! Contact Systemadministrator or try again		
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.USERNAME_EXISTS)){ %>
			<div id="errorMsgBox">
				Username already in use!		
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.USERNAME_INVALID)){ %>
			<div id="errorMsgBox">
				Username invalid! You may only use letters and numbers [a-z,A-Z,0-9]		
			</div>
	<%	}else if(regState.equals(""+Const.SessionAttributs.RegistrationState.PASSWORD_MISSMATCH)){ 
			username=(String)session.getAttribute(Const.SessionAttributs.RegistrationUsername.NAME); %>
			<div id="errorMsgBox">
				Passwords don't match!		
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.SUCCESS)){ %>
			<div id="welcomeMsgBox">
				Account successfully created! You can now go back and login		
			</div>
	<%	}else{ %>
			<div id="welcomeMsgBox">
				In order to get access to this site you need to be registred.
			</div>
	<%	}
		session.removeAttribute(Const.SessionAttributs.RegistrationUsername.NAME); %>

		<div id="regBox">
			Create a new Account. <br>
			<form method="post" action="regJsp">
				<fieldset>
					<label for "regName">Name: </label>
					<input type="text" name="<%= Const.RequestParameters.REGISTER_USERNAME %>" id="regName"><br>
					<label for "regPassword">Password: </label>
					<input type="password" name="<%= Const.RequestParameters.REGISTER_PASSWORD_1 %>" id="regPassword"><br>
					<label for "regPassword2">Confirm Password: </label>
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