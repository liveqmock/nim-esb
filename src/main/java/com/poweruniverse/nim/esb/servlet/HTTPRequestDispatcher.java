package com.poweruniverse.nim.esb.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.poweruniverse.nim.bean.Environment;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.esb.message.InvokeEnvelope;
import com.poweruniverse.nim.esb.utils.ServiceRouter;
import com.poweruniverse.nim.interfaces.message.InvokeEnvelopeI;
import com.poweruniverse.nim.interfaces.message.ReturnI;
import com.poweruniverse.nim.message.JsonReturn;

/**
 * 用于过滤客户端通过servlet方式对webservice的访问请求
 * 将请求转发到适当的服务类中
 * @author Administrator
 *
 */
public class HTTPRequestDispatcher extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ReturnI ret = dispatch(req, resp);
		if(ret!=null){
			resp.setCharacterEncoding("utf-8");         
			resp.setContentType("text/html; charset=utf-8"); 
			resp.getWriter().write(ret.toString());
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ReturnI ret = dispatch(req, resp);
		if(ret!=null){
			resp.setCharacterEncoding("utf-8");         
			resp.setContentType("text/html; charset=utf-8"); 
			resp.getWriter().write(ret.toString());
		}
	}
	
	/**
	 * 用户向servlet发出的请求 转发到正确的webservice服务
	 * @param req
	 * @param resp
	 * @return
	 */
	private ReturnI dispatch(HttpServletRequest req, HttpServletResponse resp){
		String appName = req.getParameter("application");
		String wsName = req.getParameter("service");
		String methodName = req.getParameter("method");
		ReturnI ret = null;
		try {
			JSONObject paramterJsonObj = null;
			String parameters = req.getParameter("parameters");
			if(parameters!=null){
				paramterJsonObj = JSONObject.fromObject(parameters);
			}
			
			UserInfo user = null;
			Environment env = (Environment)req.getSession().getAttribute(Environment.ENV);
			if(env!=null){
				user = env.getAuthUser();
			}
			
			//客户端ip
			String ip = getServletClientIp(req);
			
//			ComponentInfo currentApp =  Component.Components.get(Component.current_APP_name);
			if(ServiceRouter.hasIPPermission(ip, user)){
				//无论session中 是否有已登录用户信息 均以UserClientInfo形式调用 
				//是否需要登录 由webservice方法自己判断
				InvokeEnvelopeI newEnvelope = new InvokeEnvelope(appName,wsName,methodName,paramterJsonObj,user);
				ret = ServiceRouter.invokeService(newEnvelope);
			}else{
				ret = new JsonReturn("当前用户("+user.getCode()+")无权限从此ip("+ip+")访问系统！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			ret = new JsonReturn(e.getMessage());
		}
		return ret;
	}
	
	
	private static String getServletClientIp(HttpServletRequest req) {

	    String ip = req.getHeader("X-Forwarded-For");
	    if(ip != null) {
	        if(ip.indexOf(",") == -1) {
	            return ip;
	        }
	        return ip.split(",")[0];
	    }

	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	        ip = req.getRemoteAddr();
	    }

	    return ip;
	}

}
