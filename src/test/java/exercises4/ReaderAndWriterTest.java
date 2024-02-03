package exercises4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReaderAndWriterTest {
    public static final int MAX_ALLOW_READERS = 5;
    private ReaderAndWriter rw;
    private CyclicBarrier barrier;
    @BeforeEach
    public void initialize() {
        // init set
        rw = new ReaderAndWriter();
    }
    @RepeatedTest(5000)
    @DisplayName("Test Reader and Writer")
    public void testingReaderAndWriter() {
        final int N = 100;
        final int nThreads = 16;
        System.out.printf("Parallel counter tests with %d threads and %d iterations",
                nThreads, N);
        // init barrier
        barrier = new CyclicBarrier(nThreads + 1);

        for (int i = 0; i < nThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int j = 0; j < N; j++) {
                        assertTrue(rw.getReaders() <= MAX_ALLOW_READERS, "illegal number of readers");
                        rw.readLock();
                        assertTrue(rw.getReaders() <= MAX_ALLOW_READERS, "illegal number of readers");
                        rw.readUnlock();
                        assertTrue(rw.getReaders() <= MAX_ALLOW_READERS, "illegal number of readers");
                        rw.writeLock();
                        assertTrue(rw.getReaders() <= MAX_ALLOW_READERS, "illegal number of readers");
                        rw.writeUnlock();
                        assertTrue(rw.getReaders() <= MAX_ALLOW_READERS, "illegal number of readers");
                    }
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        try {
            barrier.await();
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class ReaderAndWriter {
    public static final int MAX_ALLOW_READERS = 5;
    private int readers = 0;
    private boolean can_write = true;
    private int writeRequests = 0;

    public synchronized void readLock() {
        while (!can_write || writeRequests > 0 || readers == MAX_ALLOW_READERS) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readers++;
    }

    public synchronized void readUnlock() {
        readers--;
        if (readers == 0) {
            notifyAll();
        }
    }

    public synchronized void writeLock() {
        writeRequests++;
        while (!can_write || readers > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeRequests--;
        can_write = false;
    }

    public synchronized void writeUnlock() {
        can_write = true;
        notifyAll();
    }

    public synchronized int getReaders() {
        return readers;
    }
}

