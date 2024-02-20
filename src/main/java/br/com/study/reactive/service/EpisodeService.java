package br.com.study.reactive.service;

import br.com.study.reactive.domain.Episode;
import br.com.study.reactive.dto.EpisodeResponse;
import br.com.study.reactive.repository.EpisodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class EpisodeService {

    private final EpisodeRepository episodeRepository;

    public Flux<EpisodeResponse> findAll() {
        return episodeRepository.findAll()
                .flatMap(episode -> Mono.just(EpisodeResponse.builder()
                        .title(episode.getTitle())
                        .name(episode.getName())
                        .build())
                );
    }

    public Flux<EpisodeResponse> findByName(String name) {
        return episodeRepository.findByName(name)
                .flatMap(episode -> Mono.just(EpisodeResponse.builder()
                        .title(episode.getTitle())
                        .name(episode.getName())
                        .build())
                );
    }

    public Mono<EpisodeResponse> findByTitle(String title) {
        return episodeRepository.findByTitle(title)
                .flatMap(episode -> Mono.just(EpisodeResponse.builder()
                        .title(episode.getTitle())
                        .name(episode.getName())
                        .build())
                );
    }

    public Mono<EpisodeResponse> create(Episode episode) {
        return episodeRepository.save(episode)
                .flatMap(savedEpisode -> Mono.just(EpisodeResponse.builder()
                        .title(savedEpisode.getTitle())
                        .name(savedEpisode.getName())
                        .build()));
    }

    public Mono<EpisodeResponse> update(String title, Episode episode) {

        Mono<Episode> existingEpisode = episodeRepository.findByTitle(title);

        return existingEpisode
                .flatMap(currentEpisode -> {
                    episode.withId(currentEpisode.getId());

                    return episodeRepository.save(episode)
                            .flatMap(savedEpisode -> Mono.just(EpisodeResponse.builder()
                                    .title(savedEpisode.getTitle())
                                    .name(savedEpisode.getName())
                                    .build()));
                });
    }

    public Mono<EpisodeResponse> delete(String title) {
        return episodeRepository.findByTitle(title)
                .flatMap(episode -> {
                    episodeRepository.delete(episode);

                    return Mono.just(EpisodeResponse.builder()
                            .title(episode.getTitle())
                            .name(episode.getName())
                            .build());
                });
    }
}
