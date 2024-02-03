package exercises3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// changllenge 3
public class Semaphore {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    private volatile int capacity;
    private volatile int wait_number = 0;

    Semaphore(int capacity) {
        this.capacity = capacity;
    }

    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            if (capacity > 0) {
                capacity--;
            } else {
                wait_number++;
                condition.await();
            }
        } catch (InterruptedException e) {
            throw e;
        } finally {
            lock.unlock();
        }

    }

    public void release() {
        lock.lock();
        if (wait_number > 0) {
            condition.signal();
            wait_number--;
        } else {
            capacity++;
        }
        lock.unlock();
    }

    public static void main(String[] args) {
        Semaphore s = new Semaphore(1);
        Thread t1 = new Thread() {
            public void run() {
                while (true) {
                    try {
                        s.acquire();
                        System.out.println("t1 get the semaphore");
                        System.out.println("It can guarantee the atomic for semaphore cap is 1");
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s.release();
                }
            };
        };
        Thread t2 = new Thread() {
            public void run() {
                while (true) {
                    try {
                        s.acquire();
                        System.out.println("t2 get the semaphore");
                        System.out.println("It can guarantee the atomic for semaphore cap is 1");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s.release();
                }
            };
        };
        Thread t3 = new Thread() {
            public void run() {
                while (true) {
                    try {
                        s.acquire();
                        System.out.println("t3 get the semaphore");
                        System.out.println("It can guarantee the atomic for semaphore cap is 1");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    s.release();
                }
            };
        };
        t1.start();
        t2.start();
        t3.start();
    }
}