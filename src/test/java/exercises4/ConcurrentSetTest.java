package exercises4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CyclicBarrier;

import static org.junit.jupiter.api.Assertions.assertTrue;
// TODO: Very likely you need to expand the list of imports

public class ConcurrentSetTest {

    // Variable with set under test
    private ConcurrentIntegerSet set;

    private CyclicBarrier barrier;
    // TODO: Very likely you should add more variables here


    // Uncomment the appropriate line below to choose the class to
    // test
    // Remember that @BeforeEach is executed before each test
    @BeforeEach
    public void initialize() {
        // init set
//        set = new ConcurrentIntegerSetBuggy();
        // set = new ConcurrentIntegerSetSync();
         set = new ConcurrentIntegerSetLibrary();
    }

    @RepeatedTest(5000)
    @DisplayName("Test add to set")
    public void testAddToSetParallel() {
        int nrThreads = 16;
        int N = 10;

        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        for (int i = 0; i < nrThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int j = 0; j < N; j++) {
                        boolean res = set.add(j);
                        if (res) {
                            System.out.println("Thread " + Thread.currentThread().getId() + " added " + j);
                        }
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

        assertTrue(set.size() == N, "Set size is " + set.size() + " but should be " + N);
    }


    @RepeatedTest(5000)
    @DisplayName("Test remove from set")
    public void testRemoveToSetParallel() {
        int nrThreads = 16;
        int N = 10;
        int removeCnt = 8;

        // init barrier
        barrier = new CyclicBarrier(nrThreads + 1);

        for (int i = 0; i < N; i++) {
            set.add(i);
        }

        for (int i = 0; i < nrThreads; i++) {
            new Thread(() -> {
                try {
                    barrier.await();
                    for (int j = 0; j < removeCnt; j++) {
                        boolean res = set.remove(j);
                        if (res) {
                            System.out.println("Thread " + Thread.currentThread().getId() + " remove " + j);
                        }
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

        assertTrue(set.size() == N - removeCnt, "Set size is " + set.size() + " but should be " + (N - removeCnt));
    }
}