package br.com.study.reactive;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Locale;

@Slf4j
class MonoTest {

    @Test
    void monoSubscriber() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<String> mono = Mono.just(name).log();

        mono.subscribe();

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumer() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<String> mono = Mono.just(name).log();

        mono.subscribe(s -> log.info("Value: {}", s));

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumerError() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<String> mono = Mono.just(name)
                .log()
                .map(s -> {
                    throw new RuntimeException("Error");
                });

        mono.subscribe(s -> log.info("Value: {}", s), e -> log.error(e.getMessage(), e));

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void monoSubscriberConsumerComplete() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<String> mono = Mono.just(name)
                .log()
                .map(s -> s.toUpperCase(Locale.ROOT));

        mono.subscribe(s -> log.info("Value: {}", s),
                Throwable::printStackTrace,
                () -> log.info("Finished."));

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectNext(name.toUpperCase(Locale.ROOT))
                .verifyComplete();
    }

    @Test
    void monoSubscriberConsumerSubscription() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<String> mono = Mono.just(name)
                .log()
                .map(s -> s.toUpperCase(Locale.ROOT));

        mono.subscribe(s -> log.info("Value: {}", s),
                Throwable::printStackTrace,
                () -> log.info("Finished."),
                Subscription::cancel);

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectNext(name.toUpperCase(Locale.ROOT))
                .verifyComplete();
    }

    @Test
    void monoDoOnMethods() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<Object> mono = Mono.just(name)
                .log()
                .map(s -> s.toUpperCase(Locale.ROOT))
                .doOnSubscribe(subscription -> log.info("Subscribed"))
                .doOnRequest(longNumber -> log.info("Request Received. Starting doing something..."))
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("Value is here. Executing doOnNext {}", s))
                .doOnSuccess(s -> log.info("doOnSuccess executed {}", s));

        mono.subscribe(s -> log.info("Value: {}", s),
                Throwable::printStackTrace,
                () -> log.info("Finished."),
                Subscription::cancel);

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void monoDoOnError() {
        // Actions
        Mono<Object> mono = Mono.error(new IllegalArgumentException("Error"))
                .doOnError(e -> log.error(e.getMessage(), e))
                .doOnNext(s -> log.info("Executing this doOnNext"))
                .log();

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void monoDoOnErrorResume() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<Object> mono = Mono.error(new IllegalArgumentException("Error"))
                .doOnError(e -> log.error(e.getMessage(), e))
                .onErrorResume(s -> {
                    log.info("Inside On Error Resume");
                    return Mono.just(name);
                })
                .log();

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();
    }

    @Test
    void monoDoOnErrorReturn() {
        // Assemble
        String name = "Thiago Alves de Moura";

        // Actions
        Mono<Object> mono = Mono.error(new IllegalArgumentException("Error"))
                .onErrorReturn("Empty")
                .onErrorResume(s -> {
                    log.info("Inside On Error Resume");
                    return Mono.just(name);
                })
                .log();

        log.info("---------------");

        // Assertions
        StepVerifier.create(mono)
                .expectNext("Empty")
                .verifyComplete();
    }
}
