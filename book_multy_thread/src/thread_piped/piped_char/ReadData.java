package thread_piped.piped_char;

import java.io.PipedReader;

public class ReadData {
	
	public void readMethod (PipedReader inputStream) {
		try {
			System.out.println(Thread.currentThread().getName() + " read data : ");
			char[] buf = new char[5];
			int readLength = inputStream.read(buf);
			while (readLength != -1) {
				String str = new String (buf, 0, readLength);
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
