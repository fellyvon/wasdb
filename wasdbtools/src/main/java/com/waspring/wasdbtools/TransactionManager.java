package com.waspring.wasdbtools;

import java.sql.Connection;
import java.sql.Savepoint;

import com.waspring.wasdbtools.exp.SavePointNotSupportException;
import com.waspring.wasdbtools.exp.TransactionException;

/**
 * 事务管理器
 * 
 * @author felly
 * 
 */
public class TransactionManager {
	private String connName;

	public TransactionManager(String connName) {
		this.connName = connName;
	}

	private DatabaseConnection connectionManager = DatabaseConnection
			.getInstance();

	private ThreadLocal<Transaction> transactions = new ThreadLocal<Transaction>();

	public Transaction beginTransaction() {
		try {
			Transaction tr = this.transactions.get();
			if (tr == null) {
				Connection connection = connectionManager
						.getConnection(connName);
				connectionManager.setTransactionActive(true);
				tr = new Transaction(connection);
				this.transactions.set(tr);
			} else {
				tr.addNewService();
			}
			return tr;
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransactionException();
		}
	}

	public void commitTransaction(Transaction transaction) {
		try {
			if (transaction.isNewTransaction()) {
				connectionManager.setTransactionActive(false);
				connectionManager.commit(transaction.getConnection());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransactionException();
		}
	}

	public void rollbackTransaction(Transaction transaction, Exception e) {
		try {
			if (transaction.isNewTransaction()) {
				connectionManager.setTransactionActive(false);
				if (transaction.containsSavepoint()) {
					Savepoint savepoint = transaction
							.getSavePointByException(e);
					if (savepoint == null) {
						connectionManager.rollback(transaction.getConnection());
					} else {
						connectionManager.rollback(transaction.getConnection(),
								savepoint);
					}
				} else {
					connectionManager.rollback(transaction.getConnection());
				}
			}
		} catch (Exception e2) {
			throw new TransactionException();
		}
	}

	public void closeTransaction(Transaction transaction) {
		try {
			if (transaction.isNewTransaction()) {
				this.transactions.remove();
				connectionManager.setTransactionActive(false);
				connectionManager
						.releaseConnection(transaction.getConnection());
			} else {
				transaction.completeService();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new TransactionException();
		}

	}

	/**
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */

	/**
	 * @return the connectionManager
	 */
	public DatabaseConnection getConnectionManager() {
		return connectionManager;
	}

	/**
	 * @param connectionManager
	 *            the connectionManager to set
	 */
	public void setConnectionManager(DatabaseConnection connectionManager) {
		this.connectionManager = connectionManager;
	}

	public void addSavePoint( String savePointName,
			Exception rollbackException) {
		Transaction tr = this.transactions.get();
		if (tr == null) {
			tr = this.beginTransaction();
		}
		try {
			if (!tr.isSupportSavepoint()) {
				throw new SavePointNotSupportException();
			}
			tr.addSavepointAndRollbackException(savePointName,
					rollbackException);
		} catch (Exception e) {
			throw new TransactionException();
		}
	}
}
