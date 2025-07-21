package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record BilledNotBilledDTO(
	    BigDecimal billed,
	    int notBilled
		) {

}
