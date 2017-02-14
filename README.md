#关于wasdb
>一个对JDBC进行简单封装的开源工具类库.
>简化JDBC应用程序的开发，不会影响程序的性能,与jdbc保持高度的统一.

1.对于数据表的读操作，他可以把ResultSet结果转换成缓存对象，内部数据格式为XML，同时可以转化为List，Array，Set等java集合，便于程序员操作。
2.对于数据表的写操作，也变得很简单（只需写sql语句）。
3.可以使用数据源，数据库连接池等技术来优化性能--重用已经构建好的数据库连接对象，而不用费时费力的不断重复的构建和析构这样的对象。
4.支持ResultSet数据集的动态增加、删除、修改、查找、排序、并集等操作。
5.支持分布式数据库,多数据源。

本项目主要包含两个模块，wasdb和wasdbtools:
1.wasdb模块是本项目的核心，用于实现JDBC的操作。
2.wasdbtools模块是wasdb使用的一个实现，包括数据源配置,事务管理,数据库的读写工具,简单实例等。


#如何使用?
1.建立新工程并引入wasdb-1.0.0.jar、wasdbtools-1.0.0.jar
   可以使用maven方式引入:
 ``` xml
     	<dependency>
			<groupId>com.waspring.wasdb</groupId>
			<artifactId>wasdbtools</artifactId>
			<version>1.0.0</version>
		</dependency>
```
2.建立配置文件database.xml中配置数据源,bean管理采用spring3.2的配置,数据连接池使用的是druid。
3.新建立类然后设置配置文件的路径，应用启动的时候设置：
 ``` java
com.waspring.wasdbtools.DatabaseConnection.setConnConfig("classpath:你的路径/database.xml");
 ```
4.然后使用工具类操作数据：
   (1)查询数据：
 ``` java
	/**
	 * 查询数据测试
	 * @throws Exception
	 */
	public void queryData() throws Exception {
		String sql = "select *  from d_order limit ?,?";
       ///注意这里的connName即为database.xml的beanId
		ResultSet rs = DaoUtil.queryData(connName, sql, new Object[] { 1,10 });

		while (rs.next()) {
			System.out.println("data=" + rs.getString(1) + ":"
					+ rs.getString(2)
					+":"+rs.getDate("create_date")
					+":"+rs.getDouble(1));
		}
	}
 ```
  (2)更新、删除等操作
     a.带事务的处理
	    i.使用事务模版
 ``` java
	 /**
	   * 使用事务模版
	 */
	public void testTras() {
	   ///注意这里的connName即为database.xml的beanId
       TransactionTemplate tmp = new TransactionTemplate(connName);

		Object o = tmp.executeTransaction(new TransactionCallback() {
			public Object doTransactionEvent() throws Exception {
				Exception e = new Exception();
				String sql = "insert into a_api_topy(topy_name,topy_value) values(?,?)";
                ///注意这里的connName即为database.xml的beanId
				DaoUtil.executeUpdate(connName, sql, new Object[] { "2",4 });
                ///注意这里的connName即为database.xml的beanId
				DaoUtil.executeUpdate(connName, sql, new Object[] { "4",5 });
                ///注意这里的connName即为database.xml的beanId
				DaoUtil.executeUpdate(connName, sql, new Object[] { "5" ,7});
				// /这里可以试着抛出异常来回滚

				return "1";
			}
		});

		System.out.println(o);
	}
 ```
		ii.手动控制事务
		
 ``` java
		    /**
	 * 手动声明事务的处理
	 */
	public void testTras2() {
	 ///注意这里的connName即为database.xml的beanId
		TransactionManager trManager = new TransactionManager(connName);

		Transaction transaction = null;
		String sql = "insert into a_api_topy(topy_name,topy_value) values(?,?)";
		try {
			transaction = trManager.beginTransaction();
			 ///注意这里的connName即为database.xml的beanId
			DaoUtil.executeUpdate(connName, sql, new Object[] { "h1",1 });
            ///注意这里的connName即为database.xml的beanId
			DaoUtil.executeUpdate(connName, sql, new Object[] { "h2",2 });
             ///注意这里的connName即为database.xml的beanId
			DaoUtil.executeUpdate(connName, sql, new Object[] { "h3",3 });
			// /这里可以试着抛出异常来回滚
			trManager.commitTransaction(transaction);
		} catch (Exception e) {
			trManager.rollbackTransaction(transaction, e);
		}

	}
 ```
	 b.不带事务控制的使用
 ``` java
	   /**
	 * 默认事务的处理,自动提交
	 */
	public void testNoTras() {
		try {

			String sql = "insert into a_api_topy(topy_name,topy_value) values(?,?)";
            ///注意这里的connName即为database.xml的beanId
			DaoUtil.executeUpdate(connName, sql, new Object[] { "7",1 });
            ///注意这里的connName即为database.xml的beanId
			DaoUtil.executeUpdate(connName, sql, new Object[] { "8" ,2});
            ///注意这里的connName即为database.xml的beanId
			DaoUtil.executeUpdate(connName, sql, new Object[] { "9" ,3});
			// /这里可以试着抛出异常来回滚
		} catch (Exception e) {

		}

	}
 ```
	
	
	
	
	
	