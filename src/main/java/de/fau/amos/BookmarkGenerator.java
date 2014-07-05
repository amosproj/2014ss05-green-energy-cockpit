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
