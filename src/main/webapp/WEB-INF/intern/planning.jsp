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
<%@ page import="java.util.*"%>
<%@ page import="java.text.DateFormatSymbols" %>

<%
	User.init(getServletConfig());
%>

<!DOCTYPE html>
<html>




<head>
<title>AMOS-Cockpit</title>
<link rel="stylesheet" href="../styles/style.css" type="text/css" />
<link rel="stylesheet" href="../styles/dtree.css" type="text/css" />
<script src="jquery-2.0.0.min.js"></script>


</head>

<body>

	<header>
		<h1>AMOS PROJECT</h1>
		<h2>Green Energy Cockpit</h2>
	</header>
	<div id="loginStateBox">
		Logged in as "<%
		out.print(session
				.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME));
	%>"
	</div>
	<nav id="menue">
		<ul>
			<%
				out.println(Header.getHeaderButtons());
			%>
		</ul>
	</nav>

	<div id="completeContentBox">
		<div id="content">

			<%				
				int selectedYear=0;
				try{
					selectedYear=Integer.parseInt(request.getParameter("selectYear"));
				}catch(NumberFormatException e){
					selectedYear=0;
				}
			%>

			<div id="timeChart">
				<form id="selectionForm" method="post" action="">
					<div id="timeSelectionBar">
						<div id="labelFrom" style="display: inline">Select a year:</div>
						<select name="selectYear" id="selectYear" style="display: inline">
							<%
								for (int i = 2012; i <= 2014; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((selectedYear == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select>	
						<div id="labelFrom" style="display: inline"> Percentage change:</div>
						<input type="text" id="percentageChange" style="text-align: right">%				
					</div>
					

					<div style="width: 200px; height: 200px; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px;">
						<div id="groupSelection">
							<%out.println(ChartPreset.createLocationSelection(request));%>
						</div>
					</div>
					
					<input type="submit" id="showTable" value="Show"> 
					<input type="submit" id="downloadTable" value="Download table">	
					
				</form>
			</div>

			<div style="width: 850px; height: 380px; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px;">
			
			<%
			
			String[] months = new DateFormatSymbols(Locale.ENGLISH).getMonths();
			
			out.println("<table border cellpadding=\"7\" rules=\"all\" id= \"dataTable\" >");
			
			out.println("<tr>");
			out.println("<td style=\"word-break:break-all;word-wrap:break-word\" >Measure point</td>");
			for(int i=0;i<months.length-1;i++){
				out.println("<td style=\"word-break:break-all;word-wrap:break-word\" >");
				out.println(months[i]);
				out.println("</td>");
			}
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td style=\"word-break:break-all;word-wrap:break-word\" >Erlangen</td>");
			for(int i=0;i<12;i++){
				out.println("<td style=\"word-break:break-all;word-wrap:break-word\" >");
				out.println(String.valueOf(Math.round(Math.random()*10+100)) + "kWh <br>");
				out.println("(" + String.valueOf(Math.round(Math.random()*10+100)) + "kWh)<br>");
				out.print("<input type=\"text\" size=\"1\" maxlength=\"3\">");
				out.println("</td>");
			}
			out.println("</tr>");
			
			out.println("<tr>");
			out.println("<td style=\"word-break:break-all;word-wrap:break-word\" >Dresden</td>");
			for(int i=0;i<12;i++){
				out.println("<td style=\"word-break:break-all;word-wrap:break-word\" >");
				out.println(String.valueOf(Math.round(Math.random()*10+100)) + "kWh <br>");
				out.println("(" + String.valueOf(Math.round(Math.random()*10+100)) + "kWh)<br>");
				out.print("<input type=\"text\" size=\"1\" maxlength=\"3\">");
				out.println("</td>");
			}
			out.println("</tr>");
			
			out.println("</table>");
				
			%>


			</div>
			





		</div>
					
	</div>



</body>
</html>