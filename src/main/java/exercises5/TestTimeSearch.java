package exercises5;


// jst@itu.dk * 2023-09-05

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import exercises5.benchmarking.Benchmark;
import exercises5.benchmarking.Benchmarkable;

public class TestTimeSearch {
    public static void main(String[] args) { new TestTimeSearch();}

    public TestTimeSearch() {
        final String filename = "R:\\KU\\PCPP\\mavenAssignment\\src\\main\\resources\\long-text-file.txt";
        final String target= "ipsum";

        final PrimeCounter lc= new PrimeCounter();  //name is abit misleading, it is just a counter
        String[] lineArray= readWords(filename);

        System.out.println("Array Size: "+ lineArray.length);
        System.out.println("# Occurences of "+target+ " :"+search(target, lineArray, 0, lineArray.length, lc));

//        Benchmark.Mark7("TestTimeSearch",
//                i -> search(target, lineArray, 0, lineArray.length, new PrimeCounter()));
        Benchmark.Mark7("TestTimeSearchParallel",
                i -> countParallelN(target, lineArray, 18, new PrimeCounter()));
    }

    static long search(String x, String[] lineArray, int from, int to, PrimeCounter lc){
        //Search each line of file
        for (int i=from; i<to; i++ ) lc.add(linearSearch(x, lineArray[i])); // each line is a string with words separated by spaces
        //System.out.println("Found: "+lc.get());
        return lc.get();
    }

    static int linearSearch(String x, String line) {
        //Search for occurences of c in line
        String[] arr= line.split(" ");
        int count= 0;
        for (int i=0; i<arr.length; i++ ) if ( (arr[i].equals(x)) ) count++;
        return count;
    }

     static long countParallelN(String target, String[] lineArray, int N, PrimeCounter lc) {
        final int perThread = lineArray.length / N;
        Thread[] threads = new Thread[N];
        for (int t=0; t<N; t++) {
            final int from = perThread * t,
                    to = (t+1==N) ? lineArray.length : perThread * (t+1);
            threads[t] = new Thread( () -> {
                search(target, lineArray, from, to, lc);
            });
        }
        for (int t=0; t<N; t++) threads[t].start();
        try {
            for (int t=0; t<N; t++) threads[t].join();
        } catch (InterruptedException exn) { }
        return lc.get();
    }


        public static String[] readWords(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            return reader.lines().toArray(String[]::new);   //will be explained in Week07;
        } catch (IOException exn) { return null;}
    }


}