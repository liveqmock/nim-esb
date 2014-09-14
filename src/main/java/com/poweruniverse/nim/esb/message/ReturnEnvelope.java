package com.poweruniverse.nim.esb.message;

import java.util.HashMap;
import java.util.Map;

import com.poweruniverse.nim.interfaces.message.InvokeEnvelopeI;
import com.poweruniverse.nim.interfaces.message.ReturnEnvelopeI;



public class ReturnEnvelope implements ReturnEnvelopeI {
	private static final long serialVersionUID = -7111514299949856465L;
	
	private InvokeEnvelopeI invokeEnvelope = null;
	
	private boolean success = true;
	private String errorMsg = null;
	private Map<String,Object> data = new HashMap<String,Object>();
	
	public ReturnEnvelope(){
	}
	
	public ReturnEnvelope(InvokeEnvelopeI invokeEnvelope){
		this.success = true;
	}

	public ReturnEnvelope(InvokeEnvelopeI invokeEnvelope,String errorMsg){
		this.success = false;
		this.errorMsg = errorMsg;
	}

	public InvokeEnvelopeI getInvokeEnvelope() {
		return invokeEnvelope;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public Object get(String key) {
		return data.get(key);
	}

	@Override
	public void put(String key, Object value) {
		data.put(key, value);
	}

}
