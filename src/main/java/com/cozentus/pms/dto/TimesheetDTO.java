package com.cozentus.pms.dto;

import java.util.List;

public record TimesheetDTO(
	    String projectCode,
	    String projectName,
	    List<ProjectTimeSheetDTO> projectTimeSheet) {

}
