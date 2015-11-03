package core.db.worker;

import java.sql.ResultSet;
import java.sql.SQLException;




public interface IMysqlWorkerUser
{
	/**
	 * 查询结果
	 * @param key 
	 * @param rs
	 */
	void queryResult(String key, Object data, ResultSet rs) throws SQLException;
	
	/**
	 * 更新结果
	 * @param key
	 * @param effectedCount
	 */
	void updateResult(String key, Object data, int effectedCount) throws SQLException;
}
