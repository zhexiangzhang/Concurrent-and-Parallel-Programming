package exercises8;

// JUnit testing imports
import exercises4.ConcurrentIntegerSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestLockFreeStack {
    // The imports above are just for convenience, feel free add or remove imports

    private LockFreeStack<Integer> stack;
    private CyclicBarrier barrier;

    @BeforeEach
    public void initialize() {
        stack = new LockFreeStack<>();
    }

    // TODO: 8.2.2 - Test push
    @Test
    @RepeatedTest(100)
    @DisplayName("Test push")
    public void testPush() {
        int nrThreads = 32;
        int expectedSum = 0;
        int actualSum = 0;
        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        for (int i = 0; i < nrThreads; i++) {
            int x = i;
            new Thread(() -> {
                try {
                    barrier.await();
                    stack.push(x);
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            expectedSum += x;
        }

        try {
            barrier.await();
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < nrThreads; i++) {
            Integer value = stack.pop();
            if (value != null) {
                actualSum += value;
            }
        }

        assertEquals(expectedSum, actualSum, "Stack sum is correct");
    }

    // TODO: 8.2.3 - Test pop
    @Test
    @RepeatedTest(100)
    @DisplayName("Test pop")
    public void testPop() {
        int num = 32;
        int nrThreads = 32;
        int expectedSum = 0;
        AtomicInteger actualSum = new AtomicInteger(0);
        barrier = new CyclicBarrier(nrThreads + 1);

        // Push elements onto the stack before testing the pop method
        for (int i = 0; i < num; i++) {
            stack.push(i);
            expectedSum += i;
        }

        for (int i = 0; i < nrThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    Integer value = stack.pop();
                    if (value != null) {
                        actualSum.addAndGet(value);
                    }
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        try {
            barrier.await();
            barrier.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(expectedSum, actualSum.get(), "Stack sum is correct");
    }
}
