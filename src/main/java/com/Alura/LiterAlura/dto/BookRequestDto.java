package com.Alura.LiterAlura.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookRequestDto {
    
    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 1, max = 255, message = "El título debe tener entre 1 y 255 caracteres")
    private String title;
} 