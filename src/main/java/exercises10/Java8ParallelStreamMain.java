package exercises10;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Java8ParallelStreamMain {
    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("Using Sequential Stream");
        System.out.println("=================================");

//        int[] array= {1,2,3,4,5,6,7,8,9,10};
        int[] array= IntStream.rangeClosed(1,20000000).toArray();

        IntStream intArrStream=Arrays.stream(array);
//        intArrStream.forEach(s->
//                {
//                    System.out.println(s+" "+Thread.currentThread().getName());
//                }
//        );
        System.out.println("countSequential - " + intArrStream.filter(s->isPrime(s)).count());


        System.out.println("=================================");
        System.out.println("Using Parallel Stream");
        System.out.println("=================================");
        IntStream intParallelStream=Arrays.stream(array).parallel();
//        intParallelStream.forEach(s->
//                {
//                    System.out.println(s+" "+Thread.currentThread().getName());
//                }
//        );
        System.out.println("countParallel - " + intParallelStream.filter(s->isPrime(s)).count());
    }

    private static boolean isPrime(int n) {
        int k= 2;
        while (k * k <= n && n % k != 0)
            k++;
        return n >= 2 && k * k > n;
    }
}

/*
*
=================================
Using Sequential Stream
=================================
1 main
2 main
3 main
4 main
5 main
6 main
7 main
8 main
9 main
10 main
=================================
Using Parallel Stream
=================================
7 main
6 main
8 ForkJoinPool.commonPool-worker-2
1 ForkJoinPool.commonPool-worker-3
2 ForkJoinPool.commonPool-worker-2
10 ForkJoinPool.commonPool-worker-3
5 ForkJoinPool.commonPool-worker-2
3 ForkJoinPool.commonPool-worker-1
9 main
4 ForkJoinPool.commonPool-worker-4
*
*
* */

/*
* Array : 1-30
*
=================================
Using Parallel Stream
=================================
20 main
19 main
22 main
21 main
5 ForkJoinPool.commonPool-worker-3
10 ForkJoinPool.commonPool-worker-1
17 main
28 ForkJoinPool.commonPool-worker-2
4 ForkJoinPool.commonPool-worker-3
7 ForkJoinPool.commonPool-worker-3
11 ForkJoinPool.commonPool-worker-1
1 ForkJoinPool.commonPool-worker-5
14 ForkJoinPool.commonPool-worker-7
6 ForkJoinPool.commonPool-worker-3
2 ForkJoinPool.commonPool-worker-6
25 ForkJoinPool.commonPool-worker-4
27 ForkJoinPool.commonPool-worker-2
18 main
30 ForkJoinPool.commonPool-worker-2
26 ForkJoinPool.commonPool-worker-4
3 ForkJoinPool.commonPool-worker-6
12 ForkJoinPool.commonPool-worker-3
15 ForkJoinPool.commonPool-worker-7
13 ForkJoinPool.commonPool-worker-5
9 ForkJoinPool.commonPool-worker-1
8 ForkJoinPool.commonPool-worker-3
23 ForkJoinPool.commonPool-worker-6
24 ForkJoinPool.commonPool-worker-4
29 ForkJoinPool.commonPool-worker-2
16 main
*
*
*
*
*
*
*
*
*
*
*
*
1-18
=================================
Using Parallel Stream
=================================
12 main
13 main
11 main
10 main
16 ForkJoinPool.commonPool-worker-2
15 main
18 ForkJoinPool.commonPool-worker-2
3 ForkJoinPool.commonPool-worker-3
6 ForkJoinPool.commonPool-worker-1
4 ForkJoinPool.commonPool-worker-3
2 ForkJoinPool.commonPool-worker-6
5 ForkJoinPool.commonPool-worker-5
8 ForkJoinPool.commonPool-worker-2
17 ForkJoinPool.commonPool-worker-4
14 main
9 ForkJoinPool.commonPool-worker-6
1 ForkJoinPool.commonPool-worker-3
7 ForkJoinPool.commonPool-worker-1

Process finished with exit code 0

* */