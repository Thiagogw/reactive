package br.com.study.reactive.service;

import br.com.study.reactive.domain.Anime;
import br.com.study.reactive.repository.AnimeRepository;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepository;

    @BeforeAll
    static void blockHoundSetup() {
        BlockHound.install();
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

    @BeforeEach
    void setup() {
        Mockito.when(animeRepository.findAll()).thenReturn(Flux.just(AnimeCreator.createAnimeToBeSaved()));
        Mockito.when(animeRepository.findByName(Mockito.any())).thenReturn(Mono.just(AnimeCreator.createAnimeToBeUpdated()));
        Mockito.when(animeRepository.save(Mockito.any())).thenReturn(Mono.just(AnimeCreator.createAnimeToBeSaved()));
    }

    @Test
    void findAllReturnFluxOfAnimeWhenSuccessful() {
        StepVerifier.create(animeService.findAll())
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeSaved())
                .verifyComplete();
    }

    @Test
    void findByNameReturnMonoOfAnimeWhenSuccessful() {
        StepVerifier.create(animeService.findByName("Fullmetal Alchemist"))
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeUpdated())
                .verifyComplete();
    }

    @Test
    void findByNameReturnMonoOfEmptyWhenFailed() {
        Mockito.when(animeRepository.findByName("One Piece")).thenReturn(Mono.empty());

        StepVerifier.create(animeService.findByName("One Piece"))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void createReturnMonoOfVoidWhenSuccessful() {
        StepVerifier.create(animeService.create(Anime.builder().name("One Piece").build()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void updateReturnMonoOfAnimeWhenSuccessful() {
        StepVerifier.create(animeService.update("Fullmetal Alchemist", Anime.builder().name("Fullmetal Alchemist: Botherhood").build()))
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeSaved())
                .verifyComplete();
    }
}
