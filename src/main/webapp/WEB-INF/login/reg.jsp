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

	<div id="gesInhalt">

	<% 
		System.out.println("regjsp");
		
		session.removeAttribute(Const.SessionAttributs.LoginState.NAME);
		
		String username="";
		String regState=(String)session.getAttribute(Const.SessionAttributs.RegistrationState.NAME);
		System.out.println("got regstate "+regState);
		if(regState==null){ %>
			<div id="Begruessung">
				In order to get access to this site you need to be registred.
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.FAILED)){ %>
			<div id="Fehlermeldung">
				Systemfailure! Contact Systemadministrator or try again		
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.USERNAME_EXISTS)){ %>
			<div id="Fehlermeldung">
				Username allready in use!		
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.USERNAME_INVALID)){ %>
			<div id="Fehlermeldung">
				Username invalid! You may only use letters and numbers [a-z,A-Z,0-9]		
			</div>
	<%	}else if(regState.equals(""+Const.SessionAttributs.RegistrationState.PASSWORD_MISSMATCH)){ 
			username=(String)session.getAttribute(Const.SessionAttributs.RegistrationUsername.NAME); %>
			<div id="Fehlermeldung">
				Passwords don't match!		
			</div>
	<%	}else if(regState.equals(""+Const.UserReturns.SUCCESS)){ %>
			<div id="Fehlermeldung">
				Account successfully created! You can now go back and login		
			</div>
	<%	}else{ %>
			<div id="Begruessung">
				In order to get access to this site you need to be registred.
			</div>
	<%	}
		session.removeAttribute(Const.SessionAttributs.RegistrationUsername.NAME); %>

		<div id="Anmeldung">
			Create a new Account.
			<table>
				<tr>
					<form method="post" action="regJsp">
					<td>
						<table>
							<tr>
								<td>Name:</td>
								<td><input type="text"
									name="<%= Const.RequestParameters.REGISTER_USERNAME %>"></td>
							</tr>
							<tr>
								<td>Password:</td>
								<td><input type="password"
									name="<%= Const.RequestParameters.REGISTER_PASSWORD_1 %>"></td>
							</tr>
							<tr>
								<td>Confirm password:</td>
								<td><input type="password"
									name="<%= Const.RequestParameters.REGISTER_PASSWORD_2 %>"></td>
							</tr>
						</table>
					</td>
					<td>
						<input id="RegisterButton" type="submit" value="Register">
					</td>
					</form>
				</tr>

				<tr>
					<td></td>
					<td>
						<form method="post" action="../">
							<input id="BackButton" type="submit" value="Back">
						</form>
					</td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>