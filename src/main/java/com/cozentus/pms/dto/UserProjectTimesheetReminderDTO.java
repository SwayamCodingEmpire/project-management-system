package com.cozentus.pms.dto;

public record UserProjectTimesheetReminderDTO(
		String empId,
		String name,
		String email,
		String projectCode,
		String projectName, 
		String managerName,
		String managerEmail,
		String managerPhoneNo,
		String managerId
		) {

}
