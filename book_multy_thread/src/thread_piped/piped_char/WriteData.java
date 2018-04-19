package thread_piped.piped_char;

import java.io.PipedWriter;

public class WriteData {
	
	public void writeMethod (PipedWriter outputStream) {
		try {
			System.out.println(Thread.currentThread().getName() + " write data : ");
			for (int i = 0; i < 10; i++) {
				String str = "" + i;
				outputStream.write(str);
				System.out.print(str);
			}
			System.out.println();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
