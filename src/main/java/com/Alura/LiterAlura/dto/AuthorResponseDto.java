package com.Alura.LiterAlura.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AuthorResponseDto {
    
    private Long id;
    private String name;
    private LocalDate birthDate;
    private LocalDate deathDate;
    private String bio;
} 