package com.waspring.wasdbtools;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.context.ContextLoader;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 连接对象，引用连接池对象， 同时呢使用spring的注入配置
 * 
 * @author felly
 * 
 */
public class DatabaseConnection {

	public static String CONN_DEFAULT_NAME = "defaultConnName";
	private static String connConfig = "classpath:database.xml";
	private static Boolean isTransactionActive = Boolean.FALSE;

	/**
	 * 获取配置
	 * 
	 * @return
	 */
	public static String getConnConfig() {
		return connConfig;
	}

	/**
	 * 设置配置
	 * 
	 * @param connConfig
	 */
	public static void setConnConfig(String connConfig) {
		DatabaseConnection.connConfig = connConfig;
	}

	/**
	 * 提交事务
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void commit(Connection connection) throws SQLException {
		if (!isTransactionActive && isValid(connection)) {
			connection.commit();
		}
	}

	/**
	 * 回滚事务
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void rollback(Connection connection) throws SQLException {
		if (!isTransactionActive && isValid(connection)) {
			connection.rollback();
		}
	}

	/**
	 * 回滚事务到指定点
	 * 
	 * @param connection
	 * @param savepoint
	 * @throws SQLException
	 */
	public static void rollback(Connection connection, Savepoint savepoint)
			throws SQLException {
		if (!isTransactionActive && isValid(connection)) {
			connection.rollback(savepoint);
		}
	}

	/**
	 * 释放连接
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void releaseConnection(Connection connection)
			throws SQLException {
		if (!isTransactionActive && isValid(connection)) {
			threadLocal.remove();
			connection.close();
		}

	}

	/**
	 * 判断是否是激活事务状态
	 * 
	 * @param isTransactionFinished
	 *            the isTransactionFinished to set
	 */
	public static void setTransactionActive(Boolean itransactionActive) {
		isTransactionActive = itransactionActive;
	}

	private static ApplicationContext context = null;
	private static DruidDataSource ds = null;
	// 将线程和连接绑定，保证事务能统一执行
	private static ThreadLocal<Map<String, Connection>> threadLocal = new ThreadLocal<Map<String, Connection>>();

	/**
	 * 从线程池获取连接
	 */
	public static Connection getConnection(String connName) throws SQLException {
		Map<String, Connection> mc = threadLocal.get();
		if (mc == null) {
			Map<String, Connection> mcm = new HashMap<String, Connection>();
			ds = (DruidDataSource) context.getBean(connName);
			Connection conn = ds.getConnection();

			mcm.put(connName, conn);

			threadLocal.set(mcm);
			return conn;
		} else {
			Connection conn = mc.get(connName);
			if (!isValid(conn)) {
				ds = (DruidDataSource) context.getBean(connName);
				conn = ds.getConnection();
				Map<String, Connection> mcm = new HashMap<String, Connection>();

				mcm.put(connName, conn);

				threadLocal.set(mcm);
			}

			return conn;
		}

	}

	/**
	 * 获取数据库连接对象
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		try {
			// 默认线程里面取
			return getConnection(CONN_DEFAULT_NAME);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * 判断连接是否可用
	 * 
	 * @param conn
	 * @return
	 */
	public static boolean isValid(Connection conn) {
		try {
			if (conn == null || conn.isClosed()) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 初始化,默认
	 */
	private DatabaseConnection() {
		try {

			if (context == null) {
				// 先加载web容器的spring配置容器
				context = ContextLoader.getCurrentWebApplicationContext();

				if (context == null) {
					// 再加载本地的spring配置容器
					context = new FileSystemXmlApplicationContext(
							DatabaseConnection.connConfig);

				}
			}

		} catch (Exception ex) {
			// 加载异常再次使用本地的spring配置容器
			ex.printStackTrace();
			if (context == null) {
				context = new FileSystemXmlApplicationContext(
						DatabaseConnection.connConfig);

			}

		} finally {
			// 最终获取数据源对象

			setContext(context);
		}
	}

	private static class DatabaseConnectionImpl {
		private static DatabaseConnection db = new DatabaseConnection();
	}

	public static DatabaseConnection getInstance() {
		return DatabaseConnectionImpl.db;
	}

	public ApplicationContext getContext() {
		return context;
	}

	// //
	public void setContext(ApplicationContext context) {
		this.context = context;
	}

}
