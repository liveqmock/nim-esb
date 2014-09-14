package com.poweruniverse.nim.esb;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.bean.VariableInfo;
import com.poweruniverse.nim.interfaces.ComponentI;
import com.poweruniverse.nim.utils.ComponentServicePublisher;

/**
 * 读取系统定义 系统参数
 * 
 * 启动配置文件中确定的系统中  发布webservice服务
 */
public class Component implements ComponentI{
	
//	public final static String flag_user_in_session = "user";
//	public final static String flag_envelop_in_context = "envelop";
	public final static String serverConfigFile = "WEB-INF/server-config.xml";
	private final static String variableConfigFile = "WEB-INF/variable.xml";
	
//	public static String current_APP_Path = null;//当前系统运行目录
	public static String current_APP_name = null;
//	public static String current_APP_src = null;
//	public static String current_APP_jdk = null;
	public final static String NSB_APP_name = "nsb";//总线服务器配置信息
	public final static String NRS_APP_name = "nrs";//报表服务器配置信息
	public final static String NFS_APP_name = "nfs";//文档服务器配置信息
	public final static String NPS_APP_name = "nps";//用户权限服务配置信息
	public final static String NAS_APP_name = "nas";//用户验证服务配置信息
	
	public final static Map<String,ComponentInfo> Components = new HashMap<String,ComponentInfo>();
	
	
	public final static String ModulePath = "modulePath";
	private static HashMap<String,VariableInfo> Variables = new HashMap<String,VariableInfo>();

	
//	public final static Map<String,String> DBInfoMap = new HashMap<String,String>();
	
	/**
	 * 初始化本组件
	 * 在指定地址和端口号上 发布当前组件中的webservice服务
	 */
	public void initial(String contextPath,ComponentInfo cfg) {

		initApplicationConfig(contextPath);
		initVariableConfig(contextPath);
		
		ComponentInfo currentServerInfo = Components.get(this.getComponentName());
		
		//取得当前系统所对应的配置信息
		if(currentServerInfo==null){
			System.err.println("系统配置文件中，未找到"+this.getComponentName()+"组件的定义！");
			return;
		}
		//启动webservicef服务
		ComponentServicePublisher.publish(contextPath, currentServerInfo);
	}
	
	
//	private static void generateClientCode(String url,String packagePath,String javaSrcPath,String jdkPath){
//    	try {
//			//使用jdk的wsimport 生成client代码
//			String ls_1;
//			String cmdString = "cmd /c "+jdkPath+"bin/wsimport -p "+packagePath+" -keep "+url+"?wsdl";
//			
//			Process process2 = Runtime.getRuntime().exec(cmdString, null, new File(javaSrcPath));
//			BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process2.getInputStream()));
//			while ( (ls_1=bufferedReader2.readLine()) != null){
//				System.out.println(ls_1);
//			}
//			process2.waitFor();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }
	
	private void initApplicationConfig(String contextPath){
		try {
//			current_APP_Path = contextPath;
	
			SAXReader reader = new SAXReader();
			reader.setEncoding("utf-8");
			File cfgFile = new File(contextPath+serverConfigFile);
			if(!cfgFile.exists()){
				System.err.println("配置文件"+serverConfigFile+"不存在！");
				return;
			}
			Document doc = reader.read(cfgFile);
			Element cfgEl = doc.getRootElement();//server  XML:ROOT-EL
			
			String serverIp = cfgEl.attributeValue("ip");
			String serverWebservicePort = cfgEl.attributeValue("webservice-port");
			
			current_APP_name = cfgEl.attributeValue("name");
			if(current_APP_name==null){
				System.err.println("在配置文件"+serverConfigFile+"的根节点中未定义name属性！");
				return;
			}
			
//			current_APP_src = cfgEl.attributeValue("src");
//			current_APP_jdk = cfgEl.attributeValue("jdk");
			//component 系统服务组件
			@SuppressWarnings("unchecked")
			List<Element> componentEls = (List<Element>)cfgEl.elements("component");
			for(Element componentEl : componentEls){
				String className = componentEl.attributeValue("class-name");
				try {
					ComponentInfo componentInfo = null;
					//实例化组件对象
					Class<?> componentClass = Class.forName(className);
					ComponentI componentInst = (ComponentI)componentClass.newInstance();
					//取得名称
					String componentName = componentInst.getComponentName();
					//取得组件工作方式
					String mode = componentEl.attributeValue("mode");
					//设定为local的组件或esb组件 在本地发布服务
					if("local".equals(mode) || componentName.equals(this.getComponentName())){
						componentInfo = new ComponentInfo(componentName,componentInst,serverIp,serverWebservicePort,true);
					}else{
						componentInfo = new ComponentInfo(
							componentName,
							componentInst,
							componentEl.attributeValue("ip"),
							componentEl.attributeValue("webservice-port"),false
						);
					}
					//记录此服务组件
					Components.put(componentName, componentInfo);
				} catch (ClassNotFoundException e) {
					System.err.println("配置组件出错：组件启动类("+className+")不存在！");
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("配置组件出错："+e.getMessage());
					e.printStackTrace();
				}
			}
			
			//循环初始化所有组件
			for(String componentName:Components.keySet()){
				if(!componentName.equals(getComponentName())){
					//为非esb组件 调用initial方法
					ComponentInfo componentInfo = Components.get(componentName);
					componentInfo.getInstance().initial(contextPath, componentInfo);
				}
			}
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void initVariableConfig(String contextPath){
		try {
			File cfgFile = new File(contextPath+variableConfigFile);
			if(cfgFile.exists()){
				SAXReader reader = new SAXReader();
				reader.setEncoding("utf-8");
				Document doc = reader.read(cfgFile);
				
				Element cfgEl = doc.getRootElement();//module
				@SuppressWarnings("unchecked")
				List<Element> variableEls = (List<Element>)cfgEl.elements("variable");
				for(Element variableEl : variableEls){
					String variableCode = variableEl.attributeValue("code");
					VariableInfo variableInfo = new VariableInfo(
						variableCode,
						variableEl.attributeValue("value"),
						variableEl.attributeValue("comments"),
						variableEl.attributeValue("required")
					);
					
					Variables.put(variableCode, variableInfo);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}


	@Override
	public String getComponentName() {
		return "nim-esb";
	}
}
