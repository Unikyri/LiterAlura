package com.Alura.LiterAlura.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class SearchResultDoc {

    private String title;

    @JsonAlias("author_name")
    private List<String> authorName;

    @JsonAlias("first_publish_year")
    private Integer firstPublishYear;

    @JsonAlias("cover_i")
    private Integer coverId;

    @JsonAlias("author_key")
    private List<String> authorKey;

    private List<String> language;
} 