package exercises5;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntToDoubleFunction;

import exercises5.benchmarking.Benchmark;
import exercises5.benchmarking.Benchmarkable;
import exercises5.benchmarking.Timer;

public class TestTimeThreads {

    public static void main(String[] args) { new TestTimeThreads(); }

    public TestTimeThreads() {
        Benchmark.SystemInfo();
        System.out.println("Mark 6 measurements");
        final Point myPoint = new Point(42, 39);
        Benchmark.Mark6("hashCode()", i -> myPoint.hashCode());
        Benchmark.Mark6("Point creation",
                i -> {
                    Point p = new Point(i, i);
                    return p.hashCode();
                });
        final AtomicInteger ai = new AtomicInteger();
        Benchmark.Mark6("Thread's work",
                i -> {
                    for (int j=0; j<1000; j++)
                        ai.getAndIncrement();
                    return ai.doubleValue();
                });
        Benchmark.Mark6("Thread create",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j=0; j<1000; j++)
                            ai.getAndIncrement();
                    });
                    return t.hashCode();
                });
        Benchmark.Mark6("Thread create start",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j=0; j<1000; j++)
                            ai.getAndIncrement();
                    });
                    t.start();
                    return t.hashCode();
                });
        Benchmark.Mark6("Thread create start join",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j=0; j<1000; j++)
                            ai.getAndIncrement();
                    });
                    t.start();
                    try { t.join(); }
                    catch (InterruptedException exn) { }
                    return t.hashCode();
                });
        System.out.printf("ai value = %d%n", ai.intValue());
        final Object obj = new Object();
        Benchmark.Mark6("Uncontended lock",
                i -> {
                    synchronized (obj) {
                        return i;
                    }
                });

        // Mark7 measurements
        System.out.println("Mark 7 measurements");
        Benchmark.Mark7("hashCode()", i -> myPoint.hashCode());
        Benchmark.Mark7("Point creation",
                i -> {
                    Point p = new Point(i, i);
                    return p.hashCode();
                });
        final AtomicInteger ai7 = new AtomicInteger();
        Benchmark.Mark7("Thread's work",
                i -> {
                    for (int j=0; j<1000; j++)
                        ai7.getAndIncrement();
                    return ai7.doubleValue();
                });
        Benchmark.Mark7("Thread create",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j=0; j<1000; j++)
                            ai7.getAndIncrement();
                    });
                    return t.hashCode();
                });
        Benchmark.Mark7("Thread create start",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j=0; j<1000; j++)
                            ai7.getAndIncrement();
                    });
                    t.start();
                    return t.hashCode();
                });
        Benchmark.Mark7("Thread create start join",
                i -> {
                    Thread t = new Thread(() -> {
                        for (int j=0; j<1000; j++)
                            ai7.getAndIncrement();
                    });
                    t.start();
                    try { t.join(); }
                    catch (InterruptedException exn) { }
                    return t.hashCode();
                });
        System.out.printf("ai value = %d%n", ai.intValue());
        final Object obj7 = new Object();
        Benchmark.Mark7("Uncontended lock",
                i -> {
                    synchronized (obj7) {
                        return i;
                    }
                });
    }
}

/**
 * Immutable Point class used by DelegatingVehicleTracker
 * @author Brian Goetz and Tim Peierls
 */
class Point {
    public final int x, y;
    public Point(int x, int y) {
        this.x = x; this.y = y;
    }
}