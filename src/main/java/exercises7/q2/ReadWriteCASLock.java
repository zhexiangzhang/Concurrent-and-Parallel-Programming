package exercises7.q2;

// Very likely you will need some imports here

import java.util.concurrent.atomic.AtomicReference;

public class ReadWriteCASLock implements SimpleRWTryLockInterface {

    // TODO: Add necessary field(s) for the class
    private final AtomicReference<Holders> holders = new AtomicReference<>(null);

    // 可能有多个线程同时竞争读锁，即还没更新完list就被添加了。
    // 在这个情况下，写锁应该不断重试直至成功。但写锁就不用了，因为写锁具有排他性。一旦写锁获取成功，其他线程只能等待
    public boolean readerTryLock() {

        ReaderList newReader = new ReaderList(Thread.currentThread(), null);

        while (true) {
            Holders currectHolders = holders.get();

            if (currectHolders == null) {
                // no currect holder, try to set holders to the new reader
                if (holders.compareAndSet(null, newReader)) {
                    return true;
                }
            }
            else if (currectHolders instanceof ReaderList) {
                ReaderList currentReaderList = (ReaderList) currectHolders;
                newReader.next = currentReaderList;
                if (holders.compareAndSet(currentReaderList, newReader)) {
                    return true;
                }
            }
            // challenging solution
//            else if (currectHolders instanceof ReaderList) {
//                ReaderList currentReaderList = (ReaderList) currectHolders;
//
//                if (!currentReaderList.contains(Thread.currentThread())) {
//                    newReader.next = currentReaderList;
//                    if (holders.compareAndSet(currentReaderList, newReader)) {
//                        return true;
//                    }
//                }
//                else {
//                    throw new RuntimeException("Thread already holds the lock");
//                }
//            }

            else {
                // currect holder is a writer, return false
                return false;
            }
        }
    }

    public void readerUnlock() {
        Thread currentThread = Thread.currentThread();
        Holders currectHolders = holders.get();
        if (currectHolders == null) {
            throw new RuntimeException("reader is null");
        } else if (currectHolders instanceof ReaderList) {
            ReaderList readerList = (ReaderList) currectHolders;
            if (readerList.contains(currentThread)) {
                ReaderList newReaderList = readerList.remove(Thread.currentThread());
                if (holders.compareAndSet(readerList, newReaderList)) {
                    return;
                }
            }
            else {
                throw new RuntimeException("reader is not current thread");
            }
        } else {
            throw new RuntimeException("holder is not a reader");
        }
    }

//     check that the lock is currently unheld and then atomically set holders to an appropriate Writer object
    public boolean writerTryLock() {
        Writer writer = new Writer(Thread.currentThread());
        if (holders.compareAndSet(null, writer)) {
            return true;
        }
        return false;
    }

    public void writerUnlock() {
        Writer writer = (Writer) holders.get();
        if (writer == null || writer.thread != Thread.currentThread()) {
            throw new RuntimeException("writer is null or thread is not current thread");
        }
        if (!holders.compareAndSet(writer, null)) {
            throw new RuntimeException("writer is not current thread");
        }
    }


    // Challenging 7.2.7: You may add new methods

    private static abstract class Holders { }

    private static class ReaderList extends Holders {
        private final Thread thread;
        private ReaderList next;

        // TODO: Constructor
        ReaderList(Thread thread, ReaderList list) {
            this.thread = thread;
            this.next = list;
        }

        // TODO: contains
        public boolean contains(Thread threadToFind) {
            if (threadToFind == null) {
                return false;
            }
            if (threadToFind == thread) {
                return true;
            }
            if (next != null) {
                return next.contains(threadToFind);
            }
            return false;
        }

        // TODO: remove
        public ReaderList remove(Thread threadToRemove) {
            if (threadToRemove == thread) {
                return next;
            }
            if (next != null) {
                return new ReaderList(thread, next.remove(threadToRemove));
            }
            return this;
        }
    }

    private static class Writer extends Holders {
        public final Thread thread;

        // TODO: Constructor
        Writer(Thread thread) {
            this.thread = thread;
        }
    }
}