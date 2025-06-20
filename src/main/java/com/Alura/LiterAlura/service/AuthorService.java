package com.Alura.LiterAlura.service;

import com.Alura.LiterAlura.dto.AuthorResponseDto;
import com.Alura.LiterAlura.model.Author;
import com.Alura.LiterAlura.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "authors", key = "'all'")
    public List<AuthorResponseDto> getAllAuthors() {
        log.debug("Obteniendo todos los autores");
        List<Author> authors = authorRepository.findAll();
        return bookMapper.toAuthorResponseDtoList(authors);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "authorsAlive", key = "#year")
    public List<AuthorResponseDto> getAuthorsAliveInYear(int year) {
        log.debug("Obteniendo autores vivos en el año: {}", year);
        // Usar query optimizada en lugar de filtro en memoria
        List<Author> authors = authorRepository.findAuthorsAliveInYear(year);
        return bookMapper.toAuthorResponseDtoList(authors);
    }
} 