package com.cozentus.pms.dto;

import java.util.List;

public record TimesheetSummaryToDMAndPMDTO(
		String deliverymanagerName,
		String deluveryManagerId,
		String deliveryManagerEmail,
		List<ProjectManagerDetailsForTImesheetEmailDTO> projectManagerDetailsForTImesheetEmailDTO
		) {

}
