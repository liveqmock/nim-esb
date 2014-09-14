package com.poweruniverse.nim.esb.message;

import java.lang.reflect.Method;

import net.sf.json.JSONObject;

import com.poweruniverse.nim.bean.Applications;
import com.poweruniverse.nim.bean.UserInfo;
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
	
//	public InvokeEnvelope(String targetApp,String targetWs,String targetMethod,JSONObject params,UserInfo user){
//		ComponentInfo sourceApp = Component.Components.get(Component.current_APP_name);
//		this.source = new InvokeSource(sourceApp,user);
//		this.target = new InvokeTarget(targetApp,targetWs,targetMethod);
//		this.params = params;
//	}
	
	
	public InvokeEnvelope(InvokeSourceI invokeSource,InvokeTargetI invokeTarget,JSONObject params){
		this.source = invokeSource;
		this.target = invokeTarget;
		this.params = params;
	}
	
	/**
	 * 判断是否内部调研
	 */
	public boolean isInnerInvoke() {
		if(source instanceof InvokeComponentSource ){
			//从系统组件发起的调用 都是内部调用
			return true;
		}else if(source instanceof InvokeApplicationSource ){
			//从应用程序发起的调用 检查发起端的ip与当前系统的IP是否一致
			return ((InvokeApplicationSource)source).getApplication().getIp().equals(Applications.getCurrentApplication().getIp());
		}else if(source instanceof InvokeHttpSource){
			//从客户端浏览器发起的调用 全部都是外部调用
		}
		return false;
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
