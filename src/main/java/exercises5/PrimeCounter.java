package exercises5;
class PrimeCounter {
    private int count= 0;

    public synchronized void increment() {
        count= count + 1;
    }

    public synchronized int get() {
        return count;
    }
    public synchronized void add(int c) {
        count= count + c;
    }
    public synchronized void reset() {
        count= 0;
    }
}


