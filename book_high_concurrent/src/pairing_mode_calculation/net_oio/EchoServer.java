package pairing_mode_calculation.net_oio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器端：老网络IO实现,一个客户端对应一个服务器线程
 * 客户端慢,服务器就慢,因为服务器读取客户端的数据是阻塞的
 * 注意***当数组设置为0的时候,read(ch)并不会阻塞    当数组设置不为零read(ch, 0, 0)也同样不会阻塞****
 * 
 * 出现java.net.SocketException: Connection reset 的原因
 * 1:如果一端的Socket被关闭（或主动关闭，或因为异常退出而 引起的关闭），另一端仍发送数据，发送的第一个数据包引发该异常(Connect reset by peer)。
 * 2:一端退出，但退出时并未关闭该连接，另一端如果在从连接中读数据则抛出该异常（Connection reset）。简单的说就是在连接断开后的读和写操作引起的。
 * @author mp
 * @date 2016-7-27 上午10:45:18
 */
public class EchoServer {
	
	/**
	 * 定义一个线程池,处理客户端消息
	 */
	private static ExecutorService tp = Executors.newCachedThreadPool();
	
	/**
	 * 处理客户端消息
	 * @author mp
	 * @date 2016-7-27 上午11:01:33
	 */
	public static class HandleMsg implements Runnable {
		
		Socket clientSocket;//客户端的Socket就是Socket,服务器的是ServerSocket
		
		/**
		 * 构造方法将客户端Socket放入
		 * @param clientSocket
		 */
		public HandleMsg (Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		@Override
		public void run() {
			BufferedReader is = null;
			PrintWriter os = null;
			try {
				is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//通过InputStream,准备从客户端中读取数据
				os = new PrintWriter(clientSocket.getOutputStream(), true);//通过OutputStream将数据写回到客户端中
				long b = System.currentTimeMillis();
				
				String inputLine = null;
				while ((inputLine = is.readLine()) != null) {//当读阻塞的时候,如果客户端退出（退出前没close连接）,服务器会抛出Connection Reset异常,如果退出前调用了close，程序会继续往下走
					os.println(inputLine);
				}
/*				char[] ch = new char[1024];//注意***这里当数组设置为0的时候,read(ch)并不会阻塞    当数组设置不为零read(ch, 0, 0)也同样不会阻塞****
				int length = is.read(ch);//这里的reader是阻塞的
				System.out.println("length = " + length);
				String str = new String(ch, 0, length);
				os.println(str);//从客户端读取完数据后,再将其原封不动的返回给客户端
				os.flush();*/
				long e = System.currentTimeMillis();
				System.out.println("spend: " + (e - b) + "ms");//统计服务器处理客户端数据的读写时间
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					//关闭客户端的输入输出流,当关闭流后,连接就中断了isClosed就会成为true了     改到由客户端断开连接
/*					if (is != null) {
						is.close();
					}
					if (os != null) {
						os.close();
					}
					if (!clientSocket.isClosed()) {
						clientSocket.close();//当服务器断开客户端或者服务器把读写流关闭后,或者客户端自己关闭自己,isClosed都会变成true
					}*/
/*					System.out.println(clientSocket.isBound());//不明白的是,链接已经断开了,isBound为啥还是true
					System.out.println(clientSocket.isClosed());
					System.out.println(clientSocket.isConnected());//不明白的是,链接已经断开了,isConnected为啥还是true **含义是是否曾建链接成功过,网上介绍的**
					System.out.println(clientSocket.isInputShutdown());
					System.out.println(clientSocket.isOutputShutdown());*/
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 函数主方法
	 * @author mp
	 * @date 2016-7-27 上午11:40:22
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			serverSocket = new ServerSocket(8000);//服务器绑定8000端口
			System.out.println("-----server start-----");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//无限循环,用accept()阻塞方法来检测有无客户端连进来,一个都没有就阻塞,有任意一个就进行处理
		while (true) {
			try {
				clientSocket = serverSocket.accept();//此方法阻塞,当有客户端连入,将此客户端返回
				System.out.println(clientSocket.getRemoteSocketAddress() + " connect !");
				tp.execute(new HandleMsg(clientSocket));//服务器用一个线程池,异步处理这个连接(对这个客户端进行读写)
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
