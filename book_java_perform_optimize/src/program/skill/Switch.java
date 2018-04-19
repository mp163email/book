package program.skill;

public class Switch {
	
	/**
	 * 误人子弟******用switch比转换思想后的数组方式要快很多,跟书上说的不一样*********
	 * @author mp
	 * @date 2016-6-6 上午11:24:27
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		
		Switch switch1 = new Switch();
		
		
		long s = System.currentTimeMillis();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			switch1.method1(i);
		}
		long e = System.currentTimeMillis();
		System.out.println("method1 used-" + (e-s));
		
		
		long s1 = System.currentTimeMillis();
		int [] sw = new int [] {0,3,6,7,8,10,16,18,44};//将值放到相应下标上面,下标即为swith传的那个值,然后通过数组下标取值
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			switch1.method2(sw, i);
		}
		long e1 = System.currentTimeMillis();
		System.out.println("method2 used-" + (e1-s1));
	}
	
	/**
	 * switch方式
	 * @author mp
	 * @date 2016-6-6 上午11:18:00
	 * @param z
	 * @return
	 * @Description
	 */
	private  int method1 (int z) {
		int i = z % 10 + 1;
		switch (i) {
			case 1: return 3;
			case 2: return 6;
			case 3: return 7;
			case 4: return 8;
			case 5: return 10;
			case 6: return 16;
			case 7: return 18;
			case 8: return 44;
			default:return -1;
		}
	}
	
	/**
	 * 数组下标转换方式
	 * @author mp
	 * @date 2016-6-6 上午11:19:09
	 * @param sw
	 * @param z
	 * @return
	 * @Description
	 */
	private  int method2 (int [] sw, int z) {
		int i = z % 10 + 1;
		if (i > 7 || i < 1) {
			return -1;
		} else {
			return sw[i];
		}
	}
	
}
