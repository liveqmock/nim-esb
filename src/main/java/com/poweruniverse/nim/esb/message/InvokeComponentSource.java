package com.poweruniverse.nim.esb.message;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.bean.Components;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.interfaces.message.InvokeSourceI;
/**
 * 表示从其他系统服务组件 发出的请求
 * @author Administrator
 *
 */
public class InvokeComponentSource implements InvokeSourceI{


	private ComponentInfo component = null;
	private UserInfo userInfo = null;
	
	public InvokeComponentSource(String cmpName,UserInfo user) {
		super();
		this.component = Components.getComponent(cmpName);
		this.userInfo = user;
	}

	public InvokeComponentSource(ComponentInfo component,UserInfo user) {
		super();
		this.component = component;
		this.userInfo = user;
	}

	public ComponentInfo getComponent() {
		return component;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	
	@Override
	public String toString() {
		return component.getName()+" user:("+userInfo.getCode()+")";
	}



}