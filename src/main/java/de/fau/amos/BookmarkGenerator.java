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
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class BookmarkGenerator {

	public static final String PAIR_SEPARATOR="<-##->";
	public static final String KEY_SEPARATOR=">-##-<";
	
	/**
	 * created a bookmark and stores it in the database. Bookmark contains all needed request parameters to
	 * call the requested page again with the given settings.
	 * 
	 * @param request original request that called the page the user wants to bookmark
	 * @param username the username of the user bookmarking the page
	 * @return bookmarkurl or "FAILED" if bookmarking failed
	 */
	public static String createBookmark(HttpServletRequest request,String username){
		
		if(request==null){
			return "FAILED";
		}
		
		//build paramsString that should be saved
		String params="";
		Enumeration<String> en=request.getParameterNames();
		while(en.hasMoreElements()){
			String key=en.nextElement();
			
			//don't save parameter "generateBookmark", it's for generating the bookmark
			if(!key.equals("generateBookmark")){
//				System.out.println("["+key+"]: ["+request.getParameter(key)+"]");
				params+=key+KEY_SEPARATOR+request.getParameter(key)+PAIR_SEPARATOR;
			}
		}
		
		//create table to store bookmarks
		SQL.execute(
				"CREATE TABLE bookmarks (bookmarks_id serial primary key,created_by character varying(50), "
				+ "created_at timestamp NOT NULL, url text NOT NULL, params text NOT NULL);"
				);
		

		//get url
		String url=request.getRequestURL().toString().replace("/WEB-INF/","/");
		
		//build link for bookmark
		String link=url;
		String context=request.getContextPath();
		while(link.length()>0&&!link.endsWith(context)){
			link=link.substring(0,link.length()-1);
		}
		link+="/bookmark";
		
		//get id for bookmarklink
		ArrayList<ArrayList<String>>arr=SQL.query("SELECT nextval('bookmarks_bookmarks_id_seq');");
		if(arr==null){
			return "FAILED";
		}
		
		try{
			int id=Integer.parseInt(arr.get(1).get(0))+1;
			link+="?id="+id;
		}catch(NumberFormatException e){
			return "FAILED";
		}
		
		//save bookmark
		Calendar c=Calendar.getInstance();
		String cal=TimestampConversion.convertTimestamp(c);
		SQL.execute(
				"INSERT INTO bookmarks (created_by,created_at,url,params) VALUES ('"
				+ username
				+ "','"
				+ cal
				+ "','"
				+ url
				+ "','"
				+ params
				+ "');"
				 
				);
		
//		System.out.println("finished");
//		System.out.println("===========================");
		return link;
	}
}
