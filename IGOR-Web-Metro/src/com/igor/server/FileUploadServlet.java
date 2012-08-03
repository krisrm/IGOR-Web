package com.igor.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

public abstract class FileUploadServlet extends HttpServlet {
	private static final long serialVersionUID = -2541246915650292424L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			List<FileItem> items = upload.parseRequest(req);
			for (FileItem item : items) {
				if (item.isFormField())
					continue;
				String r = handleUpload(req, resp, item);
				resp.getWriter().print(r);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resp.sendError(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"An error occurred while creating the file : "
							+ e.getMessage());
		}

	}

	public abstract String handleUpload(HttpServletRequest req,
			HttpServletResponse resp, FileItem item);

}