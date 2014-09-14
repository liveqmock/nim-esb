package com.poweruniverse.nim.esb.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebParam;
import javax.xml.ws.BindingProvider;

import net.sf.json.JSONObject;

import com.poweruniverse.nim.bean.Environment;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.esb.message.InvokeEnvelope;
import com.poweruniverse.nim.interfaces.message.InvokeEnvelopeI;
import com.poweruniverse.nim.interfaces.message.ReturnI;
import com.poweruniverse.nim.message.JsonReturn;

/**
 * 消息路由 对各类服务组件的调用 均通过此处转发
 * @author Administrator
 *
 */
public class ServiceRouter {

	/**
	 * 从servlet发起对服务组件的调用
	 * @return
	 */
	public static ReturnI invokeService(InvokeEnvelopeI invokeEnvelope){
		ReturnI ret = null;
		try {
			//取得本次调用的目标server
			Method targetMethod = invokeEnvelope.getTargetMethod();
			if(targetMethod==null){
				return new JsonReturn("目标服务("+invokeEnvelope.getTarget().toString()+")不存在！");
			}
			
			//如果目标服务在当前服务器上 直接调用
			//否则 使用webservice调用
			if(invokeEnvelope.isInnerInvoke()){
				//取得目标webservice类的method 直接调用
				//使用客户端传递来的参数 直接调用webservice实现类
				
				/*以下调用带参的、私有构造函数*/   
	            Constructor<?> serviceConstructor = targetMethod.getDeclaringClass().getDeclaredConstructor(new Class[]{InvokeEnvelope.class});   
	            Object serviceInstance = serviceConstructor.newInstance(new Object[]{invokeEnvelope});
				ret = doMethodInvokeByParams(serviceInstance,targetMethod,invokeEnvelope);
			}else{
				//使用反射方法 取得构建方法 创建webservice实例
				Constructor<?> wsClientClassConstructor = invokeEnvelope.getTarget().getTargetWsClientServiceClass().getConstructor(new Class[]{URL.class});
				Object[] params = new Object[1];
				params[0] = new URL(ServiceRouter.class.getResource("."),invokeEnvelope.getTarget().getTargetWsUrl() );
				Object serviceInstance = wsClientClassConstructor.newInstance(params);
				//使用getXXXPort方法 得到webservice代理实例
				Method getServicePortMethod = serviceInstance.getClass().getMethod("get"+targetMethod.getDeclaringClass().getSimpleName()+"Port", new Class[]{});
				Object servicePortInstance = getServicePortMethod.invoke(serviceInstance, new Object[]{});
				//加入当前调用信息
				BindingProvider provider = (BindingProvider)servicePortInstance;  
				provider.getRequestContext().put(Environment.INVOKE, invokeEnvelope);
				//加入当前用户信息 用于目标服务不信任源情况下 重新进行用户验证的情况
				provider.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, invokeEnvelope.getSource().getUserInfo().getCode());
				provider.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, invokeEnvelope.getSource().getUserInfo().getPassword());
				//用客户端传递来的参数 调用webservice方法
				ret = doMethodInvokeByParams(servicePortInstance,targetMethod,invokeEnvelope);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			ret = new JsonReturn("访问服务器出错："+e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			ret = new JsonReturn("访问服务器出错："+e.getMessage());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			ret = new JsonReturn("访问服务器出错："+e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			ret = new JsonReturn("访问服务器出错："+e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			ret = new JsonReturn("访问服务器出错："+e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			ret = new JsonReturn("访问服务器出错："+e.getMessage());
		}
		return ret;
	}
	
//	/**
//	 * 从某一服务组件发起对另一服务组件的调用
//	 * @return
//	 */
//	public static ReturnEnvelope invokeFromService(InvokeEnvelope invokeEnvelope){
//		return null;
//	}

	
	private static ReturnI doMethodInvokeByParams(Object obj,Method method,InvokeEnvelopeI invokeEnvelope) 
			throws IllegalAccessException,IllegalArgumentException,InvocationTargetException{
		
		Class<?>[] parameterTypes =  method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		
		JSONObject paramterJsonObj = invokeEnvelope.getParams();
		
		Object[] arguments = new Object[parameterTypes.length];
		for(int i = 0;i<parameterTypes.length;i++){
			Object param = null;
			Annotation paramAnnotation = parameterAnnotations[i][0];
			String paramName = null;
			if(paramAnnotation instanceof WebParam){
				paramName = ((WebParam)paramAnnotation).name();
			}else{
				return new JsonReturn("需要使用@WebParam 为("+obj.getClass().getName()+"."+method.getName()+")webservice方法中的参数设置注释！");
			}
			//取得客户端传递来的参数值 转换为正确的类型
			if(paramterJsonObj!=null && paramterJsonObj.containsKey(paramName) ){
				param = getObjectWithCorrectValue(paramterJsonObj.get(paramName),parameterTypes[i]);
			}
			arguments[i] = param;
		}
		//执行方法得到返回结果
		return (ReturnI)method.invoke(obj,arguments);
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static Object getObjectWithCorrectValue(Object value,Class<?> type) throws IllegalArgumentException {
		if(!value.getClass().equals(type)){
			if(type.equals(String.class)){
				value = value.toString();
			}else if(type.equals(Double.class)){
				value = Double.valueOf(value.toString()).doubleValue();
			}else if(type.equals(Integer.class)){
				value = Double.valueOf(value.toString()).intValue();
			}else if (type.equals(Date.class)){
				value = sdf.format(value.toString());
			}else{
				throw new IllegalArgumentException("未定义处理方式的类型:"+type);
			}
		}
		return value;
	}
	
	/**
	 * 检查用户是否具有从此ip发出请求的许可
	 * @return
	 */
	public static boolean hasIPPermission(String ip,UserInfo user){
//		//对象相关 需要授权的操作 检查数据权限
//		JSONObject params = new JSONObject();
//		params.put("xiTongDH", xiTongDH);
//		params.put("gongNengDH", gongNengDH);
//		params.put("caoZuoDH", caoZuoDH);
//		params.put("yongHuDM", yongHuDM);
//		params.put("id", id);
//		//通过权限服务 检查
//		InvokeEnvelope permissionEnvelope = new InvokeEnvelope("nss","security","hasDataPermission",params,null);
//		Message permissionMsg = ServiceRouter.invokeService(permissionEnvelope);
//		if(!permissionMsg.isSuccess()){
//			return permissionMsg;
//		}
		return false;
	}

}
