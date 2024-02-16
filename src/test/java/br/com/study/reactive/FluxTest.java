package br.com.study.reactive;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
class FluxTest {

    @Test
    void fluxSubscriber() {
        // Assemble
        String[] names = {"James", "Ana", "Alex"};

        // Actions
        Flux<String> flux = Flux.just(names).log();

        log.info("---------------");

        // Assertions
        StepVerifier.create(flux)
                .expectNext(names)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberNumbers() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 10).log();

        flux.subscribe(i -> log.info("Number {}", i));

        log.info("---------------");

        // Assertions
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberFromList() {
        // Assemble
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        // Actions
        Flux<Integer> flux = Flux.fromIterable(numbers).log();

        flux.subscribe(i -> log.info("Number {}", i));

        log.info("---------------");

        // Assertions
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberNumbersError() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 5)
                .log()
                .map(i -> {
                    if (i == 4) {
                        throw new IndexOutOfBoundsException("Index Error");
                    }
                    return i;
                });

        flux.subscribe(i -> log.info("Number {}", i),
                Throwable::printStackTrace,
                () -> log.info("Done."),
                subscription -> subscription.request(3));

        log.info("---------------");

        // Assertions
        StepVerifier.create(flux)
                .expectNext(1, 2, 3)
                .expectError(IndexOutOfBoundsException.class)
                .verify();
    }

    @Test
    void fluxSubscriberNumbersUglyBackpressure() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        flux.subscribe(new Subscriber<>() {
            private int count = 0;
            private Subscription subscription;
            private final int requestCount = 2;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        log.info("---------------");

        // Assertions
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberNumbersNotSoUglyBackpressure() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 10)
                .log();

        flux.subscribe(new BaseSubscriber<>() {
            private int count = 0;
            private final int requestCount = 2;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(requestCount);
            }

            @Override
            protected void hookOnNext(Integer value) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    request(requestCount);
                }
            }
        });

        log.info("---------------");

        // Assertions
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberPrettyBackpressure() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 10)
                .log()
                .limitRate(3);

        flux.subscribe(i -> log.info("Number {}", i));

        log.info("---------------");

        // Assertions
        StepVerifier.create(flux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

    @Test
    void fluxSubscriberIntervalOne() throws Exception {
        // Actions
        Flux<Long> flux = Flux.interval(Duration.ofMillis(100))
                .take(10)
                .log();

        flux.subscribe(i -> log.info("Number {}", i));

        Thread.sleep(3000);

        log.info("---------------");
    }

    @Test
    void fluxSubscriberIntervalTwo() {
        // Assertions
        StepVerifier.withVirtualTime(this::createInterval)
                .expectSubscription()
                .expectNoEvent(Duration.ofHours(20))
                .thenAwait(Duration.ofDays(1))
                .expectNext(0L)
                .thenAwait(Duration.ofDays(1))
                .expectNext(1L)
                .thenCancel()
                .verify();
    }

    private Flux<Long> createInterval() {
        return Flux.interval(Duration.ofDays(1))
                .take(10)
                .log();
    }

    @Test
    void connectableFlux() {
        // Actions
        ConnectableFlux<Integer> connectableFlux = Flux.range(1, 10)
                .log()
                .delayElements(Duration.ofMillis(100))
                .publish();

        // Assertions
        StepVerifier.create(connectableFlux)
                .then(connectableFlux::connect)
                .thenConsumeWhile(i -> i <= 5)
                .expectNext(6, 7, 8, 9, 10)
                .expectComplete()
                .verify();
    }

    @Test
    void fluxAutoConnect() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 10)
                .log()
                .delayElements(Duration.ofMillis(100))
                .publish()
                .autoConnect(2);

        // Assertions
        StepVerifier.create(flux)
                .then(flux::subscribe)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .expectComplete()
                .verify();
    }
}
