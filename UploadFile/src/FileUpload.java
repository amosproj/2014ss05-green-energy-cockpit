
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


@WebServlet("/FileUpload")
public class FileUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	String UPLOAD_DIRECTORY = "C:/";
	String relativePath = "/UploadFile/WEB-INF/uploads";
	String absolutePath = getServletContext().getRealPath(relativePath);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FileUpload() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		
		if (ServletFileUpload.isMultipartContent(request)) {
			try {
				List<FileItem> list = new ServletFileUpload(
						new DiskFileItemFactory()).parseRequest(request);

				for (FileItem fi : list) {
					if (!fi.isFormField()) {
						String name = new File(fi.getName()).getName();
						fi.write(new File(UPLOAD_DIRECTORY + File.separator
								+ name));
					}
				}

				request.setAttribute("message", "File uploaded successfully");
			} catch (Exception e) {
				request.setAttribute("message", "File upload failed: "
						+ e);
			}

		} else {
			request.setAttribute("message",
					"Something went wrong.");
		}

		request.getRequestDispatcher("/result.jsp").forward(request, response);
		

	}

}
