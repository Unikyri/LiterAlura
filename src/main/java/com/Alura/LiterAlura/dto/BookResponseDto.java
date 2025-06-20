package com.Alura.LiterAlura.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookResponseDto {
    
    private Long id;
    private String title;
    private Integer firstPublishYear;
    private Integer coverId;
    private String language;
    private Integer searchCount;
    private AuthorResponseDto author;
} 