package com.cozentus.pms.dto;

import java.math.BigDecimal;

import com.cozentus.pms.helpers.SkillPriority;

public record UserSkillDetailsDTO(
		String empId,
	    String skillName,
	    BigDecimal skillExperience,
	    SkillPriority priority,
	    String level) {

}
