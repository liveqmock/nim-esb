package com.poweruniverse.nim.esb.translator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.poweruniverse.nim.bean.ComponentInfo;
import com.poweruniverse.nim.bean.UserInfo;
import com.poweruniverse.nim.esb.message.InvokeComponentSource;
import com.poweruniverse.nim.esb.message.InvokeComponentTarget;
import com.poweruniverse.nim.esb.message.InvokeEnvelope;

/**
 * 负责将客户端请求翻译/转换为对应于正确服务的 invokeEnvelope
 * @author Administrator
 *
 */
public class DataRequestTranslator {
	
	public static InvokeEnvelope getSqlInvokeEnvelope(ComponentInfo sourceComponent,UserInfo user,String sql,int start,int limit,JSONArray fields){
		InvokeComponentTarget target = new InvokeComponentTarget("nim-data","data","execute");
		
		InvokeComponentSource source = new InvokeComponentSource(sourceComponent,user);
		
		JSONObject params = new JSONObject();
		params.put("sql", sql);
		params.put("start", start);
		params.put("limit", limit);
		params.put("fields", fields);
		
		return new InvokeEnvelope(source,target,params);
	}
}
