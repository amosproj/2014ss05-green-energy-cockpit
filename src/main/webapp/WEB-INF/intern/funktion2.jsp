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
 <%@ page import="java.util.ArrayList"%>

 <% User.init(getServletConfig()); %>

<!DOCTYPE html>
<html>

<head>
<title>AMOS-Cockpit</title>
<link rel="stylesheet" href="../styles/style.css" type="text/css" />
</head>

<body>

	<header>
		<h1>AMOS PROJECT</h1>
 		<h2>Green Energy Cockpit</h2>
	</header>
	<div id="loginStateBox">
	Logged in as "<%out.print(session.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME));%>"
	
	</div>
	    <nav id="menue">
        <ul>
    		<%out.println(Header.getHeaderButtons()); %>
        </ul>
    </nav>   

	<div id="completeContentBox">
		<div id="content">
			
			<img src="../ChartSven?param1=value1&param2=value2" />
					
		</div>
	</div>
</body>
</html>