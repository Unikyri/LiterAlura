package com.Alura.LiterAlura.repository;

import com.Alura.LiterAlura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByOpenLibraryId(String openLibraryId);

    // Optimización: Query SQL directa en lugar de findAll() + filtro en memoria
    @Query("SELECT a FROM Author a WHERE " +
           "(a.birthDate IS NULL OR YEAR(a.birthDate) <= :year) AND " +
           "(a.deathDate IS NULL OR YEAR(a.deathDate) >= :year)")
    List<Author> findAuthorsAliveInYear(@Param("year") int year);

    // Optimización: Verificar existencia sin traer el objeto completo
    boolean existsByOpenLibraryId(String openLibraryId);

    // Optimización: Buscar por nombre para evitar duplicados
    Optional<Author> findByNameIgnoreCase(String name);
} 