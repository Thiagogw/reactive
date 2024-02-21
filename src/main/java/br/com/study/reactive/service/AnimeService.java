package br.com.study.reactive.service;

import br.com.study.reactive.domain.Anime;
import br.com.study.reactive.dto.AnimeResponse;
import br.com.study.reactive.dto.EpisodeResponse;
import br.com.study.reactive.repository.AnimeRepository;
import br.com.study.reactive.repository.EpisodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class AnimeService {

    private final AnimeRepository animeRepository;
    private final EpisodeService episodeService;

    public Flux<AnimeResponse> findAll() {
        return animeRepository.findAll()
                .flatMap(anime -> {
                    Flux<EpisodeResponse> episodesResponses = episodeService.findByName(anime.getName())
                            .flatMap(episode -> {
                                        EpisodeResponse episodeResponse = EpisodeResponse.builder()
                                                .id(episode.getId())
                                                .name(episode.getName())
                                                .title(episode.getTitle())
                                                .build();

                                        return Mono.just(episodeResponse);
                                    }
                            );

                    return episodesResponses.collectList()
                            .map(episodes -> AnimeResponse.builder()
                                    .id(anime.getId())
                                    .name(anime.getName())
                                    .episodes(episodes)
                                    .build());
                });
    }

    public Mono<AnimeResponse> findByName(String name) {
        return animeRepository.findByName(name)
                .flatMap(anime -> {
                    Flux<EpisodeResponse> episodesResponses = episodeService.findByName(anime.getName())
                            .flatMap(episode -> {
                                        EpisodeResponse episodeResponse = EpisodeResponse.builder()
                                                .id(episode.getId())
                                                .name(episode.getName())
                                                .title(episode.getTitle())
                                                .build();

                                        return Mono.just(episodeResponse);
                                    }
                            );

                    return episodesResponses.collectList()
                            .map(episodes -> AnimeResponse.builder()
                                    .id(anime.getId())
                                    .name(anime.getName())
                                    .episodes(episodes)
                                    .build());
                });
    }

    public Mono<AnimeResponse> create(Anime anime) {
        return animeRepository.save(anime).flatMap(savedAnime -> {
            Flux<EpisodeResponse> episodesResponses = episodeService.findByName(anime.getName())
                    .flatMap(episode -> {
                                EpisodeResponse episodeResponse = EpisodeResponse.builder()
                                        .id(episode.getId())
                                        .name(episode.getName())
                                        .title(episode.getTitle())
                                        .build();

                                return Mono.just(episodeResponse);
                            }
                    );

            return episodesResponses.collectList()
                    .map(episodes -> AnimeResponse.builder()
                            .id(anime.getId())
                            .name(anime.getName())
                            .episodes(episodes)
                            .build());
        });
    }

    public Mono<AnimeResponse> update(String name, Anime anime) {
        return animeRepository.findByName(name)
                .flatMap(currentAnime -> animeRepository.save(anime)
                        .flatMap(updatedAnime -> {

                            Flux<EpisodeResponse> episodesResponses = episodeService.findByName(currentAnime.getName())
                                    .flatMap(episode -> {
                                                EpisodeResponse episodeResponse = EpisodeResponse.builder()
                                                        .id(episode.getId())
                                                        .name(episode.getName())
                                                        .title(episode.getTitle())
                                                        .build();

                                                return Mono.just(episodeResponse);
                                            }
                                    );

                            return episodesResponses.collectList()
                                    .map(episodes -> AnimeResponse.builder()
                                            .id(anime.getId())
                                            .name(anime.getName())
                                            .episodes(episodes)
                                            .build());
                        })
                );
    }

    public Mono<AnimeResponse> delete(String name) {
        return animeRepository.findByName(name)
                .flatMap(anime -> {
                    animeRepository.delete(anime);

                    Flux<EpisodeResponse> episodesResponses = episodeService.findByName(anime.getName())
                            .flatMap(episode -> {
                                        EpisodeResponse episodeResponse = EpisodeResponse.builder()
                                                .id(episode.getId())
                                                .name(episode.getName())
                                                .title(episode.getTitle())
                                                .build();

                                        return Mono.just(episodeResponse);
                                    }
                            );

                    return episodesResponses.collectList()
                            .map(episodes -> AnimeResponse.builder()
                                    .id(anime.getId())
                                    .name(anime.getName())
                                    .episodes(episodes)
                                    .build());
                });
    }
}
