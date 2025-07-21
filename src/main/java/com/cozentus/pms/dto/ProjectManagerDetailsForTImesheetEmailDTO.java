package com.cozentus.pms.dto;

import java.util.List;

public record ProjectManagerDetailsForTImesheetEmailDTO(
		String projectManagerName,
		String projectManagerId,
		String projectManagerEmail,
		List<ProjectDetailsForTimesheetDTO> projectDetailsForTimesheetDTO
		) {

}
