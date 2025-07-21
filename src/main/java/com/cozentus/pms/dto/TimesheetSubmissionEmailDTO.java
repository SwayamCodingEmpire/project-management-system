package com.cozentus.pms.dto;

public record TimesheetSubmissionEmailDTO(
		String projectCode,
		String projectName,
		String managerName,
		String managerId,
		String managerEmail
		) {

}
