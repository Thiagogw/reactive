package br.com.study.reactive.controller;

import br.com.study.reactive.domain.Anime;
import br.com.study.reactive.service.AnimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping
    public Flux<Anime> findAll() {
        return animeService.findAll();
    }

    @GetMapping("/{name}")
    public Mono<Anime> findByName(@PathVariable String name) {
        return animeService.findByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Anime> create(@RequestBody Anime anime) {
        return animeService.create(anime);
    }

    @PutMapping("/{name}")
    public Mono<Anime> update(@PathVariable String name, @RequestBody Anime anime) {
        return animeService.update(name, anime);
    }

    @DeleteMapping("/{name}")
    public Mono<Anime> delete(@PathVariable String name) {
        return animeService.delete(name);
    }
}
