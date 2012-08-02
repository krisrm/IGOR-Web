package com.igor.client.view.main;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.ui.Widget;

abstract class ChainedAnimation {
	ArrayList<Widget> widgets = new ArrayList<Widget>();
	int started;

	public ChainedAnimation(Widget... anim) {
		widgets.addAll(Arrays.asList(anim));

	}

	public void start(final int duration, final double pct) {
		started = 0;
		start(duration, pct, 0);
	}

	public void start(final int duration, final double pct, final int i) {
		if (i >= widgets.size()) {
			finished();
			return;
		}
		final Widget cur = widgets.get(i);
		new Animation() {

			@Override
			protected void onUpdate(double progress) {
				update(cur, progress);
				if (progress > pct && started <= i) {
					started++;
					start(duration, pct, i + 1);
				}

			}

			@Override
			protected void onComplete() {
				completed(cur);
				if (started <= i) {
					start(duration, pct, i + 1);
				}
			}

		}.run(duration);
	}

	public abstract void update(Widget w, double progress);
	public abstract void completed(Widget w);
	public void finished() {}
}