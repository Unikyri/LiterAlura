package com.Alura.LiterAlura.service;

import com.Alura.LiterAlura.dto.AuthorResponseDto;
import com.Alura.LiterAlura.dto.BookResponseDto;
import com.Alura.LiterAlura.model.Author;
import com.Alura.LiterAlura.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookMapper {

    public BookResponseDto toBookResponseDto(Book book) {
        if (book == null) {
            return null;
        }

        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .firstPublishYear(book.getFirstPublishYear())
                .coverId(book.getCoverId())
                .language(book.getLanguage())
                .searchCount(book.getSearchCount())
                .author(toAuthorResponseDto(book.getAuthor()))
                .build();
    }

    public AuthorResponseDto toAuthorResponseDto(Author author) {
        if (author == null) {
            return null;
        }

        return AuthorResponseDto.builder()
                .id(author.getId())
                .name(author.getName())
                .birthDate(author.getBirthDate())
                .deathDate(author.getDeathDate())
                .bio(author.getBio())
                .build();
    }

    public List<BookResponseDto> toBookResponseDtoList(List<Book> books) {
        return books.stream()
                .map(this::toBookResponseDto)
                .collect(Collectors.toList());
    }

    public List<AuthorResponseDto> toAuthorResponseDtoList(List<Author> authors) {
        return authors.stream()
                .map(this::toAuthorResponseDto)
                .collect(Collectors.toList());
    }
} 