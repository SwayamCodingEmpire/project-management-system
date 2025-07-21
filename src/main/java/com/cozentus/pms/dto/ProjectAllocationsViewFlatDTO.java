package com.cozentus.pms.dto;

public record ProjectAllocationsViewFlatDTO(
	    String projectCode,
	    String projectName,
	    String customerName,
	    ProjectResourceAllocationsWithoutSkillsDTO resourceAllocations) {

}
