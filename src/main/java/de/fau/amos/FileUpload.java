package de.fau.amos;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet("/FileUpload/*")
public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
//	String UPLOAD_DIRECTORY = "C:/";
//	String relativePath = "/UploadFile/WEB-INF/uploads";
//	String absolutePath = getServletContext().getRealPath(relativePath);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUpload() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
			
		String pathInfo=request.getPathInfo();
		System.out.println("called fileupload: "+pathInfo);
		boolean isImport=pathInfo!=null&&pathInfo.startsWith("/import");
	
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				List<FileItem> list = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);

				for (FileItem fi : list) {
					if (!fi.isFormField()) {
						String rand="";
						for(int i=0;i<6;i++){
							rand+=(int)(Math.random()*10);
						}
						String plantId=request.getPathInfo().replace("/import","");
						if(plantId==null||plantId.length()==0){
							plantId="";
//						}else{
//							plantId="_"+plantId;
						}
						String name = new File(rand+"_"+fi.getName()).getName()+(isImport?"_imp"+plantId:"");
						
						File folder=null;
						if(isImport){
							folder=new File(System.getProperty("userdir.location"),"import");
						}else{
							folder=new File(System.getProperty("userdir.location"),"uploads");
						}
						if(!folder.exists()){
							folder.mkdirs();
						}
						fi.write(new File(folder,name));
					}
				}

				request.setAttribute("message", "File uploaded successfully");
			} catch (Exception e) {
				request.setAttribute("errorMessage", "File upload failed!");
			}

		} else {
			request.setAttribute("errorMessage",
					"Something went wrong.");
		}
		

		if(isImport){
//			request.setAttribute("plant",request.getPathInfo().replace("/import",""));
//			request.getRequestDispatcher("/intern/import.jsp").forward(request, response);
			response.sendRedirect(request.getContextPath()+"/intern/import.jsp");			
		}else{
			response.sendRedirect(request.getContextPath()+"/intern/funktion3.jsp");
		}
		

	}

}
