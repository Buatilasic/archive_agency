package com.detective_agency.archivist_backend.controller;

import com.detective_agency.archivist_backend.dto.NoteCreateRequestDto;
import com.detective_agency.archivist_backend.entity.NoteDto;
import com.detective_agency.archivist_backend.entity.Note;
import com.detective_agency.archivist_backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/notes") // Все эндпоинты для заметок будут внутри конкретного дела
public class NoteController {

    @Autowired
    private NoteService noteService;

    // Создание новой заметки для дела
    @PostMapping
    public ResponseEntity<?> createNote(@PathVariable long caseId, @RequestBody NoteCreateRequestDto noteDto) {
        try {
            Note createdNote = noteService.createNoteForCase(caseId, noteDto);
            // Преобразуем в DTO для ответа
            NoteDto responseDto = new NoteDto(
                    createdNote.getId(),
                    createdNote.getTitle(),
                    createdNote.getDescription(),
                    createdNote.getType(),
                    createdNote.getACase().getCasename()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Получение всех заметок для дела
    @GetMapping
    public ResponseEntity<?> getNotesForCase(@PathVariable long caseId) {
        try {
            List<NoteDto> notes = noteService.getNotesForCase(caseId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}