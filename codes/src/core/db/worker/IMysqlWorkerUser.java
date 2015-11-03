package core.db.worker;

import java.sql.ResultSet;




public interface IMysqlWorkerUser
{
	/**
	 * 查询结果
	 * @param key 
	 * @param rs
	 */
	void queryResult(String key, Object data, ResultSet rs);
	
	/**
	 * 更新结果
	 * @param key
	 * @param effectedCount
	 */
	void updateResult(String key, Object data, int effectedCount);
}
