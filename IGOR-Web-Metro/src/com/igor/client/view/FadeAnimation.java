package com.igor.client.view;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;

public abstract class FadeAnimation extends Animation {
	private Widget widget;
	private boolean in;
	
	public FadeAnimation(Widget w, boolean in){
		widget = w;
		this.in = in;
	}
	@Override
	protected void onUpdate(double progress) {
		if (!in){
			progress = 1-progress;
		}
		widget.getElement().getStyle().setOpacity(progress);
	}
	
	@Override
	protected void onComplete(){
		if (in)
			widget.getElement().getStyle().clearOpacity();
		finished();
	}
	
	public void run(int duration, boolean in){
		this.in = in;
		super.run(duration);
	}
	
	public abstract void finished();
}