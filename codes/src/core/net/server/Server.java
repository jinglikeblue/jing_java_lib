
package core.net.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

import core.events.EventDispatcher;
import core.net.server.interfaces.IPacket;
import core.net.server.interfaces.IProtocolCacher;

/**
 * 服务类 单例模式
 * 
 * @author Jing
 */
public class Server extends EventDispatcher
{

	/**
	 * 协议包类
	 */
	static public Class<?> packetClass = Packet.class;

	private Server()
	{

	}

	static private Server _instance = null;

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	static public Server instance()
	{
		if(null == _instance)
		{
			_instance = new Server();
		}
		return _instance;
	}

	/**
	 * 服务器事件
	 * 
	 * @author Jing
	 */
	static public enum EVENT
	{
		/**
		 * 服务器进入新的一帧
		 */
		ENTER_FRAME,
		
		/**
		 * 客户端已连接
		 */
		CLIENT_CONNECTED,

		/**
		 * 客户端断开连接
		 */
		CLIENT_DISCONNECT
	}

	// 协议捕获器字典
	private HashMap<Short, IProtocolCacher> _protocolCacherMap = new HashMap<Short, IProtocolCacher>();

	// 连接上的客户端
	private HashMap<SocketChannel, Client> _onlineMap = new HashMap<SocketChannel, Client>();

	/**
	 * 是否指定的客户端已连接
	 * 
	 * @param client
	 * @return
	 */
	public boolean containsClient(Client client)
	{
		return _onlineMap.containsValue(client);
	}

	private int _port;

	/**
	 * 监听的端口号
	 * 
	 * @return
	 */
	public int port()
	{
		return _port;
	}

	private int _buffSize;

	/**
	 * 缓冲区大小
	 * 
	 * @return
	 */
	public int buffSize()
	{
		return _buffSize;
	}

	private int _fps;

	/**
	 * 服务器帧率
	 * 
	 * @return
	 */
	public int fps()
	{
		return _fps;
	}

	/**
	 * 服务器停止标记
	 */
	private int _stopMark = 0;

	/**
	 * 停止服务器
	 */
	public void stop()
	{
		_stopMark = 1;
	}

	static public void initStop()
	{
		new Thread()
		{

			public void run()
			{
				File shutdownFile = new File("del2stop.server");
				if(false == shutdownFile.exists())
				{
					try
					{
						shutdownFile.createNewFile();
					}
					catch(IOException e)
					{
						Console.log.error(e);
					}
				}

				while(true)
				{
					try
					{
						if(shutdownFile.exists())
						{
							Thread.sleep(1000);
						}
						else
						{
							Server.instance().stop();
						}
					}
					catch(InterruptedException e)
					{
						Console.log.error(e);
					}
				}
			}
		}.start();
	}

	/**
	 * 启动服务器
	 * 
	 * @param port 监听的端口
	 * @param buffSize 缓冲区大小
	 * @param fps 服务器刷新的帧率(帧/秒)
	 * @throws IOException
	 */
	public void run(int port, int buffSize, int fps) throws IOException
	{
		Server.initStop();
		_port = port;
		_buffSize = buffSize;
		_fps = fps;

		long timeout = 1000 / fps;

		Selector selector = Selector.open();

		ServerSocketChannel listenerChannel = ServerSocketChannel.open();

		listenerChannel.socket().bind(new InetSocketAddress(port));

		listenerChannel.configureBlocking(false);

		listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

		Console.log.log("server start. listening port " + port);

		while(true)
		{
			if(selector.select(timeout) > 0)
			{
				Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();

				while(keyIter.hasNext())
				{
					SelectionKey key = keyIter.next();

					try
					{
						if(key.isAcceptable())
						{
							// 接收到连接
							handleAccept(key);
						}

						if(key.isReadable())
						{
							// 接收到数据							
							handleRead((Client)key.attachment());
						}
					}
					catch(IOException | InstantiationException | IllegalAccessException e)
					{
						Console.log.error(e);
					}
					keyIter.remove();
				}
			}

			enterFrame();

			if(1 == _stopMark)
			{
				Console.log.log("server stopped");
				System.exit(0);
				break;
			}
		}
	}

	protected void enterFrame()
	{
		this.dispatchEvent(EVENT.ENTER_FRAME.name(), null);
	}

	/**
	 * 获取协议捕捉器
	 * 
	 * @param protocolCode
	 * @return
	 */
	public IProtocolCacher getProtocolCacher(short protocolCode)
	{
		IProtocolCacher iPC = null;
		
		try
		{
			iPC = _protocolCacherMap.get(protocolCode).getClass().newInstance();
		}
		catch(InstantiationException | IllegalAccessException e)
		{
			Console.log.error(e);
		}
		return iPC;
	}

	/**
	 * 注册协议的捕获器
	 * 
	 * @param protocolCode
	 * @param cacher
	 */
	public void registProtocolCacher(short protocolCode, IProtocolCacher cacher)
	{
		_protocolCacherMap.put(protocolCode, cacher);
	}

	// ---------------------协议处理相关代码--------------------------------

	private void handleAccept(SelectionKey key) throws IOException, InstantiationException, IllegalAccessException
	{
		// 获取客户端的SocketChannel
		SocketChannel clientChannel = ((ServerSocketChannel)key.channel()).accept();
		
		if(_onlineMap.get(clientChannel) != null)
		{
			throw new IOException("can't accept same key twice");
		}		
		Client client = new Client(clientChannel, ByteBuffer.allocate(_buffSize));
		_onlineMap.put(clientChannel, client);		
		
		// 设置为非阻塞模式
		clientChannel.configureBlocking(false);
		// 向给定的选择器注册此通道，返回一个选择键
		clientChannel.register(key.selector(), SelectionKey.OP_READ, client);

		this.dispatchEvent(EVENT.CLIENT_CONNECTED.name(), client);
		Console.printInfo("one client connected");
	}

	private void handleRead(Client client) throws InstantiationException, IllegalAccessException, IOException
	{
		ByteBuffer buff = client.buff();

		int bytesRead = -1;
		try
		{
			bytesRead = client.channel().read(buff);
		}
		catch(IOException e)
		{
			bytesRead = -1;
		}

		if(bytesRead == -1)
		{
			disconnectClient(client);
		}
		else
		{
			buff.flip();
			int limit = buff.limit();
			byte[] ba = new byte[limit];
			buff.get(ba);

			// 进行协议的拆包处理
			int used = parse(ba, client);
			if(used < 0)
			{
				disconnectClient(client);
			}
			else if(used > 0)
			{
				int remain = limit - used;
				buff.clear();
				if(remain > 0)
				{
					buff.put(ba, used, remain);
				}
				buff.position(remain);
			}
			else
			{
				buff.limit(_buffSize);
			}
		}
	}

	private void disconnectClient(Client client) throws IOException
	{
		client.dispose();
		if(null == _onlineMap.remove(client.channel()))
		{
			throw new IOException("wrong client disconnect");
		}
		Console.printInfo("one client disconnected");
	}

	public void handleWrite(SelectionKey key) throws IOException
	{
		// 这个用不上
	}

	/**
	 * 协议拆包
	 * 
	 * @param buff
	 * @param client
	 * @return <ul>
	 *         <li>< 0:出错</li>
	 *         <li>>=0:使用字节数</li>
	 *         </ul>
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private int parse(byte[] ba, Client client) throws IOException, InstantiationException, IllegalAccessException
	{
		int used = 0;

		while(true)
		{
			IPacket packet = null;

			packet = (IPacket)packetClass.newInstance();

			int res = packet.unpack(ba, used);

			if(res < 0)
			{
				return res;
			}
			else if(0 == res)
			{
				break;
			}

			client.onAcceptProtocol(packet);
			used += res;

			if(res == ba.length)
			{
				break;
			}
		}

		return used;
	}
}
