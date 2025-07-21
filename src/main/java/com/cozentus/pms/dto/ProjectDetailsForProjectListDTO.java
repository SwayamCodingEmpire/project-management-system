package com.cozentus.pms.dto;

import java.time.LocalDate;

public record ProjectDetailsForProjectListDTO(
        String projectCode,
        String projectName,
        String customerName,
        String currency,
        LocalDate scheduleStartDate,
        LocalDate scheduleEndDate,
        String projectManager,
        String projectType) {

}
