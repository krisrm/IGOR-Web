package com.igor.client.view.main.attr;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.igor.client.view.main.image.IGORImage;
import com.igor.shared.VariableDTO.VarType;

class ImageEdit extends AttributeEditor.Editor implements IGORImage.CanSelect {
	/**
	 * 
	 */
	private final AttributeEditor attributeEditor;
	private List<IGORImage> model = new ArrayList<IGORImage>();
	private List<IGORImage> selected = new ArrayList<IGORImage>();
	private Button remBtn = new Button("-");
	private Button addBtn = new Button(">");

	public ImageEdit(AttributeEditor attributeEditor, LayoutPanel main) {
		attributeEditor.super(main);
		this.attributeEditor = attributeEditor;
		this.attributeEditor.current.type = VarType.IMAGE;
		final FlowPanel images = new FlowPanel();
		final ScrollPanel imagesScroll = new ScrollPanel(images);
		imagesScroll.setStyleName("images-attribute");
		HorizontalPanel btnMain = new HorizontalPanel();
		VerticalPanel btnCtr = new VerticalPanel();

		addBtn.setStyleName("action-btn");
		addBtn.getElement().getStyle().setMarginLeft(0, Unit.PX);
		addBtn.getElement().getStyle().setMarginBottom(10, Unit.PX);
		remBtn.setStyleName("action-btn");
		remBtn.getElement().getStyle().setMarginLeft(0, Unit.PX);
		btnMain.setSize("100%", "100%");
		btnMain.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		btnMain.add(btnCtr);
		btnCtr.setWidth("100%");
		btnCtr.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		btnCtr.add(addBtn);
		btnCtr.add(remBtn);
		remBtn.setEnabled(false);
		addBtn.setEnabled(this.attributeEditor.view.getSelectedImages().size() > 0);
		main.add(btnMain);
		main.setWidgetLeftWidth(btnMain, 0, Unit.PX, 70, Unit.PX);
		main.setWidgetTopBottom(btnMain, 50, Unit.PX, 0, Unit.PX);

		main.add(imagesScroll);
		main.setWidgetLeftRight(imagesScroll, 70, Unit.PX, 20, Unit.PX);
		main.setWidgetTopBottom(imagesScroll, 50, Unit.PX, 0, Unit.PX);

		// add current images to view
		for (String url : this.attributeEditor.current.urls) {
			IGORImage ii = new IGORImage(url, ImageEdit.this);
			images.add(ii);
			model.add(ii);
		}

		addBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				for (IGORImage i : ImageEdit.this.attributeEditor.view.getSelectedImages()) {
					IGORImage ii = new IGORImage(i, ImageEdit.this);
					images.add(ii);
					ImageEdit.this.attributeEditor.current.urls.add(ii.getUrl());
					model.add(ii);
					clearSelection();
					select(ii);
				}
				ImageEdit.this.attributeEditor.view.deselectImages();
				imagesScroll.scrollToBottom();
				addBtn.setEnabled(false);
			}
		});
		remBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				model.removeAll(selected);
				for (IGORImage i : selected){
					images.remove(i);				
				}
				selected.clear();
				List<String> modelUrls = new ArrayList<String>();
				for (IGORImage i : model)
					modelUrls.add(i.getUrl());
				
				ImageEdit.this.attributeEditor.current.urls = modelUrls;
				System.out.println(modelUrls);
			}
		});
	}

	public void clearSelection() {
		for (IGORImage i : selected)
			i.setSelected(false);
		selected.clear();
	}
	public void select(IGORImage image) {
		if (selected.contains(image)) {
			selected.remove(image);
			image.setSelected(false);
		} else {
			selected.add(image);
			image.setSelected(true);
		}
		remBtn.setEnabled(selected.size() > 0);
	}

	public void imagesSelected(boolean selectionExists) {
		addBtn.setEnabled(selectionExists);
	}

}