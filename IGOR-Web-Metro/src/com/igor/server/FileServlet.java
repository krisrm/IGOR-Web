package com.igor.server;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.igor.shared.User;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

public class FileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int BUFSIZE = 4096;

	private enum Type {
		IMAGE, DOWNLOAD, GENERATE
	}


	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		User u = ModelServiceImpl.currentUser(this.getServletContext(), request.getSession().getId());
		if (u == null){
			response.sendError(401);
			return;
		}
		String typeParam = request.getParameter("type");
		if (typeParam == null) {
			response.sendError(500);
			return;
		}
		Type type = Type.valueOf(typeParam.toUpperCase());
		if (type == null) {
			response.sendError(500);
			return;
		}

		switch (type) {
		case IMAGE:
			String id = request.getParameter("id");
			writeResponse(response, MediaUploadServlet.IMAGES + id, false);
			break;
		case DOWNLOAD:
			writeResponse(response, u.download, "application/xml", "model.xml");
			break;
		case GENERATE:
			writeResponse(response, u.generate, "text/html", "generated.html");
			break;
		}

	}

	public void writeResponse(HttpServletResponse response, byte[] data,
			String mimetype, String fileName) throws IOException {
		int length = 0;
		ServletOutputStream outStream = response.getOutputStream();

		if (mimetype == null) {
			mimetype = "application/octet-stream";
		}
		response.setContentType(mimetype);
		response.setContentLength(data.length);

		// sets HTTP header
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + "\"");

		byte[] byteBuffer = new byte[BUFSIZE];
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

		// reads the file's bytes and writes them to the response stream
		while ((in != null) && ((length = in.read(byteBuffer)) != -1)) {
			outStream.write(byteBuffer, 0, length);
		}

		in.close();
		outStream.close();
	}

	public void writeResponse(HttpServletResponse response, String filePath,
			boolean download) throws IOException {
		filePath = getServletContext().getRealPath("")+ File.separatorChar + filePath;
		File file = new File(filePath);
		int length = 0;
		ServletOutputStream outStream = response.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(filePath);

		// sets response content type
		if (mimetype == null) {
			mimetype = "application/octet-stream";
		}
		response.setContentType(mimetype);
		response.setContentLength((int) file.length());
		String fileName = (new File(filePath)).getName();

		// sets HTTP header
		if (download) {
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");
		} else {
			response.setHeader("Content-Disposition", "");
		}

		byte[] byteBuffer = new byte[BUFSIZE];
		DataInputStream in = new DataInputStream(new FileInputStream(file));

		// reads the file's bytes and writes them to the response stream
		while ((in != null) && ((length = in.read(byteBuffer)) != -1)) {
			outStream.write(byteBuffer, 0, length);
		}

		in.close();
		outStream.close();
	}

}
