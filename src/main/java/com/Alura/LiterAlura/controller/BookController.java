package com.Alura.LiterAlura.controller;

import com.Alura.LiterAlura.dto.BookRequestDto;
import com.Alura.LiterAlura.dto.BookResponseDto;
import com.Alura.LiterAlura.model.Book;
import com.Alura.LiterAlura.service.BookMapper;
import com.Alura.LiterAlura.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping("/search")
    public ResponseEntity<BookResponseDto> searchBookByTitle(
            @RequestParam @NotBlank(message = "El título no puede estar vacío") String title) {
        Book book = bookService.searchAndStoreByTitle(title);
        return ResponseEntity.ok(bookMapper.toBookResponseDto(book));
    }

    @PostMapping("/search")
    public ResponseEntity<BookResponseDto> searchBookByTitlePost(@Valid @RequestBody BookRequestDto request) {
        Book book = bookService.searchAndStoreByTitle(request.getTitle());
        return ResponseEntity.ok(bookMapper.toBookResponseDto(book));
    }

    @GetMapping
    public ResponseEntity<List<BookResponseDto>> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        return ResponseEntity.ok(bookMapper.toBookResponseDtoList(books));
    }

    @GetMapping("/language")
    public ResponseEntity<List<BookResponseDto>> getBooksByLanguage(
            @RequestParam @NotBlank(message = "El idioma no puede estar vacío") String lang) {
        List<Book> books = bookService.getBooksByLanguage(lang);
        return ResponseEntity.ok(bookMapper.toBookResponseDtoList(books));
    }

    @GetMapping("/top")
    public ResponseEntity<List<BookResponseDto>> getTopBooks() {
        List<Book> books = bookService.getTop5Books();
        return ResponseEntity.ok(bookMapper.toBookResponseDtoList(books));
    }
} 