package be.ixor.speakerscorner.threads.completablefuture.rest;

import be.ixor.speakerscorner.threads.completablefuture.IxorThreadUtil;
import be.ixor.speakerscorner.threads.completablefuture.virtual.Travel;
import be.ixor.speakerscorner.threads.completablefuture.virtual.VirtualService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/ixor")
public class IxorController {

    private final SlowService slowService = new SlowService();

    @GetMapping("/normal")
    public String normal() throws ExecutionException, InterruptedException {
        return slowService.slow1();
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
        IxorThreadUtil.printThreads();
        CompletableFuture<String> one = CompletableFuture.supplyAsync(slowService::slow1);
        CompletableFuture<String> two = CompletableFuture.supplyAsync(slowService::slow2);
        String result = Stream.of(one, two)
                .map(CompletableFuture::join)
                .collect(Collectors.joining(" "));
        IxorThreadUtil.printThreads();
        return result;
    }

    @GetMapping("/virtual1")
    public String virtual1() throws InterruptedException {
        IxorThreadUtil.printThreads();
        final Map<String, String> results = new LinkedHashMap<>();

        Thread threadOne = Thread.ofVirtual()
                .name("virtual-", 1)
                .start(() -> {
                    results.put("one", slowService.slow1());
                });
        Thread threadTwo = Thread.ofVirtual()
                .name("virtual-", 2)
                .start(() -> {
                    results.put("two", slowService.slow2());
                });

        threadOne.join();
        threadTwo.join();
        IxorThreadUtil.printThreads();
        return results.keySet().stream()
                .sorted()
                .collect(Collectors.joining(" "));
    }

    @GetMapping("/virtual2")
    public Travel virtual2() throws InterruptedException {
        Travel travel = VirtualService.readTravelPage();
        return travel;
    }

}
