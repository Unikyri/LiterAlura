package com.Alura.LiterAlura.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Configurar caches específicos
        cacheManager.setCacheNames(Arrays.asList(
            "books",           // Cache para todos los libros
            "booksByLanguage", // Cache para libros por idioma
            "topBooks",        // Cache para top 5 libros
            "authors",         // Cache para autores
            "authorsAlive"     // Cache para autores vivos
        ));
        
        return cacheManager;
    }
} 