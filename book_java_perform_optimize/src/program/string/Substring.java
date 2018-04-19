package program.string;

import java.util.ArrayList;
import java.util.List;



public class Substring {
	
	public static void main(String[] args) {
		
		List<String> handlerList = new ArrayList<>();
		for (int i = 0; i < 100000; i++) {
			handlerList.add(HugeStr.getSubString(1, 5));
		}
	}
	
	static class HugeStr {
		
		private static char [] chars = new char[100000];
		
		static {
			for (int i = 0; i < chars.length; i++) {
				chars[i] = '我';
			}
		}
		public static String getSubString (int begin, int end) {
			String string = new String(chars);
			System.out.println(string.getBytes().length);
			return string.substring(begin, end);
		}
	}
}
