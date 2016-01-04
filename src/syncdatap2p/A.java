package syncdatap2p;

public class A {
	int strong = 0;
	int original = 0;

	class InputThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					sleep(1 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				original = (int) Math.random() * 100;
				System.out.println(">>>>>>>>>>>>>>>>>>>" + original);
			}
		}
	}

	class HandleThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {
					sleep(2 * 1000);
					strong = original;
					System.out.println("<<<<<<<<<<<<<<<<<" + strong);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	void init() {
		new InputThread().start();
		new HandleThread().start();
	}
}
