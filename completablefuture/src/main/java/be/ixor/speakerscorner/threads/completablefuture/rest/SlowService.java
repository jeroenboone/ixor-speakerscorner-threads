package be.ixor.speakerscorner.threads.completablefuture.rest;


import java.util.Set;

public class SlowService {


    public String slow1() {
        printThreads();
        sleep();
        return "one";
    }


    public String slow2() {
        printThreads();
        sleep();
        return "two";
    }


    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printThreads() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        System.out.println("de threads: " + threads);
    }
}
