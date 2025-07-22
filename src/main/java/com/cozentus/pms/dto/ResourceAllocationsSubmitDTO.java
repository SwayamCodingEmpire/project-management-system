package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResourceAllocationsSubmitDTO(
		@NotBlank(message = "Employee ID cannot be blank")
		  String id,
		  @NotNull
		    LocalDate start,
		    @NotNull
		    LocalDate end,
		    @NotBlank
		    String role,
		    @NotNull
		    BigDecimal billability,
		    @NotNull
		    BigDecimal plannedHours
		) {
	
}
