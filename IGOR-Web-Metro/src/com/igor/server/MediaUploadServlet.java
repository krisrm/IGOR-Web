package com.igor.server;

import gwtupload.server.UploadAction;
import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.igor.shared.User;

public class MediaUploadServlet extends FileUploadServlet {
	private static final long serialVersionUID = 2263279229981253738L;
	public static final String IMAGES = "images/uploads/";
	@Override
	public String handleUpload(HttpServletRequest req, HttpServletResponse resp,
			FileItem item) {
		resp.setContentType("text/plain");
		User u = ModelServiceImpl.currentUser(this.getServletContext(), req
				.getSession().getId());
		if (u == null)
			return null;
		String response = "";
		int cont = 0;
		cont++;
		String root = ModelServiceImpl.getPath(IMAGES, getServletContext());

		String id = new File(root).listFiles().length + ""
				+ ((int) (Math.random() * 10000));
		String filename = root + File.separatorChar + id;
		File file = new File(filename);
		try {
			item.write(file);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		response = "file?type=image&id=" + id;
		return response;
	}

	// public static final String IMAGES = "images/uploads/";
	// private static final long serialVersionUID = -7154805865105663883L;
	// Hashtable<String, String> receivedContentTypes = new Hashtable<String,
	// String>();
	// /**
	// * Maintain a list with received files and their content types.
	// */
	// Hashtable<String, File> receivedFiles = new Hashtable<String, File>();
	//
	// /**
	// * Override executeAction to save the received files in a custom place and
	// * delete this items from session.
	// */
	// @Override
	// public String executeAction(HttpServletRequest request,
	// List<FileItem> sessionFiles) throws UploadActionException {
	// System.out.println("STARTING UPLOAD");
	// User u = ModelServiceImpl.currentUser(this.getServletContext(),
	// getThreadLocalRequest().getSession().getId());
	// if (u == null)
	// return null;
	// String response = "";
	// int cont = 0;
	// try {
	// for (FileItem item : sessionFiles) {
	// if (!item.isFormField()) {
	// cont++;
	// String root = ModelServiceImpl.getPath(IMAGES,
	// getServletContext());
	//
	// String id = new File(root).listFiles().length + ""
	// + ((int) (Math.random() * 10000));
	// String filename = root + id;
	// System.out.println(filename);
	// File file = new File(filename);
	// item.write(file);
	//
	// // / Save a list with the received files
	// receivedFiles.put(item.getFieldName(), file);
	// receivedContentTypes.put(item.getFieldName(),
	// item.getContentType());
	//
	// response = "file?type=image&id=" + id;
	//
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new UploadActionException(e);
	// }
	//
	// // / Remove files from session because we have a copy of them
	// removeSessionFileItems(request);
	//
	// // / Send your customized message to the client.
	// return response;
	// }
	//
	// /**
	// * Get the content of an uploaded file.
	// */
	// @Override
	// public void getUploadedFile(HttpServletRequest request,
	// HttpServletResponse response) throws IOException {
	// String fieldName = request.getParameter(PARAM_SHOW);
	// File f = receivedFiles.get(fieldName);
	// if (f != null) {
	// response.setContentType(receivedContentTypes.get(fieldName));
	// FileInputStream is = new FileInputStream(f);
	// copyFromInputStreamToOutputStream(is, response.getOutputStream());
	// } else {
	// renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
	// }
	// }
	//
	// /**
	// * Remove a file when the user sends a delete request.
	// */
	// @Override
	// public void removeItem(HttpServletRequest request, String fieldName)
	// throws UploadActionException {
	// File file = receivedFiles.get(fieldName);
	// receivedFiles.remove(fieldName);
	// receivedContentTypes.remove(fieldName);
	// if (file != null) {
	// file.delete();
	// }
	// }
}