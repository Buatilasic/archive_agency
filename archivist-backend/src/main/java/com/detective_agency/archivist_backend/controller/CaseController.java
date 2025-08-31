package com.detective_agency.archivist_backend.controller;

import com.detective_agency.archivist_backend.dto.CaseCreateRequestDto;
import com.detective_agency.archivist_backend.entity.CaseDto;
import com.detective_agency.archivist_backend.entity.Case;
import com.detective_agency.archivist_backend.service.CaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases") // Общий адрес для всех методов, связанных с делами
public class CaseController {

    @Autowired
    private CaseService caseService;

    @PostMapping() // Можно использовать более короткий адрес
    public ResponseEntity<?> addCase(@RequestBody CaseCreateRequestDto caseDto) { // 1. Принимаем DTO
        try {
            Case createdCase = caseService.addCase(caseDto); // 2. Передаём DTO в сервис

            // 3. Преобразуем результат в DTO для безопасного ответа
            CaseDto responseDto = new CaseDto(
                    createdCase.getId(),
                    createdCase.getCasename(),
                    createdCase.getCasedescription(),
                    createdCase.getCaseowner().getUsername()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping // Это GET-запрос по адресу /api/cases
    public List<CaseDto> getAllCases() {
        return caseService.getAllCases();
    }
}