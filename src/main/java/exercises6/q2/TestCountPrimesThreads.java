package exercises6.q2;

import exercises6.benchmarking.Benchmark;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class TestCountPrimesThreads {
    public static void main(String[] args) { new TestCountPrimesThreads(); }

    public TestCountPrimesThreads() {
        final int range = 100_000;
        Benchmark.Mark7("countSequential", i -> countSequential(range));
        for (int c=1; c<=32; c++) {
            final int threadCount = c;
            Benchmark.Mark7(String.format("countParallelN %2d", threadCount),
                    i -> countParallelN(range, threadCount));
            Benchmark.Mark7(String.format("countParallelNLocal %2d", threadCount),
                    i -> countParallelNLocal(range, threadCount));
            Benchmark.Mark7(String.format("countParallelNFuture %2d", threadCount),
                    i -> countParallelNFuture(range, threadCount));
        }
    }

    private static boolean isPrime(int n) {
        int k = 2;
        while (k * k <= n && n % k != 0)
            k++;
        return n >= 2 && k * k > n;
    }

    // Sequential solution
    private static long countSequential(int range) {
        long count = 0;
        final int from = 0, to = range;
        for (int i=from; i<to; i++)
            if (isPrime(i))
                count++;
        return count;
    }

    private static long countParallelNFuture(int range, int threadCount) {
        final int perThread = range / threadCount;
        int poolSize = threadCount / 2 + 1;
        final ExecutorService pool= Executors.newFixedThreadPool(poolSize);
        final Future<Long>[] futures = new Future[threadCount];
        for (int t=0; t<threadCount; t++) {
            final int from = perThread * t,
                    to = (t+1==threadCount) ? range : perThread * (t+1);
            Callable task = () -> {
                long count = 0;
                for (int i=from; i<to; i++)
                    if (isPrime(i))
                        count++;
                return count;
            };
            futures[t] = pool.submit(task);
        }
        long cnt = 0;
        for (int t=0; t<threadCount; t++)
            try {
                cnt += futures[t].get();
            } catch (Exception exn) { }
        pool.shutdown();
        return cnt;
    }

    // General parallel solution, using multiple threads
    private static long countParallelN(int range, int threadCount) {
        final int perThread = range / threadCount;
        final AtomicLong lc = new AtomicLong(0);
        Thread[] threads = new Thread[threadCount];
        for (int t=0; t<threadCount; t++) {
            final int from = perThread * t,
                    to = (t+1==threadCount) ? range : perThread * (t+1);
            threads[t] = new Thread( () -> {
                for (int i=from; i<to; i++)
                    if (isPrime(i))
                        lc.incrementAndGet();
            });
        }
        for (int t=0; t<threadCount; t++)
            threads[t].start();
        try {
            for (int t=0; t<threadCount; t++)
                threads[t].join();
            //System.out.println("Primes: "+lc.get());
        } catch (InterruptedException exn) { }
        return lc.get();
    }

    // General parallel solution, using multiple threads
    private static long countParallelNLocal(int range, int threadCount) {
        final int perThread = range / threadCount;
        final long[] results = new long[threadCount];
        Thread[] threads = new Thread[threadCount];
        for (int t=0; t<threadCount; t++) {
            final int from = perThread * t,
                    to = (t+1==threadCount) ? range : perThread * (t+1);
            final int threadNo = t;
            threads[t] = new Thread( ()-> {
                long count = 0;
                for (int i=from; i<to; i++)
                    if (isPrime(i))
                        count++;
                results[threadNo] = count;
            });
        }
        for (int t=0; t<threadCount; t++)
            threads[t].start();
        try {
            for (int t=0; t<threadCount; t++)
                threads[t].join();
        } catch (InterruptedException exn) { }
        long result = 0;
        for (int t=0; t<threadCount; t++)
            result += results[t];
        return result;
    }
}


//countSequential                 3773243.9 ns   50870.25        128
//countParallelN  1               4080372.7 ns   63530.48         64
//countParallelNLocal  1          4115000.0 ns  100894.13         64
//countParallelNFuture  1         4087351.4 ns   96381.79         64
//countParallelN  2               2554377.7 ns   34927.21        128
//countParallelNLocal  2          2489968.8 ns   33432.66        128
//countParallelNFuture  2         2601642.0 ns   36709.21        128
//countParallelN  3               1841753.7 ns   27554.13        256
//countParallelNLocal  3          1819081.5 ns   42026.99        256
//countParallelNFuture  3         2771740.3 ns   39835.21        128
//countParallelN  4               1570366.1 ns  145832.47        256
//countParallelNLocal  4          2554629.6 ns 2112700.76        256
//countParallelNFuture  4         7016475.3 ns 1122737.93         64
//countParallelN  5               1475585.0 ns   62430.74        256
//countParallelNLocal  5          1382324.9 ns   30628.87        256
//countParallelNFuture  5         1879174.3 ns   44269.58        256
//countParallelN  6               1336552.1 ns   17295.26        256
//countParallelNLocal  6          1360785.4 ns   15156.84        256
//countParallelNFuture  6         1572706.3 ns   12350.70        256
//countParallelN  7               1296852.5 ns   19989.84        256
//countParallelNLocal  7          1277322.9 ns   23193.48        256
//countParallelNFuture  7         1483645.2 ns   18472.16        256
//countParallelN  8               1231890.4 ns   31213.69        256
//countParallelNLocal  8          1185105.7 ns   29234.18        256
//countParallelNFuture  8         1362316.0 ns   11526.95        256
//countParallelN  9               1224847.9 ns   61250.11        256
//countParallelNLocal  9          1173555.6 ns   57695.47        256
//countParallelNFuture  9         1332524.5 ns   24596.10        256
//countParallelN 10               1178772.5 ns   61263.99        256
//countParallelNLocal 10          1216752.3 ns   80909.82        256
//countParallelNFuture 10         1262794.6 ns   34297.02        256
//countParallelN 11               1206105.0 ns   59207.28        256
//countParallelNLocal 11          1204912.7 ns   45061.14        256
//countParallelNFuture 11         1353950.6 ns   91665.03        256
//countParallelN 12               1729038.6 ns 1437032.18         64
//countParallelNLocal 12          5230706.4 ns  365706.75         64
//countParallelNFuture 12         5266136.7 ns  275099.30         64
//countParallelN 13               1259047.1 ns   39109.35        256
//countParallelNLocal 13          1182654.3 ns   55317.54        256
//countParallelNFuture 13         1432788.9 ns   93257.54        256
//countParallelN 14               1477703.9 ns  112996.93        256
//countParallelNLocal 14          1399322.6 ns  156312.71        256
//countParallelNFuture 14         1292151.1 ns   85595.52        256
//countParallelN 15               1320334.6 ns   84495.67        256
//countParallelNLocal 15          1318025.5 ns  105235.77        256
//countParallelNFuture 15         1225668.9 ns   23555.21        256
//countParallelN 16               1328293.8 ns   51156.55        256
//countParallelNLocal 16          1305204.2 ns   30355.93        256
//countParallelNFuture 16         2252486.1 ns 1609623.02        256
//countParallelN 17               6652607.3 ns  629465.63         64
//countParallelNLocal 17          5846794.2 ns  499136.60         64
//countParallelNFuture 17         4955208.3 ns  310872.02         64
//countParallelN 18               6175751.3 ns  909340.72         64
//countParallelNLocal 18          6668677.0 ns  338690.53         64
//countParallelNFuture 18         5308196.7 ns  234497.82         64
//countParallelN 19               6301578.3 ns  863976.25         64
//countParallelNLocal 19          6568622.8 ns  786636.28         64
//countParallelNFuture 19         5142262.3 ns  266904.52         64
//countParallelN 20               5766233.4 ns  945287.15         64
//countParallelNLocal 20          5282878.1 ns  404690.01         64
//countParallelNFuture 20         5010844.5 ns  296308.02         64
//countParallelN 21               6768498.1 ns  826686.94         64
//countParallelNLocal 21          1433528.2 ns   24257.02        256
//countParallelNFuture 21         1144980.2 ns   21463.30        256
//countParallelN 22               1489715.0 ns   26920.79        256
//countParallelNLocal 22          1499974.1 ns   17286.77        256
//countParallelNFuture 22         1174040.4 ns   23171.86        256
//countParallelN 23               1599630.7 ns   36974.82        256
//countParallelNLocal 23          2105147.1 ns  876709.59        256
//countParallelNFuture 23         4962677.7 ns  180546.44         64
//countParallelN 24               7450151.6 ns  474214.56         64
//countParallelNLocal 24          5028808.3 ns  686910.56         64
//countParallelNFuture 24         5334477.5 ns  220633.18         64
//countParallelN 25               8294875.3 ns  596579.67         32
//countParallelNLocal 25          7919089.8 ns  435709.89         64
//countParallelNFuture 25         5114154.2 ns  442231.74         64
//countParallelN 26               8737798.1 ns  675896.53         32
//countParallelNLocal 26          8448539.1 ns  367583.52         32
//countParallelNFuture 26         5047753.4 ns  675333.52         64
//countParallelN 27               8247068.6 ns  837568.96         64
//countParallelNLocal 27          8836368.4 ns  370999.15         32
//countParallelNFuture 27         4623243.6 ns  818401.67         64
//countParallelN 28               1709594.0 ns   96682.96        256
//countParallelNLocal 28          1699194.4 ns   42793.00        256
//countParallelNFuture 28         1172929.4 ns   11153.62        256
//countParallelN 29               1726824.1 ns   34907.05        256
//countParallelNLocal 29          1779224.3 ns   46050.02        256
//countParallelNFuture 29         1187661.7 ns   22107.85        256
//countParallelN 30               1864863.4 ns  104878.18        256
//countParallelNLocal 30          1864610.5 ns   50252.83        256
//countParallelNFuture 30         1277469.3 ns   39936.13        256
//countParallelN 31               1817270.2 ns   20475.48        256
//countParallelNLocal 31          1835273.2 ns   72284.39        128
//countParallelNFuture 31         1254487.6 ns   29581.63        256
//countParallelN 32               2015411.8 ns  102232.44        128
//countParallelNLocal 32          1982057.3 ns   74918.17        128
//countParallelNFuture 32         1339935.2 ns   57477.64        256