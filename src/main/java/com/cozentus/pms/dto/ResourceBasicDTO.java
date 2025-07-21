package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record ResourceBasicDTO(
		String name,
		String employeeId,
		String designation,
		BigDecimal experience,
		BigDecimal utilization,
		BigDecimal dailyWorkingHours
		) {

}
