package jvm;

/**
 * 打印GC信息
 * -XX:+PrintGCDetails
 * @author mp
 * @date 2016-6-16 下午5:16:06
 */
public class HeapPrint {
	public static void main(String[] args) {
//		byte [] b1 = new byte[1024*1024/2];
		byte [] b2 = new byte[1024*1024*8];
		b2 = null;
//		b2 = new byte[1024*1024*8];
//		System.gc();
	}
}
