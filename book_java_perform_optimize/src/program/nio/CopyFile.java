package program.nio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * BufferedInputStream 的默认缓冲区是8192b 8k,能够减少访问磁盘的次数,提高文件读写性能, 在调用close的时候内部会自动调一下flush
 * 复制文件 270M的一个文件作为测试
 * 从测试来看,旧IO随着jdk的版本提升,已经做了很大的优化，总体相差不大：
 * 1.不论旧IO还是新IO，在复制文件的时候,一次性读取,一次性写入都是非常慢的，这种方式不可取， 老IO Buffer 字字符方式也很慢
 * 2.其他几种方式新IO直接对接方式是最快的（不太稳定,快的时候很快,也会有慢的时候）,其次是新IO MAP的方式, 再次是新IO多次写入（这种方式,比较稳定）, 最次是旧IO多次写入, 但总体比较来看,这几种方式差距不算大100毫秒左右
 * 3.*******直接内存方（allocateDirect）式要快，能在200毫秒以下,但必须调整好初始大小,太小了会慢,太大了也会慢,另外其创建和销毁花费比较大***********
 * @author mp
 * @date 2016-5-30 上午10:35:04
 */
public class CopyFile {
	
	public static void main(String[] args) throws Exception {
		oldIomanytimes("E://cp.txt", "E://cp2oldIomanytimes.txt");//老IO Buffer 字节方式多次复制文件-381
//		oldIoonetimes("E://cp.txt", "E://cp2oldIoonetimes.txt");//老IO Buffer方式一次复制文件-4893
//		oiochar("E://cp.txt", "E://cp2oiochar.txt");//老IO Buffer 字字符方式多次复制文件-2000
		niomanytimes("E://cp.txt", "E://cp2niomanytimes.txt");//新IO多次  分多次比单次要快,为啥?-249
//		nioonetimes("E://cp.txt", "E://cp2nioonetimes.txt");//新IO一次   ??????为什么一次性加载到字节缓冲，然后一次性写速度会很慢呢?????-6296
		nioonetrans("E://cp.txt", "E://cp2nioonetrans.txt");//新IO直接对接方式(transferFrom)-212
		niomap("E://cp.txt", "E://cp2niomap.txt");//新IO MAP方式-221
	}
	
	/**
	 * 旧IO字符型
	 * @author mp
	 * @date 2016-6-3 上午10:20:16
	 * @param sou
	 * @param des
	 * @throws Exception
	 * @Description
	 */
	private static void oiochar (String sou, String des) throws Exception {
		long s = System.currentTimeMillis();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(sou)));
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(des)));
		char [] ch = new char[1024];
		while (true) {
			int length = bufferedReader.read(ch);
			if (length == -1) {
				break;
			}
			bufferedWriter.write(ch, 0, length);
		}
		bufferedWriter.close();
		bufferedReader.close();
		long e = System.currentTimeMillis();
		System.out.println("oiochar times used time = " + (e - s));
	}
	
	/**
	 * 新IO map 映射方式
	 * @author mp
	 * @date 2016-6-2 下午5:19:05
	 * @Description
	 */
	private static void niomap (String sou, String des) throws Exception {
		long s = System.currentTimeMillis();
		File filedes = new File (des);
		File filesou = new File (sou);
		long len = filesou.length();

		FileChannel fileChannel = new RandomAccessFile(filedes, "rw").getChannel();
		ByteBuffer byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, len);
		
		FileInputStream fis = new FileInputStream(filesou);
		BufferedInputStream bis = new BufferedInputStream(fis);
		byte [] b = new byte[1024];
		while (true) {
			int rl = bis.read(b);
			if (rl == -1) {
				break;
			}
			byteBuffer.put(b, 0, rl);
		}
		
/*		FileChannel fcsou = fis.getChannel();
		ByteBuffer bb = ByteBuffer.allocate(1024);
		while (true) {
			bb.clear();
			int bufLen = fcsou.read(bb);
			if (bufLen == -1) {
				break;
			}
			bb.flip();
			byteBuffer.put(bb);
		}*/
		
		
		fileChannel.close();
		fis.close();
//		fcsou.close();
		
		long e = System.currentTimeMillis();
		System.out.println("niomap map times used time = " + (e - s));
	}
	
	/**
	 * 老IO方式多次复制文件
	 * @author mp
	 * @date 2016-6-2 下午4:04:14
	 * @param sou
	 * @param des
	 * @throws Exception
	 * @Description
	 */
	private static void oldIomanytimes (String sou, String des) throws Exception {
		long s = System.currentTimeMillis();
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(sou)));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File (des)));
		byte [] b = new byte[1024*1024];
		while (true) {
			int len = bis.read(b);
			if (len == -1) {
				break;
			}
			bos.write(b, 0, len);//**********特别注意,这里的byte跟新IO里边的bytebuffer 不一样！！！！！,后者会直接读取pos-limit的有效值,而字节数组没有这些概念,所以要指定有效长度****************
		}
		bos.close();
		bis.close();
		long e = System.currentTimeMillis();
		System.out.println("oldIo many times used time = " + (e - s));
	}
	
	/**
	 * 老IO方式一次复制文件
	 * @author mp
	 * @date 2016-6-2 下午4:28:10
	 * @param sou
	 * @param des
	 * @throws Exception
	 * @Description
	 */
	private static void oldIoonetimes (String sou, String des) throws Exception {
		long s = System.currentTimeMillis();
		FileInputStream fis = new FileInputStream(new File(sou));
		int leng = fis.available();
		BufferedInputStream bis = new BufferedInputStream(fis);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File (des)));
		byte [] b = new byte[leng];
		while (true) {
			int len = bis.read(b);
			if (len == -1) {
				break;
			}
			bos.write(b);
		}
		bos.close();
		bis.close();
		long e = System.currentTimeMillis();
		System.out.println("oldIo one times used time = " + (e - s));
	}
	
	/**
	 * 新IO分多次比单次要快,为啥？
	 * @author mp
	 * @date 2016-6-2 下午4:01:20
	 * @param sou
	 * @param des
	 * @throws Exception
	 * @Description
	 */
	public static void niomanytimes (String sou, String des) throws Exception {
		long s = System.currentTimeMillis();
		FileInputStream fis = new FileInputStream(new File (sou));
		FileOutputStream fos = new FileOutputStream(new File (des));
		FileChannel fic = fis.getChannel();
		FileChannel foc = fos.getChannel();
		ByteBuffer bbf = ByteBuffer.allocate(1024*1024); /*****对于一个大文件270m来说，动态设置缓冲区的大小，速度会差好多倍270m用时310-400毫秒*************/
//		ByteBuffer bbf = ByteBuffer.allocateDirect(1024*1024);//*******直接内存方式要快，能在200毫秒以下,但必须调整好初始大小,太小了会慢,太大了也会慢,另外其创建和销毁花费比较大***********
		while (true) {
			bbf.clear();
			int len = fic.read(bbf);
			if (len == -1) {
				break;
			}
			bbf.flip();
			foc.write(bbf);
		}
		
		fic.close();
		foc.close();
		long e = System.currentTimeMillis();
		System.out.println("nio many times used time = " + (e - s));
	}
	
	/**
	 * 新IO transferFrom 直接对接输入流,写文件
	 * @author mp
	 * @date 2016-6-2 下午4:53:06
	 * @param sou
	 * @param des
	 * @throws Exception
	 * @Description
	 */
	private static void nioonetrans (String sou, String des) throws Exception {
		long s = System.currentTimeMillis();
		
		FileInputStream fis = new FileInputStream(new File (sou));
		FileOutputStream fos = new FileOutputStream(new File (des));
		FileChannel fic = fis.getChannel();
		FileChannel foc = fos.getChannel();
		
		foc.transferFrom(fic, 0, fic.size());
		
		fic.close();
		foc.close();
		long e = System.currentTimeMillis();
		System.out.println("nio one transferFrom used time = " + (e - s));
	}
	
	/**
	 * ？？？为什么一次性加载到字节缓冲，然后一次性写速度会很慢呢？？？
	 * @author mp
	 * @date 2016-6-2 下午4:00:36
	 * @param sou
	 * @param des
	 * @throws Exception
	 * @Description
	 */
	public static void nioonetimes (String sou, String des) throws Exception {
		long s = System.currentTimeMillis();
		FileInputStream fis = new FileInputStream(new File (sou));
		int fileLengh = fis.available();//文件字节长度
		FileOutputStream fos = new FileOutputStream(new File (des));
		FileChannel fic = fis.getChannel();
		FileChannel foc = fos.getChannel();
		ByteBuffer bbf = ByteBuffer.allocate(fileLengh); /*****对于一个大文件270m来说，动态设置缓冲区的大小，速度会差好多倍.270m用时310-400毫秒*************/

		while (true) {
			bbf.clear();
			int len = fic.read(bbf);
			if (len == -1) {
				break;
			}
			bbf.flip();
			foc.write(bbf);
		}
		fic.close();
		foc.close();
		long e = System.currentTimeMillis();
		System.out.println("nio one times used time = " + (e - s));
	}
	
}
