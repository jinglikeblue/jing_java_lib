
package core.db.worker;

/**
 * Mysql数据对象
 * 
 * @author Jing
 */
class MysqlWorkerVO
{

	/**
	 * 查询语句
	 */
	public String sql;

	/**
	 * 查询者
	 */
	public IMysqlWorkerUser user;

	/**
	 * 查询者需要回传的Key
	 */
	public String key;

	/**
	 * 查询者需要回传的对象
	 */
	public Object data;
}
