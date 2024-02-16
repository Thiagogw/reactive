package br.com.study.reactive;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
class OperatorsTest {

    @Test
    void subscribeOnSimple() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 4)
                .map(i -> {
                    log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void publishOnSimple() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 4)
                .map(i -> {
                    log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void multipleSubscribeOnSimple() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 4)
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void multiplePublishOnSimple() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 4)
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void publishAndSubscribeOnSimple() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 4)
                .publishOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void SubscribeAndPublishOnSimple() {
        // Actions
        Flux<Integer> flux = Flux.range(1, 4)
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("Map 1 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map 2 - Number {} on Thread {}", i, Thread.currentThread().getName());
                    return i;
                });

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    void subscribeOnIO() {
        //Actions
        Mono<List<String>> mono = Mono.fromCallable(() -> Files.readAllLines(Path.of("text-file")))
                .log()
                .subscribeOn(Schedulers.boundedElastic());

        mono.subscribe(s -> log.info("{}", s));

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription()
                .thenConsumeWhile(list -> {
                    Assertions.assertFalse(list.isEmpty());
                    log.info("Size {}", list.size());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void switchIfEmptyOperator() {
        // Actions
        Flux<Object> flux = emptyFlux()
                .switchIfEmpty(Flux.just("not empty anymore"))
                .log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("not empty anymore")
                .expectComplete()
                .verify();
    }

    @Test
    void deferOperator() throws Exception {
        // Actions
        Mono<Long> mono = Mono.defer(() -> Mono.just(System.currentTimeMillis()));

        mono.subscribe(l -> log.info("time {}", l));
        Thread.sleep(100);
        mono.subscribe(l -> log.info("time {}", l));
        Thread.sleep(100);
        mono.subscribe(l -> log.info("time {}", l));
        Thread.sleep(100);
        mono.subscribe(l -> log.info("time {}", l));
        Thread.sleep(100);
        mono.subscribe(l -> log.info("time {}", l));

        // Assertions
        AtomicLong atomicLong = new AtomicLong();
        mono.subscribe(atomicLong::set);
        Assertions.assertTrue(atomicLong.get() > 0);
    }

    private Flux<Object> emptyFlux() {
        return Flux.empty();
    }

    @Test
    void concatOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = Flux.concat(flux1, flux2).log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("a", "b", "c", "d")
                .expectComplete()
                .verify();
    }

    @Test
    void concatWithOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = flux1.concatWith(flux2).log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("a", "b", "c", "d")
                .expectComplete()
                .verify();
    }

    @Test
    void combineLastestOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b");
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = Flux.combineLatest(flux1, flux2, (s1, s2) -> s1.toUpperCase(Locale.ROOT) + s2.toUpperCase(Locale.ROOT)).log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("BC", "BD")
                .expectComplete()
                .verify();
    }

    @Test
    void mergeOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = Flux.merge(flux1, flux2)
                .delayElements(Duration.ofMillis(200))
                .log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("c", "d", "a", "b")
                .expectComplete()
                .verify();
    }

    @Test
    void mergeWithOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = flux1.mergeWith(flux2)
                .delayElements(Duration.ofMillis(200))
                .log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("c", "d", "a", "b")
                .expectComplete()
                .verify();
    }

    @Test
    void mergeSequencialOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b").delayElements(Duration.ofMillis(200));
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = Flux.mergeSequential(flux1, flux2, flux1)
                .delayElements(Duration.ofMillis(200))
                .log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("a", "b", "c", "d", "a", "b")
                .expectComplete()
                .verify();
    }

    @Test
    void concatDelayErrorOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b")
                .map(s -> {
                    if (s.equals("b")) {
                        throw new IllegalArgumentException();
                    }
                    return s;
                });
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = Flux.concatDelayError(flux1, flux2).log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("a", "c", "d")
                .expectError()
                .verify();
    }

    @Test
    void mergeDelayErrorOperator() {
        // Assemble
        Flux<String> flux1 = Flux.just("a", "b")
                .map(s -> {
                    if (s.equals("b")) {
                        throw new IllegalArgumentException();
                    }
                    return s;
                });
        Flux<String> flux2 = Flux.just("c", "d");

        // Actions
        Flux<String> flux = Flux.mergeDelayError(1, flux1, flux2, flux1)
                .delayElements(Duration.ofMillis(200))
                .log();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext("a", "c", "d")
                .expectError()
                .verify();
    }

    @Test
    void flatMapOperator() {
        // Actions
        Flux<String> flux = Flux.just("a", "b");

        Flux<String> flatFlux = flux.map(String::toUpperCase)
                .flatMap(this::findByName)
                .log();

        // Assertions
        StepVerifier.create(flatFlux)
                .expectSubscription()
                .expectNext("Name A1", "Name A2", "Name B1", "Name B2")
                .verifyComplete();
    }

    @Test
    void flatMapSequencialOperator() {
        // Actions
        Flux<String> flux = Flux.just("a", "b");

        Flux<String> flatFlux = flux.map(String::toUpperCase)
                .flatMapSequential(this::findByName)
                .log();

        // Assertions
        StepVerifier.create(flatFlux)
                .expectSubscription()
                .expectNext("Name A1", "Name A2", "Name B1", "Name B2")
                .verifyComplete();
    }

    public Flux<String> findByName(String name) {
        return name.equals("A") ? Flux.just("Name A1", "Name A2") : Flux.just("Name B1", "Name B2");
    }

    @Test
    void zipOperator() {
        // Assemble
        Flux<String> titleFlux = Flux.just("Grand Blue", "Baki");
        Flux<String> studioFlux = Flux.just("Zero-G", "TMS Entertainment");
        Flux<Integer> episodesFlux = Flux.just(12, 24);

        // Actions
        Flux<Anime> zipped = Flux.zip(titleFlux, studioFlux, episodesFlux)
                .flatMap(tuple -> Flux.just(new Anime(tuple.getT1(), tuple.getT2(), tuple.getT3())));

        zipped.subscribe(anime -> log.info(anime.toString()));

        // Assertions
        StepVerifier.create(zipped)
                .expectSubscription()
                .expectNext(
                        new Anime("Grand Blue", "Zero-G", 12),
                        new Anime("Baki", "TMS Entertainment", 24))
                .verifyComplete();
    }

    @Test
    void zipWithOperator() {
        // Assemble
        Flux<String> titleFlux = Flux.just("Grand Blue", "Baki");
        Flux<Integer> episodesFlux = Flux.just(12, 24);

        // Actions
        Flux<Anime> zipped = titleFlux.zipWith(episodesFlux)
                .flatMap(tuple -> Flux.just(new Anime(tuple.getT1(), null, tuple.getT2())));

        zipped.subscribe(anime -> log.info(anime.toString()));

        // Assertions
        StepVerifier.create(zipped)
                .expectSubscription()
                .expectNext(
                        new Anime("Grand Blue", null, 12),
                        new Anime("Baki", null, 24))
                .verifyComplete();
    }

    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    class Anime {
        private String title;
        private String studio;
        private int episodes;
    }
}
