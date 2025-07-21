package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record ResourceProjectUtilizationSummaryDTO(
		  String empId,
		    String projectCode,
		    Boolean isCustomerProject,
		    BigDecimal totalApprovedHours,
		    Long approvedEntryDays,
		    BigDecimal billabilityPercent,
		    BigDecimal plannedHours,
		    BigDecimal dailyWorkingHours
		) {

}
