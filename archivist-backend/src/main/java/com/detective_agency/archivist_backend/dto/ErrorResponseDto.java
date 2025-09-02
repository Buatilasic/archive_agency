package com.detective_agency.archivist_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Создаст конструктор для поля message
public class ErrorResponseDto {
    private String message;
}