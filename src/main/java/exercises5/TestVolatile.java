package exercises5;

import exercises5.benchmarking.Benchmark;

public class TestVolatile {
    private volatile int vCtr;
    private int ctr;

    public void vInc() {
        vCtr++;
    }

    public void inc() {
        ctr++;
    }

    public static void main(String[] args) {
        new TestVolatile().performTest();
    }

    public void performTest() {
        Benchmark.SystemInfo();
        // Mark7 measurements
        System.out.println("Mark 7 measurements");

        // Testing volatile increment
        Benchmark.Mark7("TestVolatile",
                i -> {
                    final TestVolatile testVolatile = new TestVolatile();
                    testVolatile.vInc();
                    return testVolatile.vCtr; // 确保编译器不会优化掉递增操作
                });

        // Testing non-volatile increment
        Benchmark.Mark7("TestNonVolatile",
                i -> {
                    final TestVolatile testNonVolatile = new TestVolatile();
                    testNonVolatile.inc();
                    return testNonVolatile.ctr;
                });
    }
}
