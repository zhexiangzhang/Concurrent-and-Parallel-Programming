package exercises8;

import java.util.concurrent.atomic.AtomicReference;

// Treiber's LockFree Stack (Goetz 15.4 & Herlihy 11.2)
class LockFreeStack<T> {
    AtomicReference<Node<T>> top = new AtomicReference<Node<T>>(); // Initializes to null
    //  1 2 (top)
    // push 3
    // pop=>2
    // 我们需要也argue 一个现线程在另一个线程已经执行完push1后再执行吗，还是说只是覆盖所有的if分支就行
    public void push(T value) {
        Node<T> newHead = new Node<T>(value);
        Node<T> oldHead;
        do {
            oldHead      = top.get();
            newHead.next = oldHead;
            //
        } while (!top.compareAndSet(oldHead,newHead));  // PUSH1

    }

    // 对与这个，也要分析一个线程在执行pop一个在执行push的情况吗，还是只要找两个情景覆盖所有的if分支就行
    public T pop() {
        Node<T> newHead;
        Node<T> oldHead;
        do {
            oldHead = top.get();  // POP1
            if(oldHead == null) { // POP2
                return null;
            }
            newHead = oldHead.next;
        } while (!top.compareAndSet(oldHead,newHead));  // POP3

        return oldHead.value;
    }

    // class for nodes
    private static class Node<T> {
        public final T value;
        public Node<T> next;

        public Node(T value) {
            this.value = value;
            this.next  = null;
        }
    }
}
