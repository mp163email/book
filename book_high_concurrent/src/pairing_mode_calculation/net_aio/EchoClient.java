package pairing_mode_calculation.net_aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;


/**
 * AIO-Echo Client
 * @author mp
 * @date 2016-8-1 下午5:01:26
 */
public class EchoClient {
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-8-1 下午5:03:12
	 * @param args
	 * @throws Exception
	 * @Description
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("---- client start -----");
		final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
		client.connect(new InetSocketAddress("localhost", 8000), null, new CompletionHandler<Void, Object>() {

			@Override
			public void completed(Void result, Object attachment) {
				try {
					client.write(ByteBuffer.wrap("Hello".getBytes()), null, new CompletionHandler<Integer, Object>() {

						@Override
						public void completed(Integer result, Object attachment) {
							try {
								ByteBuffer buffer = ByteBuffer.allocate(1024);
								client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

									@Override
									public void completed(Integer result, ByteBuffer buffer) {
										buffer.flip();
										int size = buffer.remaining();
										byte[] bt = new byte[size];
										for (int i = 0; i < size; i++) {
											bt[i] = buffer.get();
										}
										System.out.println(new String(bt));
										try {
											client.close();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									@Override
									public void failed(Throwable exc, ByteBuffer attachment) {
										
									}
									
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void failed(Throwable exc, Object attachment) {
							
						}
						
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable exc, Object attachment) {
				
			}
		});
		
		//由于主线程马上结束,这里等待上述处理全部完成
		Thread.sleep(1000);
	}
	
	
	
	
}
