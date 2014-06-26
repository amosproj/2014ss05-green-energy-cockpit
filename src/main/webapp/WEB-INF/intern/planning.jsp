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
	<% PlanningPreset.setValues(request, session); %>
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
				<form id="selectionPlants" method="post" action="">
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
						<% 
							out.println(PlanningPreset.getPercentageChange());	
						%>
									
					</div>	
					<div id="labelFrom" style="display: inline"> Precision:</div>
						<% 
						int precision = 1;
							try {
								precision = Integer.parseInt(request
										.getParameter("selectedPrecision"));
							} catch (NumberFormatException e) {
								precision = 1;
							} 
						%>
						<select name="selectedPrecision"  style="display: inline">
							<%
								for (int i = 1; i <= 3; i++) {
									%>
									<option value="<%=i%>"
										<%out.print(((precision == i) ? " selected" : "") + "");%>><%=i%></option>
									<%
								}
							%>
						</select>	
						</div>	
					
					
					
							
					<div style="width: 200px; height: 150px; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px ;">
						<div id="groupSelection">
							<%out.println(PlanningPreset.LocationSelection(request));%>
						</div>
					</div>
					<div style="width: 600px; height: 75px; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px;">
						<div id="groupSelection">
						Saved Planning Data: <br>
							<% out.println(PlanningPreset.getSavedPlannings()); %>
							<input type="submit" value="Load"><input type="submit" name = "Delete" value="Delete"> 	 	
						</div>
					</div>
					<input type="submit" value="Show"> 		
					<input type="submit" name = "reset" value="Reset"> 		
				
				<input type="submit" name ="saveInput" value="Save Planning Data">	
			</div>

			<div style="width: 850px; height: 380px; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px;">
			
			<%						
			out.println(PlanningPreset.getTable());				
			%>
			</form>

			</div>
			





		</div>
					
	</div>



</body>
</html>