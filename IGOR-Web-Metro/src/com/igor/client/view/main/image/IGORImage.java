package com.igor.client.view.main.image;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.igor.client.IGOR_Web_Metro;
import com.reveregroup.gwt.imagepreloader.FitImage;

public class IGORImage extends Composite {
	
	private FocusPanel main = new FocusPanel();
	private FitImage pic = new FitImage();
	
	public interface CanSelect{
		public void select(IGORImage i);
	}
	
	
	public IGORImage(String url, final CanSelect p){
		main.setStyleName("igor-image-outline");
		
		url = url.replace(" ", "%20");
		url = url.replace("&amp;", "&");
		pic = new FitImage(url, 120,120);
		pic.setStyleName("igor-image");
		main.add(pic);
		initWidget(main);
		main.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				p.select(IGORImage.this);
			}
		});
		main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}
	
//	public IGORImage(CanSelect p){
//		this(randomImage().getSafeUri().asString(),p);
//	}

	public IGORImage(IGORImage i, CanSelect p) {
		this(i.getUrl(), p);
		
	}

//	private static ImageResource randomImage() {
//		int r = Random.nextInt(3);
//		switch (r){
//		case 0:
//			return IGOR_Web_Metro.R.donut();
//		case 1:
//			return IGOR_Web_Metro.R.moneyLoonie();
//		default:
//			return IGOR_Web_Metro.R.moneyPaper();
//		}
//	}

	public void setSelected(boolean selected) {
		if (selected){
			main.setStyleName("selected-image");
		} else {
			main.setStyleName("igor-image-outline");
		}
	}

	public String getUrl() {
		return pic.getUrl();
	}
	
}
