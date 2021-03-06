<!-- 
 - Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob H?bler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
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
<script src="jquery-2.0.0.min.js"></script>
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
		<div id="setupNavigation">
			<input type="button" id="setupPlants" value="setup plants">
			<input type="button" id="setupProducts" value="setup products">
		</div>

		<div id="content">
		
		
			<script>
			$(document).ready(
				//change setup for products or plants
				function() {
					$("#setupPlants").click(function() {
						//clicked on "setup pants"
						$("#plants").show();
						$("#products").hide();
					}),
					$("#setupProducts").click(function() {		
						//clicked on "setup products"
						$("#products").show();
						$("#plants").hide();
					});
					
				}
			)
				
			
			</script>
			<%
			//flag for saving which setup has to be displayed (products or plants) - saved as parameter in request
			boolean showProducts="show".equals(request.getParameter("showProducts"));
			String addedProductSessionAttr=(session.getAttribute("addedNewProduct")!=null?(String)session.getAttribute("addedNewProduct"):"");
			if(addedProductSessionAttr!=null&&addedProductSessionAttr.equals("true")){
				showProducts=true;
			}
			session.removeAttribute("addedNewProduct");
			%>
			
			<!-- products section -->			
			<div id="products" <% if(!showProducts){out.println("style=\"display: none;\"");}%>>
				
				<%

				//if new products are added, the information on the products are added to the request and the page will be called again
				
				//check parameters for adding a new Product
				String prodName=request.getParameter("prodName");
				String prodShortName=request.getParameter("prodShortName");
				double tnf=-1;
				try{
					String tmp=request.getParameter("prodTnf");
					if(tmp!=null){
						tnf=Double.parseDouble(tmp);
					}
				}catch(NumberFormatException e){
					
				}
				
				//now step through variables for new product and check if they are valid
				
				//parameters are correct/complete
				boolean newProductIsOk=true;
				
				//check product name
				if(prodName==null||prodName.length()==0){
					newProductIsOk=false;
				}
				
				//check product shortName
				if(prodShortName==null||prodShortName.length()==0){
					newProductIsOk=false;	
				}
				
				//check tnfvalue
				if(tnf==-1){
					newProductIsOk=false;					
				}
				
				//query known products
				ArrayList<ArrayList<String>> products=SQL.query("SELECT * FROM products;");

				//if table "products" doesn't exist yet, no products were found -> create tabel
				if(products==null){
					String command=
						"CREATE TABLE products (products_id serial primary key, "+ 
						"product_name varchar(50) NOT NULL, product_short_name varchar(20), tnf decimal not null"+
						")";			
					SQL.execute(command);			
	
					//query again, to get an empty ArrayList
					products=SQL.query("SELECT * FROM products;");
				}
					
				//passed product has valid parameters, check for existing product
				if(newProductIsOk){
					boolean prodExists=false;
					
					//step through known products (line 0 contains headers, skip this line)
					for(int i=1;i<products.size();i++){
						if(products.get(i).size()>=4){
							if(prodName.equals(products.get(i).get(1))||
									prodShortName.equals(products.get(i).get(2))){
								//found product with same name/shortname
								prodExists=true;
								break;
							}
						}
					}
					
					if(prodExists){
						//product exists
						out.println("Product allready exists, choose other name!");
					}else{
						//product doesn't exist, add it and reload
						ArrayList<String>values=new ArrayList<String>();
						values.add("");
						values.add(""+prodName);
						values.add(""+prodShortName);
						values.add(""+tnf);
						SQL.addColumn("products",SQL.getColumns("products"),values);
						response.setIntHeader("Refresh",0);
						session.setAttribute("addedNewProduct","true");
					}
					
				}
				

				
				%>
				<form method="post" action="">
					
				<table style="border:1px solid black;border-radius:2px;"  id="productTable" >
					<tr>
						<td>
							Product
						</td>
						<td>
							Shortname
						</td>
						<td>
							equivalent pieces to 1 TNF
						</td>
					</tr>
						<tr>
							<td><input type="text" name="prodName"></td>
							<td><input type="text" name="prodShortName"></td>
							<td><input type="text" name="prodTnf"></td>
							<td>
							<input type="hidden" name="addProduct">
							<input type="hidden" name="showProducts" value="show">
							<input type="submit" value="Add product"></td>
					</tr>				
					<%for(int i=1;i<products.size();i++){ 
						out.println("<tr><td>"+products.get(i).get(1)+"</td><td>"+products.get(i).get(2)+"</td><td>"+products.get(i).get(3)+"</td></tr>\n");
					} %>
				</table>				
					</form>
					
			</div>
			
			<!-- plants section -->			
			<div id="plants" <%if(showProducts){out.println("style=\"display: none;\"");}%>>
		
			<%
			
			//store known plants
			ArrayList<ArrayList<String>> plants=SQL.query("SELECT * FROM plants;");

			if(plants==null){
				//no plants found, create tabel and reload
				SQL.createTable("plants",new String[]{"plant_name"});
				plants=SQL.query("SELECT * FROM plants;");
				response.sendRedirect("");
			}
			
			//selected plant
			int showPlantNameArrayPosition=0;
			int selected=0;
			try{
				selected=Integer.parseInt(request.getParameter("selName"));
			}catch(NumberFormatException e){
				selected=0;
			}
			
			
			//controlPoints for selected plant
			ArrayList<ArrayList<String>> controlPoints=null;
			
			if(selected>0||selected==-1){
				String getControlPoints="Select plant_name,control_point_name,reference_point from plants,controlpoints";
				if(selected>0){
					getControlPoints+=" where plant_id="+selected+" and plants_id="+selected+";";
				}else if(selected==-1){
					getControlPoints+=" where plant_id=plants_id;";
				}
			
				controlPoints=SQL.query(getControlPoints);
				if(controlPoints==null){
					String command=
						"CREATE TABLE controlpoints (controlpoints_ID serial primary key, plant_id integer NOT NULL, "+ 
						"control_point_short_name varchar(10) not null, control_point_name varchar(50), reference_point boolean DEFAULT false, "+
					  	"CONSTRAINT controlpoint_plant_id_fkey FOREIGN KEY (plant_id) "+
					    "REFERENCES plants (plants_id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION)";					
					SQL.execute(command);			
					//controlPoints=SQL.query("SELECT * FROM controlpoints;");
					controlPoints=SQL.query(getControlPoints);
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
				
				boolean nameOK=true;
				if(controlPointName==null||controlPointName.trim().length()<3){
					nameOK=false;
				}else{
					for(int i=0;i<controlPoints.size();i++){
						if(controlPoints.get(i).get(1).equals(controlPointName)){
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
					values.add(""+selected);
					values.add(controlPointName.substring(0,3).toUpperCase());
					values.add(controlPointName);
					values.add("set".equals(request.getParameter("reference_point"))+"");
					SQL.addColumn("controlpoints",SQL.getColumns("controlpoints"),values);
					response.setIntHeader("Refresh",0);
				}else{
					System.out.println("name not ok");
				}

			}
			
			%>

			<table>
				<tr>
					<%if(plants!=null){%>
					<td>
						<form method="post" action="">
							<select name="selName" onChange="this.form.submit()">
								<option value="0" />
								<%
								for(int i=1;i<plants.size();i++){
									if(selected==Integer.parseInt(plants.get(i).get(0))){
										showPlantNameArrayPosition=i;
									}
									out.println("<option value="+'"'+plants.get(i).get(0)+'"'+((selected==Integer.parseInt(plants.get(i).get(0)))?" selected":"")+">"+plants.get(i).get(1)+"</option>");			
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


				<%if(selected==0||selected==-1){out.println("<!--");} %>
				<tr>
					<td>
						<form method="post" action="">
							Name : <input type="text"
								name="<%=Const.RequestParameters.SETUP_NEW_CONTROL_POINT_NAME%>">
							<input type="hidden" name="selName" value="<%=selected %>">
							<input type="checkbox" name="reference_point" value="set"> Referencecontrolpoint
							<input type="submit"
								value="Add control point<%out.println(((showPlantNameArrayPosition!=0&&plants.size()>showPlantNameArrayPosition&&showPlantNameArrayPosition>0)?" to "+plants.get(showPlantNameArrayPosition).get(1):"")); %>">
						</form>
					</td>

				</tr>
				<tr>
					<td>
						<form action="../file/upload/importEnergyData<%=selected %>" method="post"
							enctype="multipart/form-data">
							<input type="file" name="File" /> <input type="submit"
								value="Import Energydata" />
						</form>
					</td>
					<td>
						<form action="../file/upload/importProductionData<%=selected %>" method="post"
							enctype="multipart/form-data">
							<input type="file" name="File" /> <input type="submit"
								value="Import Productiondata" />
						</form>
					</td>

				</tr>
				<%if(selected==0||selected==-1){out.println("-->");} %>

				<tr>
					<%
					if(plants==null||plants.size()==0){
						out.println("No data available");
					}else{
			
					}
					%>
				</tr>
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
					for(int j=0;j<2;j++){
						out.println("<td style=\"word-break:break-all;word-wrap:break-word\" width=\"180\">");
						if(j==1&&controlPoints.get(i).get(2).equals("t")){
							out.println("<i>");
						}
						out.println(controlPoints.get(i).get(j));
						if(j==1&&controlPoints.get(i).get(2).equals("t")){
							out.println("</i>");
						}
						out.println("</td>");
						
					}
					out.println("</tr>");
				}
		
				out.println("</table>");
			}
					
			%>

			</div>
		</div>
	</div>
</body>
</html>