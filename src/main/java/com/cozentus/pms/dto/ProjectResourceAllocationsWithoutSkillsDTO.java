package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectResourceAllocationsWithoutSkillsDTO(
		String empId,
		String emailId,
		String name,
		String designation,
		BigDecimal totalWorkingHoursDaily,
		Long totalDaysWorked,
		LocalDate allocationStartDate,
		LocalDate allocationEndDate,
		LocalDate actualAllocationEndDate,
		String role,
		BigDecimal experience,
		BigDecimal billability,
		BigDecimal plannedUtil,
		BigDecimal actualUtil
		) {

}
