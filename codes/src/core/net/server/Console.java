package core.net.server;

import core.net.server.utils.Log;



/**
 * 控制台
 * @author Jing
 *
 */
public class Console
{
	
	private Console()
	{
		
	}
	
	static public final Log log = new Log("log");
	
	/**
	 * 打印一条信息
	 * @param content
	 */
	static public void printInfo(String content, Object... args)
	{
		content = String.format(content, args);
		System.out.print(content + "\r\n");
	}
	
	/**
	 * 打印一条信息
	 * @param content
	 */
	static public void printInfo(String content)
	{
		System.out.print(content + "\r\n");
	}
	
	/**
	 * 打印一条报错信息
	 * @param content
	 */
	static public void printError(String content)
	{
		content = String.format("\r\n%-30s    %s\r\n", "***   ERROR   ***:", content);
		printInfo(content);
	}
	
	/**
	 * 打印一条警告信息
	 * @param content
	 */
	static public void printWarning(String content)
	{
		content = String.format("\r\n%-30s    %s\r\n", "***  WARNING  ***:", content);
		printInfo(content);
	}	
}
