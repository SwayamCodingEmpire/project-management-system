package com.cozentus.pms.dto;

import java.util.List;

public record ProjectTypeDropdownGroupDTO(
	    Boolean isCustomerType,
	    List<ProjectTypeOptionsDTO> options
	) {}

