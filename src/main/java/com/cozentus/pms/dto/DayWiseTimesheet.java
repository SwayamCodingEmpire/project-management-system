package com.cozentus.pms.dto;

import java.math.BigDecimal;

public record DayWiseTimesheet(
		boolean attendanceStatus,
	    String projectCode,
	    BigDecimal hoursWorked
		) {

}
