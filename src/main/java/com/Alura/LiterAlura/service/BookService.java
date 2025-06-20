package com.Alura.LiterAlura.service;

import com.Alura.LiterAlura.model.Author;
import com.Alura.LiterAlura.model.Book;
import com.Alura.LiterAlura.dto.openlibrary.SearchResult;
import com.Alura.LiterAlura.dto.openlibrary.SearchResultDoc;
import com.Alura.LiterAlura.repository.AuthorRepository;
import com.Alura.LiterAlura.repository.BookRepository;
import com.Alura.LiterAlura.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final OpenLibraryClient openLibraryClient;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final IdGeneratorService idGeneratorService;

    @Transactional
    public Book searchAndStoreByTitle(String title) {
        log.info("Buscando libro por título: {}", title);
        
        // Buscar en OpenLibrary
        SearchResult searchResult = openLibraryClient.searchByTitle(title, 1);
        if (searchResult == null || searchResult.getDocs() == null || searchResult.getDocs().isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron resultados en OpenLibrary para el título: " + title);
        }

        SearchResultDoc doc = searchResult.getDocs().get(0);
        
        // Procesar autor de manera optimizada
        Author author = processAuthor(doc);
        
        // Procesar libro de manera optimizada
        return processBook(doc, author);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "'all'")
    public List<Book> getAllBooks() {
        log.debug("Obteniendo todos los libros");
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "booksByLanguage", key = "#language")
    public List<Book> getBooksByLanguage(String language) {
        log.debug("Obteniendo libros por idioma: {}", language);
        return bookRepository.findByLanguageIgnoreCase(language);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "topBooks", key = "'top5'")
    public List<Book> getTop5Books() {
        log.debug("Obteniendo top 5 libros");
        List<Book> topBooks = bookRepository.findTopBooksBySearchCount();
        return topBooks.size() > 5 ? topBooks.subList(0, 5) : topBooks;
    }

    // Métodos privados optimizados
    
    private Author processAuthor(SearchResultDoc doc) {
        String authorOlid = extractAuthorOlid(doc);
        String authorName = extractAuthorName(doc);
        
        if (authorOlid != null) {
            // Buscar por OLID de OpenLibrary
            return authorRepository.findByOpenLibraryId(authorOlid)
                    .orElseGet(() -> createNewAuthor(authorOlid, authorName));
        } else if (authorName != null) {
            // Buscar por nombre para evitar duplicados
            return authorRepository.findByNameIgnoreCase(authorName)
                    .orElseGet(() -> createNewAuthor(null, authorName));
        } else {
            // Autor desconocido
            return createNewAuthor(null, "Autor Desconocido");
        }
    }

    private Book processBook(SearchResultDoc doc, Author author) {
        String bookId = idGeneratorService.generateBookId(doc.getTitle(), author.getOpenLibraryId());
        
        // Verificar si el libro ya existe de manera optimizada
        if (bookRepository.existsByOpenLibraryId(bookId)) {
            // Si existe, incrementar contador directamente en BD
            Book existingBook = bookRepository.findByOpenLibraryId(bookId).orElseThrow();
            bookRepository.incrementSearchCount(existingBook.getId());
            log.info("Incrementado contador para libro existente: {}", existingBook.getTitle());
            return existingBook;
        } else {
            // Crear nuevo libro
            Book newBook = createNewBook(doc, author, bookId);
            log.info("Creado nuevo libro: {}", newBook.getTitle());
            return newBook;
        }
    }

    private Author createNewAuthor(String openLibraryId, String name) {
        String finalOlid = openLibraryId != null ? openLibraryId : idGeneratorService.generateAuthorId(name);
        
        Author author = Author.builder()
                .openLibraryId(finalOlid)
                .name(name != null ? name : "Autor Desconocido")
                .build();
        
        return authorRepository.save(author);
    }

    private Book createNewBook(SearchResultDoc doc, Author author, String bookId) {
        Book book = Book.builder()
                .openLibraryId(bookId)
                .title(doc.getTitle())
                .firstPublishYear(doc.getFirstPublishYear())
                .coverId(doc.getCoverId())
                .language(extractLanguage(doc))
                .searchCount(1)
                .author(author)
                .build();
        
        return bookRepository.save(book);
    }

    private String extractAuthorOlid(SearchResultDoc doc) {
        return doc.getAuthorKey() != null && !doc.getAuthorKey().isEmpty() 
                ? doc.getAuthorKey().get(0) : null;
    }

    private String extractAuthorName(SearchResultDoc doc) {
        return doc.getAuthorName() != null && !doc.getAuthorName().isEmpty() 
                ? doc.getAuthorName().get(0) : null;
    }

    private String extractLanguage(SearchResultDoc doc) {
        return doc.getLanguage() != null && !doc.getLanguage().isEmpty() 
                ? doc.getLanguage().get(0) : "unknown";
    }
} 