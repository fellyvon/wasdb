package com.waspring.wasdbtools.exp;
/**
 * 事务超时异常
 * @author felly
 *
 */
public class TransactionTimeoutException extends Error {

	public TransactionTimeoutException() {
		super();
	}

	public TransactionTimeoutException(String exp) {
		super(exp);
	}

	public TransactionTimeoutException(Exception exp) {
		super(exp);
	}
}
