package com.igor.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable{
	public User(){}
	public String name;
	public String session;
	public byte[] upload;
	public byte[] download;
	public byte[] generate;
}
