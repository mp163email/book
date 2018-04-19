package program.nio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * 新IO比旧IO在性能上最起码要快7-8倍
 * @author mp
 * @date 2016-6-2 下午2:20:04
 */
public class ReadWriteFileCompare {
	
	private static int times = 400 * 10000;
	
	public static void main(String[] args) throws Exception {
		
		oldiowrite ();//老IO带缓冲-写 385
		oldioread ();//老IO带缓冲-读 369
		newiowrite ();//新IO-写 52
		newioread ();//新IO-读 29
		mapPut ();//文件映射成map写-20
	}
	
	/**
	 * 文件映射到内存-写
	 * @author mp
	 * @date 2016-6-2 下午3:30:23
	 * @Description
	 */
	private static void  mapPut () throws Exception {
		long s = System.currentTimeMillis();
		FileChannel fChannel = new RandomAccessFile("E://compmap.txt", "rw").getChannel();
		IntBuffer ib = fChannel.map(FileChannel.MapMode.READ_WRITE, 0, times * 4).asIntBuffer();
		for (int i = 0; i < times; i++) {
			ib.put(i);//映射以后（指定大小）,可以通过修改内存,直接修改文件
		}
		if (fChannel != null) {
			fChannel.close();
		}
		long e = System.currentTimeMillis();
		System.out.println("--map write finish , used time = " + (e - s));
	}
	
	/**
	 * 新IO-读 29
	 * @author mp
	 * @date 2016-6-2 下午2:14:12
	 * @throws Exception
	 * @Description
	 */
	private static void newioread () throws Exception {
		long s = System.currentTimeMillis();
		File file = new File("E://compn.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(times * 4);
		
		FileInputStream fos = new FileInputStream(file);
		FileChannel fc = fos.getChannel();
		fc.read(byteBuffer);
		fc.close();
		
		byteBuffer.flip();
		while (byteBuffer.hasRemaining()) {
			byteBuffer.getInt();
		}
		
		long e = System.currentTimeMillis();
		System.out.println("--new io read finish , used time = " + (e - s));
	}
	
	/**
	 * 新IO-写 52
	 * @author mp
	 * @date 2016-6-2 下午1:45:58
	 * @Description
	 */
	private static void newiowrite () throws Exception {
		long s = System.currentTimeMillis();
		File file = new File("E://compn.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(times * 4);
		for (int i = 0; i < times; i++) {
			byteBuffer.putInt(i);
		}
		byteBuffer.flip();
		
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		FileChannel fc = fileOutputStream.getChannel();
		fc.write(byteBuffer);
		fc.close();
		long e = System.currentTimeMillis();
		System.out.println("--new io write finish , used time = " + (e - s));
	}
	
	/**
	 * 老IO带缓冲-写  385
	 * @author mp
	 * @date 2016-6-2 下午1:34:37
	 * @Description
	 */
	private static void oldiowrite () throws Exception {
		long s = System.currentTimeMillis();
		File file = new File("E://compo.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		for (int i = 0; i < times; i++) {
			dos.writeInt(i);
		}
		dos.close();
		long e = System.currentTimeMillis();
		System.out.println("--old io write finish , used time = " + (e - s));
	}
	
	/**
	 * 老IO带缓冲-读  369
	 * @author mp
	 * @date 2016-6-2 下午1:41:46
	 * @throws Exception
	 * @Description
	 */
	private static void oldioread () throws Exception {
		long s = System.currentTimeMillis();
		File file = new File("E://compo.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		for (int i = 0; i < times; i++) {
			dis.readInt();
		}
		dis.close();
		long e = System.currentTimeMillis();
		System.out.println("--old io read finish , used time = " + (e - s));
	}
	
}
