
package core.net.server;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import core.net.server.Server.EVENT;
import core.net.server.interfaces.IPacket;
import core.net.server.interfaces.IProtocolCacher;

/**
 * 连接到服务器的客户端
 * 
 * @author Jing
 */
public class Client
{

	/**
	 * 客户端的连接通道
	 */
	private SocketChannel _channel = null;

	/**
	 * 连接客户端的通道
	 */
	public SocketChannel channel()
	{
		return _channel;
	}
	
	private ByteBuffer _buff = null;
	
	/**
	 * 缓冲区
	 * @return
	 */
	public ByteBuffer buff()
	{
		return _buff;
	}

	public Client(SocketChannel channel, ByteBuffer buff)
	{
		_channel = channel;
		_buff = buff;
	}

	/**
	 * 接收到数据
	 * 
	 * @param buff 数据内容
	 * @return 使用了的数据长度
	 */
	public void onAcceptProtocol(IPacket packet) throws IOException
	{
		// 获取协议号码
		short code = packet.getProtoId();

		IProtocolCacher cacher = Server.instance().getProtocolCacher(code);
		if(null == cacher)
		{
			System.out.println(String.format("protocol code [%d] no cacher", code));
			return;
		}
		cacher.onCacheProtocol(this, packet);
	}

	/**
	 * 向对应的客户端发送数据
	 * 
	 * @param id 协议编号
	 * @param data 协议的数据
	 */
	public void sendProtocol(short id, byte[] data)
	{
		if(false == _channel.isOpen())
		{
			return;
		}

		IPacket packet = null;

		try
		{
			packet = (IPacket)Server.packetClass.newInstance();
		}
		catch(InstantiationException | IllegalAccessException e1)
		{
			Console.log.error(e1);
		}

		byte[] buff = packet.pack(id, data);
		ByteBuffer bb = ByteBuffer.wrap(buff);
		try
		{
			_channel.write(bb);
		}
		catch(IOException e)
		{
			Console.log.error(e);
		}
	}

	/**
	 * 向对应的客户端发送数据
	 * 
	 * @param id 协议编号
	 * @param data 协议的数据
	 */
	public void sendProtocol(short id, ByteBuffer data)
	{
		byte[] ba = new byte[data.limit()];
		data.position(0);
		data.get(ba);
		sendProtocol(id, ba);
	}

	/**
	 * 销毁客户端
	 */
	public void dispose()
	{
		InetAddress address = _channel.socket().getLocalAddress();

		try
		{
			_channel.close();
		}
		catch(IOException e)
		{
			Console.log.error("dispose client error [" + address + "]", e);
		}

		// 广播一个客户端断开连接的消息
		Server.instance().dispatchEvent(EVENT.CLIENT_DISCONNECT.name(), this);
		Console.log.log("client " + address.toString() + " disposed");
	}

}
