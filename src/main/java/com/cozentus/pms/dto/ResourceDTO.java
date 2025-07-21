package com.cozentus.pms.dto;

import java.util.List;



public record ResourceDTO(
	    String id,
	    String name,
	    String emailId,
	    String phoneNumber,
	    List<SkillDTO> primarySkill,
	    List<SkillDTO> secondarySkill,
	    String designation,
	    double experience,
	    String role,
	    String reportingManagerId,
	    String reportingManagerName,
	    List<ProjectAllocationDTO> allocation
	) {}

