
package core.net.server.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 字节数组
 * 
 * @author Jing
 */
public class ByteArray
{

	private byte[] _bytes = null;

	private ByteBuffer _bb = null;

	public ByteArray(byte[] bytes)
	{
		_bytes = bytes;
		_bb = ByteBuffer.wrap(bytes);
	}

	/**
	 * 字节数据
	 * 
	 * @return
	 */
	public byte[] bytes()
	{
		return this._bytes;
	}

	public int position()
	{
		return _bb.position();
	}

	/**
	 * 设置位置
	 * 
	 * @param pos
	 */
	public void setPosition(int pos)
	{
		if(pos >= _bb.limit())
		{
			pos = _bb.limit() - 1;
		}
		else if(pos < 0)
		{
			pos = 0;
		}
		_bb.position(pos);
	}

	public byte readByte()
	{
		return _bb.get();
	}

	public char readChar()
	{
		return _bb.getChar();
	}

	public short readShort()
	{
		return _bb.getShort();
	}

	public int readInt()
	{
		return _bb.getInt();
	}

	public long readLong()
	{
		return _bb.getLong();
	}

	public String readUTFString()
	{
		short strSize = _bb.getShort();
		byte[] strBytes = new byte[strSize];
		_bb.get(strBytes);
		String str = null;
		try
		{
			str = new String(strBytes, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return str;
	}

	public void writeByte(byte b)
	{
		_bb.put(b);
	}

	public void writeChar(char c)
	{
		_bb.putChar(c);
	}

	public void writeShort(short c)
	{
		_bb.putShort(c);
	}

	public void writeInt(int c)
	{
		_bb.putInt(c);
	}

	public void writeLong(long c)
	{
		_bb.putLong(c);
	}

	public void writeUTFString(String string)
	{
		byte[] strBytes = null;
		try
		{
			strBytes = string.getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		if(null != strBytes)
		{
			_bb.putShort((short)strBytes.length);
			_bb.put(strBytes);
		}
	}

}
