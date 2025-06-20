package com.Alura.LiterAlura.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@Service
public class IdGeneratorService {

    /**
     * Genera un ID único basado en título y autor usando hash SHA-256
     * Esto evita colisiones y es más robusto que concatenación simple
     */
    public String generateBookId(String title, String authorOpenLibraryId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        
        // Normalizar título
        String normalizedTitle = normalizeString(title);
        String authorPart = authorOpenLibraryId != null ? authorOpenLibraryId : "unknown";
        
        // Crear string único
        String uniqueString = normalizedTitle + "|" + authorPart;
        
        // Generar hash SHA-256
        return "BOOK_" + generateHash(uniqueString);
    }

    /**
     * Genera un ID para autor si no tiene openLibraryId
     */
    public String generateAuthorId(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del autor no puede estar vacío");
        }
        
        String normalizedName = normalizeString(name);
        return "AUTHOR_" + generateHash(normalizedName);
    }

    private String normalizeString(String input) {
        return input.trim()
                   .toLowerCase(Locale.ROOT)
                   .replaceAll("[^a-z0-9\\s]", "") // Remover caracteres especiales
                   .replaceAll("\\s+", " ");       // Normalizar espacios
    }

    private String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // Convertir a hex y tomar los primeros 12 caracteres
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString().substring(0, 12).toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            // Fallback a hashCode si SHA-256 no está disponible
            return String.valueOf(Math.abs(input.hashCode()));
        }
    }
} 