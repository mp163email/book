package other;

/**
 * 重试三次
 * return很关键
 * try-catch必须要有
 * @author mp
 * @date 2016-7-28 上午11:26:30
 */
public class TryAgain {
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-28 上午11:30:16
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		test ();
	}
	
	/**
	 * 测试方法
	 * @author mp
	 * @date 2016-7-28 上午11:30:29
	 * @Description
	 */
	public static void test () {
		int retry = 1;
		Exception e1;
		do {
			try {
				System.out.println("times = " + retry);
				int i = 0;
				i = i / i;
				return;// ****这里的return是关键,如果没报错的话,这里的return就会中断do-while循环****
			} catch (Exception e) {
				e1 = e;
				System.out.println("重连");
			}/* finally {
//				System.out.println("重连111"); //finally没用
			}*/
		} while (++ retry <= 3);
		
		e1.printStackTrace();
	}
}
