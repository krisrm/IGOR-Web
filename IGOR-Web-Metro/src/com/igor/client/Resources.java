package com.igor.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

	@Source("images/sample2.png")
	ImageResource donut();
	@Source("images/sample1.png")
	ImageResource moneyPaper();
	@Source("images/sample3.png")
	ImageResource moneyLoonie();

}
