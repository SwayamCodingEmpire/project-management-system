package com.cozentus.pms.dto;

import java.util.List;

public record ConvertedSkills(
		  List<SkillDTO> primarySkill,
		    List<SkillDTO> secondarySkill
		) {

}
