package exercises7.q1;

import exercises5.benchmarking.Timer;

import java.util.function.IntToDoubleFunction;

public class TestCASLockHistogram {
    // Testing correctness and evaluating performance
    public static void main(String[] args) {

        // Create an object `histogramCAS` with your Histogram CAS implementation
        // Create an object `histogramLock` with your Histogram Lock from week 5


        // Evaluate the performance of CAS vs Locks histograms
        // Below you have the code for `countParallel`
        // You also have the benchmarking code for Mark7
        final int maxNrLocks = 4;
        final int range = 5_000_000;

        for (int j = 0; j <= maxNrLocks; j++) {


            final CasHistogram histogramCAS = new CasHistogram(25);
            final Histogram2 histogramLock = new Histogram2(25);
            System.out.println("j: " + j);
            final int nrThreads = (int) Math.pow(2, j);
            Mark7(String.format("CAS with %d thread", nrThreads), i -> {
                countParallel(range, nrThreads, histogramCAS);
                return 0;
            });
            Mark7(String.format("Lock with %d thread", nrThreads), i -> {
                countParallel(range, nrThreads, histogramLock);
                return 0;
            });
        }
    }

    // Function to count the prime factors of a number `p`
    private static int countFactors(int p) {
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

    // Parallel execution of counting the number of primes for numbers in `range`
    private static void countParallel(int range, int threadCount, Histogram h) {
        final int perThread= range / threadCount;
        Thread[] threads= new Thread[threadCount];
        for (int t=0; t<threadCount; t= t+1) {
            final int from= perThread * t,
                    to= (t+1==threadCount) ? range : perThread * (t+1);
            threads[t]= new Thread( () -> {
                for (int i= from; i<to; i++) h.increment(countFactors(i));

            });
        }
        for (int t= 0; t<threadCount; t= t+1)
            threads[t].start();
        try {
            for (int t= 0; t<threadCount; t= t+1)
                threads[t].join();
        } catch (InterruptedException exn) { }
    }

    // Benchmark function
    public static double Mark7(String msg, IntToDoubleFunction f) {
        int n = 10, count = 1, totalCount = 0;
        double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
        do {
            count *= 2;
            st = sst = 0.0;
            for (int j=0; j<n; j++) {
                Timer t = new Timer();
                for (int i=0; i<count; i++)
                    dummy += f.applyAsDouble(i);
                runningTime = t.check();
                double time = runningTime * 1e9 / count;
                st += time;
                sst += time * time;
                totalCount += count;
            }
        } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
        double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
        System.out.printf("%-25s %15.1f ns %10.2f %10d%n", msg, mean, sdev, count);
        return dummy / totalCount;
    }
}


//        j: 0
//        CAS with 1 locks             3679635790.0 ns 54530065.04          2
//        Lock with 1 locks            3820499660.0 ns 255180476.57          2
//        j: 1
//        CAS with 2 locks             2521102850.0 ns 83937117.73          2
//        Lock with 2 locks            2684692615.0 ns 110947722.17          2
//        j: 2
//        CAS with 4 locks             1634964755.0 ns 39586531.86          2
//        Lock with 4 locks            1782170865.0 ns 57214556.79          2
//        j: 3
//        CAS with 8 locks             1010244000.0 ns 26282580.77          2
//        Lock with 8 locks            1415625890.0 ns 18361342.84          2