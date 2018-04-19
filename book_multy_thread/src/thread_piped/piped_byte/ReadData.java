package thread_piped.piped_byte;

import java.io.PipedInputStream;

public class ReadData {
	
	public void readMethod (PipedInputStream inputStream) {
		try {
			System.out.println("\n" + Thread.currentThread().getName() + " read data : ");
			byte[] buf = new byte[5000];
			int readLength = inputStream.read(buf);
//			System.out.println("=======================================" + readLength);
			while (readLength != -1) {
				String str = new String (buf, 0, readLength);//读取1024字节的数据,读完了以后，内部buff为空了，阻塞，直到写满数据，第二次读
				System.out.print(str);
				readLength = inputStream.read(buf);
			}
			System.out.println();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
