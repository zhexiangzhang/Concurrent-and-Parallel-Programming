package exercises4;

class ReaderAndWriter {
    public static final int MAX_ALLOW_READERS = 5;
    private int readers = 0;
    private boolean can_write = true;
    private int writeRequests = 0;

    public synchronized void readLock() {
        while (!can_write || writeRequests > 0 || readers == MAX_ALLOW_READERS) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readers++;
    }

    public synchronized void readUnlock() {
        readers--;
        if (readers == 0) {
            notifyAll();
        }
    }

    public synchronized void writeLock() {
        writeRequests++;
        while (!can_write || readers > 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writeRequests--;
        can_write = false;
    }

    public synchronized void writeUnlock() {
        can_write = true;
        notifyAll();
    }

    public synchronized int getReaders() {
        return readers;
    }
}

