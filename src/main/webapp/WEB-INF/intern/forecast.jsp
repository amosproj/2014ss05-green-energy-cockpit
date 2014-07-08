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
<%@ page import="java.util.Calendar"%>

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

	<%
	int plant=0;
	try{
		plant=Integer.parseInt(request.getParameter("selectedPlant"));
	}catch(NumberFormatException e){
		plant=0;
	}

	int year=0;
	try{
		year=Integer.parseInt(request.getParameter("selectedYear"));
	}catch(NumberFormatException e){
		year=Calendar.getInstance().get(Calendar.YEAR);
	}

	int method=0;
	try{
		method=Integer.parseInt(request.getParameter("selectedMethod"));
	}catch(NumberFormatException e){
		method=0;
	}
	
	int influencingUnits=0;
	try{
		influencingUnits=Integer.parseInt(request.getParameter("influencingUnits"));
	}catch(NumberFormatException e){
		influencingUnits=0;
	}
	%>

	<div id="completeContentBox">
		<div id="content">
		
			<div id="forecastSelection">
				<form id="selectionForm" method="post" action="">
					Factory: 
					<select id="selectedPlant" name="selectedPlant" onChange="this.form.submit()">
						<option value="0" <%out.print(((plant == 0) ? " selected" : "") + "");%>></option>
						<%
						//query plant names with id for display
						ArrayList<ArrayList<String>>plants=SQL.query("select plant_name,plants_id from plants order by plant_name");
						if(plants!=null){
							
							//scipt line 0 -> header line
							for(int i=1;i<plants.size();i++){
								try{
									out.println("<option value=\""+plants.get(i).get(1)+"\" " 
											+((plant == Integer.parseInt(plants.get(i).get(1))) ? " selected" : "")
											+">"+plants.get(i).get(0)+"</option>");
								}catch(NumberFormatException e){}
							}
						}
						%>
					</select>
					
					Year:
					<select id="selectedYear" name="selectedYear" onChange="this.form.submit()">
						<%
						Calendar c=Calendar.getInstance();
						for(int i=0;i<4;i++){
							out.println("<option value=\""+c.get(Calendar.YEAR)+"\" " 
									+((year == c.get(Calendar.YEAR)) ? " selected" : "")
									+">"+c.get(Calendar.YEAR)+"</option>");
							c.add(Calendar.YEAR,1);
						}
						%>
					</select>
					
					<!-- 
					Depending on:
					<select id="selectedMethod" name="selectedMethod" onChange="this.form.submit()">
						<option value="0" <%out.print(((method == 0) ? " selected" : "") + "");%>>previous x months</option>
						<option value="1" <%out.print(((method == 1) ? " selected" : "") + "");%>>previous same months from last x years</option>
						<option value="2" <%out.print(((method == 2) ? " selected" : "") + "");%>>previous x years</option>
					</select>
					
					Amount:
					<select id="influencingUnits" name="influencingUnits" onChange="this.form.submit()">
						<%
						for(int i=1;i<12;i++){
						%>
						<option value="<%=i %>" <%out.print(((influencingUnits == i) ? " selected" : "") + "");%>><%=i %></option>
						<%
						}
						%>
					</select>
				  -->
					
				</form>
			</div>
            
            <div id="forecastChart">
            	<%
            	int width=900-30;
            	int height=600;
            	%>
            	<img src="../ChartRenderer?w=<%=width%>&h=<%=height%>&selectedChartType=forecast&forecastPlant=<%=plant%>&forecastYear=<%=year%>&forecastMethod=<%=method%>&forecastUnits=<%=influencingUnits%>" /> 
			
            </div>
		</div>
	</div>
</body>
</html>