package com.detective_agency.archivist_backend.controller;

import com.detective_agency.archivist_backend.entity.NoteDto;
import com.detective_agency.archivist_backend.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @GetMapping("/my-notes")
    public List<NoteDto> getAllMyNotes() {
        return noteService.getAllNotesForCurrentUser();
    }
}