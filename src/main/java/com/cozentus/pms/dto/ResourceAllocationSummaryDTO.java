package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ResourceAllocationSummaryDTO(
		String projectCode,
		String projectName,
		String empId,
		String empEmail,
		String empName,
		LocalDate allocationStartDate,
		LocalDate allocationEndDate,
	    String designation,
	    BigDecimal yearsOfExperience,
	    BigDecimal billabilityPercentage,
	    BigDecimal dailtyWOrkingHours,
	    BigDecimal plannedUtilizationPercentage) {

}
