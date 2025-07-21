package com.cozentus.pms.dto;

import java.util.List;

public record ProjectDetailsForTimesheetDTO(
		String projectCode,
		String projectName,
		List<ResourceDetailsForTimesheetDTO> resourceDetailsForTimesheetDTO
		) {

}
