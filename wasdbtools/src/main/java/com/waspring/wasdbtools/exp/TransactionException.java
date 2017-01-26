package com.waspring.wasdbtools.exp;
/**
 * 事务异常定义
 * @author felly
 *
 */
public class TransactionException extends Error {

	public TransactionException() {
		super();
	}

	public TransactionException(String exp) {
		super(exp);
	}

	public TransactionException(Exception exp) {
		super(exp);
	}
}
