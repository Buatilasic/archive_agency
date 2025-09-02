package com.detective_agency.archivist_backend.service;

import com.detective_agency.archivist_backend.dto.NoteCreateRequestDto;
import com.detective_agency.archivist_backend.entity.NoteDto;
import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.entity.Note;
import com.detective_agency.archivist_backend.entity.User;
import com.detective_agency.archivist_backend.repository.CaseRepository;
import com.detective_agency.archivist_backend.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.detective_agency.archivist_backend.utils.SecurityUtils.getCurrentUser;

// этот сервис управляет заметками и уликами внутри конкретного дела.
@Service("ns")
public class NoteService {

    // прямой доступ к хранилищу заметок.
    @Autowired
    private NoteRepository noteRepository;

    // нужен, чтобы связывать заметки с их делами.
    @Autowired
    private CaseRepository caseRepository;

    // создаём новую заметку и прикрепляем её к делу.
    public Note createNoteForCase(long caseId, NoteCreateRequestDto noteDto) {
        User currentUser = getCurrentUser();

        // сначала находим дело, к которому относится заметка.
        Case aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("дело с id " + caseId + " не найдено!"));

        // важная проверка: убеждаемся, что пользователь — владелец этого дела.
        if (!aCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("доступ запрещён: вы не являетесь владельцем этого дела.");
        }

        // создаём саму заметку и наполняем её данными.
        Note newNote = new Note();
        newNote.setTitle(noteDto.getTitle());
        newNote.setDescription(noteDto.getDescription());
        newNote.setType(noteDto.getType());
        // устанавливаем связь с родительским делом.
        newNote.setParentCase(aCase);

        // сохраняем заметку в базу.
        return noteRepository.save(newNote);
    }

    // получаем все заметки для указанного дела.
    @Transactional(readOnly = true)
    public List<NoteDto> getNotesForCase(long caseId) {
        User currentUser = getCurrentUser();
        Case aCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("дело с id " + caseId + " не найдено!"));

        // снова проверка: показываем заметки, только если пользователь владеет делом.
        if (!aCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("доступ запрещён.");
        }

        // находим все заметки, связанные с этим делом, и преобразуем их в dto.
        return noteRepository.findByParentCase(aCase)
                .stream()
                .map(note -> new NoteDto(
                        note.getId(),
                        note.getTitle(),
                        note.getDescription(),
                        note.getType(),
                        note.getParentCase().getCasename()
                ))
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<NoteDto> getAllNotesForCurrentUser() {
        User currentUser = getCurrentUser();

        return noteRepository.findAllByUser(currentUser)
                .stream()
                .map(note -> new NoteDto(
                        note.getId(),
                        note.getTitle(),
                        note.getDescription(),
                        note.getType(),
                        note.getParentCase().getCasename()
                ))
                .collect(Collectors.toList());
    }
}