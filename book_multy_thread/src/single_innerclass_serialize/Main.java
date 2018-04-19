package single_innerclass_serialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Main {
	public static void main(String[] args) {
		
		Single single = Single.getInstance();
		
		//序列化
		try {
			FileOutputStream fileOutputStream = new FileOutputStream("E://seriolize.txt");
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(single);
			
			fileOutputStream.close();
			objectOutputStream.close();
			
			System.out.println("serialize = " + single.hashCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("============================================");
		
		try {
			FileInputStream fileInputStream = new FileInputStream("E://seriolize.txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			Single singleuns = (Single)objectInputStream.readObject();
			fileInputStream.close();
			objectInputStream.close();
			
			System.out.println("unserialize = " + singleuns.hashCode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
