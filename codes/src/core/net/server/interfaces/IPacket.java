
package core.net.server.interfaces;

/**
 * 协议包接口
 * 
 * @author Jing
 */
public interface IPacket
{

	/**
	 * 协议打包
	 * 
	 * @param protoId
	 * @param protoData
	 * @return
	 */
	byte[] pack(short protoId, byte[] protoData);

	/**
	 * 协议解包
	 * 
	 * @param buffer
	 * @param offset
	 * @return
	 * <ul>
	 * <li>< 0:出错</li>
	 * <li>>= 0:使用字节数</li> 
	 * </ul>
	 */
	int unpack(byte[] buffer, int offset);

	/**
	 * 协议包的长度
	 * 
	 * @return
	 */
	short getLength();
	
	/**
	 * 协议号
	 * 
	 * @return
	 */
	short getProtoId();

	/**
	 * 协议数据
	 * 
	 * @return
	 */
	byte[] getProtoData();

}
