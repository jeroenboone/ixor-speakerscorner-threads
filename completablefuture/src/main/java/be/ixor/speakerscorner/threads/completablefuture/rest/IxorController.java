package be.ixor.speakerscorner.threads.completablefuture.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/ixor")
public class IxorController {

    private final SlowService slowService = new SlowService();

    @GetMapping("/one")
    public String one() throws ExecutionException, InterruptedException {
        return CompletableFuture
                .supplyAsync(slowService::slow1)
                .get();
    }

    @GetMapping("/completable")
    public CompletableFuture<String> completable() throws ExecutionException, InterruptedException {
        return CompletableFuture
                .supplyAsync(slowService::slow1);
    }

    @GetMapping("/serial")
    public String serial() throws ExecutionException, InterruptedException {
        return slowService.slow1() + " " + slowService.slow2();
    }

    @GetMapping("/parallel")
    public String parallel() throws ExecutionException, InterruptedException {
        printThreads();
        CompletableFuture<String> one = CompletableFuture.supplyAsync(slowService::slow1);
        CompletableFuture<String> two = CompletableFuture.supplyAsync(slowService::slow2);
        String result = Stream.of(one, two)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));
        printThreads();
        return result;
    }

    private void printThreads() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        System.out.println("de threads: " + threads);
    }
}
