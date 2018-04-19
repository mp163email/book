package jvm;

public class RunTimeMem {
	
	/**
	 * 这三个值,尤其是前两个,是应该密切关注的,好多宕机问题是由于内存溢出造成的,内存溢出会导致GC频繁,会是CPU暴涨
	 * @author mp
	 * @date 2016-6-17 上午11:41:37
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		System.out.println("Max memory:" + Runtime.getRuntime().maxMemory());//当前这个Java进程（虚拟机）,最大能从物理内存拿多少内存，相当于-Xmx参数
		System.out.println("Total memory:" + Runtime.getRuntime().totalMemory());//当前这个Java进程已经从物理内存拿了多少了,会一点一点的拿,如果达到或超过-Xmx,内存溢出,相当于-Xms
		System.out.println("Free memory:" + Runtime.getRuntime().freeMemory());//当前这个Java进程从物理内存拿过来的有多少是空闲状态的,有时候-Xms设置的比较大,而程序暂时全用不完,这里是出去用的剩下的
	}
}
