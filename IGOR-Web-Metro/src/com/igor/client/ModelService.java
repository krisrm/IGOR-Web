package com.igor.client;


import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.igor.shared.TemplateDTO;
import com.igor.shared.User;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("model")
public interface ModelService extends RemoteService {
	public String generate(TemplateDTO template);
	public TemplateDTO upload();
	public String download(TemplateDTO template) throws Exception;
	
	public User authenticate(String token);
	public User login(String name, String password);
	public void logout();
	
	void logError(Throwable e);
}
