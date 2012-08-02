package com.igor.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.igor.client.view.FadeAnimation;
import com.igor.client.view.login.LoginView;
import com.igor.client.view.main.MainView;
import com.igor.shared.TemplateDTO;
import com.igor.shared.User;

public class IGOR_Web_Metro implements EntryPoint {

	public static final ModelServiceAsync model = GWT
			.create(ModelService.class);
	public static Resources R = GWT.create(Resources.class);
	private boolean loggedIn;
	private LoginView loginView = new LoginView(this);
	private MainView mainView = new MainView(this);
	private final FadeAnimation loginFade = new FadeAnimation(loginView, false) {

		@Override
		public void finished() {
			buildUI();
		}
	};
	private final Animation mainFadeIn = new FadeAnimation(mainView, true) {

		@Override
		public void finished() {
		}

	};
	public User user;

	public void onModuleLoad() {
		//for testing
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void onUncaughtException(Throwable e) {
				model.logError(e, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
			}
		});
		
		String cookie = Cookies.getCookie("IGOR");
		if (cookie != null) {
			model.authenticate(cookie, new LoggedCallback<User>() {

				@Override
				public void onSuccess(User user) {
					if (user != null) {
						loggedIn = true;
						IGOR_Web_Metro.this.user = user;
					} else {
						loggedIn = false;
					}
					buildUI();
				}

				@Override
				public void handleError(Throwable caught) {
					buildUI();

				}
			});
		} else {
			loggedIn = false;
			buildUI();
		}

	}

	private void buildUI() {
		RootLayoutPanel.get().clear();
		if (loggedIn) {
			loginView.clear();
			RootLayoutPanel.get().add(mainView);
			mainView.getElement().getStyle().setOpacity(0);

			IGOR_Web_Metro.model.upload(new LoggedCallback<TemplateDTO>() {

				@Override
				public void handleError(Throwable caught) {
					mainFadeIn.run(200);
				}

				@Override
				public void onSuccess(TemplateDTO result) {
					mainView.setTemplate(result);
					mainFadeIn.run(200);
				}
			});

		} else {
			RootLayoutPanel.get().add(loginView);
		}

	}

	public void login(String username, String password) {

		model.login(username, password, new LoggedCallback<User>() {

			@Override
			public void onSuccess(User u) {
				if (u != null) {
					Cookies.setCookie("IGOR", u.session);
					loggedIn = true;
					loginFade.run(200, false);
					IGOR_Web_Metro.this.user = u;
				} else {
					loginView.failed();
				}
			}

			@Override
			public void handleError(Throwable caught) {
				loginView.failed();
			}
		});

	}

	public void logout() {
		model.logout(new LoggedCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				Cookies.removeCookie("IGOR");
				loggedIn = false;
				loginFade.run(200, true);
			}

			@Override
			public void handleError(Throwable caught) {
				onSuccess(null);
			}
		});

	}

}
