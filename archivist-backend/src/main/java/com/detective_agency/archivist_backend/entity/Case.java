package com.detective_agency.archivist_backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "title", length = 64, nullable = false)
    private String casename;

    @Column(name = "description")
    private String casedescription;

    @ManyToOne(fetch = FetchType.LAZY) // Указываем, что много "дел" могут принадлежать одному "пользователю"
    @JoinColumn(name = "owner_id", nullable = false) // Указываем, что в таблице 'cases' будет колонка 'owner_id', которая ссылается на ID пользователя
    private User caseowner; // Тип поля - сама сущность User, а не String
}