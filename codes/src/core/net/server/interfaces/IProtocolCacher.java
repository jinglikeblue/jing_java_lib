
package core.net.server.interfaces;

import java.io.IOException;

import core.net.server.Client;

/**
 * 协议捕获者
 * 
 * @author Jing
 */
public interface IProtocolCacher
{

	/**
	 * 捕获到了协议
	 * 
	 * @param client
	 * @param protocolCode
	 * @param buff
	 * @throws IOException
	 */
	void onCacheProtocol(Client client, IPacket packet) throws IOException;
}
