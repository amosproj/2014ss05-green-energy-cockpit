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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id=request.getParameter("id");
		System.out.println("called bookmark with id "+id);
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
