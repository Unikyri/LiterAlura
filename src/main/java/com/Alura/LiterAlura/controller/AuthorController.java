package com.Alura.LiterAlura.controller;

import com.Alura.LiterAlura.dto.AuthorResponseDto;
import com.Alura.LiterAlura.service.AuthorService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Validated
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorResponseDto>> getAllAuthors() {
        List<AuthorResponseDto> authors = authorService.getAllAuthors();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/alive")
    public ResponseEntity<List<AuthorResponseDto>> getAuthorsAliveInYear(
            @RequestParam @Min(value = 1, message = "El año debe ser mayor a 0") int year) {
        List<AuthorResponseDto> authors = authorService.getAuthorsAliveInYear(year);
        return ResponseEntity.ok(authors);
    }
} 