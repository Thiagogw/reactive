package br.com.study.reactive.controller;

import br.com.study.reactive.domain.Episode;
import br.com.study.reactive.dto.EpisodeResponse;
import br.com.study.reactive.service.EpisodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("episodes")
public class EpisodeController {

    private final EpisodeService episodeService;

    @GetMapping
    public Flux<EpisodeResponse> findAll() {
        return episodeService.findAll();
    }

    @GetMapping("/{title}")
    public Mono<EpisodeResponse> findByTitle(@PathVariable String title) {
        return episodeService.findByTitle(title);
    }

    @PostMapping
    public Mono<EpisodeResponse> create(@RequestBody Episode episode) {
        return episodeService.create(episode);
    }

    @PutMapping("/{title}")
    public Mono<EpisodeResponse> update(@PathVariable String title, @RequestBody Episode episode) {
        return episodeService.update(title, episode);
    }

    @DeleteMapping("/{title}")
    public Mono<EpisodeResponse> delete(@PathVariable String title) {
        return episodeService.delete(title);
    }
}
