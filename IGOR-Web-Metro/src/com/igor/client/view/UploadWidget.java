package com.igor.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

public class UploadWidget extends Composite {
	private FormPanel main = new FormPanel();

	public UploadWidget(HasClickHandlers button, String url) {
		main.setAction(GWT.getModuleBaseURL() + url);
		main.setEncoding(FormPanel.ENCODING_MULTIPART);
		main.setMethod(FormPanel.METHOD_POST);
		initWidget(main);
		final FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");
		main.add(upload);
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				jsClickUpload(upload.getElement());
			}
		});
		upload.setVisible(false);
		upload.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				main.submit();
			}
		});
	}

	public void addOnFinishedHandler(SubmitCompleteHandler handler) {
		main.addSubmitCompleteHandler(handler);
	}

	native void jsClickUpload(Element pElement) /*-{
		pElement.click();
	}-*/;
}
