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

import static com.detective_agency.archivist_backend.utils.SecurityUtils.getCurrentUser;

@Service("cs")
public class CaseService {

    @Autowired
    private CaseRepository caseRepository;

    public Case addCase(CaseCreateRequestDto caseDto) { // <-- Принимаем DTO
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (caseRepository.findByCasenameAndCaseowner(caseDto.getCasename(), currentUser).isPresent()) {
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

    // Метод для частичного обновления "Дела"
    public Case updateCase(long caseId, CaseCreateRequestDto updateDto) {
        // 1. Получаем текущего пользователя
        User currentUser = getCurrentUser();

        // 2. Находим дело в архиве по его ID
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Дело с id " + caseId + " не найдено!"));

        // 3. Проверяем владение - это обязательно!
        if (!existingCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("Доступ запрещён: вы не являетесь владельцем этого дела.");
        }

        // Частично обновляем поля, если они пришли в DTO
        // Проверяем, что новое имя не пустое и не состоит из пробелов
        if (updateDto.getCasename() != null && !updateDto.getCasename().isBlank()) {
            existingCase.setCasename(updateDto.getCasename());
        }

        // Описание может быть и пустым, поэтому просто проверяем на null
        if (updateDto.getCasedescription() != null) {
            existingCase.setCasedescription(updateDto.getCasedescription());
        }

        // 5. Сохраняем обновлённое дело в архив. Метод save() используется и для создания, и для обновления.
        return caseRepository.save(existingCase);
    }
    public void deleteCase(long caseId) {
        User currentUser = getCurrentUser();

        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Дело с id " + caseId + " не найдено!"));

        if (!existingCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("Доступ запрещён: вы не являетесь владельцем этого дела.");
        }
        caseRepository.deleteById(caseId);
    }
}