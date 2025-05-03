package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccinationEventDto {
    private Long vaccinationId;
    private String identityDocument;
    private String vaccineName;
    private LocalDate vaccinationDate;
}