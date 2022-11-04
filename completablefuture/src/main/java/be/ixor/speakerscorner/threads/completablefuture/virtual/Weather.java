package be.ixor.speakerscorner.threads.completablefuture.virtual;

import be.ixor.speakerscorner.threads.completablefuture.IxorThreadUtil;
import jdk.incubator.concurrent.StructuredTaskScope;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

public record Weather(String agency, String weather) implements TravelComponent {

    public static final Weather UNKNOWN = new Weather("", "Mostly Sunny");

    private static class WeatherScope implements AutoCloseable {

        private StructuredTaskScope.ShutdownOnSuccess<Weather> scope =
              new StructuredTaskScope.ShutdownOnSuccess<>();
        private boolean timeout = false;

        public WeatherScope joinUntil(Instant deadline) throws InterruptedException {
            try {
                scope.joinUntil(deadline);
            } catch (TimeoutException e) {
                scope.shutdown();
                this.timeout = true;
            }
            return this;
        }

        public Future<Weather> fork(Callable<? extends Weather> task) {
            return scope.fork(task);
        }

        @Override
        public void close() {
            scope.close();
        }

        public Weather weather() throws ExecutionException {
            if (!timeout) {
                return this.scope.result();
            } else {
                return Weather.UNKNOWN;
            }
        }
    }

    public static Weather readWeather() throws InterruptedException, ExecutionException {

        IxorThreadUtil.printThreads();

        var random = ThreadLocalRandom.current();

        try (var scope = new WeatherScope()) {

            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(30, 110), VirtualService.CHRONO_UNIT));
                return new Weather("WA", "Sunny");
            });
            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(20, 90), VirtualService.CHRONO_UNIT));
                return new Weather("WB", "Sunny");
            });
            scope.fork(() -> {
                Thread.sleep(Duration.of(random.nextInt(10, 120), VirtualService.CHRONO_UNIT));
                return new Weather("WC", "Sunny");
            });

            scope.joinUntil(Instant.now().plus(100, VirtualService.CHRONO_UNIT));

            return scope.weather();
        }
    }
}
