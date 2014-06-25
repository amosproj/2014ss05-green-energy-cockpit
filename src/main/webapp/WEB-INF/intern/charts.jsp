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

<%
	User.init(getServletConfig());

System.out.println("reloaded "+request.getParameter("selectedChartType"));
%>

<!DOCTYPE html>
<html>




<head>
<title>AMOS-Cockpit</title>
<link rel="stylesheet" href="../styles/style.css" type="text/css" />
<link rel="stylesheet" href="../styles/dtree.css" type="text/css" />
<script src="jquery-2.0.0.min.js"></script>

</head>

<body onload="changeTimeGranularity();">


	<script>
		function changeChartType() {
			alert("changeChartType");
			var e = document.getElementById("selectChartType");
			var val = e.options[e.selectedIndex].value;
			//document.getElementById("selectedChartType").value = val;
			
			if (val == '1') {
				//show time chart and call init functions
				$("#timeChart").show();
				changeTimeGranularity();
				//hide other charts
				$("#locationFormatChart").hide();
			} else if (val == '2') {
				//show locationFormat chart and call init functions
				$("#locationFormatChart").show();
				changeTimeGranularity();
				//TODO initFunctions
				//hide other charts
				$("#timeChart").hide();
			} else {
				//hide all charts
				$("#timeChart").hide();
				$("#locationFormatChart").hide();
			}
		}

		function changeTimeGranularity() {
			var e = document.getElementById("timeGranularity");
			var val = e.options[e.selectedIndex].value;

			if (val <= '0') {
				$("#selectDay").show();
			} else {
				$("#selectDay").hide();
			}

			if (val <= '1') {
				$("#selectMonth").show();
			} else {
				$("#selectMonth").hide();
			}

			//if(val<='2'){
			$("#selectYear").show();
			//}else{
			//$("#selectYear").hide();
			//}

			if (val == '3') {
				$("#selectEndYear").show();
				$("#labelAt").hide();
				$("#labelFrom").show();
				$("#labelTo").show();
			} else {
				$("#selectEndYear").hide();
				$("#labelAt").show();
				$("#labelFrom").hide();
				$("#labelTo").hide();
			}
		}

		function changeSelectDays() {

			var month = document.getElementById("selectMonth").value;
			var year = document.getElementById("selectYear").value;
			var endYear = document.getElementById("selectEndYear").value;

			var days;

			switch (month) {
			case "1":
			case "3":
			case "5":
			case "7":
			case "8":
			case "10":
			case "12":
				days = 31;
				break;
			case "2":
				if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
					days = 29;
				} else {
					days = 28;
				}
				break;
			default:
				days = 30;
			}

			document.getElementById("selectDay").options.length = days;

			for (var i = 1; i <= days; i++) {
				document.getElementById("selectDay").options[i - 1].innerHTML = i;
				document.getElementById("selectDay").options[i - 1].value = i;
			}

			if (endYear < year) {
				document.getElementById("selectEndYear").selectedIndex = document
						.getElementById("selectYear").selectedIndex;
			}
		}

		function changeSelectEndYear() {
			var year = document.getElementById("selectYear").value;
			var endYear = document.getElementById("selectEndYear").value;

			if (endYear < year) {
				document.getElementById("selectYear").selectedIndex = document
						.getElementById("selectEndYear").selectedIndex;
			}
			changeSelectDays();
		}
	</script>



	<header>
		<h1>AMOS PROJECT</h1>
		<h2>Green Energy Cockpit</h2>
	</header>

	<div id="loginStateBox">
		Logged in as "<%out.print(session.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME));	%>"
	</div>

	<nav id="menue">
		<ul><%out.println(Header.getHeaderButtons());%></ul>
	</nav>

	<div id="completeContentBox">
		<div id="content">

			<%
				int chartType = 0;
				try {
					chartType=Integer.parseInt(request.getParameter("selectChartType"));					
				} catch (NumberFormatException e) {
					chartType = 0;
				}

				int timeGranularity = 0;
				try {
					timeGranularity = Integer.parseInt(request
							.getParameter("timeGranularity"));
				} catch (NumberFormatException e) {
					timeGranularity = 2;
				}

				int selectedDay = 0;
				try {
					selectedDay = Integer.parseInt(request
							.getParameter("selectDay"));
				} catch (NumberFormatException e) {
					selectedDay = 0;
				}

				int selectedMonth = 0;
				try {
					selectedMonth = Integer.parseInt(request
							.getParameter("selectMonth"));
				} catch (NumberFormatException e) {
					selectedMonth = 0;
				}

				int selectedYear = 0;
				try {
					selectedYear = Integer.parseInt(request
							.getParameter("selectYear"));
				} catch (NumberFormatException e) {
					//selectedYear = Calendar.getInstance().get(Calendar.YEAR);
					selectedYear = 2012;
				}

				int selectedEndYear = 0;
				try {
					selectedEndYear = Integer.parseInt(request
							.getParameter("selectEndYear"));
				} catch (NumberFormatException e) {
					selectedEndYear = 0;
				}

				int countType = 0;
				try {
					countType = Integer.parseInt(request.getParameter("countType"));
				} catch (NumberFormatException e) {
					countType = 1;
				}

				int numberOfLocationGroups = 1;
				try {
					numberOfLocationGroups = Integer.parseInt(request
							.getParameter("numberOfLocationGroups"));
				} catch (NumberFormatException e) {
				}

				int numberOfFormatGroups = 1;
				try {
					numberOfFormatGroups = Integer.parseInt(request
							.getParameter("numberOfFormatGroups"));
				} catch (NumberFormatException e) {
				}

				int unit = 1;
				try {
					unit = Integer.parseInt(request
							.getParameter("selectUnit"));
				} catch (NumberFormatException e) {
				}
				
				String sessionID=request.getParameter("sessionID");
				if(sessionID==null||sessionID.trim().length()==0){
					do{
						sessionID="";
						for(int i=0;i<6;i++){
							sessionID+=(int)(Math.random()*10);
						}
					}while(session.getAttribute(sessionID)!=null);
				}
				
				String startTime="nix";
				
				//Create Timestamp for SQL-Query (Start- and End-Date)
				String start = TimestampConversion.convertTimestamp(0, 0,(timeGranularity == 0 ? selectedDay : 1),
						(timeGranularity == 0 || timeGranularity == 1 ? selectedMonth : 1), selectedYear);
				String end = "";
				Calendar c = Calendar.getInstance();
				c.set(selectedYear, selectedMonth - 1, selectedDay);
				switch (timeGranularity) {
					case 0 :
						//show one day
						c.add(Calendar.DATE, 1);
						end = TimestampConversion.convertTimestamp(0, 0,c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1,c.get(Calendar.YEAR));
						break;
					case 1 :
						//show one month
						c.add(Calendar.MONTH, 1);
						end = TimestampConversion.convertTimestamp(0, 0, c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
						break;
					case 2 :
						//show one year
						c.add(Calendar.YEAR, 1);
						end = TimestampConversion.convertTimestamp(0, 0, c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
						break;
					case 3 :
						//show more years
						//no break, use default clause

					default :
						c.set(selectedEndYear + 1, 0, 1);
						end = TimestampConversion.convertTimestamp(0, 0, c.get(Calendar.DATE), c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR));
				}

				
				String endTime=end;
				startTime=start;
			%>

			
			<div id="chartTypeSelection">
				<form id="chartTypeForm" method="post" action="">
					Chart type: 
					<select id="selectChartType" name="selectChartType" onchange="this.form.submit();">
						<option value="0" <%out.print(((chartType == 0) ? " selected" : "") + "");%>></option>
						<option value="1" <%out.print(((chartType == 1) ? " selected" : "") + "");%>>Time</option>
						<option value="2" <%out.print(((chartType == 2) ? " selected" : "") + "");%>>Location - Format</option>
						<option value="3" <%out.print(((chartType == 3) ? " selected" : "") + "");%>>Format - Location</option>
					</select>
				</form>
			</div>

			<div id="chartParameter" <%out.print(chartType == 0 ? "style=\"display: none;\"" : "");%>>

				<form id="selectionForm" method="post" action="">
					<input type="hidden" name="selectChartType" value="<%=chartType%>">
					<input type="hidden" name="sessionID" value="<%=sessionID %>">
					<input type="hidden" id="startTime" name="startTime" value="not set">
					<input type="hidden" id="endTime" name="endTime" value="not set">
					<input type="hidden" id="double" name ="double" value="false">
													
					<div id="timeSelectionBar">

						<div class="timeGranularityDiv" style="display: inline">
							Show me the 
							<select name="timeGranularity" id="timeGranularity" onchange="changeTimeGranularity();">
								<%if(unit==1){ %><option value="0" <%out.print(((timeGranularity == 0) ? " selected" : "") + "");%>>day</option><%} %>
								<option value="1" <%out.print(((timeGranularity == 1) ? " selected" : "") + "");%>>month</option>
								<option value="2" <%out.print(((timeGranularity == 2) ? " selected" : "") + "");%>>year</option>
								<option value="3" <%out.print(((timeGranularity == 3) ? " selected" : "") + "");%>>years</option>
							</select>
						</div>

						<div id="labelFrom" style="display: inline">from</div>
						<select name="selectDay" id="selectDay" style="display: inline">
							<%
								for (int i = 1; i <= 31; i++) {
							%>
							<option value="<%=i%>"<%out.print(((selectedDay == i) ? " selected" : "") + "");%>><%=i%>.</option>
							<%
								}
							%>
						</select>
						<select name="selectMonth" id="selectMonth" onchange="changeSelectDays();" style="display: inline">
							<%
								for (int i = 1; i <= 12; i++) {
							%>
							<option value="<%=i%>"<%out.print(((selectedMonth == i) ? " selected" : "") + "");%>><%=i%>.</option>
							<%
								}
							%>
						</select>
						<select name="selectYear" id="selectYear" onchange="changeSelectDays();" style="display: inline">
							<%
								for (int i = 2004; i <= 2020; i++) {
							%>
							<option value="<%=i%>"<%out.print(((selectedYear == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select>
						<div id="labelTo" style="display: inline">to</div>
						<select name="selectEndYear" id="selectEndYear" onchange="changeSelectEndYear();" style="display: inline">
							<%
								for (int i = 2004; i <= 2020; i++) {
							%>
							<option value="<%=i%>"<%out.print(((selectedEndYear == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select>
						
						<div id="labelUnit" style="display: inline">in units of</div>
						<select name="selectUnit" id="selectUnit" style="display: inline" onChange="this.form.submit()">
							<option value="1"<%out.print(((unit==1)?" selected":"")+"");%>>kWh</option>
							<option value="2"<%out.print(((unit==2)?" selected":"")+"");%>>kWh/TNF</option>
							<%if(chartType!=1){ %><option value="3"<%out.print(((unit==3)?" selected":"")+"");%>>TNF</option><%} %>
						</select>
					</div>

					<input type="button" id="showChart" class="buttonDesign" value="Show" onclick="javascript:setValues();this.form.submit()">
					
					<input type="hidden" name="numberOfLocationGroups" id="numberOfLocationGroups" value="<%=numberOfLocationGroups%>">
					<div style="width: 200px; height: <%out.print(chartType==1?"500px":"244px"); %>; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px;">
						<div id="groupSelection">
							<%
							out.println(ChartPreset.createLocationSelection(request));
							%>
						</div>
						<input type="button" class="buttonDesign" value="Add Group" onclick="javascript:clickAddLocationGroup(); this.form.submit()">
					</div>
					<% if(chartType==2||chartType==3){%>
					<input type="hidden" name="numberOfFormatGroups" id="numberOfFormatGroups" value="<%=numberOfFormatGroups%>">
					<div style="width: 200px; height: 244px; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px;">
						<div id="groupSelection">
							<%
							out.println(ChartPreset.createFormatSelection(request));
							%>
						</div>
						<input type="button" class="buttonDesign" value="Add Group" onclick="javascript:clickAddFormatGroup(); this.form.submit()">
					</div>
					<%} %>
				</form>
			</div>

			<script type="text/javascript">
				function clickAddLocationGroup() {
					numberOfLocationGroups.value++;
					document.getElementById("numberOfLocationGroups").value = numberOfLocationGroups.value;
				};
			</script>

			<script type="text/javascript">
				function clickAddFormatGroup() {
					numberOfFormatGroups.value++;
					document.getElementById("numberOfFormatGroups").value = numberOfFormatGroups.value;
				}
			</script>
	
			<script type="text/javascript">
				function setValues() {
					document.getElementById("double").value = 'true';
				}
			</script>
	
	
	
			<div id="chart">
				<%
					int width=870-201-15;
					int height=512;
					if (chartType == 1) {
				
					session.setAttribute(sessionID,request);
				
				%>
				<!-- img src="../ChartRenderer?id=<%=sessionID %>"-->
				
				<img src="../ChartRenderer?w=<%=width%>&h=<%=height%>&selectedChartType=<%=chartType%>&startTime=<%=startTime%>&endTime=<%=endTime%>&timeGranularity=<%=timeGranularity%>&countType=<%=countType%>&groupLocationParameters=<%out.println(ChartPreset.createLocationParameterString(request));%>&unit=<%out.println(unit); %>" /> 
				<%
					}else if (chartType == 2||chartType == 3) {
				%>
				<img src="../ChartRenderer?w=<%=width%>&h=<%=height%>&selectedChartType=<%=chartType%>&startTime=<%=startTime%>&endTime=<%=endTime%>&timeGranularity=<%=timeGranularity%>&countType=<%=countType%>&groupLocationParameters=<%out.println(ChartPreset.createLocationParameterString(request));%>&groupFormatParameters=<%out.println(ChartPreset.createFormatParameterString(request));%>&unit=<%out.println(unit); %>" />
				<%
					}
				%>
	
	
			</div>

		</div>
	</div>



</body>
</html>