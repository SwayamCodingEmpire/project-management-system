package com.cozentus.pms.dto;

import java.util.List;

public record UserSkillDTO(
		String empId,
		List<String> skills) {

}
