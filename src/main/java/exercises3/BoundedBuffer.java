package exercises3;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class BoundedBuffer implements BoundedBufferInteface{

    private final LinkedList<Object> buffer;
    private volatile int cnt;
    private final Semaphore empty; // single position in list, only have empty position can produce
    private final Semaphore full; // single position in list, only have full position can consume
    private final Semaphore mutex; // only one thread operate

    BoundedBuffer(int capacity) {
        this.buffer = new LinkedList<>();
        this.cnt = 0;
        this.empty = new Semaphore(capacity);
        this.full = new Semaphore(0);
        this.mutex = new Semaphore(1);
    }

    @Override
    public Object take() throws Exception {
        this.full.acquire();
        this.mutex.acquire();
        Object elem = this.buffer.getFirst();
        this.buffer.removeFirst();
        cnt--;
        this.empty.release();
        this.mutex.release();
        return elem;
    }

    @Override
    public void insert(Object elem) throws Exception {
        this.empty.acquire();
        this.mutex.acquire();
        this.buffer.add(elem);
        cnt++;
        this.full.release();
        this.mutex.release();
    }

    public int getCnt(){
        return cnt;
    }

    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBuffer(3);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    try {
                        buffer.insert(j);
                        System.out.println("producer thread" + j + " cnt:" + buffer.getCnt());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    try {
                        buffer.take();
                        System.out.println("consumer thread" + j + " cnt:" + buffer.getCnt());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}