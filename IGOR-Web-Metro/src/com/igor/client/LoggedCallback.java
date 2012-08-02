package com.igor.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class LoggedCallback<T> implements AsyncCallback<T> {

	@Override
	public void onFailure(final Throwable caught) {
		caught.printStackTrace();
		IGOR_Web_Metro.model.logError(caught, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				handleError(caught);
			}

			@Override
			public void onSuccess(Void result) {
				handleError(caught);
			}
		});
	}

	protected void handleError(Throwable caught) {
		//override this
	}

}
