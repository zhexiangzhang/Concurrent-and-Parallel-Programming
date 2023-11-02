package exercises7.q1;

public class Histogram2 implements Histogram{

    private final int[] counts;

    public Histogram2(int span) {
        this.counts = new int[span];
    }

    @Override
    public synchronized void increment(int bin) {
        counts[bin] = counts[bin] + 1;
    }


    @Override
    public int getCount(int bin) {
        return counts[bin];
    }

    @Override
    public int getSpan() {
        return counts.length;
    }

    @Override
    public int getAndClear(int bin) {
        int old = counts[bin];
        counts[bin] = 0;
        return old;
    }
}

