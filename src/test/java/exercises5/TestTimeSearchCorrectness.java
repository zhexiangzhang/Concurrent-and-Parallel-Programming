package exercises5;

import exercises4.SemaphoreImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import exercises5.TestTimeSearch;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestTimeSearchCorrectness {
    private TestTimeSearch testTimeSearch;
    private CyclicBarrier barrier;
    private String[] lineArray;
    private String target;

    @BeforeEach
    public void initialize() {
        final String filename = "E:\\KU\\1\\PCPP\\mavenAssignment\\src\\main\\resources\\long-text-file.txt";
        target= "ipsum";

        final PrimeCounter lc= new PrimeCounter();  //name is abit misleading, it is just a counter
        lineArray= TestTimeSearch.readWords(filename);

        testTimeSearch = new TestTimeSearch();
    }

    @Test
    @RepeatedTest(5000)
    public void testCountParallelN() throws InterruptedException {

        long res = TestTimeSearch.search(target, lineArray, 0, lineArray.length, new PrimeCounter());

        int nThreads = 8;
        for (int i = 0; i < nThreads; i++) {
            long resConcurrent = TestTimeSearch.countParallelN(target, lineArray, 8, new PrimeCounter());
            assertTrue(res == resConcurrent, "res: " + res + " resConcurrent: " + resConcurrent);
        }
    }
}
