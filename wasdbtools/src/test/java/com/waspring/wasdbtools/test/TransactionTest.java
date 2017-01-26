package com.waspring.wasdbtools.test;

import com.waspring.wasdbtools.DaoUtil;
import com.waspring.wasdbtools.Transaction;
import com.waspring.wasdbtools.TransactionCallback;
import com.waspring.wasdbtools.TransactionManager;
import com.waspring.wasdbtools.TransactionTemplate;

/**
 * 事务举例
 * 
 * @author felly
 * 
 */
public class TransactionTest {
	private String connName = "wdataSource";
	private TransactionTemplate tmp = new TransactionTemplate(connName);

	/**
	 * 手动声明事务的处理
	 */
	public void testTras2() {
		TransactionManager trManager = new TransactionManager(connName);

		Transaction transaction = null;
		String sql = "insert into a_api_topy(topy_name) values(?)";
		try {
			transaction = trManager.beginTransaction();
			DaoUtil.executeUpdate(connName, sql, new Object[] { "h1" });

			DaoUtil.executeUpdate(connName, sql, new Object[] { "h2" });

			DaoUtil.executeUpdate(connName, sql, new Object[] { "h3" });
			// /这里可以试着抛出异常来回滚
			trManager.commitTransaction(transaction);
		} catch (Exception e) {
			trManager.rollbackTransaction(transaction, e);
		}

	}

	/**
	 * 使用事务模版
	 */
	public void testTras() {

		Object o = tmp.executeTransaction(new TransactionCallback() {
			public Object doTransactionEvent() throws Exception {
				Exception e = new Exception();
				String sql = "insert into a_api_topy(topy_name) values(?)";

				DaoUtil.executeUpdate(connName, sql, new Object[] { "2" });

				DaoUtil.executeUpdate(connName, sql, new Object[] { "4" });

				DaoUtil.executeUpdate(connName, sql, new Object[] { "5" });
				// /这里可以试着抛出异常来回滚

				return "1";
			}
		});

		System.out.println(o);
	}

	/**
	 * 默认事务的处理,自动提交
	 */
	public void testNoTras() {
		try {

			String sql = "insert into a_api_topy(topy_name) values(?)";

			DaoUtil.executeUpdate(connName, sql, new Object[] { "7" });

			DaoUtil.executeUpdate(connName, sql, new Object[] { "8" });

			DaoUtil.executeUpdate(connName, sql, new Object[] { "9" });
			// /这里可以试着抛出异常来回滚
		} catch (Exception e) {

		}

	}

	public static void main(String sfs[]) throws Exception {
		TransactionTest t = new TransactionTest();
		t.testTras();
		t.testNoTras();
		t.testTras2();

	}
}
