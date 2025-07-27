package com.cozentus.pms.dto;

import java.math.BigDecimal;

import com.cozentus.pms.helpers.Roles;

public record ResourceFlatDTO( 
	    String id,
	    String name,
	    String emailId,
	    String phoneNumber,
	    String designation,
	    BigDecimal experience,
	    String role,
	    String reportingManagerId,
	    String reportingManagerName,
	    String deliveryManagerEmpId,
	    String deliveryManagerName,
	    Roles resourceRole,
	    ProjectAllocationDTO allocation 
	) {}
