
package core.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySql连接工具
 * 
 * @author Jing
 */
public class MySql
{

	private String _address;

	/**
	 * 数据库地址
	 * 
	 * @return
	 */
	public String address()
	{
		return _address;
	}

	private int _port;

	/**
	 * 数据库端口
	 * 
	 * @return
	 */
	public int port()
	{
		return _port;
	}

	private String _dbName;

	private String _user;

	private String _pwd;

	// 创建用于连接数据库的Connection对象
	private Connection _conn = null;

	/**
	 * 到数据库的连接对象
	 * 
	 * @return
	 */
	public Connection connection()
	{
		return _conn;
	}
	
	/**
	 * Mysql是否连接
	 * @return
	 */
	public boolean isConnected()
	{
		boolean isConnected = false;		
		try
		{
			if(_conn != null && false == _conn.isClosed())
			{
				isConnected = true;
			}
		}
		catch(SQLException e)
		{			
			e.printStackTrace();
			isConnected = false;
		}
		return isConnected;
	}

	public MySql()
	{

	}

	/**
	 * 关闭到数据库的连接
	 */
	public void close()
	{
		try
		{
			if(_conn != null && false == _conn.isClosed())
			{
				_conn.close();
				_conn = null;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 连接数据库
	 * 
	 * @param address 数据库地址
	 * @param port 数据库端口
	 * @param user 数据库用户名
	 * @param password 数据库密码
	 * @param dbName 连接到的数据库
	 * @return
	 */
	public boolean connect(String address, int port, String user, String password, String dbName)
	{
		_address = address;
		_port = port;
		_dbName = dbName;
		_user = user;
		_pwd = password;

		return createConnection();
	}

	/**
	 * 创建连接
	 * 
	 * @return
	 */
	private boolean createConnection()
	{
		try
		{
			if(_conn != null && false == _conn.isClosed())
			{
				return true;
			}

			// 加载Mysql数据驱动
			Class.forName("com.mysql.jdbc.Driver");
			String connectStr = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8", _address, _port, _dbName);
			// 创建数据连接
			_conn = DriverManager.getConnection(connectStr, _user, _pwd);

		}
		catch(Exception e)
		{
			System.out.println("数据库连接失败" + e.getMessage());
			return false;
		}
		return true; // 返回所建立的数据库连接
	}

	/**
	 * 更新数据库
	 * 
	 * @param sql 更新语句
	 * @return 影响的行数
	 */
	public int update(String sql)
	{
		int changedCount = -1;
		
		if(createConnection())
		{

			try
			{
				changedCount = _conn.createStatement().executeUpdate(sql);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				changedCount = -1;
			}

		}

		return changedCount;
	}

	/**
	 * 查询数据库
	 * 
	 * @param sql 查询语句
	 * @return 查询结果集
	 */
	public ResultSet query(String sql)
	{
		if(createConnection())
		{
			try
			{
				return _conn.createStatement().executeQuery(sql);
			}
			catch(SQLException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 查询指定表的数据
	 * 
	 * @param table 表名
	 * @param fields 查询的字段集, null 表示查询所有字段
	 * @param wheres 查询的条件依次为[字段,值,字段,值.....], null 表示没有条件
	 * @return
	 */
//	public ResultSet query(String table, String[] fields, Object[] wheres)
//	{
//		String fieldSql = "*";
//		String whereSql = "";
//
//		if(null != fields)
//		{
//			for(int i = 0; i < fields.length; i++)
//			{
//				if(fieldSql == "*")
//				{
//					fieldSql = fields[i];
//				}
//				else
//				{
//					fieldSql += "," + fields[i];
//				}
//			}
//		}
//
//		if(null != wheres)
//		{
//			for(int i = 0; i < wheres.length; i += 2)
//			{
//				String where = wheres[i] + "='" + wheres[i + 1] + "'";
//				if(whereSql == "")
//				{
//					whereSql = "WHERE " + where;
//				}
//				else
//				{
//					whereSql += " AND " + where;
//				}
//			}
//		}
//
//		String querySql = "SELECT %s FROM %s %s";
//		querySql = String.format(querySql, fieldSql, table, whereSql);
//		return query(querySql);
//	}

	/**
	 * 向指定表插入数据
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public int insert(String table, Object[] values)
	{
		if(null == values || values.length < 1)
		{
			return 0;
		}

		String valueSql = null;

		for(int i = 0; i < values.length; i++)
		{
			if(valueSql == null)
			{
				valueSql = String.format("'%s'", values[i]);
			}
			else
			{
				valueSql += String.format(",'%s'", values[i]);
			}
		}

		String insertSql = "INSERT INTO %s VALUES(%s)";
		insertSql = String.format(insertSql, table, valueSql);
		return update(insertSql);
	}

	/**
	 * 向指定表插入数据
	 * 
	 * @param table
	 * @param values
	 * @return
	 */
	public int insert(String table, String[] fields, Object[] values)
	{
		if(null == fields || fields.length < 1 || null == values || values.length < 1)
		{
			return 0;
		}

		String fieldSql = "";
		String valueSql = "";

		for(int i = 0; i < fields.length; i++)
		{
			String format = "" == fieldSql ? "%s" : ",%s";
			fieldSql += String.format(format, fields[i]);
		}

		for(int i = 0; i < values.length; i++)
		{
			String format = "" == valueSql ? "'%s'" : ",'%s'";
			valueSql += String.format(format, values[i]);
		}

		String insertSql = "INSERT INTO %s(%s) VALUES(%s)";
		insertSql = String.format(insertSql, table, fieldSql, valueSql);
		return update(insertSql);
	}

	/**
	 * 修改表的数据
	 * @param table
	 * @param values
	 * @param wheres 条件依次为[字段,值,字段,值.....], null 表示没有条件
	 * @return
	 */
//	public int update(String table, Object[] values, Object[] wheres)
//	{
//		if(null == values || values.length < 0)
//		{
//			return 0;
//		}
//
//		String valueSql = "";
//		String whereSql = "";
//
//		for(int i = 0; i < values.length; i += 2)
//		{
//			String format = "" == valueSql ? "%s='%s'" : ",%s='%s'";
//			valueSql += String.format(format, values[i], values[i + 1]);
//		}
//		
//		if(null != wheres)
//		{
//			for(int i = 0; i < wheres.length; i += 2)
//			{
//				String where = wheres[i] + "='" + wheres[i + 1] + "'";
//				if(whereSql == "")
//				{
//					whereSql = "WHERE " + where;
//				}
//				else
//				{
//					whereSql += " AND " + where;
//				}
//			}			
//		}
//
//		String sql = "UPDATE %s SET %s %s";
//		sql = String.format(sql, table, valueSql, whereSql);
//		return update(sql);
//	}
}
