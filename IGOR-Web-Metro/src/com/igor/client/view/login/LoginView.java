package com.igor.client.view.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.igor.client.IGOR_Web_Metro;

public class LoginView extends Composite {
	private static final String USER_NAME_DEFAULT = "User Name";

	private static final String PASSWORD_DEFAULT = "Password";

	interface LoginUIBinder extends UiBinder<LayoutPanel, LoginView> {
	}

	private static LoginUIBinder uiBinder = GWT.create(LoginUIBinder.class);

	private IGOR_Web_Metro startingPoint;

	@UiField
	Button login;
	@UiField
	TextBox username;
	@UiField
	PasswordTextBox password;
	@UiField
	Label error;
	
	public LoginView(IGOR_Web_Metro sp) {
		this.startingPoint = sp;
		initWidget(uiBinder.createAndBindUi(this));
		username.addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				if (username.getText().equals(USER_NAME_DEFAULT)){
					username.setText("");
				}
			}
		});
		password.addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				if (password.getText().equals(PASSWORD_DEFAULT)){
					password.setText("");
				}
			}
		});
		username.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if (username.getText().equals("")){
					username.setText(USER_NAME_DEFAULT);
				}
			}
		});
		password.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				if (password.getText().equals("")){
					password.setText(PASSWORD_DEFAULT);
				}
			}
		});
		
		login.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doLogin();
			}

			
		});
		KeyPressHandler kh = new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					doLogin();
				}
			}
		};
		password.addKeyPressHandler(kh);
		username.addKeyPressHandler(kh);
	}
	protected void doLogin() {
		error.setText("");
		login.addStyleName("clicked");
		new Timer(){

			@Override
			public void run() {
				startingPoint.login(username.getText(), password.getText());
				login.removeStyleName("clicked");
			}
			
		}.schedule(500);
	}
	public void clear() {
		username.setText(USER_NAME_DEFAULT);
		password.setText(PASSWORD_DEFAULT);
		
		error.setText("");
		getElement().getStyle().clearOpacity();
	}

	public void failed() {
		error.setText("Incorrect username or password. Please try again");
	}
	
}
