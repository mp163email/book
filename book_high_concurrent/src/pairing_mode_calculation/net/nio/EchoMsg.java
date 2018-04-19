package pairing_mode_calculation.net.nio;

import java.nio.ByteBuffer;
import java.util.LinkedList;


/**
 * 封装了一个队列,保存了需要回复给这个客户端的所有消息,这样在进行回复时,只要从outq对象中取出元素即可
 * @author mp
 * @date 2016-7-28 下午3:57:28
 */
public class EchoMsg {
	
	/**
	 * 有序队列,之所以是ByteBuffer类型的,是因为它可以封装所有类型
	 */
	private LinkedList<ByteBuffer> outq;
	
	/**
	 * 构造方法的时候实例化
	 */
	public EchoMsg() {
		outq = new LinkedList<>();
	}
	
	/**
	 * 获取这个List
	 * @author mp
	 * @date 2016-7-28 下午4:35:13
	 * @return
	 * @Description
	 */
	public LinkedList<ByteBuffer> getLinkedList () {
		return outq;
	}
	
	/**
	 * 追加消息
	 * @author mp
	 * @date 2016-7-28 下午4:34:28
	 * @param bb
	 * @Description
	 */
	public void addFirst (ByteBuffer bb) {
		outq.addFirst(bb);
	}
	
}
