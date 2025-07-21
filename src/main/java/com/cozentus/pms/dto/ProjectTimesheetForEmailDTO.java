package com.cozentus.pms.dto;

import java.time.LocalDate;

import com.cozentus.pms.helpers.ApprovalStatus;

public record ProjectTimesheetForEmailDTO(
		String deliverymanagerName,
		String deluveryManagerId,
		String deliveryManagerEmail,
		String projectManagerName,
		String projectManagerId,
		String projectManagerEmail,
		String projectCode,
		String projectName,
		String resourceId,
		String resourceEmail,
		String resourceName,
		String resourceRole,
		String allocationRole,
		LocalDate allocationStartDate,
		LocalDate allocationEndDate
		) {

}
