package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResourceAllocationsSubmitDTO(
		  String id,
		    LocalDate start,
		    LocalDate end,
		    String role,
		    BigDecimal billability,
		    BigDecimal plannedHours
		) {
	
}
