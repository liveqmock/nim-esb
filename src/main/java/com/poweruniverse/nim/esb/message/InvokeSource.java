package com.poweruniverse.nim.esb.message;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.esb.Component;
import com.poweruniverse.nim.interfaces.message.InvokeSourceI;
/**
 * 谁从那个系统发起的调用请求
 * @author Administrator
 *
 */
public class InvokeSource implements InvokeSourceI{


	private ComponentInfo application = null;
	private UserInfo userInfo = null;
	
	protected InvokeSource(String appName,UserInfo user) {
		super();
		this.application = Component.Components.get(appName);
		this.userInfo = user;
	}

	protected InvokeSource(ComponentInfo application,UserInfo user) {
		super();
		this.application = application;
		this.userInfo = user;
	}

	public ComponentInfo getApplication() {
		return application;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	
	@Override
	public String toString() {
		return application.getName()+" user:("+userInfo.getCode()+")";
	}



}