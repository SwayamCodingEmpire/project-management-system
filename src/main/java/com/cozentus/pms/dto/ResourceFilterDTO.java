package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResourceFilterDTO(
		String skill,
		    List<String> designation,
		    BigDecimal experience
		) {

}
