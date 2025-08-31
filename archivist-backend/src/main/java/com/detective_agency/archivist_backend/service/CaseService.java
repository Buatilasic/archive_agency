package com.detective_agency.archivist_backend.service;

import com.detective_agency.archivist_backend.dto.CaseCreateRequestDto;
import com.detective_agency.archivist_backend.entity.CaseDto;
import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.entity.User;
import com.detective_agency.archivist_backend.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("cs")
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    public Case addCase(CaseCreateRequestDto caseDto) { // <-- Принимаем DTO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (caseRepository.findByCasenameAndCaseowner(caseDto.getCasename()).isPresent()) {
            throw new IllegalStateException("Дело с таким названием у вас уже есть!");
        }

        // Создаём новую сущность Case и наполняем её данными
        Case newCase = new Case();
        newCase.setCasename(caseDto.getCasename());
        newCase.setCasedescription(caseDto.getCasedescription());
        newCase.setCaseowner(currentUser); // <-- Назначаем владельца сами!

        return caseRepository.save(newCase);
    }

    public List<CaseDto> getAllCases() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return caseRepository.findByCaseowner(currentUser)
                .stream()
                .map(aCase -> new CaseDto(
                        aCase.getId(),
                        aCase.getCasename(),
                        aCase.getCasedescription(),
                        aCase.getCaseowner().getUsername()
                ))
                .collect(Collectors.toList());
    }
}