package br.com.study.reactive.service;

import br.com.study.reactive.domain.Anime;
import br.com.study.reactive.repository.AnimeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class AnimeService {

    private final AnimeRepository animeRepository;

    public Flux<Anime> findAll() {
        return animeRepository.findAll();
    }

    public Mono<Anime> findByName(String name) {
        return animeRepository.findByName(name)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found.")));
    }

    public Mono<Anime> create(Anime anime) {
        return animeRepository.save(anime)
                .then(Mono.empty());
    }

    public Mono<Anime> update(String name, Anime anime) {
        return findByName(name)
                .map(animeFound -> anime.withId(animeFound.getId()))
                .flatMap(animeRepository::save);
    }

    public Mono<Anime> delete(String name) {
        return findByName(name)
                .flatMap(anime -> {
                    animeRepository.delete(anime);

                    return Mono.just(Anime.builder()
                            .name(anime.getName())
                            .build());
                });
    }
}
