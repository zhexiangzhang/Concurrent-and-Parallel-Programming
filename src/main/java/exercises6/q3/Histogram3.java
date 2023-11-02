package exercises6.q3;
// raup@itu.dk * 05/10/2022
// jst@itu.dk * 23/9/2023

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Histogram3 implements Histogram {
    private final int[] counts;
    private final Lock[] locks;

    public Histogram3(int span, int nrLocks) {
        this.counts = new int[span];
        this.locks = new Lock[nrLocks];
        for (int i = 0; i < nrLocks; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    public void increment(int bin) {
        int lock = bin % locks.length;

//        System.out.println("thread " + Thread.currentThread().getId() + " is get lock " + lock + " lock number " + locks.length);
        synchronized (locks[lock]) {
//            System.out.println("thread " + Thread.currentThread().getId() + " is in lock " + lock);
            counts[bin] = counts[bin] + 1;
        }
    }

    public int getCount(int bin) {
        synchronized (locks[bin % locks.length]) {
            return counts[bin];
        }
    }

    public int getSpan() {
        return counts.length;
    }
}