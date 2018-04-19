package program.string;

/**
 * 字节数组，字符数组向字符串转换及其反转
 * @author mp
 * @date 2016-6-2 上午11:50:15
 */
public class Convert {
	
	public static void main(String[] args) throws Exception{
		byte [] mb = "哈".getBytes("utf-8");
		String newString = new String(mb, "utf-8");
		System.out.println(newString);
		
		String str = "我们";
		char ch [] = str.toCharArray();
		System.out.println(String.valueOf(ch));
	}
}
