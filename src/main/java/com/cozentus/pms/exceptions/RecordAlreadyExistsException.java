package com.cozentus.pms.exceptions;

public class RecordAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RecordAlreadyExistsException(String message) {
		super(message);
	}

	public RecordAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

}
