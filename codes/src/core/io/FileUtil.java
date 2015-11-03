
package core.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 文件工具
 * 
 * @author Jing
 */
public class FileUtil
{

	private FileUtil()
	{
	}

	/**
	 * 以默认字符编码UTF-8读取文件内容
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	static public String readFile(String path) throws IOException
	{
		return readFile(path, "UTF-8");
	}

	/**
	 * 默认以UTF-8编码将内容附加到指定文件末尾处
	 * 
	 * @param content
	 * @param path
	 * @throws IOException
	 */
	static public void appendFile(String content, String path) throws IOException
	{
		writeFile(content, path, "UTF-8", true);
	}

	/**
	 * 默认以UTF-8编码写文件
	 * 
	 * @param content
	 * @param path
	 * @throws IOException
	 */
	static public void writeFile(String content, String path) throws IOException
	{
		writeFile(content, path, "UTF-8", false);
	}

	/**
	 * 以指定编码读取文件内容
	 * 
	 * @param path
	 * @param charsetName
	 * @return
	 * @throws IOException
	 */
	static public String readFile(String path, String charsetName) throws IOException
	{
		File file = new File(path);

		if(!file.exists() || file.isDirectory())
		{
			// 不是一个文件
			return null;
		}

		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charsetName);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		StringBuffer stringBuffer = new StringBuffer();
		int temp = 0;
		while((temp = bufferedReader.read()) != -1)
		{
			stringBuffer.append((char)temp);
		}

		bufferedReader.close();

		return stringBuffer.toString();
	}

	/**
	 * 向磁盘写文件
	 * 
	 * @param content
	 * @param fileName
	 * @param dirPath
	 * @throws IOException
	 */
	static public void writeFile(String content, String path, String charsetName, boolean append) throws IOException
	{
		FileOutputStream fileOutputStream = new FileOutputStream(path, append);
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, charsetName);
		BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

		bufferedWriter.write(content);
		bufferedWriter.flush();
		bufferedWriter.close();
	}
}
