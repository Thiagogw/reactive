package br.com.study.reactive.repository;

import br.com.study.reactive.domain.Anime;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AnimeRepository extends ReactiveMongoRepository<Anime, String> {
    Mono<Anime> findByName(String name);
}
