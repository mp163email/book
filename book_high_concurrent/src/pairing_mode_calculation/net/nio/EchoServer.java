package pairing_mode_calculation.net.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 新IO-服务器端
 * 当客户端通过connect方法连接服务器的时候,就会触发服务器的OP_ACCEPT
 * 当服务器通过accept方法接入客户端,就会触发客户端的OP_CONNECTION
 * 当客户端write了数据以后,也就是InputStream有值了,服务器就能监听到OP_READ
 * 当客户端的OutputStream为空时,就会触发OP_WRITE
 * @author mp
 * @date 2016-7-28 下午3:40:17
 */
public class EchoServer {
	
	private Selector selector;//选择器
	
	private ExecutorService tp = Executors.newCachedThreadPool();//处理客户端请求
	
	public static Map<Socket, Long> time_stat = new HashMap<Socket, Long>();//用于统计某个socket上花费的时间
	
	/**
	 * 起服务器服务
	 * @author mp
	 * @date 2016-7-28 下午5:41:08
	 * @throws Exception
	 * @Description
	 */
	private void startServer () throws Exception {
		selector = SelectorProvider.provider().openSelector();//实例化一个Selector
		ServerSocketChannel ssc = ServerSocketChannel.open();//实例化一个服务器Channel
		ssc.configureBlocking(false);//设置成非阻塞模式
		
		InetSocketAddress isa = new InetSocketAddress(InetAddress.getLocalHost(), 8000);
		ssc.socket().bind(isa);
		
		System.out.println("----server start----");
		
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		for (;;) {
			selector.select();//阻塞的,只要检测到客户端有选择器感兴趣的事件,就会得到响应
			Set<SelectionKey> readyKeys = selector.selectedKeys();//因为是对应多个客户端,所以有可能响应的是多个
			Iterator<SelectionKey> i = readyKeys.iterator();
			long e = 0;
			while (i.hasNext()) {
				SelectionKey sk = (SelectionKey) i.next();
				System.out.println();
				System.out.println("----------------------------------");
				System.out.println("sk.isAcceptable()" + sk.isAcceptable());
				System.out.println("sk.isConnectable()" + sk.isConnectable());
				System.out.println("sk.isReadable()" + sk.isReadable());
				System.out.println("sk.isWritable()" + sk.isWritable());
				System.out.println("sk.isValid()" + sk.isValid());
				System.out.println("----------------------------------");
				System.out.println();
				
				i.remove();//获取了以后就将其删除,避免再次响应
				
				//处理客户端连接
				if (sk.isAcceptable()) {
					doAccept(sk);
				}
				
				//处理客户端写
				else if (sk.isValid() && sk.isReadable()) {
					if (!time_stat.containsKey(((SocketChannel)sk.channel()).socket())) {
						time_stat.put(((SocketChannel)sk.channel()).socket(), System.currentTimeMillis());
					}
					doRead(sk);
				}
				
				//处理客户端写
				else if (sk.isValid() && sk.isWritable()) {
					doWrite(sk);
					e = System.currentTimeMillis();
					long b = time_stat.remove(((SocketChannel)sk.channel()).socket());
					System.out.println("spend: " + (e - b) + "ms");
				}
			}
		}
	}
	
	/**
	 * 服务器接受客户端连接
	 * @author mp
	 * @date 2016-7-28 下午3:47:24
	 * @param sk
	 * @Description
	 */
	private void doAccept (SelectionKey sk) {
		ServerSocketChannel server = (ServerSocketChannel) sk.channel();
		SocketChannel clientChannel;
		try {
			clientChannel = server.accept();//此方法是阻塞的,当有客户端接入,返回此客户端
			clientChannel.configureBlocking(false);//设置成非阻塞模式
			SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);//将这个Channel注册到Selector上来管理,注册Selector感兴趣的事件是READ
			EchoMsg echoClient = new EchoMsg();
			clientKey.attach(echoClient);//将这个对象做为附件,附加到表示这个连接的SelectionKey上,这样在整个连接的处理过程中,我们都可以共享这个附件对象
		} catch (Exception e) {
			System.out.println("Failed to accept new client.");
			e.printStackTrace();
		}
	}
	
	/**
	 * 服务器从客户端读数据
	 * @author mp
	 * @date 2016-7-28 下午5:11:12
	 * @param sk
	 * @throws IOException 
	 * @Description
	 */
	private void doRead (SelectionKey sk) throws IOException {
		SocketChannel channel = (SocketChannel)sk.channel();
		ByteBuffer bb = ByteBuffer.allocate(100);
		int len;
		try {
			len = channel.read(bb);
			if (len < 0) {
				//关闭连接
				disconnect(sk);
				return;
			}
		} catch (Exception e) {
			System.out.println("Failed to read from client.");
			e.printStackTrace();
			//断开链接
			disconnect(sk);
			return;
		}
		;
		bb.flip();//准备操作数据
		tp.execute(new HandleMsg(sk, bb, selector));//这里用线程池进行数据处理,这样,如果数据处理很复杂,就能在单独的线程中进行,而不用阻塞任务派发线程
	}
	
	/**
	 * 服务器向客户端写数据
	 * @author mp
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @date 2016-7-28 下午5:12:48
	 * @Description
	 */
	private void doWrite (SelectionKey sk) throws IOException, InterruptedException {
		SocketChannel channel = (SocketChannel) sk.channel();
		EchoMsg echoClient = (EchoMsg) sk.attachment();
		LinkedList<ByteBuffer> outq = echoClient.getLinkedList();
		ByteBuffer bb = outq.getFirst();
		try {
			int len = channel.write(bb);//write完了以后,bytebuffer里的数据就没有了
			System.out.println("len = " + len);
			if (len == -1) {
				//关闭连接
				disconnect(sk);
				return;
			}
			if (bb.remaining() == 0) {//没有数据的时候,这个值会是0
				outq.removeLast();
			}
		} catch (Exception e) {
			System.out.println("Failed to write to client.");
			e.printStackTrace();
			//关闭连接
			disconnect(sk);
		}
		if (outq.size() == 0) {
			sk.interestOps(SelectionKey.OP_READ);//***这里最重要的,也最容易忽略的是在全部数据发送完成后,也就是outq长度为0的时候,需要将写事件从选择器中移除,只保留读事件。如果不这么做,每次Channel准备
			//好写时,都会来执行doWrite方法,而实际上你又无数据可写,这显然是不合理的,因此这个操作很重要
		}
	}
	
	/**
	 * 关闭channel
	 * @author mp
	 * @date 2016-7-29 上午10:47:47
	 * @param sk
	 * @throws IOException
	 * @Description
	 */
	private void disconnect (SelectionKey sk) throws IOException {
		sk.channel().close();
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-28 下午5:54:14
	 * @param args
	 * @throws Exception
	 * @Description
	 */
	public static void main(String[] args) throws Exception {
		new EchoServer().startServer();
	}
	
}
