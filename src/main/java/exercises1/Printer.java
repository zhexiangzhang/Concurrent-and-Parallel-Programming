package exercises1;

import java.util.concurrent.locks.ReentrantLock;

public class Printer {

    private final ReentrantLock lock = new ReentrantLock();

    public void print() {
        lock.lock();
        System.out.print("-");                                      // (1)
        try {
            Thread.sleep(50); 
        } catch (InterruptedException exn) { lock.unlock(); }
        System.out.print("|");  
        lock.unlock();                                                // (2)
    }

    public static void main(String[] args) {

        Printer p = new Printer();

        Thread t1 = new Thread(() -> {
            while (true) {
                p.print();
            }

        });

        Thread t2 = new Thread(() -> {
            while (true) {
                p.print();
            }
        });

        t1.start(); t2.start();
        
    }
}
