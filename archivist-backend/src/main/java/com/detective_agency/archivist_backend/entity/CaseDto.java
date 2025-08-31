package com.detective_agency.archivist_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseDto {
    private long id;
    private String casename;
    private String casedescription;
    private String ownerUsername;;

}
