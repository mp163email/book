package pairing_mode_calculation.net_aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * AIO-Echo Server
 * Future模式的典型运用
 * accept, connect, read, write 方法都是异步的,立刻返回的, 返回的是一个Future
 * @author mp
 * @date 2016-8-1 下午4:38:59
 */
public class EchoServer {

	public final static int port = 8000;
	
	private AsynchronousServerSocketChannel server;
	
	public EchoServer () throws IOException {
		server = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(port));
	}
	
	/**
	 * 启动
	 * @author mp
	 * @date 2016-8-1 下午4:59:20
	 * @Description
	 */
	public void start () {
		//第一个参数为一个附件,用于通道间共享数据,第二个参数为一个handler,处理连接成功跟失败的操作(回调函数)
		System.out.println("---- server start -----");
		server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {

			final ByteBuffer buffer = ByteBuffer.allocate(1024);

			/**
			 * 一旦有客户端连入,就会执行completed方法
			 * @author mp
			 * @date 2016-8-1 下午4:45:15
			 * @param result
			 * @param attachment
			 * @Description
			 */
			@Override
			public void completed(AsynchronousSocketChannel result, Object attachment) {
				System.out.println(Thread.currentThread().getName());
				Future<Integer> writeResult = null;
				try {
					buffer.clear();
					result.read(buffer).get(100, TimeUnit.SECONDS);//为了读取完整数据,通过get方法,把异步转成了同步
					buffer.flip();
					writeResult = result.write(buffer);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						server.accept(null, this);//为下一个做准备
						writeResult.get();//等待把数据写完
						result.close();//把当前关闭
					} catch (Exception e2) {
						System.out.println(e2.toString());
					}
				}
			}

			/**
			 * 如果连接失败,就会调用failed方法
			 * @author mp
			 * @date 2016-8-1 下午4:49:01
			 * @param exc
			 * @param attachment
			 * @Description
			 */
			@Override
			public void failed(Throwable exc, Object attachment) {
				System.out.println("failed " + exc);
			}
		});
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-8-1 下午4:59:12
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		new EchoServer().start();//这个方法是异步的,会立刻返回
		//模拟main函数继续执行其他操作
		while (true) {
			Thread.sleep(1000);
		}
	}
	
}
