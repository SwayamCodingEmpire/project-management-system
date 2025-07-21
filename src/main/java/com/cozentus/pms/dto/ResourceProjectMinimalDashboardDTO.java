package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record ResourceProjectMinimalDashboardDTO(
		String code,
			String name,
			String role,
			BigDecimal utiliation) {

}
