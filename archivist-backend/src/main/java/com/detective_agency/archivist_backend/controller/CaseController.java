package com.detective_agency.archivist_backend.controller;

import com.detective_agency.archivist_backend.dto.CaseCreateRequestDto;
import com.detective_agency.archivist_backend.dto.NoteCreateRequestDto;
import com.detective_agency.archivist_backend.entity.CaseDto;
import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.entity.Note;
import com.detective_agency.archivist_backend.entity.NoteDto;
import com.detective_agency.archivist_backend.service.CaseService;
import com.detective_agency.archivist_backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// этот контроллер — наш "диспетчер". он принимает все http-запросы по делам.
@RequestMapping("/api/cases")
public class CaseController {

    // подключаем сервис, который будет выполнять основную работу.
    @Autowired
    private CaseService caseService;

    @Autowired
    private NoteService noteService;

    // принимает post-запрос на создание нового дела.
    @PostMapping()
    public ResponseEntity<?> addCase(@RequestBody CaseCreateRequestDto caseDto) {
        try {
            // передаём данные из запроса в сервис.
            Case createdCase = caseService.addCase(caseDto);

            // упаковываем результат обратно в dto для безопасного ответа клиенту.
            CaseDto responseDto = new CaseDto(
                    createdCase.getId(),
                    createdCase.getCasename(),
                    createdCase.getCasedescription(),
                    createdCase.getCaseowner().getUsername()
            );

            // если всё прошло успешно, возвращаем статус 201 created.
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalStateException e) {
            // ловим ошибку, если такое дело уже существует, и сообщаем о конфликте.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // обрабатывает patch-запрос на частичное обновление дела.
    @PatchMapping("/{caseId}")
    public ResponseEntity<?> updateCase(@PathVariable long caseId, @RequestBody CaseCreateRequestDto caseDto) {
        try {
            Case updatedCase = caseService.updateCase(caseId, caseDto);

            CaseDto responseDto = new CaseDto(
                    updatedCase.getId(),
                    updatedCase.getCasename(),
                    updatedCase.getCasedescription(),
                    updatedCase.getCaseowner().getUsername()
            );

            return ResponseEntity.ok(responseDto);

        } catch (SecurityException e) {
            // ловим ошибку доступа, если агент пытается править не своё дело.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());

        } catch (RuntimeException e) {
            // или ошибку, если дело вообще не найдено в архиве.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // принимает delete-запрос на удаление дела.
    @DeleteMapping("/{caseId}")
    public ResponseEntity<?> deleteCase(@PathVariable long caseId) {
        try {
            caseService.deleteCase(caseId);
            // если всё прошло гладко, отвечаем 204 no content (стандарт для delete).
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            // обработка ошибок доступа и поиска такая же, как при обновлении.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // get-запрос на получение списка всех дел текущего пользователя.
    @GetMapping
    public List<CaseDto> getAllCases() {
        // просто передаём запрос в сервис и возвращаем то, что он найдёт.
        return caseService.getAllCases();
    }

    // post-запрос на добавление заметок
    @PostMapping("/{caseId}/notes")
    public ResponseEntity<?> createNoteForCase(@PathVariable long caseId, @RequestBody NoteCreateRequestDto noteDto) {
        try {
            Note createdNote = noteService.createNoteForCase(caseId, noteDto);
            NoteDto responseDto = new NoteDto(
                    createdNote.getId(),
                    createdNote.getTitle(),
                    createdNote.getDescription(),
                    createdNote.getType(),
                    createdNote.getParentCase().getCasename()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // get-запрос на получение всех заметок по делу
    @GetMapping("/{caseId}/notes")
    public ResponseEntity<?> getNotesForCase(@PathVariable long caseId) {
        try {
            List<NoteDto> notes = noteService.getNotesForCase(caseId);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}