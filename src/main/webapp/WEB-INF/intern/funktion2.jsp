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
			
			<%
			int selected=0;
			try{
				selected=Integer.parseInt(request.getParameter("selChart"));
			}catch(NumberFormatException e){
				selected=0;
			}
			%>
			
			<form method="post" action="">
				<select name="selChart" onChange="this.form.submit()">
					<option value="0"<% out.print(((selected==0)?" selected":"")+""); %>></option>
					<option value="1"<% out.print(((selected==1)?" selected":"")+""); %>>Area Chart</option>
					<option value="2"<% out.print(((selected==2)?" selected":"")+""); %>>Bar Chart</option>
					<option value="3"<% out.print(((selected==3)?" selected":"")+""); %>>Bar Chart 3D</option>
					<option value="4"<% out.print(((selected==4)?" selected":"")+""); %>>Line Chart</option>
					<option value="5"<% out.print(((selected==5)?" selected":"")+""); %>>Pie Chart</option>
					<option value="6"<% out.print(((selected==6)?" selected":"")+""); %>>Graph Chart</option>
				</select>
				<!-- input type="submit" value="Show chart"-->
				More charts are comming soon!
			</form>
			
			
			
			
			<%if(selected!=0){ %>
			<img src="../ChartSven?param1=<%=selected %>&param2=value2" />
			<%}else{ %>
			Selected a chart type.
			<%} %>
					
		</div>
	</div>
</body>
</html>