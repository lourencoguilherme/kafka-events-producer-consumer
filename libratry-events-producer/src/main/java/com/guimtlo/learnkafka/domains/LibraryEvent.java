package com.guimtlo.learnkafka.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LibraryEvent implements Serializable {

    public static Long serialVersionUID = 1L;

    @JsonProperty("library_event")
    private Integer libraryEvent;

    @JsonProperty("book")
    private Book book;
}
