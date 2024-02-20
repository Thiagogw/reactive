package br.com.study.reactive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import reactor.core.publisher.Flux;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class AnimeResponse {

    private String name;

    private Flux<EpisodeResponse> episodes;
}
