package pairing_mode_calculation.net.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

/**
 * 操作数据任务类
 * 如果需要处理业务逻辑,就可以在这里处理
 * @author mp
 * @date 2016-7-28 下午4:56:40
 */
public class HandleMsg implements Runnable {

	SelectionKey sk;
	
	ByteBuffer bb;
	
	Selector selector;
	
	public HandleMsg (SelectionKey sk, ByteBuffer bb, Selector selector) {
		this.sk = sk;
		this.bb = bb;
		this.selector = selector;
	}
	
	@Override
	public void run() {
		EchoMsg echoClient = (EchoMsg)sk.attachment();
		echoClient.addFirst(bb);//将读取到的客户端数据放到附件对象里
		sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);//在数据处理完成后,就可以准备将结果回写到客户端,因此,重新注册感兴趣的,将写OP_WRITE也作为选择器感兴趣的事件(这样通道准备好写入时,就能通知到线程)
		//强迫selector立即返回
		selector.wakeup();
	}
	
}
