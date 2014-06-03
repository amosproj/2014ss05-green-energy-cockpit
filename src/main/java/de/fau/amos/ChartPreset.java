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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class ChartPreset {
	

//	private static int g = 1;	
//	
//	public static String createPreset(int groupId){
//		
//		String preset = " <div class=\"group\">"
//				+ "<input type=\"checkbox\" name=\"group" + groupId + "\" value=\"group" + groupId + "\"> Group " + groupId + "<br>"
//				+ "</div><br>";
//		
//		ArrayList<ArrayList<String>> id = SQL.querry("select controlpoints_id from controlpoints;");
//		ArrayList<ArrayList<String>> name = SQL.querry("select control_point_name from controlpoints;");
//		
//		preset += "<div class=\"measure_points\"><br>";
//		
//		for(int i=1; i < id.size(); i++ ){
//			preset += "<input type=\"checkbox\" name=\"" + name.get(i) + "\" value=" + id.get(i) + "> " + name.get(i) + " \n";
//		}
//		preset += "</div><br>";
//		
////		g++;
//		
//		return preset;
//	}
//	
//	public static String addGroup(){
//		
//		String add = "<br>" + 
//				" <div class=\"group\">"
//				+ "<input type=\"checkbox\" name=\"group" + g + "\" value=\"group" + g + "\"> Group " + g + " 	"
//				+ "</div>";
//		
//		ArrayList<ArrayList<String>> id = SQL.querry("select controlpoints_id from controlpoints;");
//		ArrayList<ArrayList<String>> name = SQL.querry("select control_point_name from controlpoints;");
//		
//		add += "<div class=\"measure_points\">";
//		
//		for(int i=1; i < id.size(); i++ ){
//			add += "<input type=\"checkbox\" name=\"" + name.get(i) + "\" value=" + id.get(i) + "> " + name.get(i) + " <br>";
//		}
//		add += "</div>";
//		
//		//g++;
//		
//		return add;
//	}


	public static String createGroup(int groupId){
		
//		System.out.println("called group with "+groupId);
		String out = "";		

		out+="<script type=\"text/javascript\">\n"
				+"$(document).ready(function() {\n"
				+"$(\"#groupButtonUp"+groupId+"\").click(function() {\n"
				+"$(\".groupContent"+groupId+"\").slideDown(\"fast\");\n"
				+"$(\"#groupButtonDown"+groupId+"\").show();\n"
				+"$(\"#groupButtonUp"+groupId+"\").hide();\n"
				+"});\n"
				+"$(\"#groupButtonDown"+groupId+"\").click(function() {\n"
				+"$(\".groupContent"+groupId+"\").slideUp(\"fast\");\n"
				+"$(\"#groupButtonUp"+groupId+"\").show();\n"
				+"$(\"#groupButtonDown"+groupId+"\").hide();\n"
				+"});\n"
				+"});\n"
				+"</script>\n";
		
		out+=" <div class=\"group"+groupId+"\">\n"
				+"<img id=\"groupButtonUp"+groupId+"\" src=\"../images/up.png\" style=\"display: none;\">\n"
				+"<img id=\"groupButtonDown"+groupId+"\" src=\"../images/down.png\">\n"
				//+ "<input type=\"checkbox\" name=\"group_" + groupId + "\" value=\"group_" + groupId + "\" onclick=\"javascript:showHide"+groupId+"()\" checked>Group " + groupId +"<br>\n"
//				+"Group "+groupId
				+"<input type=\"text\" name=\"groupName"+groupId+"\" placeholder=\"Group "+groupId+"\">\n"
				+ "</div>\n";
		
		out+="<div class=\"groupContent"+groupId+"\">\n";
				
		ArrayList<ArrayList<String>> plants=SQL.query("select plant_id,plant_name, count(controlpoints_id) from controlpoints inner join plants on plants.plants_id=controlpoints.plant_id group by plant_name,plant_id;");
		ArrayList<ArrayList<String>> data = SQL.query("select plant_id,plant_name,controlpoints_id, control_point_name from controlpoints inner join plants on plants.plants_id=controlpoints.plant_id order by plant_name, control_point_name;");
		//number of plants
		int j=1;
		for(int i=1; i < plants.size(); i++ ){
			//for each plant
			out += "<div class=\"plants\">\n";
			out += "<div class=\"plants"+groupId+"_"+plants.get(i).get(0)+"\">\n";
//			out+="<p style=\"margin-left: 10px;\">\n";
			out+="<img id=\"plantButtonUp"+groupId+"_"+plants.get(i).get(0)+"\" src=\"../images/up.png\" style=\"display: none;\">\n";
			out+="<img id=\"plantButtonDown"+groupId+"_"+plants.get(i).get(0)+"\" src=\"../images/down.png\">\n";
			out +="<input type=\"checkbox\" name=\"plantCheckBox_"+groupId+"_"+plants.get(i).get(0)+"\" id=\"plantCheckBox_"+groupId+"_"+plants.get(i).get(0)+"\" value=\"plantCheckBox_"+groupId+"_"+plants.get(i).get(0)+"\">";//+plants.get(i).get(1)+"<br>\n";
			out +="<div class=\"label_plant_"+groupId+"_"+plants.get(i).get(0)+"\" style=\"display:inline\">"+plants.get(i).get(1)+"</div><br>\n";
			//			out += "</p>";
			out+="</div>\n";
			out+="</div>\n";
			
			out+="<script type=\"text/javascript\">\n"
					+"$(document).ready(function() {\n"

					+"$(\"#plantButtonUp"+groupId+"_"+plants.get(i).get(0)+"\").click(function() {\n"
					+"$(\".controlpoints"+groupId+"_"+plants.get(i).get(0)+"\").slideDown(\"fast\");\n"
					+"$(\"#plantButtonDown"+groupId+"_"+plants.get(i).get(0)+"\").show();\n"
					+"$(\"#plantButtonUp"+groupId+"_"+plants.get(i).get(0)+"\").hide();\n"
					+"});\n"
					+"$(\"#plantButtonDown"+groupId+"_"+plants.get(i).get(0)+"\").click(function() {\n"
					+"$(\".controlpoints"+groupId+"_"+plants.get(i).get(0)+"\").slideUp(\"fast\");\n"
					+"$(\"#plantButtonUp"+groupId+"_"+plants.get(i).get(0)+"\").show();\n"
					+"$(\"#plantButtonDown"+groupId+"_"+plants.get(i).get(0)+"\").hide();\n"
					+"});\n"

					
//					+"$(\".label_plant_"+groupId+"_"+plants.get(i).get(0)+"\").click(function() {\n"		
//					+"$(\".controlpoints"+groupId+"_"+plants.get(i).get(0)+"\").slideToggle(\"fast\");\n"
//					+"});\n"
					
					+"$('#plantCheckBox_"+groupId+"_"+plants.get(i).get(0)+"').click(function() {\n"
					+"if($(\"#plantCheckBox_"+groupId+"_"+plants.get(i).get(0)+"\").is(':checked')){\n"
					+"$(\".controlpoints"+groupId+"_"+plants.get(i).get(0)+" input[type='checkbox']\").prop(\"checked\",true);\n"						
					+"}else{\n"
					+"$(\".controlpoints"+groupId+"_"+plants.get(i).get(0)+" input[type='checkbox']\").prop(\"checked\",false);\n"						
					+"}\n"
					+"});\n"

					+"});\n"		
					+"</script>\n";

			
			int max=(j+Integer.parseInt(plants.get(i).get(2)));
			out+="<div class=\"controlpoints\">\n";
			out+="<div class=\"controlpoints"+groupId+"_"+plants.get(i).get(0)+"\">\n";
//			out+="<p style=\"margin-left: 20px;\">\n";
			for(; j < max;j++){
				out += "<input type=\"checkbox\" name=\"controlPointCheckBox_"+groupId+"_"+plants.get(i).get(0)+"_"+data.get(j).get(2) + "\" id=\"controlPointCheckBox_"+groupId+"_"+plants.get(i).get(0)+"_"+data.get(j).get(2) + "\" value=\"controlPointCheckBox_"+groupId+"_"+plants.get(i).get(0)+"_"+data.get(j).get(2) + "\">" + data.get(j).get(3) +"<br>\n";				
			}
//			out+="</p>\n";
			out+="</div>\n";
			out+="</div>\n";
		}
		
		out+="</div>\n";
		out+="<br>";
		
		return out;
	}

	public static String createParameterString(HttpServletRequest request){
		String out="";
//		System.out.println("called createParameterString");
		ArrayList<String> groups=new ArrayList<String>();
		ArrayList<String[]> groupNames=new ArrayList<String[]>();
		ArrayList<String> plants=new ArrayList<String>();
		ArrayList<String> points=new ArrayList<String>();
		int numOfGroups=1;
		Enumeration<String> en=request.getParameterNames();
		while(en.hasMoreElements()){
			String key=en.nextElement();
//			System.out.println("found key ["+key+"]["+request.getParameter(key)+"]");
			if(key.startsWith("group_")){
//				groups.add(request.getParameter(key));
			}else if(key.startsWith("groupName")){
				groupNames.add(new String[]{key,request.getParameter(key)});
			}else if(key.startsWith("plantCheckBox_")){
				plants.add(request.getParameter(key));
			}else if(key.startsWith("controlPointCheckBox_")){
				points.add(request.getParameter(key));			
			}else if(key.equals("numberOfGroups")){
				try{
					numOfGroups=Integer.parseInt(request.getParameter(key));
				}catch(NumberFormatException e){}
			}
		}
		
		for(int i=0;i<groupNames.size();i++){
//			System.out.println("groupName "+groupNames.get(i)[0]+" "+groupNames.get(i)[1]);
			int group=0;
			try{
				group=Integer.parseInt(groupNames.get(i)[0].substring("groupName".length()));
			}catch(NumberFormatException e){
				continue;
			}
			while(groups.size()<group+1){
				groups.add(""+(groups.size()));
			}
			if(groupNames.get(i)[1]!=null&&groupNames.get(i)[1].length()!=0){
				groups.set(group, groupNames.get(i)[1]);
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
				groups.add(""+(groups.size()));
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
		System.out.println("generated "+out);
		out=out.replace("'", "%27");
		out=out.replace("|", "%7C");
		out=out.replace(",", "%2C");
		return out;
	}
	
	public static String createSelection(HttpServletRequest request){
		
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
			if(key.startsWith("group_")){
//				System.out.println(key+" = "+request.getParameter(key));
				groups.add(request.getParameter(key));
			}else if(key.startsWith("groupName")){
				groupNames.add(new String[]{key,request.getParameter(key)});
			}else if(key.startsWith("plantCheckBox_")){
//				System.out.println(key+" = "+request.getParameter(key));
				plants.add(request.getParameter(key));
			}else if(key.startsWith("controlPointCheckBox_")){
//				System.out.println(key+" = "+request.getParameter(key));
				points.add(request.getParameter(key));			
			}else if(key.equals("numberOfGroups")){
//				System.out.println(key+" = "+request.getParameter(key));
				try{
					numOfGroups=Integer.parseInt(request.getParameter(key));
				}catch(NumberFormatException e){}
			}
		}
		
		for(int i=1;i<=numOfGroups;i++){
			out+=createGroup(i);
		}
		
//		System.out.println("===================================");
//		System.out.println(out);
//		System.out.println("===================================");

		for(int i=0;i<groupNames.size();i++){
			if(groupNames.get(i)[1]!=null&&groupNames.get(i)[1].length()!=0&&!groupNames.get(i)[1].equals("Group "+groupNames.get(i)[0].substring("groupName".length()))){
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
}
