package jvm;

import java.util.Vector;

/**
 * 设置-Xms后JVM总是先设法在指定的-Xms内完成任务(通过不断的GC来保证),当内存实际使用量触及-Xms大小时,会触发Full GC,所以把-Xms设大点(或者和-Xmx一样大)可以减少GC和Full GC的次数和耗时 
 * @author mp
 * @date 2016-6-17 上午11:49:54
 */
public class SetXms {
	public static void main(String[] args) throws Exception{
		Vector<Object> vector = new Vector<>();
		for (int i = 0; i < 10; i++) {
			byte[] b = new byte [1024 * 1024];
			vector.add(b);
		}
	}
}

/**
 * -Xms2M -Xmx15M -verbose:gc
[GC 2707K->2592K(6528K), 0.0033021 secs]
[Full GC 2592K->2522K(9472K), 0.0038079 secs]
[GC 4669K->4602K(10816K), 0.0007498 secs]
[Full GC 4602K->4570K(13824K), 0.0024076 secs]
[GC 8790K->8698K(13824K), 0.0018983 secs]
[Full GC 8698K->8666K(15872K), 0.0043791 secs]
 * 
 */


/**
 * -Xms15M -Xmx15M -verbose:gc
[GC 3732K->3672K(14720K), 0.0015391 secs]
[GC 6941K->6696K(15040K), 0.0009650 secs]
*/