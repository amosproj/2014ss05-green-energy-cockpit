package de.fau.amos;

public class Header {

	public static String getHeaderButtons(){
		String out="";
				
		out+="<a href="+'"'+
				"funktion1.jsp"
				+'"'+"><li>"+
				"Funktion #1"
				+"</li></a>";
		out+="<a href="+'"'+
				"funktion2.jsp"
				+'"'+"><li>"+
				"Charts"
				+"</li></a>";
		out+="<a href="+'"'+
				"#"
				+'"'+"><li>"+
				"Funktion #3"
				+"</li></a>";
		out+="<a href="+'"'+
				"setup.jsp"
				+'"'+"><li>"+
				"Setup"
				+"</li></a>";
		out+="<a href="+'"'+
				"../login/logout"
				+'"'+"><li>"+
				"Logout"
				+"</li></a>";
		
		return out;
	}
}
