package exercises3;

// exercise 3.2

public class Person {

    private static long idCounter = 0;
    private static boolean isFirstInstance = true;

    private final long id;
    private String name = "";
    private int zip = 0;
    private String address = "";

    public Person() {
        synchronized (this.getClass()) {
            if (isFirstInstance) {
                System.out.println("First instance id is 0");
                isFirstInstance = false;
            }
            this.id = idCounter;
            idCounter++;
        }
    }

    public Person(long id_) {
        synchronized (this.getClass()) {
            if (isFirstInstance) {
                System.out.println("First instance id is " + id_);
                idCounter = id_;
                isFirstInstance = false;
            }
            this.id = idCounter;
            idCounter++;
        }
    }

    public synchronized void setZipAndAddress(int zip, String address) {
        this.zip = zip;
        this.address = address;
    }

    public synchronized int getZip() { return zip; }

    public synchronized String getAddress() { return address; }

    public synchronized long getId() { return id; }

    public synchronized String getName() { return name; }

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            Person p1 = new Person(5);
            p1.setZipAndAddress(1234, "A");
            System.out.println("p1: " + p1.getId() + " " + p1.getZip() + " " + p1.getAddress());
        });

        Thread t2 = new Thread(() -> {
            Person p2 = new Person(3);
            p2.setZipAndAddress(5678, "B");
            System.out.println("p2: " + p2.getId() + " " + p2.getZip() + " " + p2.getAddress());
        });

        Thread t3 = new Thread(() -> {
            Person p3 = new Person();
            p3.setZipAndAddress(4321, "C");
            System.out.println("p3: " + p3.getId() + " " + p3.getZip() + " " + p3.getAddress());
        });

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }
    }
}