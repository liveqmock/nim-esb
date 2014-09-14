package com.poweruniverse.nim.esb.message;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.esb.Component;
import com.poweruniverse.nim.interfaces.message.InvokeSourceI;
/**
 * 表示从客户端浏览器直接发出的请求
 * @author Administrator
 *
 */
public class InvokeHttpSource implements InvokeSourceI{


	private String ip = null;
	private UserInfo userInfo = null;
	
	public InvokeHttpSource(String ip,UserInfo user) {
		super();
		this.ip = ip;
		this.userInfo = user;
	}

	public String getIP() {
		return ip;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	
	@Override
	public String toString() {
		return ip+" user:("+userInfo.getCode()+")";
	}



}