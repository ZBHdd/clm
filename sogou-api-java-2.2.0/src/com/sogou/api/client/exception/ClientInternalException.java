package com.sogou.api.client.exception;

public class ClientInternalException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8687265146246742535L;

	public ClientInternalException() {
		super();
	}

	public ClientInternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientInternalException(String message) {
		super(message);
	}

	public ClientInternalException(Throwable cause) {
		super(cause);
	}

}
