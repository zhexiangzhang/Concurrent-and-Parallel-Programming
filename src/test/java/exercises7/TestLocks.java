package exercises7;

// JUnit testing imports
import exercises7.q2.ReadWriteCASLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

// Data structures imports
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Concurrency imports
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestLocks {
    // The imports above are just for convenience, feel free add or remove imports

    private CyclicBarrier barrier;
    AtomicInteger writeLockHolders;

    // TODO: 10.2.5
    @Test
    @RepeatedTest(100)
    @DisplayName("Test SingleThread")
    public void singleThreadTest() {
        ReadWriteCASLock readWriteCASLock = new ReadWriteCASLock();

        // 10.2.5.1
        assertTrue(readWriteCASLock.writerTryLock());
        assertFalse(readWriteCASLock.readerTryLock());
        readWriteCASLock.writerUnlock();

        // 10.2.5.2
        assertTrue(readWriteCASLock.readerTryLock());
        assertFalse(readWriteCASLock.writerTryLock());
        readWriteCASLock.readerUnlock();

        // 10.2.5.3
        assertThrows(RuntimeException.class, () -> readWriteCASLock.readerUnlock(),
                "cant unlock reader when not locked");
        assertThrows(RuntimeException.class, () -> readWriteCASLock.writerUnlock(),
                "cant unlock writer when not locked");
    }


    // TODO: 10.2.6
    @Test
    @RepeatedTest(100)
    @DisplayName("Test MultipleWriterThreads")
    public void testMultipleWriterThreads() {
        ReadWriteCASLock readWriteCASLock = new ReadWriteCASLock();
        writeLockHolders = new AtomicInteger(0);
        final int numThreads = 16;
        final int numIterations = 100;

        barrier = new CyclicBarrier(numThreads + 1);

        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int j = 0; j < numIterations; j++) {
                        if (readWriteCASLock.writerTryLock()) {
                            assertTrue(writeLockHolders.incrementAndGet() < 2, writeLockHolders.get() + " writers");
                            // if we unlock before decrementing, holders number may be greater than 1
                            writeLockHolders.decrementAndGet();
                            readWriteCASLock.writerUnlock();
                        }
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
