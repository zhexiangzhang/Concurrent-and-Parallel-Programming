# WEEK 1

## 1.1

```
public class Problem1 {

    LongCounter lc = new LongCounter();
    int counts = 10_000_000;

    public Problem1() {

       Thread t1 = new Thread(() -> {
          for (int i = 0; i < counts; i++)
             lc.increment();
       });
       Thread t2 = new Thread(() -> {
          for (int i = 0; i < counts; i++)
             lc.increment();
       });
       t1.start();
       t2.start();
       try {
          t1.join();
          t2.join();
       } catch (InterruptedException exn) {
          System.out.println("Some thread was interrupted");
       }
       System.out.println("Count is " + lc.get() + " and should be " + 2 * counts);
    }

    public static void main(String[] args) {
       new Problem1();
    }

    class LongCounter {
       private long count = 0;

       public void increment() {
          count = count + 1;
       }

       public long get() {
          return count;
       }
    }
}
```

the output values is not 20 million. 

problem reason:  

* The statement count = count + 1 is not atomic
* Some interleaving result in threads reading stale  (outdated) data (two threads may both read the count, get the same value and increment at the same time, and then they only increment once instead of twice)
* Consequently, the program has race conditions that result  in incorrect outputs 

Use ReentrantLock to ensure the output = 20, argue correctness

```
class LongCounter {
    private long count = 0;

    private final Lock lock = new ReentrantLock();

    public void increment() {
       lock.lock();
       count = count + 1;
       lock.unlock();
    }

    public long get() {
       return count;
    }
}
```

use concepts and vocabulary introduced during the lecture, e.g., critical sections, interleaving, race conditions, mutual exclusion, etc.

The original code has a race condition (data races on counter) due to concurrent access to the shared `LongCounter` instance. The modified solution uses a `ReentrantLock` to enforce mutual exclusion in the critical section of the `increment` method. This  remove undesired interleaving and preventing race conditions (Operations in the critical section are always executed sequentially  by the same thread) and only one thread at a time can modify the `count` variable, guaranteeing the correct result of `2 * counts`. （变成了顺序执行，那肯定一样



<u>“Operations in the critical section are always executed sequentially by the same thread</u>“

“<u>We use locks to remove undesired interlinings, and happens-before can help  us reasoning about correctness</u>”

”<u>In the absence of happens-before relation between operations, the JVM is free to choose any  execution order • In that case we say that operations are executed concurrently</u>“



in terms of the happens-before relation: 

the use of `ReentrantLock` establishes a happens-before relationship between the lock and unlock operations, which ensures that (1) any operations or modifications made within the critical section by one thread are visible to subsequent threads that acquire the lock. (2) Also, happens-before relationship ensuring that the operations within the critical section are sequential executed（eliminates incorrect interleaving and only allow valid interleaving preserving the happens-before relation）, which preventing data races and ensuring the correctness of the final result (`2 * counts`).

    public void increment() {
       lock.lock();
       count = count + 1;
       lock.unlock();
    }

it can be written as : 

    public void increment() {
       lock.lock(); (1)
       int tmp = count; (2)
       count = tmp + 1; (3)
       lock.unlock(); (4)
    }

The happens-before relation tell us that for all interleaving, Let 𝑥, 𝑦 ∈ 1,2 . (By lock rule)  t_x(4) → t_y(1) 

• But also that  t_x(1 →  t_x(2 and  t_x(2 →  t_x(3 and  t_x(3 →  t_x(4 

• Then we can derive that   t_x(1 →  t_x(2 →  t_x(3 →  t_x(4 →  t_y(1 →  t_y(2 →

Thus, all the interleavings must include instructions  1-4 in a sequence of the form 𝑡𝑥 1 ,𝑡𝑥 2 ,𝑡𝑥 3 ,𝑡𝑥 4 ∗

This prevents that operations (2) and (3) are  executed concurrently (which was the  source of the race condition we saw above)



Set the variable counts to 3. What is the minimum value that the program prints?  Provide an interleaving showing how the minimum value output can occur.

thread: t1, t2, 

(1): int temp = count; 

(2): count = temp + 1; 

(3): count = temp - 1;

如果是一个Thread执行+1，一个执行-1: -3  （整体范围在-3到3）

​	t1(1) t2(1) t1(2) t2 (3)  t1(1) t2(1) t1(2) t2 (3)  t1(1) t2(1) t1(2) t2 (3) 

如果只有+1：2 （整体范围在2到6）

​	t1(1) t2(1) 

​	t1(2) t1(1) t1(2) t2(2)

​	t1(1) t2(1) t2(2) t2(1) t2(2) t1(2) 







In Goetz chapter 1.1, three motivations for concurrency is given: resource utilization, fairness and convenience. I

Compare the categories in the concurrency note and Goetz, try to find some examples of systems which are included in the categories of Goetz, but not in those in the concurrency note, and vice versa s

**Resource utilization=inherent**.（they both consider input and output） Programs sometimes have to wait for external operations such as input or output, and while waiting can do no useful work. It is more efficient to use that wait time to let another program run. 

**Fairness=hidden**. <u>(they both considered the allocation of resources)</u> Multiple users and programs may have equal claims on the machine’s resources. It is preferable to let them share the computer via finer-grained time slicing than to let one program run to completion and then start another

**Convenience**. It is often easier or more desirable to write several programs that each perform a single task and have them coordinate with each other as necessary than to write a single program that performs all the tasks

Convenience: Android apps being able to navigate between each other to perform tasks, such as the share button taking the user to the social media app, instead of implementing this share functionality in the original app.

Exploitation: xxxx



**Inherent**: 从数据库中读数据，同时可以继续操作

**Exploitation**: multiple threads are used to speed up counting the primes on muti-core CPU computer (each thread responsible for a range)

**Hidden**: we can listen to the music while navigate through Google map on mobile phone. (or download sth)



# EXE2

第二个例子

```
public class TestMutableInteger {

    public static void main(String[] args) {
        final MutableInteger mi = new MutableInteger();
        
		Thread t = new Thread(() -> {
				while (mi.get() == 0) {}     // Loop while zero
				System.out.println("I completed, mi = " + mi.get());
		});
		t.start();
		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
		
		mi.set(42);
		System.out.println("mi set to 42, waiting for thread ...");
		try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }

		System.out.println("Thread t completed, and so does main");
    }
}

class MutableInteger {
    private int value = 0;
    public void set(int value) { this.value = value; }
    public int get() {return value;}
}
```

"main" thread’s write to mi.value remains invisible to the t thread, so that it loops forever (threads may always use the cache value. 

we cant guarantee two threads are executed on same CPU. there is no happen-before relation between statements of different threads (t(mi.get() and mi.set(42))), CPU is allowed to keep the value of running in the register of the CPU or cache and not flush it to main memory, so it is not guaranteed that they will see the same shared memory state.



2. Use Java Intrinsic Locks (synchronized) on the methods to ensure that thread t always terminates. Explain why your solution prevents thread t from running forever.

```
class MutableInteger {
    private int value = 0;
    public synchronized void set(int value) { this.value = value; }
    public synchronized int get() {return value;}
}
```

we use it to Establishing a happen-before relation, so In the program below, it holds 𝑤ℎ𝑖𝑙𝑒 𝑟𝑢𝑛𝑛𝑖𝑛𝑔 → 𝑟𝑢𝑛𝑛𝑖𝑛𝑔 ≔ 𝑓𝑎𝑙𝑠𝑒 or 𝑟𝑢𝑛𝑛𝑖𝑛𝑔 ≔ 𝑓𝑎𝑙𝑠𝑒 → 𝑤ℎ𝑖𝑙𝑒. Consequently, when unlock() is executed, CPU registers and low-level cache are flushed (entirely) to memory levels shared by all CPUs, and before lock, it need to read data from memory, ensuring that the values of shared variables are up to date, and thus ensuring visibility. 



3. Would thread t always terminate if get() is not defined as synchronized? Explain your answer. 

   No, because if the get() method is not marked as synchronized, it may still choose to read the data from cache instead of main memory to get the value, which leads to an infinite loop.

   In terms of happen before: set() and get() are not restricted to the same monitor lock anymore, so we lose the happen before relationship. 

   

4. Remove all the locks in the program, and define value in Mutable_Integer as a volatile variable. Does thread t always terminate in this case? Explain your answer. 

   Answer: Yes, it will always terminate. Because the volatile keyword will keep the value in the main memory, so it is visible to all the threads, ensuring the thread t will eventually terminate.

​	In terms of happen before: A write to a volatile variable happens before any subsequent read to the volatile variables

​		* Writes to volatile variables flush registers and low level cache to shared memory levels (flashes memory for all variables in CPU registers/cache ==> => ensures visibility to writes on non-volatile variables prior that of the volatile variables)



# WEEK3