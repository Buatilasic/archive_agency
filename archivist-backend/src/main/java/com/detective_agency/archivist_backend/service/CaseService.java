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

// это наш главный сервис для работы с делами в архиве.
@Service("cs")
public class CaseService {

    // подключаемся к самому архиву.
    @Autowired
    private CaseRepository caseRepository;

    // метод для заведения нового дела.
    public Case addCase(CaseCreateRequestDto caseDto) {
        // сначала выясняем, кто ведёт расследование.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // проверяем, нет ли у этого следователя уже дела с таким же "позывным".
        if (caseRepository.findByCasenameAndCaseowner(caseDto.getCasename(), currentUser).isPresent()) {
            throw new IllegalStateException("дело с таким названием у вас уже есть!");
        }

        // создаём новую папку для дела и заполняем её данными из запроса.
        Case newCase = new Case();
        newCase.setCasename(caseDto.getCasename());
        newCase.setCasedescription(caseDto.getCasedescription());
        // и, конечно, указываем, что владелец дела - текущий следователь.
        newCase.setCaseowner(currentUser);

        // отправляем дело в архив на хранение.
        return caseRepository.save(newCase);
    }

    // достаём из архива все дела, которые ведёт текущий следователь.
    public List<CaseDto> getAllCases() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // запрашиваем у архива все папки, помеченные его именем,
        // а затем преобразуем "сырые" данные в удобные карточки (dto).
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

    // здесь мы вносим правки в уже существующее дело.
    public Case updateCase(long caseId, CaseCreateRequestDto updateDto) {
        // узнаём, кто пытается внести изменения.
        User currentUser = getCurrentUser();

        // находим нужное дело в архиве по его номеру.
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("дело с id " + caseId + " не найдено!"));

        // ключевая проверка: удостоверяемся, что это его дело, а не чужое.
        if (!existingCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("доступ запрещён: вы не являетесь владельцем этого дела.");
        }

        // если поступила новая информация (имя или описание), обновляем досье.
        if (updateDto.getCasename() != null && !updateDto.getCasename().isBlank()) {
            existingCase.setCasename(updateDto.getCasename());
        }

        if (updateDto.getCasedescription() != null) {
            existingCase.setCasedescription(updateDto.getCasedescription());
        }

        // сохраняем обновлённое дело обратно в архив.
        return caseRepository.save(existingCase);
    }

    // метод для отправки дела в шредер.
    public void deleteCase(long caseId) {
        // как всегда, сначала протокол: кто удаляет?
        User currentUser = getCurrentUser();

        // находим папку, которую нужно уничтожить.
        Case existingCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("дело с id " + caseId + " не найдено!"));

        // и снова проверка безопасности. мы же не можем позволить кому-то удалять чужие улики.
        if (!existingCase.getCaseowner().equals(currentUser)) {
            throw new SecurityException("доступ запрещён: вы не являетесь владельцем этого дела.");
        }

        // всё чисто. удаляем.
        caseRepository.deleteById(caseId);
    }
}