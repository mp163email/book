package main;


public class KingMain {
	
	public static void main(String[] args) {
		System.out.println(getStr());
	}
	
	public static String getStr () {
		try {
			System.out.println("try");
			return "sss";
		} catch (Exception e) {
			System.out.println("catch");
		} finally {
			System.out.println("finally");
			return "ffff";
		}
	}
	
		/*
		
		List<Integer> intList = new ArrayList<>();
		intList.add(1);
		intList.add(2);
		
		for (int i = 0; i < intList.size(); i++) {
			System.out.println(intList.get(i));
		}
		
		List<Integer> intList1 = new ArrayList<>();
		intList1.add(11);
		intList1.add(22);
		
		intList = intList1;
		
		System.out.println("==========================");
		
		for (int i = 0; i < intList.size(); i++) {
			System.out.println(intList.get(i));
		}
		
//		System.out.println(UUID.randomUUID());
		
		//[{"name":"1","id":1,"age":1},{"name":"11","id":11,"age":11}]
		
		
//		ConcurrentLinkedQueue<Integer> intList = new ConcurrentLinkedQueue<>();
//		intList.add(10);
//		intList.add(20);
//		intList.add(30);
//		
//		System.out.println(intList.size());
//		
//		int i = intList.poll();
//		
//		System.out.println("i=" + i + "  " + intList.size());
//		
//		i = intList.peek();
//		 
//		System.out.println("i=" + i + "  " + intList.size());
		
//		String str = "75425296xiaomi17";
//		System.out.println(str.substring(0, str.indexOf("@")));
		
//		List<String> strList = new ArrayList<>();
//		String str = "我们是中国人";
//		for (int i = 0; i < 25; i++) {
//			str += str;
//		}
//		System.out.println(str.getBytes().length);
//		for (;;) {
//			strList.add("str");
//		}
		
		
	
//		for (int i = 0; i < 100; i++) {
//			System.out.println(Math.random());
//		}
		
//		System.out.println(new Date().getTime());
		
//		for (int i = 1; i <= 22; i++) {
//			
//			int openServerDays = i;
//			
//			int week = openServerDays / 7;
//			int y = openServerDays % 7;
//			if (y != 0) {
//				week = week + 1;
//			}
//			System.out.println(i + "=" + week);
//		}
		
//		StringBuilder activityBuilder = new StringBuilder();
//		activityBuilder.append("a");
//		te(activityBuilder);
//		System.out.println(activityBuilder.toString());
	*/
	
	public static void te (StringBuilder activityBuilder) {
		activityBuilder.append("b");
		activityBuilder.append("c");
	}
}
