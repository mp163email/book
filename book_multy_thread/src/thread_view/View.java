package thread_view;


public class View {
	
	private boolean flag = true;
	
	public void viewFlag () {
		while (flag) {
			try {
				System.out.println(flag);
				Thread.sleep(1000);
			} catch (Exception e) {
				
			}
		}
	}
	
	public void update () {
		flag = false;
	}
	
}
