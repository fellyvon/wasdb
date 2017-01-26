package com.waspring.wasdbtools;

import com.waspring.wasdbtools.exp.TransactionTimeoutException;

/**
 * 事务处理模版
 * 
 * @author felly
 * 
 */
public class TransactionTemplate {

	private String connName;

	public TransactionTemplate(String connName) {
		this.connName = connName;
		trManager = new TransactionManager(connName);
	}

	private TransactionManager trManager = null;

	// the timeout monitor is used to find out all long time transaction in
	// advance.
	private boolean useTimeoutMonitor = Boolean.FALSE;
	private Long maxTime = Long.valueOf(2);

	public void addSavePoint( String savePointName,
			Exception rollbackException) {
		this.trManager.addSavePoint(savePointName, rollbackException);
	}

	/**
	 * 事务处理模版执行方法
	 * 
	 * @param transactionCallback
	 *            事务回调事件
	 * @param isolationLevel
	 *            事务隔离级别
	 * @return
	 */
	public Object executeTransaction( 
			TransactionCallback transactionCallback, int isolationLevel) {
		long startTime = System.currentTimeMillis();
		Transaction transaction = null;
		Object obj = null;
		try {
			transaction = trManager.beginTransaction();
			checkTimeout(startTime);
			transaction.setTransactionIsolationLevel(isolationLevel);
			obj = transactionCallback.doTransactionEvent();
			checkTimeout(startTime);
			trManager.commitTransaction(transaction);
		} catch (Exception e) {
			e.printStackTrace();
			trManager.rollbackTransaction(transaction, e);

		} finally {
			trManager.closeTransaction(transaction);
		}

		return obj;
	}

	/**
	 * If the transaction is timeout throw a transaction time out exception.
	 */
	private void checkTimeout(long startTime) {
		if (this.useTimeoutMonitor) {
			if (isTimeout(startTime)) {
				throw new TransactionTimeoutException();
			}
		}
	}

	private boolean isTimeout(long startTime) {
		return System.currentTimeMillis() - startTime > this.maxTime;
	}

	/**
	 * 
	 * 事务处理模版执行方法 默认隔离级别
	 * 
	 * @param transactionCallback
	 *            事务回调事件
	 * @return
	 */
	public Object executeTransaction(
			TransactionCallback transactionCallback) {
		return this.executeTransaction( transactionCallback,
				Transaction.DEFAULT_ISOLATION_LEVEL);

	}

	public TransactionManager getTrManager() {
		return trManager;
	}

	public void setTrManager(TransactionManager trManager) {
		this.trManager = trManager;
	}

	public Long getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(Long maxTime) {
		this.maxTime = maxTime;
	}
}
