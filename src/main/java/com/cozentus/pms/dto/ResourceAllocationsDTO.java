package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResourceAllocationsDTO(
	    String id,
	    String name,
	    List<SkillDTO> primarySkill,
	    List<SkillDTO> secondarySkill,
	    String designation,
	    BigDecimal experience,
	    List<ProjectAllocationDetailsDTO> currentAllocation,
	    BigDecimal billability,
	    BigDecimal plannedUtil,
	    BigDecimal actualUtil
	) {}
