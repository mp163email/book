package jvm;

import java.util.Vector;

/**
 * 设置成  -Xmx5M  后,只能运行5次,每次1M,5次5M
 * 设置成  -Xmx13M后,可以执行完10次
 * Runtime.getRuntime().maxMemory()查看运行时设置的最大内存
 * @author mp
 * @date 2016-6-17 上午10:59:12
 */
public class SetXmx {
	public static void main(String[] args) throws Exception{
		Vector<Object> vector = new Vector<>();
		for (int i = 0; i < 10; i++) {
			byte[] b = new byte [1024 * 1024];//1024个b的1k  1024个1k = 1M   有时候即使长度单位也是大小单位
			vector.add(b);
			System.out.println(i + "M is allocated");
		}
		System.out.println("Max memory:" + Runtime.getRuntime().maxMemory());
	}
}
