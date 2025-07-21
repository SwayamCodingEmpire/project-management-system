package com.cozentus.pms.dto;

import java.time.LocalDate;

public record ProjectAllocationDTO(
		String projectCode,
	    String projectName,
	    String description,
	    LocalDate startDate,
	    LocalDate endDate,
	    String projectManager,
	    String projectType
	) {}
