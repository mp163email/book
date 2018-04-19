package thread.interrupted;


public class MyThread extends Thread {

	@Override
	public void run() {
		try {
			while (true) {
				if (Thread.interrupted()) {//等于true的时候只有一次,随后就会被初始回来,成为false
					System.out.println("--interrupted--");
					System.out.println(Thread.interrupted());
					System.out.println(Thread.interrupted());
					System.out.println(Thread.interrupted());
					break;
				} else {
					System.out.println("--no interruted--");
				}
			}
			System.out.println("out while--" + Thread.interrupted());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
