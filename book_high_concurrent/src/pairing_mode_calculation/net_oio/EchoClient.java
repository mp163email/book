package pairing_mode_calculation.net_oio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * 客户端端：老网络IO实现
 * @author mp
 * @date 2016-7-27 下午1:55:00
 */
public class EchoClient {
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-27 下午1:55:38
	 * @param args
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		Socket client = null;
		PrintWriter writer = null;
		BufferedReader reader = null;
		
		try {
			client = new Socket();//创建一个客户端
			client.connect(new InetSocketAddress("localhost", 8000));//用这个客户端去连接服务器端,在本行代码建立链接后,服务器就能检测到,而不是等客户端发送完消息才检测到
			
			writer = new PrintWriter(client.getOutputStream(), true);//通过Writer准备客户端写数据
			TimeUnit.SECONDS.sleep(10);//这里模拟客户端链接后,要等很久才发送消息,看看客户端的这个操作会不会对服务器也产生影响?????
			writer.println("hello");//写数据
//			writer.flush();//清空缓冲区数据,清不清缓冲区,数据都会发过去,跟写文件不一样,写文件如果不flush数据就不会写到文件上
			
			reader = new BufferedReader(new InputStreamReader(client.getInputStream()));//准备用InputStream来获取客户端获取数据
			System.out.println("from server: " + reader.readLine());//这个地方是阻塞的
			
			//关闭客户端的输入输出流,当关闭流后,连接就中断了isClosed就会成为true了     改到由客户端断开连接
			if (writer != null) {
				writer.close();
			}
			if (reader != null) {
				reader.close();
			}
			if (!client.isClosed()) {
				client.close();//当服务器断开客户端或者服务器把读写流关闭后,或者客户端自己关闭自己,isClosed都会变成true
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
