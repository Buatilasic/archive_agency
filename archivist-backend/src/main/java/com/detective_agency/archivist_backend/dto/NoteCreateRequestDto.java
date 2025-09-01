package com.detective_agency.archivist_backend.dto;

import com.detective_agency.archivist_backend.entity.NoteType;
import lombok.Data;

@Data
public class NoteCreateRequestDto {
    private String title;
    private String description;
    private NoteType type;
}