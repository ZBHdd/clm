package com.sogou.api.client.exception;

import com.sogou.api.sem.v1.common.ResHeader;

public class BusinessException extends RuntimeException {

	private ResHeader resHeader;
	
	
	private static final long serialVersionUID = -4682187814875138746L;

	public BusinessException(ResHeader resHeader, Object response) {
		super("" + resHeader);
		this.resHeader = resHeader;
	}

	

	public ResHeader getResHeader() {
		return resHeader;
	}

	public void setResHeader(ResHeader resHeader) {
		this.resHeader = resHeader;
	}

	
	
	
}
