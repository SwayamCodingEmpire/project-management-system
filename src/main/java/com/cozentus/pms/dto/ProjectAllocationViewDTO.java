package com.cozentus.pms.dto;

import org.springframework.data.domain.Page;

public record ProjectAllocationViewDTO(
	    String projectCode,
	    String projectName,
	    String customerName,
	    Page<ProjectResourceAllocationsDTO> resourceAllocations
		) {

}
