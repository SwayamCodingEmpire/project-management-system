package com.cozentus.pms.dto;

import java.util.List;

public record SkillCountDTO(
		 String name,
		    int totalCount,
		    List<LevelCount> levels) {

}
