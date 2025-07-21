package com.cozentus.pms.dto;

import java.math.BigDecimal;

import com.cozentus.pms.helpers.SkillPriority;

public record ResourceAllocationsFlatDTO(
    String id,
    String name,
    String designation,
    BigDecimal experience,
    BigDecimal dailyWorkingHours,
    ProjectAllocationDetailsDTO currentAllocation
) {}

