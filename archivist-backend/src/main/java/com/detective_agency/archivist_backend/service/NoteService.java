package com.detective_agency.archivist_backend.service;

import com.detective_agency.archivist_backend.dto.NoteCreateRequestDto;
import com.detective_agency.archivist_backend.entity.NoteDto;
import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.entity.Note;
import com.detective_agency.archivist_backend.entity.User;
import com.detective_agency.archivist_backend.repository.CaseRepository;
import com.detective_agency.archivist_backend.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("ns")
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private CaseRepository caseRepository; // Нам нужен доступ и к репозиторию "дел"

    public Note createNoteForCase(long caseId, NoteCreateRequestDto noteDto) {
        User currentUser = getCurrentUser();

        Case aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Дело с id " + caseId + " не найдено!"));

        if (!aCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("Доступ запрещён: вы не являетесь владельцем этого дела.");
        }

        Note newNote = new Note();
        newNote.setTitle(noteDto.getTitle());
        newNote.setDescription(noteDto.getDescription());
        newNote.setType(noteDto.getType());
        newNote.setACase(aCase); // Устанавливаем связь с делом

        return noteRepository.save(newNote);
    }

    /**
     * Возвращает все заметки для дела с указанным caseId.
     */
    @Transactional(readOnly = true)
    public List<NoteDto> getNotesForCase(long caseId) {
        User currentUser = getCurrentUser();
        Case aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Дело с id " + caseId + " не найдено!"));

        // Снова ПРОВЕРКА ВЛАДЕНИЯ: Показываем заметки, только если агент - владелец дела
        if (!aCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("Доступ запрещён.");
        }

        // Находим все заметки, связанные с этим делом, и преобразуем их в DTO
        return noteRepository.findByACase(aCase)
                .stream()
                .map(note -> new NoteDto(
                        note.getId(),
                        note.getTitle(), // Используем getTitle()
                        note.getDescription(),
                        note.getType(),
                        note.getACase().getCasename()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод для получения текущего пользователя.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}