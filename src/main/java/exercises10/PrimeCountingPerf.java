package exercises10;

//Exercise 10.1
//JSt vers Oct 23, 2023

import java.util.*;
import java.util.stream.*;
import exercises10.benchmarking.Benchmark;

class PrimeCountingPerf {
    public static void main(String[] args) { new PrimeCountingPerf(); }
    static final int range= 100000;

    //Test whether n is a prime number
    private static boolean isPrime(int n) {
        int k= 2;
        while (k * k <= n && n % k != 0)
            k++;
        return n >= 2 && k * k > n;
    }

    // Sequential solution
    private static long countSequential(int range) {
        long count = 0;
        final int from = 0, to = range;
        for (int i=from; i<to; i++)
            if (isPrime(i)) count++;
        return count;
    }

//     IntStream solution
//    private static long countIntStream(int range) {
//        // to be filled out
//        long count = IntStream.range(2, range)
//                .filter(PrimeCountingPerf::isPrime)
//                .count();
//        return count;
//    }

     private static long countIntStream(int range) {

         long count = IntStream.range(2, range)
                 .filter(PrimeCountingPerf::isPrime)
                 .map((prime) -> {
                     System.out.println(prime);
                     return prime;
                 }).count();
         return count;
     }

    // Parallel Stream solution
    private static long countParallel(int range) {
        long count = IntStream.range(2, range)
                .parallel()
                .filter(PrimeCountingPerf::isPrime)
                .count();
        return count;
    }

    // parallelStream solution
    private static long countparallelStream(List<Integer> list) {
        long count= list
                .parallelStream()
                .filter(PrimeCountingPerf::isPrime)
                .count();
        return count;
    }

    public PrimeCountingPerf() {
        Benchmark.Mark7("Sequential", i -> countSequential(range));

        Benchmark.Mark7("IntStream", i -> countIntStream(range));

        Benchmark.Mark7("Parallel", i -> countParallel(range));

        List<Integer> list = new ArrayList<Integer>();
        for (int i= 2; i< range; i++){ list.add(i); }
        Benchmark.Mark7("ParallelStream", i -> countparallelStream(list));
    }
}

/*
Sequential                      5589770.8 ns  103319.64         64
IntStream                             1.3 ns       0.03  268435456
Parallel                              3.0 ns       0.06  134217728
ParallelStream                        3.6 ns       0.38  134217728
* */