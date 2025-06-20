package com.Alura.LiterAlura.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_book_openlibrary_id", columnList = "openlibrary_id"),
    @Index(name = "idx_book_title", columnList = "title"),
    @Index(name = "idx_book_language", columnList = "language"),
    @Index(name = "idx_book_search_count", columnList = "search_count"),
    @Index(name = "idx_book_author", columnList = "author_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "openLibraryId")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Clave única mejorada
    @Column(name = "openlibrary_id", unique = true, nullable = false)
    private String openLibraryId;

    @Column(nullable = false)
    private String title;

    @Column(name = "first_publish_year")
    private Integer firstPublishYear;

    @Column(name = "cover_id")
    private Integer coverId;

    private String language;

    @Column(name = "search_count")
    @Builder.Default
    private Integer searchCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    // Métodos helper optimizados
    public void incrementSearchCount() {
        this.searchCount = this.searchCount == null ? 1 : this.searchCount + 1;
    }

    public String getLanguageDisplay() {
        if (language == null) return "Desconocido";
        return switch (language.toLowerCase()) {
            case "es" -> "Español";
            case "en" -> "Inglés";
            case "fr" -> "Francés";
            case "de" -> "Alemán";
            case "it" -> "Italiano";
            case "pt" -> "Portugués";
            default -> language;
        };
    }
} 