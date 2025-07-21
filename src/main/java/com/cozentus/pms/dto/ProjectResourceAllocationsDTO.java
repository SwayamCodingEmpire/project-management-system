package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjectResourceAllocationsDTO(
		String empId,
		String name,
		List<SkillDTO> primarySkill,
		List<SkillDTO> secondarySkill,
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
		)
{

}
