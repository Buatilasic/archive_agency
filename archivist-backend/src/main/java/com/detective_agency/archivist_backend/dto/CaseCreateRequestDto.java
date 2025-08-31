// В новом файле CaseCreateRequestDto.java в пакете dto
package com.detective_agency.archivist_backend.dto;

import lombok.Data;

@Data
public class CaseCreateRequestDto {
    private String casename;
    private String casedescription;
}