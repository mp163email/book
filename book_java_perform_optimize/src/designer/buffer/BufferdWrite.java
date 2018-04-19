package designer.buffer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * 写文件 是否使用buffer性能对比
 * 有3-4倍的差别
 * @author mp
 * @date 2016-5-27 下午3:40:00
 */
public class BufferdWrite {
	
	public static void main(String[] args) throws Exception{
		noBuffeWrite();
		buffeWrite();
	}

	public static void noBuffeWrite () throws Exception{
		Writer writer = new FileWriter(new File("E://f1.txt"));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			writer.write(i+ "");
		}
		writer.close();
		long end = System.currentTimeMillis();
		System.out.println("used time = " + (end - start));
	}
	
	public static void buffeWrite () throws Exception{
		Writer writer = new BufferedWriter(new FileWriter(new File("E://f2.txt")));
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			writer.write(i+ "");
		}
		writer.close();
		long end = System.currentTimeMillis();
		System.out.println("used time = " + (end - start));
	}
}
