package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record SkillDTO(
	    String skillName,
	    BigDecimal skillExperience,
	    String level) {

}
