package br.com.study.reactive.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
@Document("anime")
public class Anime {

    @Id
    private String id;

    private String name;

}
