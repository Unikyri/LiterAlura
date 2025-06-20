package com.Alura.LiterAlura.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class SearchResult {

    @JsonAlias("numFound")
    private Integer numFound;

    private Integer start;

    private List<SearchResultDoc> docs;
} 