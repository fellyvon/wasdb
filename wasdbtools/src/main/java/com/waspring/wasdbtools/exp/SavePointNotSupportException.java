package com.waspring.wasdbtools.exp;

/**
 * 断点事务不支持异常
 * @author felly
 *
 */
public class SavePointNotSupportException extends Error {

	public SavePointNotSupportException() {
		super();
	}

	public SavePointNotSupportException(String exp) {
		super(exp);
	}

	public SavePointNotSupportException(Exception exp) {
		super(exp);
	}
}
