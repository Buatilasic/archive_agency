package com.detective_agency.archivist_backend.repository;

import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.entity.Note;
import com.detective_agency.archivist_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByTitle(String title);

    List<Note> findByParentCase(Case aCase);

    @Query("SELECT n FROM Note n WHERE n.parentCase.caseowner = :user")
    List<Note> findAllByUser(@Param("user") User user);
}