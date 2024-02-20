package br.com.study.reactive.repository;

import br.com.study.reactive.domain.Episode;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EpisodeRepository extends ReactiveMongoRepository<Episode, String> {
    Flux<Episode> findByName(String name);

    Mono<Episode> findByTitle(String name);
}
