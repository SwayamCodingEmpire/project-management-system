package com.cozentus.pms.dto;

public record TimesheetForManagerFlatDTO(
		String resourceId,
		String projectCode,
	    String projectName,
	    ProjectTimeSheetDTO projectTimeSheet
		) {

}
