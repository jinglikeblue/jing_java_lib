package core.net.server.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Vector;

/**
 * 序列化数据工具
 * @author Jing
 *
 */
public class Serialize
{
	/**
	 * 数据类型
	 * @author Jing
	 *
	 */
	private enum EType
	{
		BYTE,
		SHORT,
		INT,
		LONG,
		UTF_STRING
	}
	
	/**
	 * 写入的对象
	 * @author Jing
	 *
	 */
	private class Obj
	{
		public EType type;
		public Object value;
		
		public Obj(EType t, Object v)
		{
			this.type = t;
			this.value = v;
		}
	}
	
	private int _size;
	private Vector<Obj> _objs= null;
	
	public Serialize()
	{
		_size = 0;
		_objs = new Vector<Obj>();
	}
	
	public void writeByte(byte v)
	{
		_objs.add(new Obj(EType.BYTE, v));
		_size += 1;		
	}

	public void writeShort(short v)
	{
		_objs.add(new Obj(EType.SHORT, v));
		_size += 2;		
	}

	public void writeInt(int v)
	{
		_objs.add(new Obj(EType.INT, v));
		_size += 4;	
	}

	public void writeLong(long v)
	{
		_objs.add(new Obj(EType.LONG, v));
		_size += 8;	
	}

	public void writeUTFString(String v)
	{
		byte[] strBytes = null;
		try
		{
			strBytes = v.getBytes("UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}

		if(null != strBytes)
		{
			_objs.add(new Obj(EType.UTF_STRING, strBytes));
			_size += strBytes.length + 2;	
		}
	}
	
	public byte[] toBytes()
	{
		//byte[] b = new byte[_size];
		ByteBuffer bb = ByteBuffer.allocate(_size);
		
		Iterator<Obj> iter = _objs.iterator();
		while(iter.hasNext())
		{
			Obj obj = iter.next();
			
			switch(obj.type)
			{
				case BYTE:			
					bb.put((byte)obj.value);
					break;
				case SHORT:
					bb.putShort((short)obj.value);
					break;
				case INT:
					bb.putInt((int)obj.value);
					break;
				case LONG:
					bb.putLong((long)obj.value);
					break;
				case UTF_STRING:
					byte[] bytes = (byte[])obj.value;
					bb.putShort((short)bytes.length);
					bb.put(bytes);
					break;
			}
		}		
		return bb.array();
	}
}
