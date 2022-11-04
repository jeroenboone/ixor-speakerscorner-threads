package be.ixor.speakerscorner.threads.completablefuture;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IxorThreadUtil {

    public static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printThreads() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        System.out.println("de threads: " + threads);

        List<Thread> virtualThreads = threads.stream()
                .filter(Thread::isVirtual)
                .collect(Collectors.toList());
        System.out.println("de virtualThreads: " + virtualThreads);

        List<Thread> activeThreads = threads.stream()
                .filter(Thread::isVirtual)
                .collect(Collectors.toList());
        System.out.println("de activeThreads: " + activeThreads);
    }

}
