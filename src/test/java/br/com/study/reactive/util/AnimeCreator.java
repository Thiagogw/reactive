package br.com.study.reactive.util;

import br.com.study.reactive.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeToBeSaved() {
        return Anime.builder()
                .name("Fullmetal Alchemist")
                .build();
    }

    public static Anime createAnimeToBeFound() {
        return Anime.builder()
                .id("e8f945f7-7faa-4504-aeb8-52122b113d9c")
                .name("Fullmetal Alchemist")
                .build();
    }

    public static Anime createAnimeToBeUpdated() {
        return Anime.builder()
                .id("e8f945f7-7faa-4504-aeb8-52122b113d9c")
                .name("Fullmetal Alchemist: Brotherhood")
                .build();
    }
}
