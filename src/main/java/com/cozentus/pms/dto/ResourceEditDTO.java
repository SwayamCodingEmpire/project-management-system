package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record ResourceEditDTO(
		 String id,
		    String role,
		    String designation,
		    BigDecimal experience,
		    String reportingManager
		) {

}
