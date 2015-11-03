package core.db.worker;

import java.sql.SQLException;

import core.net.server.Console;


/**
 * 不使用该对象时一定要调用dispose销毁对象
 */
public class MysqlUpdateWorker extends AMysqlWorker
{	
	private MysqlUpdateWorker()
	{
		
	}
	
	public static MysqlUpdateWorker create(String address, int port, String user, String password, String dbName)
	{
		MysqlUpdateWorker worker = new MysqlUpdateWorker();
		if(false == worker.connect(address, port, user, password, dbName))
		{
			return null;
		}
		
		worker.start("MysqlUpdatePool_Thread");
		return worker;
	}

	protected void excute(MysqlWorkerVO vo)
	{
		int count = ms.update(vo.sql);
		try
		{
			vo.user.updateResult(vo.key, vo.data, count);
		}
		catch(SQLException e)
		{
			Console.log.error(e);
		}
	}
}
