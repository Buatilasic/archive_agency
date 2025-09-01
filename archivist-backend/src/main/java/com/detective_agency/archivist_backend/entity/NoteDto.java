package com.detective_agency.archivist_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDto {
    private long id;
    private String notename;
    private String description;
    private NoteType type;
    private String caseCasename;

}
