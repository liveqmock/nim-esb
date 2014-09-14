package com.poweruniverse.nim.esb.message;

import java.lang.reflect.Method;

import net.sf.json.JSONObject;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.esb.Component;
import com.poweruniverse.nim.interfaces.message.InvokeEnvelopeI;
import com.poweruniverse.nim.interfaces.message.InvokeSourceI;
import com.poweruniverse.nim.interfaces.message.InvokeTargetI;


public class InvokeEnvelope implements InvokeEnvelopeI {
	private static final long serialVersionUID = -7111514299949856465L;
	
	private InvokeSourceI source = null;
	private InvokeTargetI target = null;
	private JSONObject params = null;
	
	public InvokeEnvelope(){
		
	}
	
	public InvokeEnvelope(String targetApp,String targetWs,String targetMethod,JSONObject params,UserInfo user){
		ComponentInfo sourceApp = Component.Components.get(Component.current_APP_name);
		this.source = new InvokeSource(sourceApp,user);
		this.target = new InvokeTarget(targetApp,targetWs,targetMethod);
		this.params = params;
	}
	
	
	public InvokeEnvelope(ComponentInfo sourceApp,String targetApp,String targetWs,String targetMethod,JSONObject params,UserInfo user){
		this.source = new InvokeSource(sourceApp,user);
		this.target = new InvokeTarget(targetApp,targetWs,targetMethod);
		this.params = params;
	}
	
	public boolean isInnerInvoke() {
		return source.getApplication().getIp().equals(target.getApplication().getIp());
	}

	public Method getTargetMethod() throws Exception{
		//如果目标服务在当前服务器上 直接调用
		//否则 使用webservice调用
		Class<?> targetWsClass = null;
		if(isInnerInvoke()){
			//取得目标webservice类的method 直接调用
			targetWsClass = target.getTargetWsClass();
		}else{
			//使用webservice方式调用
			targetWsClass = target.getTargetWsClientClass();
		}
		return getMethod(targetWsClass,target.getMethodName());
	}

	private static Method getMethod(Class<?> serviceClass,String methodName) throws Exception{
		Method method=null;
		if(serviceClass!=null){
			//检查类中是否存在此名称的方法
			for(Method m:serviceClass.getMethods()){
				if(m.getName().equals(methodName)){
					method = m;
					break;
				}
			}
		}
		return method;
	}

	
	public InvokeSourceI getSource() {
		return source;
	}

	public void setSource(InvokeSourceI source) {
		this.source = source;
	}

	public InvokeTargetI getTarget() {
		return target;
	}

	public void setTarget(InvokeTargetI target) {
		this.target = target;
	}

	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
