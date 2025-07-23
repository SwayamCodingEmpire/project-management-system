package com.cozentus.pms.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record ProjectResourceAllocationDTO(
		@NotBlank(message = "Project code cannot be blank")
	    String projectCode,
	    @Valid
	    List<ResourceAllocationsSubmitDTO> allocations
	) {}