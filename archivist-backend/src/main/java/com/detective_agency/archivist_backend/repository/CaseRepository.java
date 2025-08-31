package com.detective_agency.archivist_backend.repository;

import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    List<Case> findByCaseowner(User caseowner);

    Optional<Case> findByCasenameAndCaseowner(String casename, User caseowner);
}