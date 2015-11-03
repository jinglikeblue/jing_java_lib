
package core.db.worker;

import java.util.Vector;

import core.db.MySql;

/**
 * 数据库基类
 * 
 * @author Jing
 */
abstract public class AMysqlWorker
{

	protected MySql ms;

	private boolean _disposed = false;

	/**
	 * 查询语句队列
	 */
	private Vector<MysqlWorkerVO> sqlList = new Vector<MysqlWorkerVO>();

	public AMysqlWorker()
	{

	}

	/**
	 * 销毁该操作池
	 */
	public void dispose()
	{
		ms.close();
		this._disposed = true;
	}

	/**
	 * 启动线程
	 * @param name
	 */
	public void start(String name)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				while(false == _disposed)
				{
					if(ms.isConnected() && sqlList.size() > 0)
					{
						MysqlWorkerVO vo = sqlList.remove(0);
						excute(vo);
					}
				}
			}
		}, name).start();
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
		if(null != ms)
		{
			ms.close();
			ms = null;
		}

		ms = new MySql();

		boolean success = ms.connect(address, port, user, password, dbName);

		if(false == success)
		{
			ms.close();
			ms = null;
		}
		return success;
	}

	abstract protected void excute(MysqlWorkerVO vo);

	/**
	 * 查询数据库
	 * 
	 * @param sql 查询语句
	 * @param user 查询者
	 * @param key 查询者需要回传的Key
	 * @param data 查询者需要回传的对象
	 */
	public void excute(String sql, IMysqlWorkerUser user, String key, Object data)
	{
		MysqlWorkerVO vo = new MysqlWorkerVO();
		vo.sql = sql;
		vo.user = user;
		vo.key = key;
		vo.data = data;
		sqlList.addElement(vo);
	}
}
