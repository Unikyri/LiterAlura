package com.Alura.LiterAlura.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors", indexes = {
    @Index(name = "idx_author_openlibrary_id", columnList = "openlibrary_id"),
    @Index(name = "idx_author_name", columnList = "name"),
    @Index(name = "idx_author_dates", columnList = "birth_date, death_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "openLibraryId")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // OLID del autor en OpenLibrary, ejemplo "OL34184A"
    @Column(name = "openlibrary_id", unique = true, nullable = false)
    private String openLibraryId;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Book> books = new ArrayList<>();

    // Métodos helper optimizados
    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }

    public boolean isAliveInYear(int year) {
        boolean bornBeforeOrInYear = birthDate == null || birthDate.getYear() <= year;
        boolean diedAfterYear = deathDate == null || deathDate.getYear() >= year;
        return bornBeforeOrInYear && diedAfterYear;
    }
} 