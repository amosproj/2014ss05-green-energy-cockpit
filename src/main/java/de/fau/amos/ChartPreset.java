/*
 * Copyright (c) 2014 by Sven Huprich, Dimitry Abb, Jakob Hübler, Cindy Wiebe, Ferdinand Niedermayer, Dirk Riehle, http://dirkriehle.com
 *
 * This file is part of the Green Energy Cockpit for the AMOS Project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.fau.amos;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class ChartPreset {
	

	public static String createLocationGroup(int locationGroupId){
		
//		System.out.println("called group with "+groupId);
		String out = "";		

		out+="<script type=\"text/javascript\">\n"
				+"$(document).ready(function() {\n"
				+"$(\"#locationGroupButtonUp"+locationGroupId+"\").click(function() {\n"
				+"$(\".locationGroupContent"+locationGroupId+"\").slideDown(\"fast\");\n"
				+"$(\"#locationGroupButtonDown"+locationGroupId+"\").show();\n"
				+"$(\"#locationGroupButtonUp"+locationGroupId+"\").hide();\n"
				+"});\n"
				+"$(\"#locationGroupButtonDown"+locationGroupId+"\").click(function() {\n"
				+"$(\".locationGroupContent"+locationGroupId+"\").slideUp(\"fast\");\n"
				+"$(\"#locationGroupButtonUp"+locationGroupId+"\").show();\n"
				+"$(\"#locationGroupButtonDown"+locationGroupId+"\").hide();\n"
				+"});\n"
				+"});\n"
				
				+"function checkKeys"+locationGroupId+"(){\n" 
				+"var is=$(\"#locationGroupName"+locationGroupId+"\").val();\n"
				+"var validChars=\"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890-_\";\n"
				+"var foundWrong=false;\n"
				+"var validResult=\"\";\n"
				+"for(var i=0;i<is.length;i++){\n"
				+"if(validChars.indexOf(is.charAt(i))==-1){\n"
				+"foundWrong=true;\n"	
				+"}else{\n"
				+"validResult+=is.charAt(i);\n"
				+"}}\n"					
				+"if(foundWrong){alert(\"Use only [a-z],[A-Z],[0-9],[-] or [_]!\");\n"
				+"$(\"#locationGroupName"+locationGroupId+"\").val(validResult);\n"
				+"}}\n" 
				+"</script>\n";
		
		out+=" <div class=\"locationGroup"+locationGroupId+"\">\n"
				+"<img id=\"locationGroupButtonUp"+locationGroupId+"\" src=\"../images/up.png\" style=\"display: none;\">\n"
				+"<img id=\"locationGroupButtonDown"+locationGroupId+"\" src=\"../images/down.png\">\n"
				//+ "<input type=\"checkbox\" name=\"locationGroup_" + locationGroupId + "\" value=\"locationGroup_" + locationGroupId + "\" onclick=\"javascript:showHide"+locationGroupId+"()\" checked>Group " + locationGroupId +"<br>\n"
//				+"Group "+locationGroupId
				+"<input type=\"text\" id=\"locationGroupName"+locationGroupId+"\" name=\"locationGroupName"+locationGroupId+"\" placeholder=\"Group "+locationGroupId+"\" onkeyup=\"checkKeys"+locationGroupId+"()\">\n"
				+ "</div>\n";
		
		out+="<div class=\"locationGroupContent"+locationGroupId+"\">\n";
				
		ArrayList<ArrayList<String>> plants=SQL.query("select plant_id,plant_name, count(controlpoints_id) from controlpoints inner join plants on plants.plants_id=controlpoints.plant_id group by plant_name,plant_id;");
		ArrayList<ArrayList<String>> data = SQL.query("select plant_id,plant_name,controlpoints_id, control_point_name from controlpoints inner join plants on plants.plants_id=controlpoints.plant_id order by plant_name, control_point_name;");
		//number of plants
		int j=1;
		for(int i=1; i < plants.size(); i++ ){
			//for each plant
			out += "<div class=\"plants\">\n";
			out += "<div class=\"plants"+locationGroupId+"_"+plants.get(i).get(0)+"\">\n";
//			out+="<p style=\"margin-left: 10px;\">\n";
			out+="<img id=\"plantButtonUp"+locationGroupId+"_"+plants.get(i).get(0)+"\" src=\"../images/up.png\" style=\"display: none;\">\n";
			out+="<img id=\"plantButtonDown"+locationGroupId+"_"+plants.get(i).get(0)+"\" src=\"../images/down.png\">\n";
			out +="<input type=\"checkbox\" name=\"plantCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"\" id=\"plantCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"\" value=\"plantCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"\">";//+plants.get(i).get(1)+"<br>\n";
			out +="<div class=\"label_plant_"+locationGroupId+"_"+plants.get(i).get(0)+"\" style=\"display:inline\">"+plants.get(i).get(1)+"</div><br>\n";
			//			out += "</p>";
			out+="</div>\n";
			out+="</div>\n";
			
			out+="<script type=\"text/javascript\">\n"
					+"$(document).ready(function() {\n"

					+"$(\"#plantButtonUp"+locationGroupId+"_"+plants.get(i).get(0)+"\").click(function() {\n"
					+"$(\".controlpoints"+locationGroupId+"_"+plants.get(i).get(0)+"\").slideDown(\"fast\");\n"
					+"$(\"#plantButtonDown"+locationGroupId+"_"+plants.get(i).get(0)+"\").show();\n"
					+"$(\"#plantButtonUp"+locationGroupId+"_"+plants.get(i).get(0)+"\").hide();\n"
					+"});\n"
					+"$(\"#plantButtonDown"+locationGroupId+"_"+plants.get(i).get(0)+"\").click(function() {\n"
					+"$(\".controlpoints"+locationGroupId+"_"+plants.get(i).get(0)+"\").slideUp(\"fast\");\n"
					+"$(\"#plantButtonUp"+locationGroupId+"_"+plants.get(i).get(0)+"\").show();\n"
					+"$(\"#plantButtonDown"+locationGroupId+"_"+plants.get(i).get(0)+"\").hide();\n"
					+"});\n"

					
//					+"$(\".label_plant_"+locationGroupId+"_"+plants.get(i).get(0)+"\").click(function() {\n"		
//					+"$(\".controlpoints"+locationGroupId+"_"+plants.get(i).get(0)+"\").slideToggle(\"fast\");\n"
//					+"});\n"
					
					+"$('#plantCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"').click(function() {\n"
					+"if($(\"#plantCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"\").is(':checked')){\n"
					+"$(\".controlpoints"+locationGroupId+"_"+plants.get(i).get(0)+" input[type='checkbox']\").prop(\"checked\",true);\n"						
					+"}else{\n"
					+"$(\".controlpoints"+locationGroupId+"_"+plants.get(i).get(0)+" input[type='checkbox']\").prop(\"checked\",false);\n"						
					+"}\n"
					+"});\n"

					+"});\n"		
					+"</script>\n";

			
			int max=(j+Integer.parseInt(plants.get(i).get(2)));
			out+="<div class=\"controlpoints\">\n";
			out+="<div class=\"controlpoints"+locationGroupId+"_"+plants.get(i).get(0)+"\">\n";
//			out+="<p style=\"margin-left: 20px;\">\n";
			for(; j < max;j++){
				out += "<input type=\"checkbox\" name=\"controlPointCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"_"+data.get(j).get(2) + "\" id=\"controlPointCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"_"+data.get(j).get(2) + "\" value=\"controlPointCheckBox_"+locationGroupId+"_"+plants.get(i).get(0)+"_"+data.get(j).get(2) + "\">" + data.get(j).get(3) +"<br>\n";				
			}
//			out+="</p>\n";
			out+="</div>\n";
			out+="</div>\n";
		}
		
		out+="</div>\n";
		out+="<br>";
		
		return out;
	}

	public static String createFormatGroup(int formatGroupId){
		
//		System.out.println("called group with "+groupId);
		String out = "";		

		out+="<script type=\"text/javascript\">\n"
				+"$(document).ready(function() {\n"
				+"$(\"#formatGroupButtonUp"+formatGroupId+"\").click(function() {\n"
				+"$(\".formatGroupContent"+formatGroupId+"\").slideDown(\"fast\");\n"
				+"$(\"#formatGroupButtonDown"+formatGroupId+"\").show();\n"
				+"$(\"#formatGroupButtonUp"+formatGroupId+"\").hide();\n"
				+"});\n"
				+"$(\"#formatGroupButtonDown"+formatGroupId+"\").click(function() {\n"
				+"$(\".formatGroupContent"+formatGroupId+"\").slideUp(\"fast\");\n"
				+"$(\"#formatGroupButtonUp"+formatGroupId+"\").show();\n"
				+"$(\"#formatGroupButtonDown"+formatGroupId+"\").hide();\n"
				+"});\n"
				+"});\n"
				
				+"function checkKeys"+formatGroupId+"(){\n" 
				+"var is=$(\"#formatGroupName"+formatGroupId+"\").val();\n"
				+"var validChars=\"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 1234567890-_\";\n"
				+"var foundWrong=false;\n"
				+"var validResult=\"\";\n"
				+"for(var i=0;i<is.length;i++){\n"
				+"if(validChars.indexOf(is.charAt(i))==-1){\n"
				+"foundWrong=true;\n"	
				+"}else{\n"
				+"validResult+=is.charAt(i);\n"
				+"}}\n"					
				+"if(foundWrong){alert(\"Use only [a-z],[A-Z],[0-9],[-] or [_]!\");\n"
				+"$(\"#formatGroupName"+formatGroupId+"\").val(validResult);\n"
				+"}}\n" 
				+"</script>\n";
		
		out+=" <div class=\"formatGroup"+formatGroupId+"\">\n"
				+"<img id=\"formatGroupButtonUp"+formatGroupId+"\" src=\"../images/up.png\" style=\"display: none;\">\n"
				+"<img id=\"formatGroupButtonDown"+formatGroupId+"\" src=\"../images/down.png\">\n"
				//+ "<input type=\"checkbox\" name=\"formatGroup_" + formatGroupId + "\" value=\"formatGroup_" + formatGroupId + "\" onclick=\"javascript:showHide"+formatGroupId+"()\" checked>Group " + formatGroupId +"<br>\n"
//				+"Group "+formatGroupId
				+"<input type=\"text\" id=\"formatGroupName"+formatGroupId+"\" name=\"formatGroupName"+formatGroupId+"\" placeholder=\"Group "+formatGroupId+"\" onkeyup=\"checkKeys"+formatGroupId+"()\">\n"
				+ "</div>\n";
		
		out+="<div class=\"formatGroupContent"+formatGroupId+"\">\n";
				
		ArrayList<ArrayList<String>> formats=SQL.query("select products_id,product_name,product_short_name from products;");
		
//		ArrayList<ArrayList<String>> formats=SQL.query("select plant_id,plant_name, count(controlpoints_id) from controlpoints inner join plants on plants.plants_id=controlpoints.plant_id group by plant_name,plant_id;");
//		ArrayList<ArrayList<String>> data = SQL.query("select plant_id,plant_name,controlpoints_id, control_point_name from controlpoints inner join plants on plants.plants_id=controlpoints.plant_id order by plant_name, control_point_name;");

			
		out+="<div class=\"formats\">\n";
		out+="<div class=\"formats"+formatGroupId+"\">\n";
			for(int i=1;i<formats.size();i++){
				out += "<input type=\"checkbox\" name=\"formatCheckBox_"+formatGroupId+"_"+formats.get(i).get(0)+"\" id=\"formatCheckBox_"+formatGroupId+"_"+formats.get(i).get(0)+ "\" value=\"formatCheckBox_"+formatGroupId+"_"+formats.get(i).get(0) + "\">" + formats.get(i).get(1)+"("+formats.get(i).get(2)+")" +"<br>\n";				
			}
		out+="</div>\n";
		out+="</div>\n";
		out+="<br>";
		out+="</div>\n";
		
		return out;
	}


	
	public static String createLocationParameterString(HttpServletRequest request){
		String out="";
//		System.out.println("called createParameterString");
		ArrayList<String> groups=new ArrayList<String>();
		ArrayList<String[]> groupNames=new ArrayList<String[]>();
		ArrayList<String> plants=new ArrayList<String>();
		ArrayList<String> points=new ArrayList<String>();
//		int numOfGroups=1;
		Enumeration<String> en=request.getParameterNames();
		while(en.hasMoreElements()){
			String key=en.nextElement();
//			System.out.println("found key ["+key+"]["+request.getParameter(key)+"]");
			if(key.startsWith("locationGroup_")){
//				groups.add(request.getParameter(key));
			}else if(key.startsWith("locationGroupName")){
				groupNames.add(new String[]{key,request.getParameter(key)});
			}else if(key.startsWith("plantCheckBox_")){
				plants.add(request.getParameter(key));
			}else if(key.startsWith("controlPointCheckBox_")){
				points.add(request.getParameter(key));			
//			}else if(key.equals("numberOfLocationGroups")){
//				try{
//					numOfGroups=Integer.parseInt(request.getParameter(key));
//				}catch(NumberFormatException e){}
			}
		}
		
		for(int g=0;g<groupNames.size();g++){
			//			System.out.println("groupName "+groupNames.get(i)[0]+" "+groupNames.get(i)[1]);
			int group=0;
			try{
				group=Integer.parseInt(groupNames.get(g)[0].substring("locationGroupName".length()));
			}catch(NumberFormatException e){
				continue;
			}
			while(groups.size()<group+1){
				groups.add("Group "+(groups.size()));
			}
			if(groupNames.get(g)[1]!=null&&groupNames.get(g)[1].length()!=0){
				groups.set(group, groupNames.get(g)[1]);
			}else{
				//set empty groupnames with only one selected item to product name
				if(groupNames.get(g)[1].equals("")){
					int foundItems=0;
					String posIdx="";
					String searchPatern="controlPointCheckBox_"+groupNames.get(g)[0].substring("locationGroupName".length())+"_";
//					System.out.println("ggg search for ["+searchPatern+"]");
					for(int j=0;j<points.size();j++){
//						System.out.println("ggg compare with ["+points.get(j)+"]");
						if(points.get(j).startsWith(searchPatern)){
//							System.out.println("ggg match");
							if(foundItems==0){
								posIdx=points.get(j).substring(points.get(j).lastIndexOf("_")+1);
//								System.out.println("ggg now its "+posIdx);
							}
							foundItems++;
						}
					}
					if(foundItems==1){
						//get controlpoint name
						groupNames.get(g)[1]=SQL.getValueOfFieldWithId("controlpoints","control_point_name",posIdx);
//						System.out.println("ggg one item ["+groupNames.get(g)[1]+"]");
					}else{
//						System.out.println("ggg to many");
						foundItems=0;
						posIdx="";
						searchPatern="plantCheckBox_"+groupNames.get(g)[0].substring("locationGroupName".length())+"_";
						for(int j=0;j<plants.size();j++){
							if(plants.get(j).startsWith(searchPatern)){
								if(foundItems==0){
									posIdx=plants.get(j).substring(plants.get(j).lastIndexOf("_")+1);
								}
								foundItems++;
							}
						}
						if(foundItems==1){
							//get plant name
							groupNames.get(g)[1]=SQL.getValueOfFieldWithId("plants","plant_name",posIdx);
						}						
					}
				}
				groups.set(group, groupNames.get(g)[1]);
			}
		}
		
//		System.out.println("no having "+groups.size());
		
		for(int i=0;i<points.size();i++){
//			System.out.println("work with "+points.get(i));
			String point=points.get(i);
			point=point.substring("controlPointCheckBox_".length());
			int group=0;
			try{
				group=Integer.parseInt(point.substring(0,point.indexOf("_")));
			}catch(NumberFormatException e){
				continue;
			}
			while(groups.size()<group+1){
				groups.add("Group "+(groups.size()));
			}
			String last=point.substring(point.lastIndexOf("_")+1);
			groups.set(group,groups.get(group)+"'"+last+"',");			
		}
		
		for(int g=1;g<groups.size();g++){
			out+=groups.get(g);
			if(groups.get(g).contains("'")){
				out=out.substring(0,out.length()-1);
			}else{
				out+="'0'";
			}
		
//			System.out.println(out);
			
//			out+=groups.get(i).substring("group_".length());
//			boolean added=false;
//			for(int j=0;j<points.size();j++){
//				if(points.get(j).startsWith("controlPointCheckBox_"+ groups.get(i).substring("group_".length()) )){
//					System.out.println("points "+points.get(j));
//					String last=points.get(j);
//					points.remove(j);
//					j--;
//					last=last.substring(last.lastIndexOf("_")+1);
//					if(added){
//						out+=",";
//					}
//					out+="'"+last+"'";
//					added=true;
//				}
//			}
			
			out+="|";
//			System.out.println(out);
		}
//		System.out.println("generated "+out);
		out=out.replace("'", "%27");
		out=out.replace("|", "%7C");
		out=out.replace(",", "%2C");
		return out;
	}
	
	public static String createLocationSelection(HttpServletRequest request){
		
//		System.out.println("called createSelection");
		
		String out="";
		
		ArrayList<String> groups=new ArrayList<String>();
		ArrayList<String[]>groupNames=new ArrayList<String[]>();
		ArrayList<String> plants=new ArrayList<String>();
		ArrayList<String> points=new ArrayList<String>();
		int numOfGroups=1;
		Enumeration<String> en=request.getParameterNames();
		while(en.hasMoreElements()){
			String key=en.nextElement();
//			System.out.println(key+" ["+request.getParameter(key)+"]");
			if(key.startsWith("locationGroup_")){
//				System.out.println(key+" = "+request.getParameter(key));
				groups.add(request.getParameter(key));
			}else if(key.startsWith("locationGroupName")){
				groupNames.add(new String[]{key,request.getParameter(key)});
			}else if(key.startsWith("plantCheckBox_")){
//				System.out.println(key+" = "+request.getParameter(key));
				plants.add(request.getParameter(key));
			}else if(key.startsWith("controlPointCheckBox_")){
//				System.out.println(key+" = "+request.getParameter(key));
				points.add(request.getParameter(key));			
			}else if(key.equals("numberOfLocationGroups")){
//				System.out.println(key+" = "+request.getParameter(key));
				try{
					numOfGroups=Integer.parseInt(request.getParameter(key));
				}catch(NumberFormatException e){}
			}
		}
		
		for(int i=1;i<=numOfGroups;i++){
			out+=createLocationGroup(i);
		}
		
		//set empty groupnames with only one selected item to product name
		for(int i=0;i<groupNames.size();i++){
			if(groupNames.get(i)[1]!=null){
				if(groupNames.get(i)[1].equals("")){
					int foundItems=0;
					String posIdx="";
					String searchPatern="controlPointCheckBox_"+groupNames.get(i)[0].substring("locationGroupName".length())+"_";
//					System.out.println("search for ["+searchPatern+"]");
					for(int j=0;j<points.size();j++){
//						System.out.println("compare with ["+points.get(j)+"]");
						if(points.get(j).startsWith(searchPatern)){
//							System.out.println("match!");
							if(foundItems==0){
								posIdx=points.get(j).substring(points.get(j).lastIndexOf("_")+1);
//								System.out.println("now its "+posIdx);
							}
							foundItems++;
						}
					}
					if(foundItems==1){
						//get controlpoint name
						groupNames.get(i)[1]=SQL.getValueOfFieldWithId("controlpoints","control_point_name",posIdx);
//						System.out.println("get the name ["+groupNames.get(i)[1]+"]");
					}else{
//						System.out.println("to many");
						foundItems=0;
						posIdx="";
						searchPatern="plantCheckBox_"+groupNames.get(i)[0].substring("locationGroupName".length())+"_";
						for(int j=0;j<plants.size();j++){
							if(plants.get(j).startsWith(searchPatern)){
								if(foundItems==0){
									posIdx=plants.get(j).substring(plants.get(j).lastIndexOf("_")+1);
								}
								foundItems++;
							}
						}
						if(foundItems==1){
							//get plant name
							groupNames.get(i)[1]=SQL.getValueOfFieldWithId("plants","plant_name",posIdx);
						}						
					}
				}
			}
		}
		
		
//		System.out.println("===================================");
//		System.out.println(out);
//		System.out.println("===================================");

		for(int i=0;i<groupNames.size();i++){
			if(groupNames.get(i)[1]!=null&&groupNames.get(i)[1].length()!=0&&!groupNames.get(i)[1].equals("Group "+groupNames.get(i)[0].substring("locationGroupName".length()))){
				out=out.replace("name=\""+groupNames.get(i)[0]+"\"","name=\""+groupNames.get(i)[0]+"\" value=\""+groupNames.get(i)[1]+"\"");				
			}
		}
		
		for(int i=0;i<groups.size();i++){
//			System.out.println("replaced one for group");
			out=out.replace(groups.get(i)+"\">",groups.get(i)+"\" checked>");
		}
		
		for(int i=0;i<plants.size();i++){
//			System.out.println("replaced one for plant");
			out=out.replace(plants.get(i)+"\">",plants.get(i)+"\" checked>");
		}
		
		for(int i=0;i<points.size();i++){
//			System.out.println("replaced one for point");
			out=out.replace(points.get(i)+"\">",points.get(i)+"\" checked>");
		}
		
//		System.out.println("===================================");
//		System.out.println(out);
//		System.out.println("===================================");

		return out;
	}

	public static String createFormatParameterString(HttpServletRequest request){
		String out="";
//		System.out.println("called createParameterString");
		ArrayList<String> groups=new ArrayList<String>();
		ArrayList<String[]> groupNames=new ArrayList<String[]>();
		ArrayList<String> formats=new ArrayList<String>();

		Enumeration<String> en=request.getParameterNames();
		while(en.hasMoreElements()){
			String key=en.nextElement();
//			System.out.println("found key ["+key+"]["+request.getParameter(key)+"]");
			if(key.startsWith("formatGroup_")){
//				groups.add(request.getParameter(key));
			}else if(key.startsWith("formatGroupName")){
				groupNames.add(new String[]{key,request.getParameter(key)});
			}else if(key.startsWith("formatCheckBox_")){
				formats.add(request.getParameter(key));
			}
		}
		
		for(int i=0;i<groupNames.size();i++){
//			System.out.println("groupName "+groupNames.get(i)[0]+" "+groupNames.get(i)[1]);
			int group=0;
			try{
				group=Integer.parseInt(groupNames.get(i)[0].substring("formatGroupName".length()));
			}catch(NumberFormatException e){
				continue;
			}
			while(groups.size()<group+1){
				groups.add("Group "+(groups.size()));
			}
			if(groupNames.get(i)[1]!=null&&groupNames.get(i)[1].length()!=0){
				groups.set(group, groupNames.get(i)[1]);
			}else{
				//check if only one format is selected and set its name as groupName
				if(groupNames.get(i)[1].equals("")){
					int foundItems=0;
					String posIdx="";
					String searchPatern="formatCheckBox_"+groupNames.get(i)[0].substring("formatGroupName".length())+"_";
					for(int j=0;j<formats.size();j++){
//						System.out.println("Search for ["+searchPatern+"] in ["+formats.get(j)+"]");
						if(formats.get(j).startsWith(searchPatern)){
							if(foundItems==0){
								posIdx=formats.get(j).substring(searchPatern.length());
							}
							foundItems++;
						}
					}
					if(foundItems==1){
//						System.out.println("found only one item-> set to its name");
//						System.out.println("name: ["+posIdx+"]");
						//get product short name
						groupNames.get(i)[1]=SQL.getValueOfFieldWithId("products","product_short_name",posIdx);
					}
				}

				groups.set(group, groupNames.get(i)[1]);
				
			}
		}
		
		
		for(int i=0;i<formats.size();i++){
//			System.out.println("work with "+points.get(i));
			String point=formats.get(i);
			point=point.substring("formatCheckBox_".length());
			int group=0;
			try{
				group=Integer.parseInt(point.substring(0,point.indexOf("_")));
			}catch(NumberFormatException e){
				continue;
			}
			while(groups.size()<group+1){
				groups.add("Group "+(groups.size()));
			}
			String last=point.substring(point.lastIndexOf("_")+1);
			groups.set(group,groups.get(group)+"'"+last+"',");			
		}
		
		for(int i=1;i<groups.size();i++){
			out+=groups.get(i);
			if(groups.get(i).contains("'")){
				out=out.substring(0,out.length()-1);
			}else{
				out+="'0'";
			}
//			System.out.println(out);
			
//			out+=groups.get(i).substring("group_".length());
//			boolean added=false;
//			for(int j=0;j<points.size();j++){
//				if(points.get(j).startsWith("controlPointCheckBox_"+ groups.get(i).substring("group_".length()) )){
//					System.out.println("points "+points.get(j));
//					String last=points.get(j);
//					points.remove(j);
//					j--;
//					last=last.substring(last.lastIndexOf("_")+1);
//					if(added){
//						out+=",";
//					}
//					out+="'"+last+"'";
//					added=true;
//				}
//			}
			
			out+="|";
//			System.out.println(out);
		}
//		System.out.println("generated "+out);
		out=out.replace("'", "%27");
		out=out.replace("|", "%7C");
		out=out.replace(",", "%2C");
		return out;
	}
	

	public static String createFormatSelection(HttpServletRequest request){
		
//		System.out.println("called createSelection");
		
		String out="";
		
		ArrayList<String> groups=new ArrayList<String>();
		ArrayList<String[]>groupNames=new ArrayList<String[]>();
		ArrayList<String> formats=new ArrayList<String>();
		int numOfGroups=1;
		Enumeration<String> en=request.getParameterNames();
		while(en.hasMoreElements()){
			String key=en.nextElement();
//			System.out.println(key+" ["+request.getParameter(key)+"]");
			if(key.startsWith("formatGroup_")){
//				System.out.println(key+" = "+request.getParameter(key));
				groups.add(request.getParameter(key));
			}else if(key.startsWith("formatGroupName")){
				groupNames.add(new String[]{key,request.getParameter(key)});
			}else if(key.startsWith("formatCheckBox_")){
//				System.out.println(key+" = "+request.getParameter(key));
				formats.add(request.getParameter(key));
			}else if(key.equals("numberOfFormatGroups")){
//				System.out.println(key+" = "+request.getParameter(key));
				try{
					numOfGroups=Integer.parseInt(request.getParameter(key));
				}catch(NumberFormatException e){}
			}
		}
		
		for(int i=1;i<=numOfGroups;i++){
			out+=createFormatGroup(i);
		}
		
		//set empty groupnames with only one selected item to product name
		for(int i=0;i<groupNames.size();i++){
			if(groupNames.get(i)[1]!=null){
				if(groupNames.get(i)[1].equals("")){
					int foundItems=0;
					String posIdx="";
					String searchPatern="formatCheckBox_"+groupNames.get(i)[0].substring("formatGroupName".length())+"_";
					for(int j=0;j<formats.size();j++){
//						System.out.println("Search for ["+searchPatern+"] in ["+formats.get(j)+"]");
						if(formats.get(j).startsWith(searchPatern)){
							if(foundItems==0){
								posIdx=formats.get(j).substring(searchPatern.length());
							}
							foundItems++;
						}
					}
					if(foundItems==1){
//						System.out.println("found only one item-> set to its name");
//						System.out.println("name: ["+posIdx+"]");
						//get product short name
						groupNames.get(i)[1]=SQL.getValueOfFieldWithId("products","product_short_name",posIdx);
					}
				}
			}
		}
		
//		System.out.println("===================================");
//		System.out.println(out);
//		System.out.println("===================================");

		//replace standardgroupname with passed groupname
		for(int i=0;i<groupNames.size();i++){
			if(groupNames.get(i)[1]!=null&&groupNames.get(i)[1].length()!=0&&!groupNames.get(i)[1].equals("Group "+groupNames.get(i)[0].substring("formatGroupName".length()))){
				out=out.replace("name=\""+groupNames.get(i)[0]+"\"","name=\""+groupNames.get(i)[0]+"\" value=\""+groupNames.get(i)[1]+"\"");				
			}
		}
		
//		for(int i=0;i<groups.size();i++){
////			System.out.println("replaced one for group");
//			out=out.replace(groups.get(i)+"\">",groups.get(i)+"\" checked>");
//		}
		
		//set selected items
		for(int i=0;i<formats.size();i++){
//			System.out.println("replaced one for plant");
			out=out.replace(formats.get(i)+"\">",formats.get(i)+"\" checked>");
		}
		
//		System.out.println("===================================");
//		System.out.println(out);
//		System.out.println("===================================");

		return out;
	}
}
