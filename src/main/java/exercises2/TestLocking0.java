// For week 2
// sestoft@itu.dk * 2015-10-29
package exercises02;

import java.util.concurrent.locks.ReentrantLock;

public class TestLocking0 {
    public static void main(String[] args) {
		final int count = 1_000_000;
		var m = new NewVersionMystery();
		
		Thread t1 = new Thread(() -> { 
				for (int i = 0; i < count; i++)
					m.addInstance(1); 
		});

		Thread t2 = new Thread(() -> { 
				for (int i = 0; i < count; i++)
					m.addStatic(1); 
		});

		t1.start(); t2.start();
		try { t1.join(); t2.join(); } catch (InterruptedException exn) { }
		System.out.printf("Sum is %f and should be %f%n", m.sum(), 2.0 * count);
    }
}

class Mystery {
    private static double sum = 0;

    public static synchronized void addStatic(double x) {
		sum += x;
    }

    public synchronized void addInstance(double x) {
		sum += x;
    }

    public static synchronized double sum() {
		return sum;
    }
}

class NewVersionMystery {
    private static double sum = 0;
	private static ReentrantLock lock = new ReentrantLock(false);

    public static void addStatic(double x) {
		try {
			lock.lock();
			sum += x;
		} finally {
			lock.unlock();
		}
    }

    public void addInstance(double x) {
		try {
			lock.lock();
			sum += x;
		} finally {
			lock.unlock();
		}
    }

    public static double sum() {
		return sum;
    }
}