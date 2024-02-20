package br.com.study.reactive.service;

import br.com.study.reactive.domain.Anime;
import br.com.study.reactive.dto.AnimeResponse;
import br.com.study.reactive.repository.AnimeRepository;
import br.com.study.reactive.repository.EpisodeRepository;
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
class AnimeServiceTest {

    @InjectMocks
    private AnimeService animeService;

    @Mock
    private AnimeRepository animeRepository;

    @Mock
    private EpisodeRepository episodeRepository;

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

        Mockito.when(episodeRepository.findByName(Mockito.any())).thenReturn(Flux.empty());
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
        Flux<AnimeResponse> flux = animeService.findAll();

        // Assertions
        StepVerifier.create(flux)
                .expectSubscription();
    }

    @Test
    void findByNameReturnMonoOfAnimeWhenSuccessful() {
        // Actions
        Mono<AnimeResponse> mono = animeService.findByName("Fullmetal Alchemist");

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription();
    }

    @Test
    void createReturnMonoOfVoidWhenSuccessful() {
        // Assemble
        Anime anime = Anime.builder().name("One Piece").build();

        // Actions
        Mono<AnimeResponse> mono = animeService.create(anime);

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription();
    }

    @Test
    void updateReturnMonoOfAnimeWhenSuccessful() {
        // Assemble
        Anime anime = Anime.builder().name("Fullmetal Alchemist: Brotherhood").build();

        // Actions
        Mono<AnimeResponse> mono = animeService.update("Fullmetal Alchemist", anime);

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription();
    }

    @Test
    void deleteReturnMonoOfVoidWhenSuccessful() {
        // Actions
        Mono<AnimeResponse> mono = animeService.delete("Fullmetal Alchemist");

        // Assertions
        StepVerifier.create(mono)
                .expectSubscription();
    }
}
