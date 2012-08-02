package com.igor.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import com.igor.shared.User;

public class XmlUploadServlet extends FileUploadServlet {
	private static final long serialVersionUID = 7038339957077888410L;

	@Override
	public String handleUpload(HttpServletRequest req,
			HttpServletResponse resp, FileItem item) {
		String sess = req.getSession().getId();
		User u = ModelServiceImpl.currentUser(this.getServletContext(), sess);
		if (u == null)
			return null;
		u.upload = item.get();
		ModelServiceImpl.saveUser(getServletContext(), u, sess);
		return "";
	}

}