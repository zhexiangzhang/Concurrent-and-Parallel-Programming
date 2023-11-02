package exercises6.q3;

// first version by Kasper modified by jst@itu.dk 24-09-2021
// raup@itu.dk * 05/10/2022
// jst@itu.dk 22-09-2023

import exercises6.benchmarking.Benchmark;

public class HistogramPrimesThreadsNLocks {
    public static void main(String[] args) { new HistogramPrimesThreadsNLocks(); }
    private static final int range = 5_000_000,
            threadCount = 10,
            maxNrLocks = 10;

    public HistogramPrimesThreadsNLocks() {
//        Benchmark.Mark7(String.format("test_histogram2 %2d", threadCount),
//                i -> test_Histogram2(threadCount));
        for (int lockCnt = 1; lockCnt <= maxNrLocks; lockCnt++) {
            final int nrLocks = lockCnt;
            Benchmark.Mark7(String.format("test_histogram3 %2d with %2d locks", threadCount, nrLocks),
                    i -> test_Histogram3(threadCount, nrLocks));
        }
    }

    private static double test_Histogram2(int threadCount) {
        final Histogram histogram = new Histogram2(25); // 25 bins sufficient for a range of 0..4_999_999

        final int perThread = range / threadCount;
        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            threads[t] = new Thread(() -> {
                for (int p = from; p < to; p++) {
                    int factors = countFactors(p);
                    histogram.increment(factors);
                }
            });
        }
        for (int t = 0; t < threadCount; t++) {
            threads[t].start();
        }
        for (int t = 0; t < threadCount; t++) {
            try {
                threads[t].join();
            } catch (InterruptedException exn) {
                System.out.println("A thread was interrupted");
            }
        }

        // Finally we plot the result to ensure that it looks as expected (see example output in the exercise script)
//        dump(histogram);
        return histogram.getCount(0);
    }

    private static double test_Histogram3(int threadCount, int nrLocks) {
//        System.out.println("test_Histo " + nrLocks + " locks ------------------");
        final Histogram histogram = new Histogram3(25, nrLocks); // 25 bins sufficient for a range of 0..4_999_999

        final int perThread = range / threadCount;
        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++) {
            final int from = perThread * t,
                    to = (t + 1 == threadCount) ? range : perThread * (t + 1);
            threads[t] = new Thread(() -> {
                for (int p = from; p < to; p++) {
                    int factors = countFactors(p);
                    histogram.increment(factors);
                }
            });
        }
        for (int t = 0; t < threadCount; t++) {
            threads[t].start();
        }
        for (int t = 0; t < threadCount; t++) {
            try {
                threads[t].join();
            } catch (InterruptedException exn) {
                System.out.println("A thread was interrupted");
            }
        }

        // Finally we plot the result to ensure that it looks as expected (see example output in the exercise script)
//        dump(histogram);
        return histogram.getCount(0);
    }

    // Returns the number of prime factors of `p`
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

    public static void dump(Histogram histogram) {
        for (int bin= 0; bin < histogram.getSpan(); bin= bin+1) {
            System.out.printf("%4d: %9d%n", bin, histogram.getCount(bin));
        }
    }
}
