<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="de.fau.amos.Const"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
<%

	System.out.println("called publicLogin "+request.getPathInfo());

	String pathInfo=request.getPathInfo();
	if(pathInfo==null){
		response.sendRedirect(request.getContextPath());
	}else if(pathInfo.equals("/reg")){		
		request.getRequestDispatcher(Const.URL.REGISTRTION_PAGE).forward(request,response);		
	}else if(pathInfo.equals("/regJsp")){		
		request.getRequestDispatcher(Const.URL.REGISTRATION_JSP).forward(request,response);
	}else if(pathInfo.equals("/change")){
		
	}else if(pathInfo.equals("/changeJsp")){		
		
	}else if(pathInfo.equals("/logout")){
		System.out.println("logout");
		session.removeAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME);
		session.removeAttribute(Const.SessionAttributs.LoginState.NAME);
		response.sendRedirect(request.getContextPath()+Const.URL.LOGIN_PAGE);
	}else if(pathInfo.equals("/login")){
		
		System.out.println("want to login");
		//is a user logged in to this session?
		if((Const.SessionAttributs.LoginState.Valeus.LOGGED_IN.equals(session.getAttribute(Const.SessionAttributs.LoginState.NAME)))
			&&(request.getParameter(Const.RequestParameters.LOGIN_USERNAME).equals(session.getAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME)))){
			//user is logged in -> can forward
			//System.out.println("if1 (session valid) want to load "+request.getRequestURL());
			System.out.println("allready logged in");
			response.sendRedirect(request.getContextPath()+Const.URL.INTERN_HOME);
			//request.getRequestDispatcher(request.getContextPath()+"/intern/forward").forward(request,response);
				//request.getRequestDispatcher("/WEB-INF/intern"+forwardURL).forward(request,response);
		}else{
			//no user logged in
			if(request.getParameter(Const.RequestParameters.COMING_FROM_LOGINPAGE)!=null){
				//coming from loginPage -> forward to login.jsp
				System.out.println("coming from index");
				request.getRequestDispatcher(Const.URL.LOGIN_JSP).forward(request,response);
			}else{
				System.out.println("coming form nowhere");
				//forward to loginPage
				session.removeAttribute(Const.SessionAttributs.LOGGED_IN_USERNAME);
				session.removeAttribute(Const.SessionAttributs.LoginState.NAME);
				request.getRequestDispatcher(request.getContextPath()+Const.URL.LOGIN_PAGE).forward(request,response);
			}
			
			
		}
	}
%>
</body>
</html>