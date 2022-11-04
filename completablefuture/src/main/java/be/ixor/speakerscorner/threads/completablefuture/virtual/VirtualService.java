package be.ixor.speakerscorner.threads.completablefuture.virtual;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.Future;

public class VirtualService {
    public static final TemporalUnit CHRONO_UNIT = ChronoUnit.MILLIS;

    private static class TravelPageScope extends StructuredTaskScope<TravelComponent> {

        private volatile Quotation quotation;
        private volatile Weather weather = Weather.UNKNOWN;
        private volatile Quotation.QuotationException exception;

        @Override
        protected void handleComplete(Future<TravelComponent> future) {
            switch (future.state()) {
                case RUNNING -> throw new IllegalStateException("Task is still running");
                case SUCCESS -> {
                    TravelComponent travelComponent = future.resultNow();
                    switch (travelComponent) {
                        case Quotation(String agency, int price) quotation -> this.quotation = quotation;
                        case Weather(String agency, String weatherText) weather -> this.weather = weather;
                    }
                }
                case FAILED -> {
                    switch (future.exceptionNow()) {
                        case Quotation.QuotationException e -> this.exception = e;
                        default -> throw new RuntimeException(future.exceptionNow());
                    }
                }
                case CANCELLED -> {
                }
            }
        }

        public Travel travelPage() {
            if (this.quotation != null) {
                return new Travel(this.quotation, this.weather);
            } else {
                throw exception;
            }
        }
    }

    public static Travel readTravelPage() throws InterruptedException {
        try (var scope = new TravelPageScope()) {
            scope.fork(Weather::readWeather);
            scope.fork(Quotation::readQuotation);

            scope.join();

            return scope.travelPage();
        }
    }

}
