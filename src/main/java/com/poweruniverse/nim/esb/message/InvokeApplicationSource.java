package com.poweruniverse.nim.esb.message;

import com.poweruniverse.nim.bean.ApplicationInfo;
import com.poweruniverse.nim.bean.Applications;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.interfaces.message.InvokeSourceI;
/**
 * 表示从应用程序代码中 发出的访问请求（包括自己的应用程序和第三方应用程序 以后要对访问权限进行管理） 
 * @author Administrator
 *
 */
public class InvokeApplicationSource implements InvokeSourceI{


	private ApplicationInfo application = null;
	private UserInfo userInfo = null;
	
	protected InvokeApplicationSource(String appName,UserInfo user) {
		super();
		this.application = Applications.getApplication(appName);
		this.userInfo = user;
	}

	protected InvokeApplicationSource(ApplicationInfo application,UserInfo user) {
		super();
		this.application = application;
		this.userInfo = user;
	}

	public ApplicationInfo getApplication() {
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