package exercises02;

public class ReadWriteMonitor {
    private int readers = 0;
    private boolean writer      = false;

    //////////////////////////
    // Read lock operations //
    //////////////////////////

    public synchronized void readLock() throws InterruptedException {
        while(writer)
            this.wait();
        readers++;
    }

    public synchronized void readUnlock() {
        readers--;
        if(readers==0)
            this.notifyAll();
    }

    ///////////////////////////
    // Write lock operations //
    ///////////////////////////

    public synchronized void writeLock() throws InterruptedException {
        while(writer || readers>0)
            this.wait();
        writer=true;
    }

    public synchronized void writeUnlock() {
        writer=false;
        this.notifyAll();
    }

    public static void main(String[] args) {
        ReadWriteMonitor m = new ReadWriteMonitor();
        for (int i = 0; i < 10; i++) {
        // start a reader
            new Thread(() -> {
                try {
                    m.readLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" Reader " + Thread.currentThread().getId() + " started reading");
                // read
                System.out.println(" Reader " + Thread.currentThread().getId() + " stopped reading");
                m.readUnlock();
            }).start();


            // start a writer
            new Thread(() -> {
                try {
                    m.writeLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(" Writer " + Thread.currentThread().getId() + " started writing");
                // write
                System.out.println(" Writer " + Thread.currentThread().getId() + " stopped writing");
                m.writeUnlock();
            }).start();
        }
    }

}
