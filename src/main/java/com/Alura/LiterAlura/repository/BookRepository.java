package com.Alura.LiterAlura.repository;

import com.Alura.LiterAlura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByOpenLibraryId(String openLibraryId);

    // Optimización: Query case-insensitive con índice
    List<Book> findByLanguageIgnoreCase(String language);

    // Optimización: Query con límite directo en SQL
    @Query("SELECT b FROM Book b ORDER BY b.searchCount DESC")
    List<Book> findTopBooksBySearchCount();

    // Optimización: Verificar existencia sin traer el objeto completo
    boolean existsByOpenLibraryId(String openLibraryId);

    // Optimización: Buscar por título y autor para evitar duplicados
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) = LOWER(:title) AND b.author.id = :authorId")
    Optional<Book> findByTitleAndAuthor(@Param("title") String title, @Param("authorId") Long authorId);

    // Optimización: Incrementar searchCount directamente en BD
    @Modifying
    @Query("UPDATE Book b SET b.searchCount = b.searchCount + 1 WHERE b.id = :bookId")
    void incrementSearchCount(@Param("bookId") Long bookId);

    // Optimización: Contar libros por idioma sin traer todos los registros
    @Query("SELECT COUNT(b) FROM Book b WHERE LOWER(b.language) = LOWER(:language)")
    long countByLanguage(@Param("language") String language);
} 