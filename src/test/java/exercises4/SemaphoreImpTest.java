package exercises4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SemaphoreImpTest {
    private SemaphoreImp semaphore;
    private CyclicBarrier barrier;
    private final int capacity = 3;

    @BeforeEach
    public void initialize() {
        semaphore = new SemaphoreImp(capacity);
    }

    @Test
    @RepeatedTest(5000)
    public void testSemaphoreImp() throws InterruptedException {
        int nrThreads = 4;
        int N = 3;
        AtomicInteger acquireCount = new AtomicInteger(0);
        // init barrier
//        barrier = new CyclicBarrier(2*nrThreads + 1);

        semaphore.release();

        for (int i = 0; i < nrThreads; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    acquireCount.getAndIncrement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }


        Thread.sleep(1000);
        assertTrue(acquireCount.get() == capacity, "acquireCount: " + acquireCount.get() + " capacity: " + capacity);

        System.exit(0);
    }
}
