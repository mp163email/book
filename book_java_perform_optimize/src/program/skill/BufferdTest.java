package program.skill;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * 带缓冲的IO处理工具,默认缓冲大小是一个8129b=8k的一个字节数组
 * 如果out.flush(),out.close()这两个方法都不使用,则数据无法写入文件,因为数据还在缓冲区中呢
 * 但是网络IO的时候,不用flush数据也能送到服务器端,不太一样
 * @author mp
 * @date 2016-7-27 下午6:06:35
 */
public class BufferdTest {
	public static void main(String[] args) throws IOException {
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File ("d://buf.txt")), "utf-8"));
		out.write("我是中国人");
		
//		out.flush(); 如果这两个方法都不使用,则数据无法写入文件,因为数据还在缓冲区中呢
//		out.close();
		
		System.out.println("--finish--");
	}
}
