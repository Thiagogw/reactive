package br.com.study.reactive.controller;

import br.com.study.reactive.domain.Anime;
import br.com.study.reactive.service.AnimeService;
import br.com.study.reactive.util.AnimeCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

    @InjectMocks
    private AnimeController animeController;

    @Mock
    private AnimeService animeService;
    @BeforeAll
    static void blockHoundSetup() {
        BlockHound.install();
    }

    @BeforeEach
    void setup() {
        Mockito.when(animeService.findAll()).thenReturn(Flux.just(AnimeCreator.createAnimeToBeSaved()));
        Mockito.when(animeService.findByName(Mockito.any())).thenReturn(Mono.just(AnimeCreator.createAnimeToBeUpdated()));
        Mockito.when(animeService.create(Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(animeService.update(Mockito.any(), Mockito.any())).thenReturn(Mono.just(AnimeCreator.createAnimeToBeSaved()));
        Mockito.when(animeService.delete(Mockito.any())).thenReturn(Mono.empty());
    }

    @Test
    void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<Object>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);
            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");

        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }


    @Test
    void findAllReturnFluxOfAnimeWhenSuccessful() {
        StepVerifier.create(animeController.findAll())
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeSaved())
                .verifyComplete();
    }

    @Test
    void findByNameReturnMonoOfAnimeWhenSuccessful() {
        StepVerifier.create(animeController.findByName("Fullmetal Alchemist"))
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeUpdated())
                .verifyComplete();
    }

    @Test
    void createReturnMonoOfVoidWhenSuccessful() {
        StepVerifier.create(animeController.create(Anime.builder().name("One Piece").build()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void updateReturnMonoOfAnimeWhenSuccessful() {
        StepVerifier.create(animeController.update("Fullmetal Alchemist", Anime.builder().name("Fullmetal Alchemist: Botherhood").build()))
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeSaved())
                .verifyComplete();
    }

    @Test
    void deleteReturnMonoOfVoidWhenSuccessful() {
        StepVerifier.create(animeController.delete("Fullmetal Alchemist"))
                .expectSubscription()
                .verifyComplete();
    }
}
