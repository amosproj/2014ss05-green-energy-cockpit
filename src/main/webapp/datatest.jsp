<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="de.fau.amos.*"%>
<%@ page import="test.*"%>
<%@ page import="java.util.ArrayList"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>


<% System.out.println("started");

ArrayList<ArrayList<String>> data=SQL.querry(getServletConfig(),"SELECT * FROM Dresden;");
out.println("<table>");

for(int i=0;i<data.size();i++){
	out.println("<tr>");
	for(int j=0;j<data.get(j).size();j++){
		out.println("<td>");
		out.println(data.get(i).get(j));
		out.println("</td>");
		
	}
	out.println("</tr>");
}

out.println("</table>");







System.out.println(data.size());
out.println("");

System.out.println("finished what ever");

%>


</body>
</html>