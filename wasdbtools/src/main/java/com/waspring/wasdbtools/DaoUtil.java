package com.waspring.wasdbtools;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.waspring.wasdb.comp.CachedRowSet;
import com.waspring.wasdb.util.DateUtil;

/**
 * 数据库执行工具类 这个共工具类执行sql全部支持注入变量Ⅹ查询，所以尽量使用注入变量方式提高数据库性能和安全性
 * 
 * @author felly
 * 
 */
public class DaoUtil {

	/**
	 * 拼接出对应数量的"?"号来，用于注入变量
	 * 
	 * @param num
	 * @return
	 */
	public static String makePix(int num) {
		String s = "";
		for (int i = 0; i < num; i++) {
			s += "?,";
		}
		if (num > 0)
			return s.substring(0, s.length() - 1);
		return s;
	}

	/**
	 * stmt注入处理
	 */
	private static void setStmtPara(java.sql.PreparedStatement stmt, int i,
			Object para) throws SQLException {
		if (para == null) {
			stmt.setObject(i, null);
		} else if (para instanceof Integer) {
			stmt.setInt(i, ((Integer) para).intValue());
		} else if (para instanceof Double) {

			stmt.setDouble(i, ((Double) para).doubleValue());
		}

		else if (para instanceof java.io.InputStream) {

			stmt.setBinaryStream(i, (InputStream) para);
		} else if (para instanceof Boolean) {

			stmt.setBoolean(i, ((Boolean) para).booleanValue());
		} else if (para instanceof Long) {

			stmt.setLong(i, ((Long) para).longValue());
		} else if (para instanceof Float) {

			stmt.setFloat(i, ((Float) para).floatValue());
		} else if (para instanceof Byte) {

			stmt.setByte(i, ((Byte) para).byteValue());
		} else if (para instanceof Short) {

			stmt.setShort(i, ((Short) para).shortValue());
		} else if (para instanceof java.sql.Date) {

			stmt.setDate(i, ((java.sql.Date) para));
		} else if (para instanceof java.util.Date) {
			stmt.setDate(i, DateUtil.toSqlDate((java.util.Date) para));
		} else if (para instanceof BigDecimal) {
			stmt.setLong(i, ((BigDecimal) para).longValue());

		} else if (para instanceof Blob) {
			stmt.setBlob(i, (Blob) para);
		} else if (para instanceof Clob) {
			stmt.setClob(i, (Clob) para);
		} else if (para instanceof String) {
			stmt.setString(i, (String) para);
		} else {
			try {
				stmt.setString(i, (String) para);
			} catch (Exception e) {
				stmt.setObject(i, para);
			}
		}

	}

	/**
	 * 从数据库查询数据
	 * 
	 * @param conn
	 *            连接
	 * @param sql
	 *            sq语句
	 * @param paras
	 *            参数
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet queryData(Connection conn, String sql,
			Object[] paras) throws SQLException {
		java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
		for (int i = 1; i <= paras.length; i++) {
			setStmtPara(stmt, i, paras[i - 1]);

		}

		ResultSet rs = stmt.executeQuery();
		ResultSet localResultSet = rs;
		try {
			CachedRowSet localCachedRowSet = new CachedRowSet();
			localCachedRowSet.populate(localResultSet);
			return localCachedRowSet;
		} finally {
			close(localResultSet);
			close(stmt);
			close(conn);
		}
		// close(stmt);

	}

	/**
	 * 查询数据
	 * 
	 * @param sql
	 *            sq语句
	 * @param paras
	 *            参数
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet queryData(String sql, Object[] paras)
			throws SQLException {
		return queryData(DatabaseConnection.getInstance().getConnection(), sql,
				paras);

	}

	/**
	 * 查询数据
	 * 
	 * @param sql
	 *            sq语句
	 * @param paras
	 *            参数
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet queryData(String connName, String sql,
			Object[] paras) throws SQLException {
		return queryData(
				DatabaseConnection.getInstance().getConnection(connName), sql,
				paras);

	}

	/**
	 * 执行查询，已经不推荐使用
	 * 
	 * @param stmt
	 * @param sql
	 * @param paras
	 * @return
	 * @throws SQLException
	 */
	public static int executeUpdate(java.sql.PreparedStatement stmt,
			String sql, Object[] paras) throws SQLException {
		if (sql != null)
			sql = sql.toLowerCase();
		for (int i = 1; i <= paras.length; i++) {
			setStmtPara(stmt, i, paras[i - 1]);

		}

		int result = -1;
		try {
			result = stmt.executeUpdate();
		} catch (Exception e) {

			throw new SQLException(e);
		} finally {

			close(stmt);
		}
		return result;

	}

	/**
	 * 批量事务处理
	 * 
	 * @param sql
	 * @param items
	 * @return
	 * @throws SQLException
	 */
	public static int executeBatch(String connName, String sql, Object[][] items)
			throws SQLException {
		if (sql != null)
			sql = sql.toLowerCase();
		Connection conn = DatabaseConnection.getInstance().getConnection(
				connName);
		java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
		for (int j = 1; j <= items.length; j++) {
			Object paras[] = items[j - 1];

			for (int i = 1; i <= paras.length; i++) {
				setStmtPara(stmt, i, paras[i - 1]);

			}
			stmt.addBatch();
		}

		int result = -1;

		try {
			stmt.executeBatch();
		} catch (Exception e) {

			throw new SQLException(e);
		} finally {

			close(conn);
			close(stmt);
		}
		return result;
	}

	/**
	 * 批量事务处理
	 * 
	 * @param sql
	 * @param items
	 * @return
	 * @throws SQLException
	 */
	public static int executeBatch(String sql, Object[][] items)
			throws SQLException {
		return executeBatch(DatabaseConnection.CONN_DEFAULT_NAME, sql, items);
	}

	/**
	 * 事务处理，命名不太规范，方法弃用
	 * 
	 * @param sql
	 * @param paras
	 * @return
	 * @throws SQLException
	 */

	public static int executeUpdate(Connection conn, String sql, Object[] paras)
			throws SQLException {

		if (sql != null)
			sql = sql.toLowerCase();
		java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
		for (int i = 1; i <= paras.length; i++) {
			setStmtPara(stmt, i, paras[i - 1]);

		}

		int result = -1;

		try {
			result = stmt.executeUpdate();
		} catch (Exception e) {

			throw new SQLException(e);
		} finally {

			close(conn);
			close(stmt);
		}
		return result;
	}

	/**
	 * 事务处理，命名不太规范，方法弃用
	 * 
	 * @param sql
	 * @param paras
	 * @return
	 * @throws SQLException
	 */

	public static int executeUpdate(String connName, String sql, Object[] paras)
			throws SQLException {
		return executeUpdate(DatabaseConnection.getConnection(connName), sql,
				paras);
	}

	/**
	 * 事务处理，命名不太规范，方法弃用
	 * 
	 * @param sql
	 * @param paras
	 * @return
	 * @throws SQLException
	 */

	public static int executeUpdate(String sql, Object[] paras)
			throws SQLException {
		return executeUpdate(DatabaseConnection.CONN_DEFAULT_NAME, sql, paras);
	}

	/**
	 * 关闭结果集
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public static void close(ResultSet rs) throws SQLException {
		rs.close();
	}

	/**
	 * 关闭statment
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public static void close(PreparedStatement rs) throws SQLException {
		rs.close();
	}

	/**
	 * 关闭连接
	 * 
	 * @param rs
	 * @throws SQLException
	 */
	public static void close(Connection rs) throws SQLException {
		if (rs != null && rs.getAutoCommit()) {
			rs.close();
		}
	}

	/**
	 * 执行存储过程
	 */

	public static void executeProduce(String produceName, Object[] paras)
			throws SQLException {

		executeProduce(DatabaseConnection.CONN_DEFAULT_NAME, produceName, paras);

	}

	/**
	 * 执行存储过程
	 */

	public static void executeProduce(String connName, String produceName,
			Object[] paras) throws SQLException {
		{

			Connection conn = DatabaseConnection.getInstance().getConnection(
					connName);
			String sql = " call " + produceName + "(" + makePix(paras.length)
					+ ") ";
			java.sql.PreparedStatement stmt = conn.prepareStatement(sql);

			for (int i = 1; i <= paras.length; i++) {
				setStmtPara(stmt, i, paras[i - 1]);

			}
			try {
				stmt.execute();
			} catch (Exception e) {
				throw new SQLException(e);
			} finally {

				close(conn);
				close(stmt);
			}

		}

	}
}
