package exercises7;

import exercises6.q3.Histogram1;
import exercises7.q1.CasHistogram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestCasHistogram {

    private static final int range = 5_000_000,
            threadPow = 4;
    private static final int span = 25;

    private static final int[] counts = new int[span];

    @BeforeEach
    public void initialize() {
        final Histogram1 histogram = new Histogram1(span);

        for (int p = 0; p < range; p++) {
            int factors = countFactors(p);
            histogram.increment(factors);
        }

        for (int i = 0; i < span; i++) {
            counts[i] = histogram.getCount(i);
        }

        System.out.println("initialization done");
    }

    @Test
    @RepeatedTest(1)
    public void testCasHistogram() throws InterruptedException {
        for (int i = 0; i <= threadPow; i++) {
            int threadN = (int) Math.pow(2, i);
            System.out.println("threadN: " + threadN);
            final CasHistogram histogram = new CasHistogram(span);

            final int perThread = range / threadN;
            Thread[] threads = new Thread[threadN];
            for (int t = 0; t < threadN; t++) {
                final int from = perThread * t,
                        to = (t + 1 == threadN) ? range : perThread * (t + 1);
                threads[t] = new Thread(() -> {
                    for (int p = from; p < to; p++) {
                        int factors = countFactors(p);
                        histogram.increment(factors);
                    }
                });
            }
            for (int t = 0; t < threadN; t++) {
                threads[t].start();
            }
            for (int t = 0; t < threadN; t++) {
                try {
                    threads[t].join();
                } catch (InterruptedException exn) {
                    System.out.println("A thread was interrupted");
                }
            }

            for (int j = 0; j < span; j++) {
                assertTrue(histogram.getCount(j) == counts[j]);
            }

            System.out.println("threadN: " + threadN + " done");

        }
    }

    public static int countFactors(int p) {
        if (p < 2) return 0;
        int factorCount = 1, k = 2;
        while (p >= k * k) {
            if (p % k == 0) {
                factorCount++;
                p= p/k;
            } else
                k= k+1;
        }
        return factorCount;
    }
}
