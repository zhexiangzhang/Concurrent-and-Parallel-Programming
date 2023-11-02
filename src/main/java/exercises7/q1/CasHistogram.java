package exercises7.q1;

import exercises7.q1.Histogram;

import java.util.concurrent.atomic.AtomicInteger;

public class CasHistogram implements Histogram {

    private final AtomicInteger[] counts;

    public CasHistogram(int span) {
        this.counts = new AtomicInteger[span];
        for (int i = 0; i < span; i++) {
            this.counts[i] = new AtomicInteger(0);
        }
    }

    @Override
    public void increment(int bin) {
        int old;
        do {
            old = this.counts[bin].get();
        } while (!this.counts[bin].compareAndSet(old, old + 1));
    }

    @Override
    public int getCount(int bin) {
        return this.counts[bin].get();
    }

    @Override
    public int getSpan() {
        return this.counts.length;
    }

    @Override
    public int getAndClear(int bin) {
        int old;
        do {
            old = this.counts[bin].get();
        } while (!this.counts[bin].compareAndSet(old, 0));
        return old;
    }
}
