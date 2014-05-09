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
		Logged in as "

		<% 
	out.print(session.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME)); 
	%>
		"
	</div>
	<nav id="menue">
		<ul>
			<%out.println(Header.getHeaderButtons()); %>
		</ul>
	</nav>

	<div id="completeContentBox">
		<div id="content">

			<%
			
			//out.println(SQL.querry("SELECT * FROM factories"));
			ArrayList<ArrayList<String>> plants=SQL.querry("SELECT * FROM plants;");

			if(plants==null){
				SQL.createTable("plants",new String[]{"plant_name"});
				plants=SQL.querry("SELECT * FROM plants;");
				System.out.println("finished, reload");
				response.sendRedirect("");
			}
			
			//System.out.println("want to show controlPoints for "+request.getParameter("selName"));
			
			int selected=0;
			try{
				selected=Integer.parseInt(request.getParameter("selName"));
			}catch(NumberFormatException e){
				selected=0;
			}
			
			
			ArrayList<ArrayList<String>> controlPoints=null;
			
			if(selected>0||selected==-1){
				String getControlPoints="Select plant_name,control_point_name from plants,controlpoints";
				if(selected>0){
					getControlPoints+=" where plant_id="+selected+" and plants_id="+selected+";";
				}else if(selected==-1){
					getControlPoints+=" where plant_id=plants_id;";
				}
				//String getControlPoints="Select * from plants,controlpoints where plant_id="+selected+";";
				//controlPoints=SQL.querry("SELECT * FROM controlpoints;");
				System.out.println("get from db: "+getControlPoints);
				controlPoints=SQL.querry(getControlPoints);
				if(controlPoints==null){
					String command=
						"CREATE TABLE controlpoints (controlpoints_ID serial primary key, plant_id integer NOT NULL, "+ 
						"control_point_short_name varchar(10) not null, control_point_name varchar(50),"+
					  	"CONSTRAINT controlpoint_plant_id_fkey FOREIGN KEY (plant_id) "+
					    "REFERENCES plants (plants_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";					
					SQL.execute(command);			
					//controlPoints=SQL.querry("SELECT * FROM controlpoints;");
					controlPoints=SQL.querry(getControlPoints);
				}
			}
			
			
			if(request.getParameter(Const.RequestParameters.SETUP_NEW_PLANT_NAME)!=null){
				//reloaded page and want to add a new plant
				String plantName=request.getParameter(Const.RequestParameters.SETUP_NEW_PLANT_NAME);
				boolean nameOK=true;
				if(plantName==null||plantName.trim().length()==0){
					nameOK=false;
				}else{
					for(int i=0;i<plants.size();i++){
						if(plants.get(i).get(1).equals(plantName)){
							//TODO add label or sth to mark as error
							%>
			Plantname allready in use!
			<%
							nameOK=false;
							break;
						}
					}
				}
				
				if(nameOK){
					ArrayList<String>values=new ArrayList<String>();
					values.add("");
					values.add(plantName);
					SQL.addColumn("plants",SQL.getColumns("plants"),values);
					response.setIntHeader("Refresh",0);
				}
			}else if(request.getParameter(Const.RequestParameters.SETUP_NEW_CONTROL_POINT_NAME)!=null){
				//reloaded page and want to add a new control point
				String controlPointName=request.getParameter(Const.RequestParameters.SETUP_NEW_CONTROL_POINT_NAME);
				System.out.println("want to add "+controlPointName+" "+selected);
	
				boolean nameOK=true;
				if(controlPointName==null||controlPointName.trim().length()<3){
					nameOK=false;
				}else{
					for(int i=0;i<controlPoints.size();i++){
						if(controlPoints.get(i).get(1).equals(controlPointName)){
							//TODO add label or sth to mark as error
							%>
			Control point name allready in use!
			<%
							nameOK=false;
							break;
						}
					}
				}
				
				if(nameOK){
					ArrayList<String>values=new ArrayList<String>();
					values.add("");
					values.add(""+plants.get(selected).get(0));
					values.add(controlPointName.substring(0,3).toUpperCase());
					values.add(controlPointName);
					SQL.addColumn("controlpoints",SQL.getColumns("controlpoints"),values);
					response.setIntHeader("Refresh",0);
				}

			}
			
			%>

			<table>
				<tr>
					<%if(plants!=null){%>
					<td>
						<form method="post" action="">
							<select name="selName" onChange="this.form.submit()">
								<!-- select name="selName" id="selectPlant" onchange="changeSelectPlant()"-->
								<option value="0" />
								<%
								
								for(int i=1;i<plants.size();i++){
									out.println("<option value="+'"'+i+'"'+((selected==i)?" selected":"")+">"+plants.get(i).get(1)+"</option>");			
								}
								out.println("<option value="+'"'+-1+'"'+((selected==-1)?" selected":"")+">All</option>");
								%>
							</select> <input type="submit" value="Show controlpoints">
						</form>
					</td>
					<% } %>

					<td>
						<form method="post" action="">
							Name : <input type="text"
								name="<%=Const.RequestParameters.SETUP_NEW_PLANT_NAME%>">
							<input type="submit" value="Add plant">
						</form>


					</td>

				</tr>


				<%if(selected==0){out.println("<!--");} %>
				<tr>
					<td>
						<form method="post" action="">
							Name : <input type="text"
								name="<%=Const.RequestParameters.SETUP_NEW_CONTROL_POINT_NAME%>">
							<input type="hidden" name="selName" value="<%=selected %>">
							<input type="submit"
								value="Add control point<%out.println(((selected!=0&&plants.size()>selected&&selected>0)?" to "+plants.get(selected).get(1):"")); %>">
						</form>
					</td>

					<td>
						<form action="../file/upload/import<%=selected %>" method="post"
							enctype="multipart/form-data">
							<input type="file" name="File" /> <input type="submit"
								value="Upload" />
						</form>
					</td>

				</tr>
				<%if(selected==0){out.println("-->");} %>

				<tr>
					<%
					if(plants==null||plants.size()==0){
						out.println("No data available");
					}else{
			
					}
					%>
				
			</table>
			<%
			if(controlPoints==null||controlPoints.size()==0){
				if(selected==0){
					out.println("Select a plant.");
				}else{
					out.println("No control points available"+((selected!=0&&plants.size()>selected&&selected>0)?" for "+plants.get(selected).get(1):""));
				}
			}else{
			
				out.println("<table border rules=\"all\" id= \"dataTable\" >");
		
				for(int i=0;i<controlPoints.size();i++){
					out.println("<tr>");
					for(int j=0;j<controlPoints.get(i).size();j++){
						out.println("<td style=\"word-break:break-all;word-wrap:break-word\" width=\"180\">");
						out.println(controlPoints.get(i).get(j));
						out.println("</td>");
						
					}
					out.println("</tr>");
				}
		
				out.println("</table>");
			}
					
			%>


		</div>
	</div>
</body>
</html>