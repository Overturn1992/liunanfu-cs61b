package tester;


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import student.StudentArrayDeque;

public class TestArrayDequeEC {
    @Test
    public void test1() {
        StudentArrayDeque<Integer> deque1 = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> deque2 = new ArrayDequeSolution<>();
        String message = "";
        for (Integer i = 0; i < 500; i++) {
            int random = StdRandom.uniform(0, 4);
            if (random == 0) {
                deque1.addFirst(i);
                deque2.addFirst(i);
                message = message + "addFirst("+i.toString()+")\\n";
                Assert.assertEquals(message,deque1.size(), deque2.size());
            } else if (random == 1) {
                deque1.addLast(i);
                deque2.addLast(i);
                message = message + "addLast("+i.toString()+")\\n";
                Assert.assertEquals(message,deque1.size(), deque2.size());
            } else if (random == 2) {
                if (deque1.isEmpty() || deque2.isEmpty()) {
                    continue;
                }
                message = message + "removeFirst()\\n";
                Assert.assertEquals(message,deque1.removeFirst(), deque2.removeFirst());
            } else {
                if (deque1.isEmpty() || deque2.isEmpty()) {
                    continue;
                }
                message = message + "removeLast()\\n";
                Assert.assertEquals(message,deque1.removeLast(), deque2.removeLast());
            }
        }
    }

//    @Test
//    public void test2() {
//        StudentArrayDeque<Integer> deque1 = new StudentArrayDeque<>();
//        ArrayDequeSolution<Integer> deque2 = new ArrayDequeSolution<>();
//        for (Integer i = 0; i < 20; i++) {
//            deque1.addFirst(i);
//            deque2.addFirst(i);
//        }
//        for (Integer i = 0; i < 20; i++) {
//            Assert.assertEquals(deque1.removeLast(), deque2.removeLast());
//        }
//    }
}
