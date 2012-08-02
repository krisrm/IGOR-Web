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

public class CopyOfXmlUploadServlet extends UploadAction {

	private static final long serialVersionUID = -7154805865105663883L;
	Hashtable<String, String> receivedContentTypes = new Hashtable<String, String>();
	/**
	 * Maintain a list with received files and their content types.
	 */
	Hashtable<String, File> receivedFiles = new Hashtable<String, File>();

	/**
	 * Override executeAction to save the received files in a custom place and
	 * delete this items from session.
	 */
	@Override
	public String executeAction(HttpServletRequest request,
			List<FileItem> sessionFiles) throws UploadActionException {
		String sess = getThreadLocalRequest().getSession().getId();
		User u = ModelServiceImpl.currentUser(this.getServletContext(), sess);
		if (u == null)
			return null;
		String response = "";
		int cont = 0;
		for (FileItem item : sessionFiles) {
			if (! item.isFormField()) {
				cont++;
				try {
					u.upload = item.get();
					ModelServiceImpl.saveUser(getServletContext(), u, sess);
					response = "";
					
//					String root = ModelServiceImpl.getPath("xml/",getServletContext());
//					
//					String id = new File(root).listFiles().length + "-" + ((int)(Math.random() * 10000));
//					String filename = root + item.getName()+"-"+id;
//					File file = new File(filename);
//					item.write(file);
//					
//					
//					// / Save a list with the received files
//					receivedFiles.put(item.getFieldName(), file);
//					receivedContentTypes.put(item.getFieldName(),
//							item.getContentType());
//
//					// / Send a customized message to the client.
//					response = filename;

				} catch (Exception e) {
					e.printStackTrace();
					throw new UploadActionException(e);
				}
			}
		}

		// / Remove files from session because we have a copy of them
		removeSessionFileItems(request);

		// / Send your customized message to the client.
		return response;
	}

	/**
	 * Get the content of an uploaded file.
	 */
	@Override
	public void getUploadedFile(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String fieldName = request.getParameter(PARAM_SHOW);
		File f = receivedFiles.get(fieldName);
		if (f != null) {
			response.setContentType(receivedContentTypes.get(fieldName));
			FileInputStream is = new FileInputStream(f);
			copyFromInputStreamToOutputStream(is, response.getOutputStream());
		} else {
			renderXmlResponse(request, response, ERROR_ITEM_NOT_FOUND);
		}
	}

	/**
	 * Remove a file when the user sends a delete request.
	 */
	@Override
	public void removeItem(HttpServletRequest request, String fieldName)
			throws UploadActionException {
		File file = receivedFiles.get(fieldName);
		receivedFiles.remove(fieldName);
		receivedContentTypes.remove(fieldName);
		if (file != null) {
			file.delete();
		}
	}
}