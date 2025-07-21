package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UtilizationPairDTO(
		LocalDate date,
	    BigDecimal plannedUtilization,
	    BigDecimal actualUtilization
	) {}

