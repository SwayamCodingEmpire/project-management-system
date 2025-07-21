package com.cozentus.pms.dto;

import java.math.BigDecimal;

import com.cozentus.pms.helpers.SkillPriority;

public record SkillUpsertDTO(
		BigDecimal experience,
		String skillLevel,
		SkillPriority skillPriority
		) {

}
