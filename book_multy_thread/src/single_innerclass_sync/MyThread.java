package single_innerclass_sync;

public class MyThread extends Thread {

	@Override
	public void run() {
		System.out.println("hashcode = " + Single.getInstance().hashCode());
	}
	
}
