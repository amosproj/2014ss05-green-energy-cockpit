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

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class bookmark
 */
public class bookmark extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public bookmark() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * generates a dummy page with hidden inputs to build a request matching the bookmarked page and redirct to it
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id=request.getParameter("id");
//		System.out.println("called bookmark with id "+id);
		
		//if no id is passed redirect to startpage
		if(id!=null&&id.length()>0){
			ResultSet rs=SQL.queryToResultSet("SELECT * from bookmarks where bookmarks_id='"+id+"';");
			
			if(rs!=null){
				try {
					if(rs.next()){
						
						response.setContentType( "text/html" );
					    PrintWriter out = response.getWriter();
					    
					    out.println("<!DOCTYPE html>");
					    out.println("<html>");
					    out.println("<head>");
					    out.println("</head>");
					    out.println("<body onload=\"func()\">");
					    out.println("<script>");
					    out.println("function func(){");
					    out.println("document.forms[\"myForm\"].submit();");
					    out.println("}");
					    out.println("</script>");
					    out.println("<form id=\"myForm\" method=\"post\" action=\""+rs.getString(4)+"\">");
				
					    String params=rs.getString(5);
					    //System.out.println("params String: "+params);
					    String[] pairs=params.split(BookmarkGenerator.PAIR_SEPARATOR);
					    for(int i=0;i<pairs.length;i++){
					    	
					    	//System.out.println("work with "+pairs[i]);
					   
					    	String[] keyValue=pairs[i].split(BookmarkGenerator.KEY_SEPARATOR);
					    	//System.out.println("split to "+keyValue.length+" elements");
					    	//for(int j=0;j<keyValue.length;j++){
					    		//System.out.println("["+j+"]: "+keyValue[j]);
					    	//}
					    	out.println("<input type\"hidden\" name=\""+keyValue[0]+"\" value=\""+(keyValue.length>1?keyValue[1]:"")+"\">");
					    }
					    out.println("</form>");
					    out.println("</body>");

						return;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	
		response.sendRedirect("");
		
	}

}
