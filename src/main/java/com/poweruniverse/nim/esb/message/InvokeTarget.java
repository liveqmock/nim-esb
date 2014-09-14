package com.poweruniverse.nim.esb.message;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.esb.Component;
import com.poweruniverse.nim.interfaces.message.InvokeTargetI;

public class InvokeTarget implements InvokeTargetI{
	private ComponentInfo application = null;
	private String wsName = null;
	private String methodName = null;
	
	protected InvokeTarget(String appName,String wsName,String methodName) {
		super();
		this.application = Component.Components.get(appName);
		this.wsName = wsName;
		this.methodName = methodName;
	}

	public ComponentInfo getApplication() {
		return application;
	}

	public String getWsName() {
		return wsName;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?> getTargetWsClass() throws ClassNotFoundException {
		return Class.forName(application.getWebserviceClass(wsName));
	}
	
	public Class<?> getTargetWsClientClass() throws ClassNotFoundException {
		return Class.forName("com.poweruniverse."+application.getName()+".client."+wsName+"."+getTargetWsClass().getSimpleName());
	}
	
	public Class<?> getTargetWsClientServiceClass() throws ClassNotFoundException {
		return Class.forName("com.poweruniverse."+application.getName()+".client."+wsName+"."+getTargetWsClass().getSimpleName()+"Sevice");
	}
	
	public String getTargetWsUrl() {
		return "http://"+application.getIp()+":"+application.getWebservicePort()+"/ws/"+application.getName()+"/"+wsName+"?wsdl";
	}
	
	@Override
	public String toString() {
		return application.getName()+"."+wsName+"."+methodName;
	}
	
}