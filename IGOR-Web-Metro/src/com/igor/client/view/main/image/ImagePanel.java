package com.igor.client.view.main.image;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.igor.client.view.UploadWidget;
import com.igor.client.view.main.MainView;

public class ImagePanel extends Composite implements IGORImage.CanSelect{

	private HorizontalPanel imagesPanel = new HorizontalPanel();
	private LayoutPanel mainPanel = new LayoutPanel();
	private Button addBtn = new Button("+");
	private Button remBtn = new Button("-");
	private List<IGORImage> images = new ArrayList<IGORImage>();
	private List<IGORImage> selected = new ArrayList<IGORImage>();
	private boolean canUpload = true;
	private MainView view;
	
	public ImagePanel(MainView mainView) {
		initWidget(mainPanel);
		this.view = mainView;
		final ScrollPanel imscroll = new ScrollPanel(imagesPanel);
		imscroll.setStyleName("igor-image-scroll");
		imagesPanel.setStyleName("igor-image-container");
//		addBtn.setStyleName("small-btn");
		remBtn.setStyleName("small-btn");
		remBtn.setEnabled(false);
		
		mainPanel.add(imscroll);
		mainPanel.setWidgetLeftRight(imscroll, 0, Unit.PX, 0, Unit.PX);
		mainPanel.setWidgetTopBottom(imscroll, 10, Unit.PX, 0, Unit.PX);
		
		
		

//		SingleUploader uploader = new SingleUploader(FileInputType.CUSTOM.with(addBtn));
//		uploader.setValidExtensions(".jpg",".gif",".png",".bmp");
//		uploader.setServletPath("images.mediaupload");
//		uploader.setAutoSubmit(true);
//		uploader.addOnFinishUploadHandler(new IUploader.OnFinishUploaderHandler() {
//			public void onFinish(IUploader uploader) {
//				
//				if (canUpload && uploader.getStatus() == Status.SUCCESS) {
//					canUpload = false;
//					UploadedInfo info = uploader.getServerInfo();
//					IGORImage i = new IGORImage(info.message, ImagePanel.this);
//					images.add(i);
//					imagesPanel.add(i);
//					imscroll.scrollToRight();
//					new Timer() {
//						public void run() {
//							canUpload = true;
//						}
//					}.schedule(1000);
//				}
//			}
//		});
		
		UploadWidget uploader = new UploadWidget(addBtn, "upload.mediaupload");
		addBtn.setStyleName("small-btn");
		addBtn.getElement().getStyle().setMarginLeft(0, Unit.PX);
		uploader.addOnFinishedHandler(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				Element e = DOM.createLabel();
				e.setInnerHTML(event.getResults());
				IGORImage i = new IGORImage(GWT.getModuleBaseURL() + e.getInnerText(), ImagePanel.this);
				images.add(i);
				imagesPanel.add(i);
				imscroll.scrollToRight();
			}
		});
//		uploader.addStyleName("small-btn");
		
		HorizontalPanel btnPanel = new HorizontalPanel();
		btnPanel.setVerticalAlignment(HorizontalPanel.ALIGN_BOTTOM);
		btnPanel.add(uploader);
		btnPanel.add(addBtn);
		btnPanel.add(remBtn);
		mainPanel.add(btnPanel);
		mainPanel.setWidgetRightWidth(btnPanel, 0, Unit.PX, 100, Unit.PX);
		mainPanel.setWidgetTopHeight(btnPanel, 0, Unit.PX, 40, Unit.PX);
		
		remBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				for (IGORImage i : selected){
					imagesPanel.remove(i);				
				}
				images.removeAll(selected);
				selected.clear();
				remBtn.setEnabled(false);
			}
		});
		
	}
	
	public void addImages(List<IGORImage> images){
		this.images=images;
		for (IGORImage i : images){
			imagesPanel.add(i);
		}
	}

	public void select(IGORImage image) {
		if (selected.contains(image)){
			selected.remove(image);
			image.setSelected(false);
		} else {
			selected.add(image);
			image.setSelected(true);
		}
		boolean selectionExists = selected.size() > 0;
		remBtn.setEnabled(selectionExists);
		view.setHasImageSelection(selectionExists);
	}

	public List<IGORImage> getSelected() {
		return selected;
	}
	
	public List<IGORImage> getImages() {
		return images;
	}

	public void deselect() {
		for (IGORImage i : selected){
			i.setSelected(false);
		}
		selected.clear();
	}

	public void addImage(String url) {
		IGORImage i = new IGORImage(url, this);
		images.add(i);
		imagesPanel.add(i);
	}

	public void clear() {
		images.clear();
		selected.clear();
		imagesPanel.clear();
	}

}
