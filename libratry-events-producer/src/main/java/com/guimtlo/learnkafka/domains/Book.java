package com.guimtlo.learnkafka.domains;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Book implements Serializable {

    public static Long serialVersionUID = 1L;

    @JsonProperty("book_id")
    private Integer bookId;

    @JsonProperty("book_name")
    private String bookName;

    @JsonProperty("book_author")
    private String bookAuthor;
}
