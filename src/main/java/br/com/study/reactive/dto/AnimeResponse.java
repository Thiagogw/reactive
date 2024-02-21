package br.com.study.reactive.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class AnimeResponse {

    private String id;

    private String name;

    private List<EpisodeResponse> episodes;
}
