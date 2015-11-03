
package core.net.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import core.io.FileUtil;
import core.net.server.Console;

/**
 * 日志工具
 * 
 * @author Jing
 */
public class Log
{
	//目录路径
	private String _dirPath;
	//日志文件路径
	private String _logPath;
	//错误记录路径
	private String _errorLogPath;
	//警告记录路径
	private String _warningLogPath;

	/**
	 * @param path 日志存放路径
	 * @throws IOException 
	 */
	public Log(String path) 
	{
		_dirPath = path;
		File dir = new File(_dirPath);		
		dir.mkdirs();
		
		try
		{
			_logPath = createLogFile("log", dir);
			_errorLogPath = createLogFile("error", dir);
			_warningLogPath = createLogFile("warning", dir);
		}
		catch(IOException e)
		{			
			Console.printError("log create error");
		}		

		Console.printInfo("log path is [%s]", dir.getAbsolutePath());
	}
	
	private String createLogFile(String name, File dir) throws IOException
	{		
		File log = new File(dir, name + ".txt");
		
		if(log.exists())
		{
			Console.printInfo("log file [%s] existed!", log.getName());		
		}		
		else if(log.createNewFile())
		{
			Console.printInfo("log file [%s] create success!", log.getName());			
		}
		else
		{
			Console.printInfo("log file [%s] create fail!", log.getName());
		}
		
		return log.getAbsolutePath();
	}

	/**
	 * 将内容写入日志文件
	 * @param content
	 * @param path
	 */
	private void writeLog(String content, String path)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dateFormat.format(new Date());
		content = String.format("%s    %s\r\n", time, content);
	
		try
		{
			//写到本地文件
			FileUtil.appendFile(content, path);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 记录日志内容
	 * 
	 * @param content
	 * @throws IOException
	 */
	public void log(String content)
	{
		writeLog(content, _logPath);
		Console.printInfo(content);
	}
	
	/**
	 * 记录报错信息
	 * @param content
	 */	
	public void error(String content)
	{
		writeLog(content, _errorLogPath);
		Console.printError(content);
	}
	
	/**
	 * 记录报错信息
	 * @param error
	 */
	public void error(Throwable error)
	{
		ByteArrayOutputStream bOS = new ByteArrayOutputStream();
		error.printStackTrace(new PrintStream(bOS));
		error(bOS.toString());
	}
	
	/**
	 * 记录报错信息
	 * @param error
	 */
	public void error(String content, Throwable error)
	{
		ByteArrayOutputStream bOS = new ByteArrayOutputStream();
		error.printStackTrace(new PrintStream(bOS));
		error(content + "\r\n" + bOS.toString());
	}
	
	/**
	 * 记录警告内容
	 * @param content
	 */
	public void warning(String content)
	{
		writeLog(content, _warningLogPath);
		Console.printWarning(content);
	}
}
