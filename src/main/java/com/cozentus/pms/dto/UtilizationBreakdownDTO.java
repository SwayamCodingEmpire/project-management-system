package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record UtilizationBreakdownDTO(
	    BigDecimal plannedUtilCustomer,
	    BigDecimal actualUtilCustomer,
	    BigDecimal plannedUtilInternal,
	    BigDecimal actualUtilInternal
	) {}

