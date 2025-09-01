package com.detective_agency.archivist_backend.repository;

import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByTitle(String title);

    List<Note> findByACase(Case aCase);
}