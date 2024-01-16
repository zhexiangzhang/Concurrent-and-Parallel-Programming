package exercises10;

//Exercise 10.?
//JSt vers Oct 23, 2023

//install  src/main/resources/english-words.txt
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import exercises10.benchmarking.Benchmark;

public class TestWordStream {
    public static void main(String[] args) {
        String filename = "src/main/resources/english-words.txt";
        String url = "https://staunstrups.dk/jst/english-words.txt";
//        Q4(filename);
//        Q6
//        Benchmark.Mark7("Sequential Stream", i -> Q5(filename));
//        Benchmark.Mark7("Parallel Stream", i -> Q6(filename));

//        Q7
//        Q7(url);
        Q8(filename);
    }

    public static void Q1(String filename){
        System.out.println(readWords(filename).count());
    }

    public static void Q2(String filename){
        readWords(filename)
                .limit(100)
                .forEach(System.out::println);
    }

    public static void Q3(String filename){
        readWords(filename)
                .filter((word)->word.length()>=22)
                .forEach(System.out::println);
    }

    public static void Q4(String filename){
        readWords(filename)
                .filter((word)->word.length()>=22)
                .findAny()
                .ifPresent(System.out::println);
    }

    public static long Q5(String filename){
        long cnt = readWords(filename)
                .filter((word)->isPalindrome(word))
                .map((word)-> {
                    System.out.println(word);
                    return word;
                }).count();
        return cnt;
    }

    // NOTE: Need to execute the benchmark to show result of Q6
    public static long Q6(String filename){
        long cnt = readWords(filename)
                .parallel()
                .filter((word)->isPalindrome(word))
                .map((word) -> {
                    System.out.println(word);
                    return word;
                }).count();
        return cnt;
    }

    public static void Q7(String urlname){
        System.out.println(readWordsFromURL(urlname).count());
    }

    public static void Q8(String filename){
        IntSummaryStatistics stats = readWords(filename)
                .parallel()
                .map((word)->word.length())
                .collect(IntSummaryStatistics::new, // 建一个新的IntSummaryStatistics对象作为初始容器
                        IntSummaryStatistics::accept, // 将Stream的元素添加到统计信息容器的累加器。(将每个单词的长度添加到IntSummaryStatisticss对象)
                        IntSummaryStatistics::combine); // 将两个统计信息容器合并的组合器。在并行流的情况下，不同的部分可能在不同的线程上处理，因此需要将它们合并成一个。s
        System.out.println("Min: " + stats.getMin());
        System.out.println("Man: " + stats.getMax());
        System.out.println("Avg: " + stats.getAverage());
    }

    public static Stream<String> readWords(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            // TO DO: Implement properly
            Stream<String> stream = reader.lines();
            return stream;
        } catch (IOException exn) {
            return Stream.<String>empty();
        }
    }

    public static Stream<String> readWordsFromURL(String urlname) {
        try {
            HttpURLConnection connection= (HttpURLConnection) new URL(urlname).openConnection();
            BufferedReader reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return reader.lines();
        } catch (IOException exn) {
            return Stream.<String>empty();
        }
    }

    public static boolean isPalindrome(String s) {
        // TO DO: Implement properly
        String reverse = new StringBuffer(s).reverse().toString();

        if (reverse.equals(s)) {
            return true;
        }
        return false;
    }

    public static Map<Character,Integer> letters(String s) {
        Map<Character,Integer> res = new TreeMap<>();
        // TO DO: Implement properly
        return res;
    }
}

/*
*
Sequential Stream              40507218.8 ns 11028087.84          8
Parallel Stream                27105727.8 ns 10785156.56         32
*
* */