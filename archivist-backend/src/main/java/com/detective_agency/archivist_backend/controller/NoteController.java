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
// диспетчер для всех запросов, касающихся заметок в рамках одного дела.
@RequestMapping("/api/cases/{caseId}/notes")
public class NoteController {

    // подключаем сервис, который умеет работать с заметками.
    @Autowired
    private NoteService noteService;

    // принимает post-запрос на создание заметки в конкретном деле.
    @PostMapping
    public ResponseEntity<?> createNote(@PathVariable long caseId, @RequestBody NoteCreateRequestDto noteDto) {
        try {
            // id дела берём из пути, а данные для заметки — из тела запроса.
            Note createdNote = noteService.createNoteForCase(caseId, noteDto);

            // конвертируем результат в dto для ответа.
            NoteDto responseDto = new NoteDto(
                    createdNote.getId(),
                    createdNote.getTitle(),
                    createdNote.getDescription(),
                    createdNote.getType(),
                    createdNote.getACase().getCasename()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            // ловим ошибки (например, дело не найдено или нет доступа) и возвращаем общий 'плохой запрос'.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // get-запрос на получение всех заметок по id дела.
    @GetMapping
    public ResponseEntity<?> getNotesForCase(@PathVariable long caseId) {
        try {
            List<NoteDto> notes = noteService.getNotesForCase(caseId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            // если сервис выдаёт ошибку (дело не найдено), отправляем статус 'не найдено'.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}