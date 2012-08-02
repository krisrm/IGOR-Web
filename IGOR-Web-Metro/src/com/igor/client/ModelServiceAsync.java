package com.igor.client;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.igor.shared.TemplateDTO;
import com.igor.shared.User;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ModelServiceAsync {

	void generate(TemplateDTO template, AsyncCallback<String> callback);

	void upload(AsyncCallback<TemplateDTO> callback);

	void download(TemplateDTO template, AsyncCallback<String> callback);

	void login(String name, String password, AsyncCallback<User> callback);

	void logout(AsyncCallback<Void> callback);

	void authenticate(String token, AsyncCallback<User> callback);

	void logError(Throwable e, AsyncCallback<Void> callback);
}
