package com.poweruniverse.nim.esb.message;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.bean.Components;
import com.poweruniverse.nim.interfaces.message.InvokeTargetI;

public class InvokeComponentTarget implements InvokeTargetI{
	private ComponentInfo component = null;
	private String wsName = null;
	private String methodName = null;
	
	public InvokeComponentTarget(String cmpName,String wsName,String methodName) {
		super();
		this.component = Components.getComponent(cmpName);
		this.wsName = wsName;
		this.methodName = methodName;
	}

	public ComponentInfo getComponent() {
		return component;
	}

	public String getWsName() {
		return wsName;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?> getTargetWsClass() throws ClassNotFoundException {
		return Class.forName(component.getWebserviceClass(wsName));
	}
	
	public Class<?> getTargetWsClientClass() throws ClassNotFoundException {
		return Class.forName("com.poweruniverse."+component.getName()+".client."+wsName+"."+getTargetWsClass().getSimpleName());
	}
	
	public Class<?> getTargetWsClientServiceClass() throws ClassNotFoundException {
		return Class.forName("com.poweruniverse."+component.getName()+".client."+wsName+"."+getTargetWsClass().getSimpleName()+"Sevice");
	}
	
	public String getTargetWsUrl() {
		return "http://"+component.getIp()+":"+component.getWebservicePort()+"/ws/"+component.getName()+"/"+wsName+"?wsdl";
	}
	
	@Override
	public String toString() {
		return component.getName()+"."+wsName+"."+methodName;
	}
	
}