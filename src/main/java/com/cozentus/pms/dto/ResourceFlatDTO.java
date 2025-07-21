package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.util.List;

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
	    ProjectAllocationDTO allocation 
	) {}
