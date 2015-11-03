
package core.db.worker;

import java.sql.ResultSet;
import java.sql.SQLException;

import core.net.server.Console;

/**
 * 不使用该对象时一定要调用dispose销毁对象
 */
public class MysqlQueryWorker extends AMysqlWorker
{
	private MysqlQueryWorker()
	{

	}

	public static MysqlQueryWorker create(String address, int port, String user, String password, String dbName)
	{
		MysqlQueryWorker worker = new MysqlQueryWorker();
		if(false == worker.connect(address, port, user, password, dbName))
		{
			return null;
		}

		worker.start("MysqlQueryPool_Thread");
		return worker;
	}

	protected void excute(MysqlWorkerVO vo)
	{
		ResultSet rs = ms.query(vo.sql);
		if(null != rs)
		{
			try
			{			
				vo.user.queryResult(vo.key, vo.data, rs);
				rs.close();
			}
			catch(SQLException e)
			{
				Console.log.error(e);
			}
		}
	}

}
