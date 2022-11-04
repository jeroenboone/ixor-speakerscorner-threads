package be.ixor.speakerscorner.threads.completablefuture.rest;


import be.ixor.speakerscorner.threads.completablefuture.IxorThreadUtil;

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
        IxorThreadUtil.sleep();
    }

    private void printThreads() {
        IxorThreadUtil.printThreads();
    }
}
