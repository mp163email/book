package pairing_mode_calculation.net.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * NIO-Echo客户端
 * @author mp
 * @date 2016-7-29 上午10:17:48
 */
public class EchoClient {
	
	private Selector selector;
	
	/**
	 * 初始化一个客户端连接
	 * @author mp
	 * @date 2016-7-29 上午10:31:30
	 * @param ip
	 * @param port
	 * @throws Exception 
	 * @Description
	 */
	private void start (int port) throws Exception {
		SocketChannel channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.selector = SelectorProvider.provider().openSelector();
		channel.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
		channel.register(selector, SelectionKey.OP_CONNECT);//注册connect事件
		
		while (true) {
			if (!selector.isOpen()) {
				break;
			}
			selector.select();
			Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				
				System.out.println();
				System.out.println("----------------------------------");
				System.out.println("sk.isAcceptable()" + key.isAcceptable());
				System.out.println("sk.isConnectable()" + key.isConnectable());
				System.out.println("sk.isReadable()" + key.isReadable());
				System.out.println("sk.isWritable()" + key.isWritable());
				System.out.println("sk.isValid()" + key.isValid());
				System.out.println("----------------------------------");
				System.out.println();
				
				iterator.remove();
				
				//连接处理
				if (key.isConnectable()) {
					connect(key);
				}
				
				//读数据处理
				else if (key.isReadable()) {
					read(key);
				}
			}
		}
	}
	
	/**
	 * 连接方法
	 * @author mp
	 * @date 2016-7-29 上午10:38:37
	 * @param key
	 * @throws IOException
	 * @throws InterruptedException 
	 * @Description
	 */
	private void connect (SelectionKey key) throws IOException, InterruptedException {
		SocketChannel channel = (SocketChannel) key.channel();
		//如果正在连接,则完成这个连接
		if (channel.isConnectionPending()) {
			channel.finishConnect();
		}
		channel.configureBlocking(false);
		TimeUnit.SECONDS.sleep(10);//模拟写入很慢
		channel.write(ByteBuffer.wrap("hello server !\r\n".getBytes()));//连到服务器后,向服务器写了一条消息
		channel.register(selector, SelectionKey.OP_READ);
	}
	
	/**
	 * 读取消息,读完后,关闭Channel和选择器
	 * @author mp
	 * @date 2016-7-29 上午10:41:43
	 * @param key
	 * @throws Exception
	 * @Description
	 */
	private void read (SelectionKey key) throws Exception {
		SocketChannel channel = (SocketChannel) key.channel();
		//创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(100);
		channel.read(buffer);
		byte[] data = buffer.array();
		String msg = new String (data).trim();
		System.out.println("客户端收到的消息：" + msg);
		channel.close();
		key.selector().close();
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-29 上午10:42:31
	 * @param args
	 * @throws Exception 
	 * @Description
	 */
	public static void main(String[] args) throws Exception {
		EchoClient echoClient = new EchoClient();
		echoClient.start(8000);
	}
}
