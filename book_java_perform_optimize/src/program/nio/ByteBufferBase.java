package program.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * position始终执行下一个即将输入的位置
 * 读操作总是读的是[position和limit之间的数据]
 * remaining是[position和limit之间的差值]
 * @author mp
 * @date 2016-5-30 上午11:44:22
 */
public class ByteBufferBase {
	
	public static void main(String[] args) throws Exception {
		structure();
//		baseParam();
//		reset();
//		markAndReset();
//		duplicate();
//		slice();
//		index();
//		asReadOnlyBuffer();
//		map();
	}
	
	/**
	 * 处理结构化(有固定格式的)数据
	 * @author mp
	 * @date 2016-6-1 上午11:31:00
	 * @Description
	 */
	private static void structure () throws Exception{
		
		//使用聚集写操作创建文件
		ByteBuffer bookBuf = ByteBuffer.wrap("Java性能优化技巧".getBytes("utf-8"));
		ByteBuffer autBuf = ByteBuffer.wrap("苗朋".getBytes("utf-8"));
		int booklen = bookBuf.limit();
		int autlen = autBuf.limit();
		ByteBuffer [] bufs = {bookBuf, autBuf};
		File file = new File("E://book.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fop = new FileOutputStream(file);
		FileChannel fch = fop.getChannel();
		fch.write(bufs);
		fch.close();
		
		System.out.println("====write finish===");
		
		//使用散射读,将文件解析成书名,作者两个字符串
		
		ByteBuffer bookBuf1 = ByteBuffer.allocate(booklen);
		ByteBuffer autBuf1 = ByteBuffer.allocate(autlen);
		ByteBuffer [] bufs1 = {bookBuf1, autBuf1};
		
		FileInputStream fip = new FileInputStream(file);
		FileChannel fich = fip.getChannel();
		fich.read(bufs1);
		String book = new String(bufs1[0].array(), "utf-8");
		String auth = new String(bufs1[1].array(), "utf-8");
		System.out.println("book = " + book + "  auth = " + auth);
	}
	
	/**
	 * 将文件内容映射到内存,还可以改变内存在反写到文件中,但是有一点疑问,来往都用的是char
	 * @author mp
	 * @date 2016-5-30 下午5:38:03
	 * @throws Exception
	 * @Description
	 */
	private static void map () throws Exception{
		RandomAccessFile raf = new RandomAccessFile("E://mapfile.txt", "rw");
		FileChannel fc = raf.getChannel();
		
		MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, raf.length());
		while (mbb.hasRemaining()) {
			System.out.println((char)mbb.get());
		}
		mbb.put(0, (byte)(char)'1');
		fc.write(mbb);
		raf.close();
		fc.close();
	}
	
	/**
	 * **复制整个的主缓冲，不会像slice那样根据pos-limit截取**为只读不可写的缓冲,主缓冲区值改变,从缓冲也会改
	 * @author mp
	 * @date 2016-5-30 下午5:05:50
	 * @Description
	 */
	private static void asReadOnlyBuffer () {
		ByteBuffer bf = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			bf.put((byte)i);
		}
		ByteBuffer readonly = bf.asReadOnlyBuffer();
		System.out.println(readonly);
		readonly.flip();
		while (readonly.hasRemaining()) {
			System.out.println("read only " + readonly.get());
		}
		
		bf.put(2, (byte)20);
//		readonly.put(2, (byte)20);//会报错
		
		readonly.flip();
		while (readonly.hasRemaining()) {
			System.out.println("put after read only " + readonly.get());
		}
	}
	
	/**
	 * 像这种index的put和get都不改变position
	 * @author mp
	 * @date 2016-5-30 下午4:56:50
	 * @Description
	 */
	private static void index () {
		ByteBuffer bf = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			bf.put((byte)i);
		}
		System.out.println(bf);
		bf.put(2, (byte)20);
		System.out.println(bf);
	}
	
	/**
	 * 缓冲区分片(**截取**为独立的缓冲,但数据还是相互共享的)
	 * @author mp
	 * @date 2016-5-30 下午4:45:04
	 * @Description
	 */
	private static void slice () {
		ByteBuffer bf = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			bf.put((byte)i);
		}
		
		bf.position(2);
		bf.limit(6);
		ByteBuffer bfs = bf.slice();
		int cap = bfs.capacity();
		for (int i = 0; i < cap; i++) {
			byte bb = bfs.get(i);
			bb *= 10;
			bfs.put(i, bb);
			System.out.println(bb);
		}
		
		System.out.println();
		
		bf.clear();
		cap = bf.capacity();
		for (int i = 0; i < cap; i++) {
			System.out.println(bf.get());
		}
		
	}
	
	/**
	 * 复制缓冲区,在复制以后,复制前后的缓冲区共享同一块内存,缓冲里面的数据是一样的,共享的,有其中一方改变了里面的值,另一方的值也会发生改变。唯一不同的是,两个缓冲区各自维护了自己的内部位置要素
	 * @author mp
	 * @date 2016-5-30 下午4:35:41
	 * @Description
	 */
	private static void duplicate () {
		ByteBuffer bf = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			bf.put((byte)i);
		}
		
		//复制一个缓冲
		ByteBuffer bfc = bf.duplicate();
		System.out.println(bf);
		System.out.println(bfc);
		
		bf.flip();
		
		System.out.println("bf flip后");
		
		System.out.println(bf);
		System.out.println(bfc);
		
		bf.put((byte)100);
		
		System.out.println("bf 在pos位置插入了一个数值后");
//		
		System.out.println(bf.get(0));
		System.out.println(bfc.get(0));
	}
	
	/**
	 * 标记和重置
	 * @author mp
	 * @date 2016-5-30 下午4:04:41
	 * @Description
	 */
	private static void markAndReset() {
		ByteBuffer bf = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			bf.put((byte)i);
		}
		bf.flip();
		int limit = bf.limit();
		for (int i = 0; i < limit; i++) {
			System.out.println(bf.get());
			if (i == 4) {
				bf.mark();
				System.out.println("mark at " + i);
			}
		}
		bf.reset();
		
		System.out.println();
		
		while (bf.hasRemaining()) {
			System.out.println(bf.get());
		}
	}
	
	/**
	 * 重置和清空缓冲区[flip, rewind, clear]
	 * @author mp
	 * @date 2016-5-30 下午1:41:26
	 * @Description
	 */
	private static void reset () {
		
		//flip[pos=0, limit= pos] 用于读/写切换时
		ByteBuffer bf = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			bf.put((byte)i);//put=write,顺序是从右到左
		}
		bf.flip();
		for (int i = 0; i < 5; i++) {
			System.out.print(bf.get() + " ");//get=read,顺序是从左到右
		}
		System.out.println();
		
		
		//rewind[pos=0, limit=不改动]  恢复到上次操作的状态,以备在上次的基础上再做其他操作
		ByteBuffer bf1 = ByteBuffer.allocate(15);
		for (int i = 0; i < 10; i++) {
			bf1.put((byte)i);//put=write,顺序是从右到左
		}
		bf1.flip();
		//取出5个数据
		for (int i = 0; i < 5; i++) {
			System.out.print(bf1.get() + " ");
		}
		System.out.println();
		bf1.rewind();//恢复到刚才取数据的状态,准备进行下次操作（再读一次,或者把刚才读取的数据放到别的地方）
		for (int i = 0; i < 5; i++) {
			System.out.print(bf1.get() + " ");
		}
		
		
		//clear [pos=0, limit=capacity]用于将buffer设置成初始状态,好往里边写数据
		System.out.println();
		ByteBuffer bf2 = ByteBuffer.allocate(15);
		System.out.println(bf2);
		for (int i = 0; i < 10; i++) {
			bf2.put((byte)i);//put=write,顺序是从右到左
		}
		System.out.println(bf2);
		bf2.clear();
		System.out.println(bf2);
	}
	
	/**
	 * 几个重要的参数
	 * @author mp
	 * @date 2016-5-30 上午11:59:47
	 * @Description
	 */
	private static void baseParam () {
		//初始一个15字节的缓冲区
		System.out.println("=========初始化15个字节的缓冲区======");
		ByteBuffer bf = ByteBuffer.allocate(15);
		System.out.println("position = " + bf.position() + ", limit = " + bf.limit() + ", capacity = " + bf.capacity() + ", remaining = " + bf.remaining());
		
		//向缓冲区中写入10个字节
		for (int i = 0; i < 10; i++) {
			bf.put((byte)i);
		}
		System.out.println("==========写入10个字节后============");
		System.out.println("position = " + bf.position() + ", limit = " + bf.limit() + ", capacity = " + bf.capacity() + ", remaining = " + bf.remaining());
		
		//为读做准备
		bf.flip();
		System.out.println("==========flip后============");
		System.out.println("position = " + bf.position() + ", limit = " + bf.limit() + ", capacity = " + bf.capacity() + ", remaining = " + bf.remaining());
		
		//读取5个字节
		System.out.println("==========读5个字节后============");
		for (int i = 0; i < 5; i++) {
			System.out.print(bf.get() + " ");
		}
		System.out.println();
		System.out.println("position = " + bf.position() + ", limit = " + bf.limit() + ", capacity = " + bf.capacity() + ", remaining = " + bf.remaining());
		
		//再次flip
		bf.flip();
		System.out.println("==========再次flip后============");
		System.out.println("position = " + bf.position() + ", limit = " + bf.limit() + ", capacity = " + bf.capacity() + ", remaining = " + bf.remaining());
		
		//再次读取5个字节
		System.out.println("==========再次读5个字节后============");
		for (int i = 0; i < 5; i++) {
			System.out.print(bf.get() + " ");
		}
		System.out.println();
		System.out.println("position = " + bf.position() + ", limit = " + bf.limit() + ", capacity = " + bf.capacity() + ", remaining = " + bf.remaining());
	}
}
