package com.cozentus.pms.dto;

import java.util.List;

public record ProjectResourceAllocationDTO(
	    String projectCode,
	    List<ResourceAllocationsSubmitDTO> allocations
	) {}