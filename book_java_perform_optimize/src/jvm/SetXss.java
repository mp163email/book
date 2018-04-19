package jvm;

/**
 * 测试栈深度-溢出
 * -Xss10M 
 * @author mp
 * @date 2016-6-16 下午2:27:28
 */
public class SetXss {
	
	private static int count = 0;//默认最大为11422  通过-Xss10M来调整大小，调整后深度可达到141839
	
	public void recursion () {
		count ++;
		System.out.println(count);
		recursion();
	}
	
	public static void main(String[] args) {
		try {
			new SetXss().recursion();
		} catch (Exception e) {
			System.out.print("deep of stack is " + count);//为什么没有被catch捕捉到呢？
			e.printStackTrace();
		}
	}
	
}
