package com.detective_agency.archivist_backend.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}