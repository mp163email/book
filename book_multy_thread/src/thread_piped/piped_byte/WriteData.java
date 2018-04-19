package thread_piped.piped_byte;

import java.io.PipedOutputStream;

public class WriteData {
	
	public void writeMethod (PipedOutputStream outputStream) {
		try {
			System.out.println(Thread.currentThread().getName() + " write data : ");
			int size = 0;
			for (int i = 0; i < 500; i++) {
				String str = i + ";";
				size =size + str.getBytes().length;
				System.out.println("s = " + size);
				outputStream.write(str.getBytes());//内部缓冲区buffer是1024,所以如果写满了，会阻塞，等待取线程来取，取光了数据，会再次往里写数据
				System.out.print(str);
			}
			System.out.println("write finish ");
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
