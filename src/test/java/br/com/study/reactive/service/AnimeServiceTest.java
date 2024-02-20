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

    @BeforeEach
    void setup() {
        Mockito.when(animeRepository.findAll()).thenReturn(Flux.just(AnimeCreator.createAnimeToBeSaved()));
        Mockito.when(animeRepository.findByName(Mockito.any())).thenReturn(Mono.just(AnimeCreator.createAnimeToBeFound()));
        Mockito.when(animeRepository.save(Mockito.any())).thenReturn(Mono.just(AnimeCreator.createAnimeToBeSaved()));
        Mockito.when(animeRepository.delete(Mockito.any())).thenReturn(Mono.empty());
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
        // Actions
        Flux<Anime> flux = animeService.findAll();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeSaved())
                .verifyComplete();
    }

    @Test
    void findByNameReturnMonoOfAnimeWhenSuccessful() {
        // Actions
        Mono<Anime> mono = animeService.findByName("Fullmetal Alchemist");

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeFound())
                .verifyComplete();
    }

    @Test
    void findByNameReturnMonoOfEmptyWhenFailed() {
        // Assemble
        Mockito.when(animeRepository.findByName("One Piece")).thenReturn(Mono.empty());

        // Actions
        Mono<Anime> mono = animeService.findByName("One Piece");

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void createReturnMonoOfVoidWhenSuccessful() {
        // Assemble
        Anime anime = Anime.builder().name("One Piece").build();

        // Actions
        Mono<Anime> mono = animeService.create(anime);

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void updateReturnMonoOfAnimeWhenSuccessful() {
        // Assemble
        Anime anime = Anime.builder().name("Fullmetal Alchemist: Brotherhood").build();

        // Actions
        Mono<Anime> mono = animeService.update("Fullmetal Alchemist", anime);

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeSaved())
                .verifyComplete();
    }

    @Test
    void deleteReturnMonoOfVoidWhenSuccessful() {
        // Actions
        Mono<Anime> mono = animeService.delete("Fullmetal Alchemist");

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription()
                .expectNext(AnimeCreator.createAnimeToBeSaved())
                .verifyComplete();
    }
}
