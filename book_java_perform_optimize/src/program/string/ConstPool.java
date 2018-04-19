package program.string;

/**
 * String常量池的测试
 * @author mp
 * @date 2016-5-27 下午2:45:23
 */
public class ConstPool {
	public static void main(String[] args) {
		String str1 = "abc";
		String str2 = "abc";
		String str3 = new String("abc");
		System.out.println(str1 == str2);
		System.out.println(str1 == str3);
		System.out.println(str1 == str3.intern());
	}
}
