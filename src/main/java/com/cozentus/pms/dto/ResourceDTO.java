package com.cozentus.pms.dto;

import java.util.List;

import com.cozentus.pms.helpers.Roles;



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
	    String deliveryManagerEmpId,
	    String deliveryManagerName,
	    Roles resourceRole,
	    List<ProjectAllocationDTO> allocation
	) {}

