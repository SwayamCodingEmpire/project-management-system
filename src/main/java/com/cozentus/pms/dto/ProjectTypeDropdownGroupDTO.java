package com.cozentus.pms.dto;

import java.util.List;

public record ProjectTypeDropdownGroupDTO(
	    String label,
	    List<ProjectTypeOptionsDTO> options
	) {}

