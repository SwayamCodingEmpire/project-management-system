package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record DMResourceStatsDTO(
		   BigDecimal totalBillability,
		    Long totalResourceUsers,
		    Long zeroOrNoBillabilityUsers,
		    Long zeroOrNoPlannedUtilisation
		) {

}
