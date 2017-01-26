package com.waspring.wasdbtools.test;

import java.sql.ResultSet;

import com.waspring.wasdbtools.DaoUtil;

/**
 * 查询数据测试
 * 
 * @author felly
 * 
 */
public class DBQueryTest {
	private String connName = "wdataSource";

	///
	public static void main(String sfs[]) throws Exception {
		DBQueryTest t = new DBQueryTest();

		t.queryData();
	}

	
	/**
	 * 查询数据测试
	 * @throws Exception
	 */
	public void queryData() throws Exception {
		String sql = "select *  from d_order limit ?";

		ResultSet rs = DaoUtil.queryData(connName, sql, new Object[] { 10 });

		while (rs.next()) {
			System.out.println("data=" + rs.getString(1) + ":"
					+ rs.getString(2)
					+":"+rs.getDate("create_date")
					+":"+rs.getDouble(1));
		}
	}
}
