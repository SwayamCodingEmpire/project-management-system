package com.cozentus.pms.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectDetailsDTO(
		@NotBlank
        String code,
        @NotBlank
        String name,
        @NotBlank
        String description,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        @NotBlank
        String currency,
        @NotBlank
        String contractType,
        @NotBlank
        String billingFrequency
    ) {}