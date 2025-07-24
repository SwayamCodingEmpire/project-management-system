package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record DMResourceStatsPartialDTO(
	    BigDecimal totalBillability,
	    Long totalResourceUsers,
	    Long zeroOrNoBillabilityUsers
	) {}