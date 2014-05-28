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
%>

<!DOCTYPE html>
<html>




<head>
<title>AMOS-Cockpit</title>
<link rel="stylesheet" href="../styles/style.css" type="text/css" />
<link rel="stylesheet" href="../styles/dtree.css" type="text/css" />
<script src="jquery-2.0.0.min.js"></script>

<!-- script>
	$(document).ready(
			function() {
				alert("hallo");
				
				for (var i = 1; i <= 3; i++) {
					$(".group" + i).click(function() {
						
						
						$(".groupContent1").slideToggle("slow");

					});
				}
				$("#title").click(
						function() {
							if ($(".group1").is(":visible")
									&& $(".controlpoints").is(":visible")) {
								$(".controlpoints").slideToggle("fast");
								$(".group1").slideUp("slow");
							} else {
								$(".group1").slideToggle("slow");
							}

						});

			});
</script>
-->

</head>

<body onload="changeTimeGranularity();changeChartType();">


	<script>
		function changeStartDays() {
			var day = document.getElementById("startDay").value;
			var month = document.getElementById("startMonth").value;
			var year = document.getElementById("startYear").value;

			var endDay = document.getElementById("endDay").value;
			var endMonth = document.getElementById("endMonth").value;
			var endYear = document.getElementById("endYear").value;

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

			document.getElementById("startDay").options.length = days;

			for (var i = 1; i <= days; i++) {
				document.getElementById("startDay").options[i - 1].innerHTML = i;
				document.getElementById("startDay").options[i - 1].value = i;
			}

			/*
			if(endYear<year){
				document.getElementById("endYear").selectedIndex=document.getElementById("startYear").selectedIndex;
				document.getElementById("endMonth").selectedIndex=document.getElementById("startMonth").selectedIndex;
				document.getElementById("endDay").selectedIndex=document.getElementById("startDay").selectedIndex;
			}else if(endYear==year&&endMonth<month){
				document.getElementById("endMonth").selectedIndex=document.getElementById("startMonth").selectedIndex;
				document.getElementById("endDay").selectedIndex=document.getElementById("startDay").selectedIndex;		
			}else if(endYear==year&&endMonth==month&&endDay<day){
				document.getElementById("endDay").selectedIndex=document.getElementById("startDay").selectedIndex;				
			}
			 */

		}

		function changeEndDays() {
			var startDay = document.getElementById("startDay").value;
			var startMonth = document.getElementById("startMonth").value;
			var startYear = document.getElementById("startYear").value;

			var day = document.getElementById("endDay").value;
			var month = document.getElementById("endMonth").value;
			var year = document.getElementById("endYear").value;

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

			document.getElementById("endDay").options.length = days;

			for (var i = 1; i <= days; i++) {
				document.getElementById("endDay").options[i - 1].innerHTML = i;
				document.getElementById("endDay").options[i - 1].value = i;
			}

			/*
			if(startYear>year){
				document.getElementById("startYear").selectedIndex=document.getElementById("endYear").selectedIndex;
				document.getElementById("startMonth").selectedIndex=document.getElementById("endMonth").selectedIndex;
				document.getElementById("startDay").selectedIndex=document.getElementById("endDay").selectedIndex;
			}else if(startYear==year&&startMonth>month){
				document.getElementById("startMonth").selectedIndex=document.getElementById("endMonth").selectedIndex;
				document.getElementById("startDay").selectedIndex=document.getElementById("endDay").selectedIndex;		
			}else if(startYear==year&&startMonth==month&&startDay>day){
				document.getElementById("startDay").selectedIndex=document.getElementById("endDay").selectedIndex;				
			}
			 */
		}

		function showMoreGroups() {
			var groups = document.getElementById("numberOfGroups");
			groups = groups + 1;
			document.getElementById("numberOfGroups").value = groups;
		}
	</script>

					<script>
					function changeChartType() {
							var e = document.getElementById("selectChartType");
							var val = e.options[e.selectedIndex].value;
							document.getElementById("selectedChartType").value=val;;
							if (val == '1') {
								//show time chart, hide others
								$("#timeChart").show();
								changeTimeGranularity();
							} else {
								//hide all charts
								$("#timeChart").hide();
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
							var endYear = document
									.getElementById("selectEndYear").value;

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
								if ((year % 4 == 0 && year % 100 != 0)
										|| year % 400 == 0) {
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
							var endYear = document
									.getElementById("selectEndYear").value;

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
				int chartType = 0;
				try {
					chartType = Integer.parseInt(request.getParameter("selectedChartType"));
				} catch (NumberFormatException e) {
					chartType = 0;
				}
				
				int timeGranularity = 0;
				try {
					timeGranularity = Integer.parseInt(request.getParameter("timeGranularity"));
				} catch (NumberFormatException e) {
					timeGranularity = 2;
				}
			
				int selectedDay=0;
				try{
					selectedDay=Integer.parseInt(request.getParameter("selectDay"));
				}catch(NumberFormatException e){
					selectedDay=0;
				}
			
				int selectedMonth=0;
				try{
					selectedMonth=Integer.parseInt(request.getParameter("selectMonth"));
				}catch(NumberFormatException e){
					selectedMonth=0;
				}
				
				int selectedYear=0;
				try{
					selectedYear=Integer.parseInt(request.getParameter("selectYear"));
				}catch(NumberFormatException e){
					selectedYear=0;
				}
				
				int selectedEndYear=0;
				try{
					selectedEndYear=Integer.parseInt(request.getParameter("selectEndYear"));
				}catch(NumberFormatException e){
					selectedEndYear=0;
				}
				
				
				
				//old
				int startDay = 0;
				try {
					startDay = Integer.parseInt(request.getParameter("startDay"));
				} catch (NumberFormatException e) {
					startDay = 0;
				}
				int startMonth = 0;
				try {
					startMonth = Integer.parseInt(request
							.getParameter("startMonth"));
				} catch (NumberFormatException e) {
					startMonth = 0;
				}
				int startYear = 0;
				try {
					startYear = Integer.parseInt(request.getParameter("startYear"));
				} catch (NumberFormatException e) {
					startYear = 0;
				}
				int endDay = 0;
				try {
					endDay = Integer.parseInt(request.getParameter("endDay"));
				} catch (NumberFormatException e) {
					endDay = 0;
				}
				int endMonth = 0;
				try {
					endMonth = Integer.parseInt(request.getParameter("endMonth"));
				} catch (NumberFormatException e) {
					endMonth = 0;
				}
				int endYear = 0;
				try {
					endYear = Integer.parseInt(request.getParameter("endYear"));
				} catch (NumberFormatException e) {
					endYear = 0;
				}
				int countType = 0;
				try {
					countType = Integer.parseInt(request.getParameter("countType"));
				} catch (NumberFormatException e) {
					countType = 1;
				}

				int numberOfGroups = 1;
				try {
					numberOfGroups = Integer.parseInt(request
							.getParameter("numberOfGroups"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				//String groupParameterString=ChartPreset.createParameterString(request);

				//Create Timestamp for SQL-Query (Start- and End-Date)
				String time=TimestampConversion.convertTimestamp(0,0,(timeGranularity==1?selectedDay:1),(timeGranularity==1||timeGranularity==2?selectedMonth:1),selectedYear);
				String endTime="";
				Calendar c=Calendar.getInstance();
				c.set(selectedYear,selectedMonth-1,selectedDay);
				switch(timeGranularity){
				case 0:
					//show one day
					c.add(Calendar.DATE,1);
					endTime=TimestampConversion.convertTimestamp(0,0,c.get(Calendar.DATE),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR));
					break;
				case 1:
					//show one month
					c.add(Calendar.MONTH,1);
					endTime=TimestampConversion.convertTimestamp(0,0,c.get(Calendar.DATE),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR));
					break;
				case 2:
					//show one year
					c.add(Calendar.YEAR,1);
					endTime=TimestampConversion.convertTimestamp(0,0,c.get(Calendar.DATE),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR));
					break;
				case 3:
					//show more years
					//no break, use default clause
					
				default:
					c.set(selectedEndYear+1,0,1);
					endTime=TimestampConversion.convertTimestamp(0,0,c.get(Calendar.DATE),c.get(Calendar.MONTH)+1,c.get(Calendar.YEAR));
					
				}
				//old
				//String startTime = TimestampConversion.convertTimestamp(0, 0,
				//		startDay, startMonth, startYear);
				//String endTime = TimestampConversion.convertTimestamp(0, 0, endDay,
				//		endMonth, endYear);
				String startTime="";
			%>


			<div id="chartTypeSelection">
				Chart type: <select id="selectChartType" name="selectChartType"
					onchange="changeChartType();">

					<option value="0"
						<%out.print(((chartType == 0) ? " selected" : "") + "");%>></option>
					<option value="1"
						<%out.print(((chartType == 1) ? " selected" : "") + "");%>>Time</option>
					<!-- 
					<option value="2"
						<%out.print(((chartType == 2) ? " selected" : "") + "");%>>Bar
						Chart</option>
					<option value="3"
						<%out.print(((chartType == 3) ? " selected" : "") + "");%>>Bar
						Chart 3D</option>
					<option value="4"
						<%out.print(((chartType == 4) ? " selected" : "") + "");%>>Line
						Chart</option>
					<option value="5"
						<%out.print(((chartType == 5) ? " selected" : "") + "");%>>Pie
						Chart</option>
					<option value="6"
						<%out.print(((chartType == 6) ? " selected" : "") + "");%>>Graph
						Chart</option>
						 -->
				</select>
			</div>

			<div id="timeChart"
				<%out.println(chartType == 1 ? "style=\"display: none;\"" : "");%>>



				<form id="selectionForm" method="post" action="">
					<input type="hidden" name="selectedChartType" id="selectedChartType">

					<div id="timeSelectionBar">

						<div class="timeGranularityDiv" style="display: inline">
							Show me the <select name="timeGranularity" id="timeGranularity"
								onchange="changeTimeGranularity();">
								<option value="0"
									<%out.print(((timeGranularity == 0) ? " selected" : "") + "");%>>day</option>
								<option value="1"
									<%out.print(((timeGranularity == 1) ? " selected" : "") + "");%>>month</option>
								<option value="2"
									<%out.print(((timeGranularity == 2) ? " selected" : "") + "");%>>year</option>
								<option value="3"
									<%out.print(((timeGranularity == 3) ? " selected" : "") + "");%>>years</option>
							</select>
						</div>

						<script>
							$(document).ready(function() {
								$("#timeGranularity").click(function() {

								}), $("#setupProducts").click(function() {
									$("#products").show();
									$("#plants").hide();
								})

							})
						</script>

						<div id="labelFrom" style="display: inline">from</div>
						<select name="selectDay" id="selectDay" style="display: inline">
							<%
								for (int i = 1; i <= 31; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((selectedDay == i) ? " selected" : "") + "");%>><%=i%>.
							</option>
							<%
								}
							%>
						</select> <select name="selectMonth" id="selectMonth"
							onchange="changeSelectDays();" style="display: inline">
							<%
								for (int i = 1; i <= 12; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((selectedMonth == i) ? " selected" : "") + "");%>><%=i%>.
							</option>
							<%
								}
							%>
						</select> <select name="selectYear" id="selectYear"
							onchange="changeSelectDays();" style="display: inline">
							<%
								for (int i = 2012; i <= 2020; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((selectedYear == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select>
						<div id="labelTo" style="display: inline">to</div>
						<select name="selectEndYear" id="selectEndYear"
							onchange="changeSelectEndYear();" style="display: inline">
							<%
								for (int i = 2012; i <= 2020; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((selectedEndYear == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select>
						
						<select name="countType" id="countType">
							<option value="0"
								<%out.print(((countType == 0) ? " selected" : "") + "");%>>Average values</option>
							<option value="1"
								<%out.print(((countType == 1) ? " selected" : "") + "");%>>Sum values</option>


						</select>
						

					</div>

					<!-- 
					<div style="display: none;">

						<br> Start: <select name="startDay" id="startDay"
							onchange="changeStartDays();">
							<%
								for (int i = 1; i <= 31; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((startDay == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select> <select name="startMonth" id="startMonth"
							onchange="changeStartDays();">
							<%
								for (int i = 1; i <= 12; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((startMonth == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select> <select name="startYear" id="startYear"
							onchange="changeStartDays();">
							<%
								for (int i = 2000; i <= 2020; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((startYear == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select> End: <select name="endDay" id="endDay"
							onchange="changeEndDays();">
							<%
								for (int i = 1; i <= 31; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((endDay == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select> <select name="endMonth" id="endMonth" onchange="changeEndDays();">
							<%
								for (int i = 1; i <= 12; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((endMonth == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select> <select name="endYear" id="endYear" onchange="changeEndDays();">
							<%
								for (int i = 2000; i <= 2020; i++) {
							%>
							<option value="<%=i%>"
								<%out.print(((endYear == i) ? " selected" : "") + "");%>><%=i%></option>
							<%
								}
							%>
						</select> Count Type: <select name="countType" id="countType">
							<option value="0"
								<%out.print(((countType == 0) ? " selected" : "") + "");%>>Average</option>
							<option value="1"
								<%out.print(((countType == 1) ? " selected" : "") + "");%>>Sum</option>


						</select>

					</div>
					 -->
					<input type="submit" id="showChart" value="Show"> <input
						type="hidden" name="numberOfGroups" id="numberOfGroups"
						value="<%=numberOfGroups%>">

					<div style="width: 200px; height: 500px; overflow: auto; border-width: 1px; border-style: solid; border-color: black; padding: 5px;">
						<div id="groupSelection">
							<%out.println(ChartPreset.createSelection(request));%>
						</div>
						<input type="button" value="Add Group"
							onclick="javascript:clickAdd(); this.form.submit()">
					</div>
				</form>
			</div>
			<script type="text/javascript">
				function clickAdd() {
					numberOfGroups.value++;
					document.getElementById("numberOfGroups").value = numberOfGroups.value;
				};
			</script>

			<div id="chart">
				<%
					if (chartType != 0) {
				%>
				<!-- <img src="../ChartRenderer?chartType=<%=chartType%>&startTime=<%=startTime%>&endTime=<%=endTime%>&granularity=<%=timeGranularity%>&countType=<%=countType%>&groupParameters=<%out.println(ChartPreset.createParameterString(request));%>" /> -->
					
				<img src="../ChartRenderer?selectedChartType=<%=chartType%>&time=<%=time%>&endTime=<%=endTime%>&timeGranularity=<%=timeGranularity%>&countType=<%=countType%>&groupParameters=<%out.println(ChartPreset.createParameterString(request));%>" />
				<%
					}
				%>


			</div>




		</div>
	</div>



</body>
</html>