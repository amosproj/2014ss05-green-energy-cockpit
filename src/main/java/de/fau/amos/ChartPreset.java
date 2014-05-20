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

public class ChartPreset {
	

	private static int g;

		
	
	public static String createPreset(){
		g = 1;
		
		String preset = " <div class=\"group\">"
				+ "<input type=\"checkbox\" name=\"group" + g + "\" value=\"group" + g + "\"> Group " + g + " 	"
				+ "</div>";
		
		ArrayList<ArrayList<String>> id = SQL.querry("select controlpoints_id from controlpoints;");
		ArrayList<ArrayList<String>> name = SQL.querry("select control_point_name from controlpoints;");
		
		preset += "<div class=\"measure_points\">";
		
		for(int i=0; i < id.size(); i++ ){
			preset += "<input type=\"checkbox\" name=\"" + name.get(i) + "\" value=" + id.get(i) + "> " + name.get(i) + " <br>";
		}
		preset += "</div>";
		
		g++;
		
		return preset;
	}
	
	public static String addGroup(){
		
		String add = "<br>" + 
				" <div class=\"group\">"
				+ "<input type=\"checkbox\" name=\"group" + g + "\" value=\"group" + g + "\"> Group " + g + " 	"
				+ "</div>";
		
		ArrayList<ArrayList<String>> id = SQL.querry("select controlpoints_id from controlpoints;");
		ArrayList<ArrayList<String>> name = SQL.querry("select control_point_name from controlpoints;");
		
		add += "<div class=\"measure_points\">";
		
		for(int i=0; i < id.size(); i++ ){
			add += "<input type=\"checkbox\" name=\"" + name.get(i) + "\" value=" + id.get(i) + "> " + name.get(i) + " <br>";
		}
		add += "</div>";
		
		g++;
	}

}
