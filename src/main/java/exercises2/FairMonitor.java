package exercises02;

public class FairMonitor {
    private int readerCount = 0;
    private int readerReleaseCount = 0;
    private boolean writer      = false;

     public synchronized void readLock() throws InterruptedException {
        while(writer)
            this.wait();
        readerCount++;
    }

    public synchronized void readUnlock() {
        readerReleaseCount++;
        if(readerCount==readerReleaseCount)
            this.notifyAll();
    }

    public synchronized void writeLock() throws InterruptedException {
        while(writer)
            this.wait();
        writer=true;
        while(readerReleaseCount != readerCount) {
            this.wait();
        }
    }

    public synchronized void writeUnlock() {
        writer=false;
        this.notifyAll();
    }

    public static void main(String[] args) {
        FairMonitor m = new FairMonitor();
        for (int i = 0; i < 10; i++) {
        // start a reader
            new Thread(() -> {
                try {
                    m.readLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" Reader " + Thread.currentThread().getId() + " started reading" + "| readercount: " + m.readerCount + "| read releases: " + m.readerReleaseCount);
                // read
                System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading" + " started reading" + "| readercount: " + m.readerCount + "| read releases: " + m.readerReleaseCount);
                m.readUnlock();
            }).start();

            // start a writer
            new Thread(() -> {
                try {
                    m.writeLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" Writer " + Thread.currentThread().getId() + " started writing" + " started reading" + "| readercount: " + m.readerCount + "| read releases: " + m.readerReleaseCount);
                // write
                System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing" + " started reading" + "| readercount: " + m.readerCount + "| read releases: " + m.readerReleaseCount);
                m.writeUnlock();
            }).start();
        }
    }

}