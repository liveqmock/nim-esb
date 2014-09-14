package com.poweruniverse.nim.esb.servlet;

import javax.servlet.http.HttpServlet;

import com.poweruniverse.nim.esb.Component;

public class EsbServerInitial extends HttpServlet{
	private static final long serialVersionUID = 1L;

	public void init(){
		//通知esb组件初始化 
		String contextPath = this.getServletContext().getRealPath("/");
		new Component().initial(contextPath+"/",null);
	}

}
