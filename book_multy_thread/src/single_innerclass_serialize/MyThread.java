package single_innerclass_serialize;

public class MyThread extends Thread {

	@Override
	public void run() {
		System.out.println("hashcode = " + Single.getInstance().hashCode());
	}
	
}
