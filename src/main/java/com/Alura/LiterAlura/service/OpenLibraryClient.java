package com.Alura.LiterAlura.service;

import com.Alura.LiterAlura.dto.openlibrary.SearchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OpenLibraryClient {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://openlibrary.org";

    public SearchResult searchByTitle(String title, int limit) {
        String uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path("/search.json")
                .queryParam("title", title)
                .queryParam("limit", limit)
                .toUriString();
        return restTemplate.getForObject(uri, SearchResult.class);
    }
} 