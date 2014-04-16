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

	<div id="gesInhalt">

		<%
			String state = (String) session.getAttribute(Const.SessionAttributs.LoginState.NAME);
			if (state == null) { %>
				<div id="Begruessung">
					Welcome to the Log-in page of <i>Green Energy Cockpit</i>.
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.PASSWORD_WRONG)) { %>
				<div id="Fehlermeldung">
					Password wrong! Please try again.		
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.NOT_LOGGED_IN)) { %>
				<div id="Fehlermeldung">
					Access denied! 			
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.USERNAME_UNKNOWN)) { %>
				<div id="Fehlermeldung">
					Username unknown! Register or retry.
				</div>
		<%	} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.LOGGED_IN)) { 
				response.sendRedirect(request.getContextPath()+ Const.URL.INTERN_HOME);
			} else if (state.equals(Const.SessionAttributs.LoginState.Valeus.FAILURE)) { %>
				<div id="Fehlermeldung">
					Systemfailure! Contact Systemadministrator or try again.
				</div>
		<%	} %>


		

		<div id="Anmeldung">
			Please login to proceed.
			<table>
				<tr>
					<form method="post" action="login/login">
					<td height="84">
                      	<table>
							<tr>
								<td>Name:</td>
								<td><input type="text"
									name="<%= Const.RequestParameters.LOGIN_USERNAME %>"></td>
							</tr>
							<tr>
								<td>Password:</td>
								<td><input type="password"
									name="<%= Const.RequestParameters.LOGIN_PASSWORD %>"></td>
							</tr>
						</table>
					</td>
					<td>
						<input type="hidden" name="<%=Const.RequestParameters.COMING_FROM_LOGINPAGE%>" value="true" />
						<input id="LoginButton" type="submit" value="Login" ></td>
					</form>
				</tr>

				<tr>
					<td><a href="login/reg"">Not yet registered?</a></td>
				</tr>
			</table>
		</div>
	</div>
</body>
</html>